package it.pagopa.pn.timelineservice.service.mapper;

import it.pagopa.pn.timelineservice.dto.address.InformalDigitalAddressInt;
import it.pagopa.pn.timelineservice.dto.timeline.TimelineElementInternal;
import it.pagopa.pn.timelineservice.dto.timeline.details.TimelineElementCategoryInt;
import it.pagopa.pn.timelineservice.dto.timeline.details.informal.SendDigitalMessageDetailsInt;
import it.pagopa.pn.timelineservice.dto.timeline.details.informal.SendDigitalMessageFeedbackDetailsInt;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

class InformalTimelineMapperTest {

    private final InformalTimelineMapper mapper = new InformalTimelineMapper();

    @Test
    void remapSpecificTimelineElementDataDoesNothingWhenResultIsNull() {
        assertDoesNotThrow(() -> mapper.remapSpecificTimelineElementData(null, null, null));
    }

    @Test
    void remapSpecificTimelineElementDataSetsIngestionAndEventTimestamps() {
        Instant timestamp = Instant.parse("2024-01-01T10:00:00Z");
        Instant ingestionTimestamp = timestamp.plusSeconds(60);
        TimelineElementInternal result = TimelineElementInternal.builder()
                .category(TimelineElementCategoryInt.REQUEST_ACCEPTED)
                .timestamp(timestamp)
                .build();

        mapper.remapSpecificTimelineElementData(null, result, ingestionTimestamp);

        assertEquals(timestamp, result.getTimestamp());
        assertEquals(ingestionTimestamp, result.getIngestionTimestamp());
        assertEquals(timestamp, result.getEventTimestamp());
    }

    @Test
    void remapSpecificTimelineElementDataUsesMatchingSercqFeedbackTimestampForSendMessage() {
        Instant ingestionTimestamp = Instant.parse("2024-01-01T10:00:00Z");
        Instant feedbackTimestamp = Instant.parse("2024-01-01T09:05:00Z");
        TimelineElementInternal sendMessage = sendMessage(0,
                InformalDigitalAddressInt.INFORMAL_DIGITAL_ADDRESS_TYPE.SERCQ, ingestionTimestamp.minusSeconds(30));
        TimelineElementInternal matchingFeedback = feedback(0,
                InformalDigitalAddressInt.INFORMAL_DIGITAL_ADDRESS_TYPE.SERCQ, feedbackTimestamp);
        TimelineElementInternal differentRecipientFeedback = feedback(1,
                InformalDigitalAddressInt.INFORMAL_DIGITAL_ADDRESS_TYPE.SERCQ, feedbackTimestamp.minusSeconds(60));
        TimelineElementInternal differentAddressFeedback = feedback(0,
                InformalDigitalAddressInt.INFORMAL_DIGITAL_ADDRESS_TYPE.EMAIL, feedbackTimestamp.minusSeconds(120));

        mapper.remapSpecificTimelineElementData(
                Set.of(matchingFeedback, differentRecipientFeedback, differentAddressFeedback),
                sendMessage,
                ingestionTimestamp);

        assertEquals(ingestionTimestamp, sendMessage.getIngestionTimestamp());
        assertEquals(feedbackTimestamp, sendMessage.getTimestamp());
        assertEquals(feedbackTimestamp, sendMessage.getEventTimestamp());
    }

    @Test
    void remapSpecificTimelineElementDataUsesIngestionTimestampWhenNoEarlierMatchingFeedbackExists() {
        Instant ingestionTimestamp = Instant.parse("2024-01-01T10:00:00Z");
        TimelineElementInternal sendMessage = sendMessage(0,
                InformalDigitalAddressInt.INFORMAL_DIGITAL_ADDRESS_TYPE.SERCQ, ingestionTimestamp.minusSeconds(30));
        TimelineElementInternal laterFeedback = feedback(0,
                InformalDigitalAddressInt.INFORMAL_DIGITAL_ADDRESS_TYPE.SERCQ, ingestionTimestamp.plusSeconds(30));

        mapper.remapSpecificTimelineElementData(Set.of(laterFeedback), sendMessage, ingestionTimestamp);

        assertEquals(ingestionTimestamp, sendMessage.getIngestionTimestamp());
        assertEquals(ingestionTimestamp, sendMessage.getTimestamp());
        assertEquals(ingestionTimestamp, sendMessage.getEventTimestamp());
    }

    @Test
    void remapSpecificTimelineElementDataDoesNotRemapNonSercqSendMessage() {
        Instant timestamp = Instant.parse("2024-01-01T10:00:00Z");
        Instant ingestionTimestamp = timestamp.plusSeconds(30);
        TimelineElementInternal sendMessage = sendMessage(0,
                InformalDigitalAddressInt.INFORMAL_DIGITAL_ADDRESS_TYPE.EMAIL, timestamp);
        TimelineElementInternal feedback = feedback(0,
                InformalDigitalAddressInt.INFORMAL_DIGITAL_ADDRESS_TYPE.SERCQ, timestamp.minusSeconds(30));

        mapper.remapSpecificTimelineElementData(Set.of(feedback), sendMessage, ingestionTimestamp);

        assertEquals(ingestionTimestamp, sendMessage.getIngestionTimestamp());
        assertEquals(timestamp, sendMessage.getTimestamp());
        assertEquals(timestamp, sendMessage.getEventTimestamp());
    }

    private static TimelineElementInternal feedback(
            int recipientIndex,
            InformalDigitalAddressInt.INFORMAL_DIGITAL_ADDRESS_TYPE addressType,
            Instant elementTimestamp) {
        return TimelineElementInternal.builder()
                .category(TimelineElementCategoryInt.SEND_DIGITAL_MESSAGE_FEEDBACK)
                .details(SendDigitalMessageFeedbackDetailsInt.builder()
                        .recIndex(recipientIndex)
                        .notificationDate(elementTimestamp)
                        .digitalAddress(InformalDigitalAddressInt.builder().type(addressType).build())
                        .build())
                .timestamp(elementTimestamp)
                .build();
    }

    private static TimelineElementInternal sendMessage(
            int recipientIndex,
            InformalDigitalAddressInt.INFORMAL_DIGITAL_ADDRESS_TYPE addressType,
            Instant timestamp) {
        return TimelineElementInternal.builder()
                .category(TimelineElementCategoryInt.SEND_DIGITAL_MESSAGE)
                .details(SendDigitalMessageDetailsInt.builder()
                        .recIndex(recipientIndex)
                        .digitalAddress(InformalDigitalAddressInt.builder().type(addressType).build())
                        .build())
                .timestamp(timestamp)
                .build();
    }
}
