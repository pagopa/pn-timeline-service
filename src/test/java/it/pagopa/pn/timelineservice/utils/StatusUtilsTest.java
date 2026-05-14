package it.pagopa.pn.timelineservice.utils;

import it.pagopa.pn.timelineservice.dto.notification.status.NotificationStatusHistoryElementInt;
import it.pagopa.pn.timelineservice.dto.notification.status.NotificationStatusInt;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class StatusUtilsTest {

    @Test
    void emptyTimelineInitialStateTest() {
        //
        Assertions.assertEquals(NotificationStatusInt.IN_VALIDATION, StatusUtils.getCurrentStatus(Collections.emptyList()));
    }

    @Test
    void getCurrentStatusTest() {
        List<NotificationStatusHistoryElementInt> statusHistory = new ArrayList<>();
        NotificationStatusHistoryElementInt statusHistoryDelivering = NotificationStatusHistoryElementInt.builder()
                .activeFrom(Instant.now())
                .status(NotificationStatusInt.DELIVERING)
                .build();
        NotificationStatusHistoryElementInt statusHistoryAccepted = NotificationStatusHistoryElementInt.builder()
                .activeFrom(Instant.now())
                .status(NotificationStatusInt.ACCEPTED)
                .build();
        statusHistory.add(statusHistoryDelivering);
        statusHistory.add(statusHistoryAccepted);

        Assertions.assertEquals(NotificationStatusInt.ACCEPTED, StatusUtils.getCurrentStatus(statusHistory));
    }
}