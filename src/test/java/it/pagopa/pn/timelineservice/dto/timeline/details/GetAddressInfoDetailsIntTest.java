package it.pagopa.pn.timelineservice.dto.timeline.details;
import it.pagopa.pn.timelineservice.dto.address.DigitalAddressSourceInt;
import it.pagopa.pn.timelineservice.dto.address.LegalDigitalAddressInt;
import it.pagopa.pn.timelineservice.dto.informalnotification.DigitalChannelsInt;
import it.pagopa.pn.timelineservice.dto.timeline.details.legal.GetAddressInfoDetailsInt;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
class GetAddressInfoDetailsIntTest {
    private GetAddressInfoDetailsInt detailsInt;
    private final Instant instant = Instant.parse("2021-09-16T15:24:00.00Z");
    @BeforeEach
    void setUp() {
        detailsInt = new GetAddressInfoDetailsInt();
        detailsInt.setAttemptDate(instant);
        detailsInt.setDigitalAddressSource(DigitalAddressSourceInt.GENERAL);
        detailsInt.setIsAvailable(Boolean.TRUE);
        detailsInt.setDigitalAddress(new LegalDigitalAddressInt().toBuilder().type(LegalDigitalAddressInt.LEGAL_DIGITAL_ADDRESS_TYPE.PEC).build());
        detailsInt.setRecIndex(1);
        detailsInt.setIsTosAccepted(Boolean.TRUE);
        detailsInt.setChannel(DigitalChannelsInt.PEC);
    }
    @Test
    void toLog() {
        String expected = "recIndex=1 digitalAddressSource=GENERAL isAvailable=true isTosAccepted=true channel=PEC";
        Assertions.assertEquals(expected, detailsInt.toLog());
    }
    @Test
    void testEquals() {
        GetAddressInfoDetailsInt expected = buildGetAddressInfoDetailsInt();
        Assertions.assertEquals(expected.getAttemptDate(), detailsInt.getAttemptDate());
        Assertions.assertEquals(expected.getDigitalAddressSource(), detailsInt.getDigitalAddressSource());
        Assertions.assertEquals(expected.getIsAvailable(), detailsInt.getIsAvailable());
        Assertions.assertEquals(expected.getRecIndex(), detailsInt.getRecIndex());
        Assertions.assertEquals(expected.getIsTosAccepted(), detailsInt.getIsTosAccepted());
        Assertions.assertEquals(expected.getChannel(), detailsInt.getChannel());
    }
    @Test
    void getRecIndex() {
        Assertions.assertEquals(1, detailsInt.getRecIndex());
    }
    @Test
    void getDigitalAddressSource() {
        Assertions.assertEquals(DigitalAddressSourceInt.GENERAL, detailsInt.getDigitalAddressSource());
    }
    @Test
    void getIsAvailable() {
        Assertions.assertEquals(Boolean.TRUE, detailsInt.getIsAvailable());
    }
    @Test
    void getAttemptDate() {
        Assertions.assertEquals(instant, detailsInt.getAttemptDate());
    }
    @Test
    void testToString() {
        String expected = "GetAddressInfoDetailsInt(recIndex=1, digitalAddressSource=GENERAL, isAvailable=true, attemptDate=2021-09-16T15:24:00Z, digitalAddress=LegalDigitalAddressInt(type=PEC), isTosAccepted=true, channel=PEC)";
        Assertions.assertEquals(expected, detailsInt.toString());
    }
    private GetAddressInfoDetailsInt buildGetAddressInfoDetailsInt() {
        return GetAddressInfoDetailsInt.builder().recIndex(1).attemptDate(instant).digitalAddressSource(DigitalAddressSourceInt.GENERAL).isAvailable(Boolean.TRUE).isTosAccepted(Boolean.TRUE).channel(DigitalChannelsInt.PEC).digitalAddress(new LegalDigitalAddressInt().toBuilder().type(LegalDigitalAddressInt.LEGAL_DIGITAL_ADDRESS_TYPE.PEC).build()).build();
    }
}