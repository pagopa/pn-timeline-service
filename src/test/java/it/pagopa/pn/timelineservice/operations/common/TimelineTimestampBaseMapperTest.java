package it.pagopa.pn.timelineservice.operations.common;

import it.pagopa.pn.timelineservice.dto.address.LegalDigitalAddressInt;
import it.pagopa.pn.timelineservice.dto.timeline.TimelineElementInternal;
import it.pagopa.pn.timelineservice.dto.timeline.details.common.NotificationRequestAcceptedDetailsInt;
import it.pagopa.pn.timelineservice.dto.timeline.details.legal.SendDigitalFeedbackDetailsInt;
import it.pagopa.pn.timelineservice.dto.timeline.details.legal.TimelineElementCategoryInt;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Instant;

class TimelineTimestampBaseMapperTest {
    private final TimelineTimestampBaseMapper timelineTimestampBaseMapper = new TimelineTimestampBaseMapper();

    @Test
    void testMapTimelineInternalWithNullInput() {
        TimelineElementInternal result = timelineTimestampBaseMapper.mapTimelineInternal(null);
        Assertions.assertNull(result);
    }

    @Test
    void testMapTimelineInternalForElementWithBusinessTimestampInDetails(){
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

        TimelineElementInternal ret = timelineTimestampBaseMapper.mapTimelineInternal(sendDigitalFeedback);

        Assertions.assertNotSame(ret, sendDigitalFeedback);
        Assertions.assertEquals(sourceEventTimestamp, ret.getTimestamp());
    }

    @Test
    void testMapTimelineInternalForElementWithoutBusinessTimestampInDetails(){
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

        TimelineElementInternal ret = timelineTimestampBaseMapper.mapTimelineInternal(requestAccepted);

        Assertions.assertNotSame(ret, requestAccepted);
        Assertions.assertEquals(sourceIngestionTimestamp, ret.getTimestamp());
    }

}