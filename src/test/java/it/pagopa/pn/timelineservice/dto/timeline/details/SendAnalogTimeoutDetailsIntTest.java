package it.pagopa.pn.timelineservice.dto.timeline.details;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class SendAnalogTimeoutDetailsIntTest {

    @Test
    void testGetAndSetTimeoutDate() {
        SendAnalogTimeoutDetailsInt details = new SendAnalogTimeoutDetailsInt();
        Instant now = Instant.now();
        details.setTimeoutDate(now);
        assertEquals(now, details.getTimeoutDate());
    }

    @Test
    void testToLog() {
        Instant timeout = Instant.parse("2024-06-01T12:00:00Z");
        SendAnalogTimeoutDetailsInt details = SendAnalogTimeoutDetailsInt.builder()
                .recIndex(1)
                .sentAttemptMade(2)
                .relatedRequestId("REQ123")
                .timeoutDate(timeout)
                .build();

        String log = details.toLog();
        assertTrue(log.contains("recIndex=1"));
        assertTrue(log.contains("sentAttemptMade=2"));
        assertTrue(log.contains("relatedRequestId=REQ123"));
        assertTrue(log.contains("timeoutDate=2024-06-01T12:00:00Z"));
    }

    @Test
    void testGetElementTimestamp() {
        Instant timeout = Instant.now();
        SendAnalogTimeoutDetailsInt details = new SendAnalogTimeoutDetailsInt();
        details.setTimeoutDate(timeout);
        assertEquals(timeout, details.getElementTimestamp());
    }

    @Test
    void testBuilderAndToString() {
        Instant timeout = Instant.now();
        SendAnalogTimeoutDetailsInt details = SendAnalogTimeoutDetailsInt.builder()
                .timeoutDate(timeout)
                .build();
        assertNotNull(details.toString());
    }

    @Test
    void testEqualsAndHashCode() {
        Instant timeout = Instant.now();
        SendAnalogTimeoutDetailsInt details1 = SendAnalogTimeoutDetailsInt.builder()
                .timeoutDate(timeout)
                .build();
        SendAnalogTimeoutDetailsInt details2 = SendAnalogTimeoutDetailsInt.builder()
                .timeoutDate(timeout)
                .build();
        assertEquals(details1, details2);
        assertEquals(details1.hashCode(), details2.hashCode());
    }
}
