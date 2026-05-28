package it.pagopa.pn.timelineservice.utils;

import it.pagopa.pn.timelineservice.dto.notification.status.NotificationStatusHistoryElementInt;
import it.pagopa.pn.timelineservice.dto.notification.status.NotificationStatusInt;

import java.util.List;

public class StatusUtils {
    private StatusUtils() {
        // Utility class, no instantiation allowed
    }
    
    public static final NotificationStatusInt INITIAL_STATUS = NotificationStatusInt.IN_VALIDATION;

    public static NotificationStatusInt getCurrentStatus(List<NotificationStatusHistoryElementInt> statusHistory) {
        if (!statusHistory.isEmpty()) {
            return statusHistory.getLast().getStatus();
        } else {
            return INITIAL_STATUS;
        }
    }
}
