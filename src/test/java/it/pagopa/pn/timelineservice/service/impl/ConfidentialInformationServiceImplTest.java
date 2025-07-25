package it.pagopa.pn.timelineservice.service.impl;

import it.pagopa.pn.commons.exceptions.PnHttpResponseException;
import it.pagopa.pn.timelineservice.dto.address.PhysicalAddressInt;
import it.pagopa.pn.timelineservice.dto.ext.datavault.ConfidentialTimelineElementDtoInt;
import it.pagopa.pn.timelineservice.dto.timeline.TimelineElementInternal;
import it.pagopa.pn.timelineservice.dto.timeline.details.SendAnalogDetailsInt;
import it.pagopa.pn.timelineservice.generated.openapi.msclient.datavault.model.AddressDto;
import it.pagopa.pn.timelineservice.generated.openapi.msclient.datavault.model.AnalogDomicile;
import it.pagopa.pn.timelineservice.generated.openapi.msclient.datavault.model.ConfidentialTimelineElementDto;
import it.pagopa.pn.timelineservice.middleware.externalclient.datavault.PnDataVaultClientReactive;
import it.pagopa.pn.timelineservice.service.ConfidentialInformationService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertThrows;

class ConfidentialInformationServiceImplTest {
    private ConfidentialInformationService confidentialInformationService;
    private PnDataVaultClientReactive pnDataVaultClient;
    
    @BeforeEach
    void setup() {
        pnDataVaultClient = Mockito.mock( PnDataVaultClientReactive.class );
        confidentialInformationService = new ConfidentialInformationServiceImpl(pnDataVaultClient);
    }

    @Test
    void saveTimelineConfidentialInformation() {
        String iun = "testIun";
        String elementId = "testElementId";

        // GIVEN
        TimelineElementInternal element = getSendPaperDetailsTimelineElement(iun, elementId);

        Mockito.when(pnDataVaultClient.updateNotificationTimelineByIunAndTimelineElementId(Mockito.anyString(), Mockito.any(ConfidentialTimelineElementDto.class)))
                .thenReturn(Mono.empty());

        // WHEN
        Mono<Void> result = confidentialInformationService.saveTimelineConfidentialInformation(element);

        // THEN
        StepVerifier.create(result)
                .verifyComplete();

        ArgumentCaptor<ConfidentialTimelineElementDto> confDtoCaptor = ArgumentCaptor.forClass(ConfidentialTimelineElementDto.class);
        Mockito.verify(pnDataVaultClient).updateNotificationTimelineByIunAndTimelineElementId(Mockito.eq(iun), confDtoCaptor.capture());

        ConfidentialTimelineElementDto capturedDto = confDtoCaptor.getValue();
        StepVerifier.create(Mono.just(capturedDto))
            .assertNext(dto -> {
                Assertions.assertNotNull(dto.getPhysicalAddress());
                Assertions.assertEquals(((SendAnalogDetailsInt) element.getDetails()).getPhysicalAddress().getAddress(), dto.getPhysicalAddress().getAddress());
            })
            .verifyComplete();
    }

    @Test
    void saveTimelineConfidentialInformationError() {
        String iun = "testIun";
        String elementId = "testElementId";

        //GIVEN
        TimelineElementInternal element = getSendPaperDetailsTimelineElement(iun, elementId);
        
        Mockito.doThrow(PnHttpResponseException.class).when(pnDataVaultClient).updateNotificationTimelineByIunAndTimelineElementId(Mockito.anyString(), Mockito.any(ConfidentialTimelineElementDto.class));
        

        //WHEN
        assertThrows(PnHttpResponseException.class, () -> confidentialInformationService.saveTimelineConfidentialInformation(element));
    }


    @Test
        void getTimelineElementConfidentialInformation() {
            //GIVEN
            String iun = "testIun";
            String elementId = "testElementId";

            ConfidentialTimelineElementDto elementDto = ConfidentialTimelineElementDto.builder()
                    .digitalAddress(
                            AddressDto.builder()
                                    .value("indirizzo@test.com")
                                    .build()
                    )
                    .build();

            Mockito.when(pnDataVaultClient.getNotificationTimelineByIunAndTimelineElementId(Mockito.anyString(), Mockito.anyString()))
                    .thenReturn(Mono.just(elementDto));

            //WHEN
            Mono<ConfidentialTimelineElementDtoInt> monoConf = confidentialInformationService.getTimelineElementConfidentialInformation(iun, elementId);

            //THEN
            StepVerifier.create(monoConf)
                    .assertNext(conf -> {
                        Assertions.assertNotNull(conf);
                        Assertions.assertEquals(conf.getDigitalAddress(), elementDto.getDigitalAddress().getValue());
                    })
                    .verifyComplete();
        }
    
    @Test
    void getTimelineElementConfidentialInformationKo() {
        //GIVEN
        String iun = "testIun";
        String elementId = "testElementId";

        Mockito.when(pnDataVaultClient.getNotificationTimelineByIunAndTimelineElementId(Mockito.anyString(), Mockito.anyString()))
                .thenThrow(PnHttpResponseException.class);
        
        //WHEN
        assertThrows(PnHttpResponseException.class, () -> confidentialInformationService.getTimelineElementConfidentialInformation(iun, elementId));
    }
    
    @Test
    void getTimelineConfidentialInformation() {
        // GIVEN
        String iun = "testIun";
        String elementId1 = "elementId1";
        String elementId2 = "elementId2";

        ConfidentialTimelineElementDto elementDto1 = ConfidentialTimelineElementDto.builder()
                .timelineElementId(elementId1)
                .digitalAddress(
                        AddressDto.builder()
                                .value("indirizzo@test.com")
                                .build()
                )
                .build();
        ConfidentialTimelineElementDto elementDto2 = ConfidentialTimelineElementDto.builder()
                .timelineElementId(elementId2)
                .newPhysicalAddress(AnalogDomicile.builder()
                        .cap("80010")
                        .province("NA")
                        .addressDetails("Scala 41")
                        .state("IT")
                        .municipality("MO")
                        .address("Via Vecchia")
                        .build())
                .build();
        List<ConfidentialTimelineElementDto> list = new ArrayList<>();
        list.add(elementDto1);
        list.add(elementDto2);

        // WHEN
        Mockito.when(pnDataVaultClient.getNotificationTimelineByIun(iun))
                .thenReturn(Flux.fromIterable(list));

        Mono<Map<String, ConfidentialTimelineElementDtoInt>> mapOtpMono = confidentialInformationService.getTimelineConfidentialInformation(iun);

        // THEN
        StepVerifier.create(mapOtpMono)
                .assertNext(mapOtp -> {
                    Assertions.assertNotNull(mapOtp.get(elementId1));
                    Assertions.assertEquals(mapOtp.get(elementId1).getDigitalAddress(), elementDto1.getDigitalAddress().getValue());

                    Assertions.assertNotNull(mapOtp.get(elementId2));
                    Assertions.assertEquals(mapOtp.get(elementId2).getNewPhysicalAddress().getAddress(), elementDto2.getNewPhysicalAddress().getAddress());
                })
                .verifyComplete();
    }
    
    @Test
    void getTimelineConfidentialInformationKo() {
        // GIVEN
        String iun = "testIun";

        Mockito.when(pnDataVaultClient.getNotificationTimelineByIun(Mockito.anyString()))
                .thenReturn(Flux.error(new PnHttpResponseException("Error", 0)));

        // WHEN & THEN
        StepVerifier.create(confidentialInformationService.getTimelineConfidentialInformation(iun))
                .expectError(PnHttpResponseException.class)
                .verify();
    }

    @Test
    void getTimelineConfidentialInformationEmpty() {
        // GIVEN
        String iun = "testIun";

        Mockito.when(pnDataVaultClient.getNotificationTimelineByIun(Mockito.anyString()))
                .thenReturn(Flux.empty());

        // WHEN & THEN
        StepVerifier.create(confidentialInformationService.getTimelineConfidentialInformation(iun))
                .expectNextCount(0)
                .expectComplete()
                .verify();
    }
    
    private TimelineElementInternal getSendPaperDetailsTimelineElement(String iun, String elementId) {
         SendAnalogDetailsInt details =  SendAnalogDetailsInt.builder()
                .physicalAddress(
                        PhysicalAddressInt.builder()
                                .province("province")
                                .municipality("munic")
                                .at("at")
                                .build()
                )
                .relatedRequestId("abc")
                .analogCost(100)
                .recIndex(0)
                .sentAttemptMade(0)
                .build();
         
        return TimelineElementInternal.builder()
                .elementId(elementId)
                .iun(iun)
                .details( details )
                .build();
    }
}