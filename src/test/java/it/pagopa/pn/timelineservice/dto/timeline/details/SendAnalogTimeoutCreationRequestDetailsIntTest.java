package it.pagopa.pn.timelineservice.dto.timeline.details;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SendAnalogTimeoutCreationRequestDetailsIntTest {
    Instant notificationdate = Instant.now();

    @Test
    void testGettersAndSetters() {
        SendAnalogTimeoutCreationRequestDetailsInt details = new SendAnalogTimeoutCreationRequestDetailsInt();
        details.setNotificationDate(notificationdate);
        assertEquals(notificationdate, details.getNotificationDate());
    }

    @Test
    void testAllArgsConstructor() {
        SendAnalogTimeoutCreationRequestDetailsInt details = SendAnalogTimeoutCreationRequestDetailsInt.builder()
                .notificationDate(notificationdate)
                .build();
        assertEquals(notificationdate, details.getNotificationDate());
    }

    @Test
    void testToLog() {
        SendAnalogTimeoutCreationRequestDetailsInt details = SendAnalogTimeoutCreationRequestDetailsInt.builder()
                .recIndex(1)
                .sentAttemptMade(2)
                .relatedRequestId("req123")
                .notificationDate(notificationdate)
                .build();
        String log = details.toLog();
        assertTrue(log.contains("recIndex=1"));
        assertTrue(log.contains("sentAttemptMade=2"));
        assertTrue(log.contains("relatedRequestId=req123"));
        assertTrue(log.contains("notificationDate=" + notificationdate.toString()));
    }

    @Test
    void testGetElementTimestamp() {
        SendAnalogTimeoutCreationRequestDetailsInt details = new SendAnalogTimeoutCreationRequestDetailsInt();
        details.setNotificationDate(notificationdate);
        assertEquals(notificationdate, details.getElementTimestamp());
    }

}
