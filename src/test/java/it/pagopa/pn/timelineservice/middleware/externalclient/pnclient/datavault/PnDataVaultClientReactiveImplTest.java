package it.pagopa.pn.timelineservice.middleware.externalclient.pnclient.datavault;


import it.pagopa.pn.timelineservice.generated.openapi.msclient.datavault.model.AddressDto;
import it.pagopa.pn.timelineservice.generated.openapi.msclient.datavault.model.ConfidentialTimelineElementDto;
import it.pagopa.pn.timelineservice.generated.openapi.msclient.datavault_reactive.api.NotificationsApi;
import it.pagopa.pn.timelineservice.middleware.externalclient.datavault.PnDataVaultClientReactive;
import it.pagopa.pn.timelineservice.middleware.externalclient.datavault.PnDataVaultClientReactiveImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
class PnDataVaultClientReactiveImplTest {

    private NotificationsApi pnDataVaultNotificationApi;

    private PnDataVaultClientReactive client;

    @BeforeEach
    void setup() {

        pnDataVaultNotificationApi = Mockito.mock( NotificationsApi.class );
        client = new PnDataVaultClientReactiveImpl(pnDataVaultNotificationApi);
    }

  @Test
  void updateNotificationTimelineByIunAndTimelineElementId() {
      ConfidentialTimelineElementDto dto = buildConfidentialTimelineElementDto();

      Mockito.doReturn(Mono.empty())
              .when(pnDataVaultNotificationApi)
              .updateNotificationTimelineByIunAndTimelineElementId(Mockito.anyString(), Mockito.anyString(), Mockito.any(ConfidentialTimelineElementDto.class));

      assertDoesNotThrow(() -> client.updateNotificationTimelineByIunAndTimelineElementId("01", dto));
  }

    @Test
    void getNotificationTimelineByIunAndTimelineElementId() {
        ConfidentialTimelineElementDto dto = buildConfidentialTimelineElementDto();

        Mockito.when(pnDataVaultNotificationApi.getNotificationTimelineByIunAndTimelineElementId("001", "001"))
                .thenReturn(Mono.just(dto));

        Mono<ConfidentialTimelineElementDto> result = client.getNotificationTimelineByIunAndTimelineElementId("001", "001");

        StepVerifier.create(result)
                .expectNext(dto)
                .verifyComplete();
    }

   @Test
   void getNotificationTimelineByIun() {
       ConfidentialTimelineElementDto dto = buildConfidentialTimelineElementDto();
       List<ConfidentialTimelineElementDto> dtoList = new ArrayList<>();
       dtoList.add(dto);

       Mockito.when(pnDataVaultNotificationApi.getNotificationTimelineByIun("001"))
               .thenReturn(Flux.fromIterable(dtoList));

       Flux<ConfidentialTimelineElementDto> result = client.getNotificationTimelineByIun("001");

       StepVerifier.create(result)
               .assertNext(resp -> Assertions.assertEquals(resp, dto))
               .verifyComplete();
   }

    private ConfidentialTimelineElementDto buildConfidentialTimelineElementDto() {
        return ConfidentialTimelineElementDto.builder()
                .timelineElementId("001")
                .digitalAddress(
                        AddressDto.builder()
                                .value("indirizzo@test.com")
                                .build()
                )
                .build();
    }
}