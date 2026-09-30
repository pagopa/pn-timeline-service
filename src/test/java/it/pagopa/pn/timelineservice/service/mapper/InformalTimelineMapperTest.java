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
    void remapSpecificTimelineElementDataUsesLaterSercqSendMessageTimestampForFeedback() {
        Instant ingestionTimestamp = Instant.parse("2024-01-01T10:00:00Z");
        Instant sendMessageTimestamp = ingestionTimestamp.plusSeconds(60);
        TimelineElementInternal feedback = feedback(0, InformalDigitalAddressInt.INFORMAL_DIGITAL_ADDRESS_TYPE.SERCQ,
                ingestionTimestamp.minusSeconds(30));
        TimelineElementInternal matchingSendMessage = sendMessage(0,
                InformalDigitalAddressInt.INFORMAL_DIGITAL_ADDRESS_TYPE.SERCQ, sendMessageTimestamp);
        TimelineElementInternal differentRecipientSendMessage = sendMessage(1,
                InformalDigitalAddressInt.INFORMAL_DIGITAL_ADDRESS_TYPE.SERCQ, sendMessageTimestamp.plusSeconds(60));
        TimelineElementInternal differentAddressSendMessage = sendMessage(0,
                InformalDigitalAddressInt.INFORMAL_DIGITAL_ADDRESS_TYPE.PEC, sendMessageTimestamp.plusSeconds(120));

        mapper.remapSpecificTimelineElementData(
                Set.of(matchingSendMessage, differentRecipientSendMessage, differentAddressSendMessage),
                feedback,
                ingestionTimestamp);

        assertEquals(ingestionTimestamp, feedback.getIngestionTimestamp());
        assertEquals(sendMessageTimestamp, feedback.getTimestamp());
        assertEquals(sendMessageTimestamp, feedback.getEventTimestamp());
    }

    @Test
    void remapSpecificTimelineElementDataUsesIngestionTimestampWhenNoLaterSercqTimestampExists() {
        Instant ingestionTimestamp = Instant.parse("2024-01-01T10:00:00Z");
        TimelineElementInternal feedback = feedback(0, InformalDigitalAddressInt.INFORMAL_DIGITAL_ADDRESS_TYPE.SERCQ,
                ingestionTimestamp.minusSeconds(30));
        TimelineElementInternal earlierSendMessage = sendMessage(0,
                InformalDigitalAddressInt.INFORMAL_DIGITAL_ADDRESS_TYPE.SERCQ, ingestionTimestamp.minusSeconds(60));

        mapper.remapSpecificTimelineElementData(Set.of(earlierSendMessage), feedback, ingestionTimestamp);

        assertEquals(ingestionTimestamp, feedback.getIngestionTimestamp());
        assertEquals(ingestionTimestamp, feedback.getTimestamp());
        assertEquals(ingestionTimestamp, feedback.getEventTimestamp());
    }

    @Test
    void remapSpecificTimelineElementDataDoesNotRemapNonSercqFeedback() {
        Instant timestamp = Instant.parse("2024-01-01T10:00:00Z");
        Instant ingestionTimestamp = timestamp.plusSeconds(30);
        TimelineElementInternal feedback = feedback(0, InformalDigitalAddressInt.INFORMAL_DIGITAL_ADDRESS_TYPE.EMAIL, timestamp);
        TimelineElementInternal sendMessage = sendMessage(0,
                InformalDigitalAddressInt.INFORMAL_DIGITAL_ADDRESS_TYPE.SERCQ, timestamp.plusSeconds(60));

        mapper.remapSpecificTimelineElementData(Set.of(sendMessage), feedback, ingestionTimestamp);

        assertEquals(ingestionTimestamp, feedback.getIngestionTimestamp());
        assertEquals(timestamp, feedback.getTimestamp());
        assertEquals(timestamp, feedback.getEventTimestamp());
    }

    private static TimelineElementInternal feedback(
            int recipientIndex,
            InformalDigitalAddressInt.INFORMAL_DIGITAL_ADDRESS_TYPE addressType,
            Instant timestamp) {
        return TimelineElementInternal.builder()
                .category(TimelineElementCategoryInt.SEND_DIGITAL_MESSAGE_FEEDBACK)
                .details(SendDigitalMessageFeedbackDetailsInt.builder()
                        .recIndex(recipientIndex)
                        .digitalAddress(InformalDigitalAddressInt.builder().type(addressType).build())
                        .build())
                .timestamp(timestamp)
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
