package it.pagopa.pn.timelineservice.dto.timeline.details;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SendAnalogTimeoutCreationRequestDetailsIntTest {
    Instant timeoutDate = Instant.now();

    @Test
    void testGettersAndSetters() {
        SendAnalogTimeoutCreationRequestDetailsInt details = new SendAnalogTimeoutCreationRequestDetailsInt();
        details.setTimeoutDate(timeoutDate);
        assertEquals(timeoutDate, details.getTimeoutDate());
    }

    @Test
    void testAllArgsConstructor() {
        SendAnalogTimeoutCreationRequestDetailsInt details = SendAnalogTimeoutCreationRequestDetailsInt.builder()
                .timeoutDate(timeoutDate)
                .build();
        assertEquals(timeoutDate, details.getTimeoutDate());
    }

    @Test
    void testToLog() {
        SendAnalogTimeoutCreationRequestDetailsInt details = SendAnalogTimeoutCreationRequestDetailsInt.builder()
                .recIndex(1)
                .sentAttemptMade(2)
                .relatedRequestId("req123")
                .timeoutDate(timeoutDate)
                .build();
        String log = details.toLog();
        assertTrue(log.contains("recIndex=1"));
        assertTrue(log.contains("sentAttemptMade=2"));
        assertTrue(log.contains("relatedRequestId=req123"));
        assertTrue(log.contains("timeoutDate=" + timeoutDate.toString()));
    }

    @Test
    void testGetElementTimestamp() {
        SendAnalogTimeoutCreationRequestDetailsInt details = new SendAnalogTimeoutCreationRequestDetailsInt();
        details.setTimeoutDate(timeoutDate);
        assertEquals(timeoutDate, details.getElementTimestamp());
    }

}
