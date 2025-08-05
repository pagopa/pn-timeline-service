package it.pagopa.pn.timelineservice.dto.timeline.details;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

public class SendAnalogTimeoutCreationRequestDetailsIntTest {
    @Test
    void testNoArgsConstructorAndSetters() {
        SendAnalogTimeoutCreationRequestDetailsInt details = new SendAnalogTimeoutCreationRequestDetailsInt();
        Instant now = Instant.now();
        details.setTimeoutDate(now);
        details.setRecIndex(1);
        details.setSentAttemptMade(2);
        details.setRelatedRequestId("req123");
        details.setLegalFactId("fact456");

        assertEquals(now, details.getTimeoutDate());
        assertEquals(1, details.getRecIndex());
        assertEquals(2, details.getSentAttemptMade());
        assertEquals("req123", details.getRelatedRequestId());
        assertEquals("fact456", details.getLegalFactId());
    }

    @Test
    void testAllArgsConstructor() {
        Instant now = Instant.now();
        SendAnalogTimeoutCreationRequestDetailsInt details = new SendAnalogTimeoutCreationRequestDetailsInt(
                now, 1, 2, "req123", "fact456"
        );

        assertEquals(now, details.getTimeoutDate());
        assertEquals(1, details.getRecIndex());
        assertEquals(2, details.getSentAttemptMade());
        assertEquals("req123", details.getRelatedRequestId());
        assertEquals("fact456", details.getLegalFactId());
    }

    @Test
    void testToLog() {
        Instant now = Instant.parse("2024-06-01T12:00:00Z");
        SendAnalogTimeoutCreationRequestDetailsInt details = SendAnalogTimeoutCreationRequestDetailsInt.builder()
                .timeoutDate(now)
                .recIndex(1)
                .sentAttemptMade(2)
                .relatedRequestId("req123")
                .legalFactId("fact456")
                .build();

        String expected = "recIndex=1 sentAttemptMade=2 relatedRequestId=req123 timeoutDate=2024-06-01T12:00:00Z legalFactId=fact456";
        assertEquals(expected, details.toLog());
    }

    @Test
    void testGetElementTimestamp() {
        Instant now = Instant.now();
        SendAnalogTimeoutCreationRequestDetailsInt details = SendAnalogTimeoutCreationRequestDetailsInt.builder()
                .timeoutDate(now)
                .build();

        assertEquals(now, details.getElementTimestamp());
    }

    @Test
    void testToString() {
        SendAnalogTimeoutCreationRequestDetailsInt details = SendAnalogTimeoutCreationRequestDetailsInt.builder()
                .timeoutDate(Instant.parse("2024-06-01T12:00:00Z"))
                .recIndex(1)
                .sentAttemptMade(2)
                .relatedRequestId("req123")
                .legalFactId("fact456")
                .build();

        String str = details.toString();
        assertTrue(str.contains("timeoutDate=2024-06-01T12:00:00Z"));
        assertTrue(str.contains("recIndex=1"));
        assertTrue(str.contains("sentAttemptMade=2"));
        assertTrue(str.contains("relatedRequestId=req123"));
        assertTrue(str.contains("legalFactId=fact456"));
    }
}
