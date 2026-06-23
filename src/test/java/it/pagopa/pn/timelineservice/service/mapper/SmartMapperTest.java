package it.pagopa.pn.timelineservice.service.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.pagopa.pn.timelineservice.dto.address.DigitalAddressSourceInt;
import it.pagopa.pn.timelineservice.dto.address.LegalDigitalAddressInt;
import it.pagopa.pn.timelineservice.dto.address.PhysicalAddressInt;
import it.pagopa.pn.timelineservice.dto.timeline.details.legal.DownstreamIdInt;
import it.pagopa.pn.timelineservice.dto.timeline.details.legal.PrepareAnalogDomicileFailureDetailsInt;
import it.pagopa.pn.timelineservice.dto.timeline.details.legal.SendDigitalDetailsInt;
import it.pagopa.pn.timelineservice.generated.openapi.server.v1.dto.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SmartMapperTest {
    private SmartMapper smartMapper;

    @BeforeEach
    void setUp() {
        ObjectMapper objectMapper = new ObjectMapper();
        smartMapper = new SmartMapper(objectMapper);
    }

    @Test
    void fromInternalToExternalSendDigitalDetails() {
        SendDigitalDetailsInt sendDigitalDetails = SendDigitalDetailsInt.builder()
                .recIndex(0)
                .digitalAddressSource(DigitalAddressSourceInt.PLATFORM)
                .digitalAddress(LegalDigitalAddressInt.builder()
                        .type(LegalDigitalAddressInt.LEGAL_DIGITAL_ADDRESS_TYPE.PEC)
                        .address("testAddress@gmail.com")
                        .build())
                .retryNumber(0)
                .categoryType("SEND_DIGITAL_DOMICILE")
                .downstreamId(DownstreamIdInt.builder()
                        .messageId("messageId")
                        .systemId("systemId")
                        .build())
                .build();

        var details = smartMapper.mapToClassWithObjectMapper(sendDigitalDetails, TimelineElementDetails.class);
        var sendDigitalDetailsExt = (SendDigitalDetails) details;
        Assertions.assertEquals(sendDigitalDetails.getRecIndex(),  sendDigitalDetailsExt.getRecIndex());
        Assertions.assertEquals(sendDigitalDetails.getDigitalAddress().getAddress(), sendDigitalDetailsExt.getDigitalAddress().getAddress() );
    }

    @Test
    void fromExternalToInternalSendDigitalDetails() {
        var timelineElementDetails = new SendDigitalDetails()
                .recIndex(0)
                .digitalAddressSource(DigitalAddressSource.PLATFORM)
                .digitalAddress(new DigitalAddress()
                        .type("PEC")
                        .address("testAddress@gmail.com"))
                .retryNumber(0);

        SendDigitalDetailsInt details = SmartMapper.mapToClass(timelineElementDetails, SendDigitalDetailsInt.class);

        Assertions.assertEquals(timelineElementDetails.getRecIndex(), details.getRecIndex());
        Assertions.assertEquals(timelineElementDetails.getDigitalAddress().getAddress(), details.getDigitalAddress().getAddress() );
    }

    @Test
    void fromInternalToPrepareAnalogDomicileFailureDetails() {
        PrepareAnalogDomicileFailureDetailsInt sendDigitalDetails = PrepareAnalogDomicileFailureDetailsInt.builder()
                .recIndex(0)
                .foundAddress(PhysicalAddressInt.builder()
                        .foreignState("ITALIA")
                        .zip("30000")
                        .address("Via casa mia")
                        .province("MI")
                        .build())
                .build();

        var details = SmartMapper.mapToClass(sendDigitalDetails, PrepareAnalogDomicileFailureDetails.class);

        Assertions.assertEquals(sendDigitalDetails.getRecIndex(), details.getRecIndex());
        Assertions.assertEquals(sendDigitalDetails.getFoundAddress().getAddress(), details.getFoundAddress().getAddress() );
    }

}