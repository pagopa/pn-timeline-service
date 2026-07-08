package it.pagopa.pn.timelineservice.operations.informal;

import it.pagopa.pn.timelineservice.dto.address.LegalDigitalAddressInt;
import it.pagopa.pn.timelineservice.dto.timeline.TimelineElementInternal;
import it.pagopa.pn.timelineservice.dto.timeline.details.common.NotificationRequestAcceptedDetailsInt;
import it.pagopa.pn.timelineservice.dto.timeline.details.legal.SendDigitalFeedbackDetailsInt;
import it.pagopa.pn.timelineservice.dto.timeline.details.TimelineElementCategoryInt;
import it.pagopa.pn.timelineservice.operations.common.TimelineTimestampBaseMapper;
import it.pagopa.pn.timelineservice.operations.common.TimelineTimestampMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class InformalTimelineTimestampMapperTest {
    // Testo l'integrazione reale senza mock.
    private final TimelineTimestampBaseMapper timelineTimestampBaseMapper = new TimelineTimestampBaseMapper();
    private final InformalTimelineTimestampMapper informalTimelineTimestampMapper = new InformalTimelineTimestampMapper(timelineTimestampBaseMapper);

     /*
        Test che verifica che se viene passato un payload con timelineElementInternal null, allora il risultato è null e non viene lanciata nessuna eccezione
     */
    @Test
    void testMapTimelineTimestampsWithNullTimelineElementInternal() {
        InformalTimelineTimestampMapper.TimestampMapperPayload payload = new InformalTimelineTimestampMapper.TimestampMapperPayload(null, null);
        assertDoesNotThrow(() -> {
            TimelineElementInternal result = informalTimelineTimestampMapper.mapTimelineTimestamps(payload);
            assertNull(result);
        });
    }

    @Test
    void mapTimelineTimestampsForElementWithBusinessTimestampInDetails(){
        Instant sourceEventTimestamp = Instant.EPOCH;
        Instant sourceIngestionTimestamp = Instant.now();

        TimelineElementInternal sendDigitalFeedback = TimelineElementInternal.builder()
                .category(TimelineElementCategoryInt.SEND_DIGITAL_FEEDBACK)
                .details(SendDigitalFeedbackDetailsInt.builder()
                        .recIndex(0)
                        .digitalAddress(LegalDigitalAddressInt.builder().type(LegalDigitalAddressInt.LEGAL_DIGITAL_ADDRESS_TYPE.PEC).build())
                        .notificationDate(sourceEventTimestamp)
                        .build())
                .timestamp(sourceIngestionTimestamp)
                .notificationSentAt(Instant.now().plusSeconds(3600))
                .build();

        TimelineTimestampMapper.TimestampMapperPayload payload = new TimelineTimestampMapper.TimestampMapperPayload(sendDigitalFeedback, null);
        TimelineElementInternal ret = informalTimelineTimestampMapper.mapTimelineTimestamps(payload);

        Assertions.assertNotSame(ret, sendDigitalFeedback);
        Assertions.assertEquals(sourceIngestionTimestamp, ret.getIngestionTimestamp());
        Assertions.assertEquals(sourceEventTimestamp, ret.getTimestamp());
        Assertions.assertEquals(sourceEventTimestamp, ret.getEventTimestamp());
    }

    @Test
    void mapTimelineTimestampsForElementWithoutBusinessTimestampInDetails(){
        Instant sourceIngestionTimestamp = Instant.now();

        TimelineElementInternal requestAccepted = TimelineElementInternal.builder()
                .category(TimelineElementCategoryInt.REQUEST_ACCEPTED)
                .details(NotificationRequestAcceptedDetailsInt.builder()
                        .idempotenceToken("token")
                        .paProtocolNumber("protocol")
                        .notificationRequestId("requestId")
                        .build())
                .timestamp(sourceIngestionTimestamp)
                .notificationSentAt(Instant.now().plusSeconds(3600))
                .build();

        TimelineTimestampMapper.TimestampMapperPayload payload = new TimelineTimestampMapper.TimestampMapperPayload(requestAccepted, null);
        TimelineElementInternal ret = informalTimelineTimestampMapper.mapTimelineTimestamps(payload);

        Assertions.assertNotSame(ret, requestAccepted);
        Assertions.assertEquals(sourceIngestionTimestamp, ret.getTimestamp());
        Assertions.assertEquals(sourceIngestionTimestamp, ret.getIngestionTimestamp());
        Assertions.assertEquals(sourceIngestionTimestamp, ret.getEventTimestamp());
    }

}