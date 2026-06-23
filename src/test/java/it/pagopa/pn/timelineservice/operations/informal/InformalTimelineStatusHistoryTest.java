package it.pagopa.pn.timelineservice.operations.informal;

import it.pagopa.pn.timelineservice.dto.notification.status.NotificationStatusHistoryElementInt;
import it.pagopa.pn.timelineservice.dto.notification.status.NotificationStatusInt;
import it.pagopa.pn.timelineservice.dto.timeline.TimelineElementInternal;
import it.pagopa.pn.timelineservice.dto.timeline.details.legal.TimelineElementCategoryInt;
import it.pagopa.pn.timelineservice.operations.common.TimelineTimestampBaseMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Set;

class InformalTimelineStatusHistoryTest {

    private InformalTimelineStatusHistoryCalculator informalTimelineStatusHistoryStrategy;


    @BeforeEach
    void setup() {
        InformalTimelineTimestampMapper informalTimelineTimestampMapper = new InformalTimelineTimestampMapper(new TimelineTimestampBaseMapper());
        this.informalTimelineStatusHistoryStrategy = new InformalTimelineStatusHistoryCalculator(informalTimelineTimestampMapper);
    }

    @Test
    void getTimelineHistoryForRequestAcceptedTest() {

        // GIVEN a timeline
        TimelineElementInternal timelineElement1 = TimelineElementInternal.builder()
                .elementId("el1")
                .timestamp(Instant.parse("2021-09-16T15:24:00.00Z"))
                .category(TimelineElementCategoryInt.VALIDATE_NORMALIZE_ADDRESSES_REQUEST)
                .build();
        TimelineElementInternal timelineElement2 = TimelineElementInternal.builder()
                .elementId("el2")
                .timestamp(Instant.parse("2021-09-16T15:24:00.00Z"))
                .category(TimelineElementCategoryInt.NORMALIZED_ADDRESS)
                .build();
        TimelineElementInternal timelineElement3 = TimelineElementInternal.builder()
                .elementId("el3")
                .timestamp(Instant.parse("2021-09-16T15:24:00.00Z"))
                .category(TimelineElementCategoryInt.REQUEST_ACCEPTED)
                .build();


        Set<TimelineElementInternal> timelineElementList = Set.of(timelineElement1, timelineElement2, timelineElement3);

        // WHEN ask for status history
        Instant notificationCreatedAt = Instant.parse("2021-09-16T15:20:00.00Z");

        List<NotificationStatusHistoryElementInt> actualStatusHistory = informalTimelineStatusHistoryStrategy.getStatusHistory(
                timelineElementList, 1, notificationCreatedAt
        );

        printStatus(actualStatusHistory);

        // THEN status histories have same length
        Assertions.assertEquals(2, actualStatusHistory.size(), "Check length");

        //  ... 1st initial status
        Assertions.assertEquals(NotificationStatusHistoryElementInt.builder()
                        .status(NotificationStatusInt.IN_VALIDATION)
                        .activeFrom(notificationCreatedAt)
                        .relatedTimelineElements(List.of("el1", "el2"))
                        .build(),
                actualStatusHistory.get(0),
                "1st status wrong"
        );

        //  ... 2nd initial status
        Assertions.assertEquals(NotificationStatusHistoryElementInt.builder()
                        .status(NotificationStatusInt.ACCEPTED)
                        .activeFrom(timelineElement1.getTimestamp())
                        .relatedTimelineElements(List.of("el3"))
                        .build(),
                actualStatusHistory.get(1),
                "2nd status wrong"
        );
    }

    @Test
    void getTimelineHistoryForSendDigitalAfterRequestAcceptedTest() {

        // GIVEN a timeline
        TimelineElementInternal timelineElement1 = TimelineElementInternal.builder()
                .elementId("el1")
                .timestamp(Instant.parse("2021-09-16T15:24:00.00Z"))
                .category(TimelineElementCategoryInt.VALIDATE_NORMALIZE_ADDRESSES_REQUEST)
                .build();
        TimelineElementInternal timelineElement2 = TimelineElementInternal.builder()
                .elementId("el2")
                .timestamp(Instant.parse("2021-09-16T15:24:00.00Z"))
                .category(TimelineElementCategoryInt.NORMALIZED_ADDRESS)
                .build();
        TimelineElementInternal timelineElement3 = TimelineElementInternal.builder()
                .elementId("el3")
                .timestamp(Instant.parse("2021-09-16T15:24:00.00Z"))
                .category(TimelineElementCategoryInt.REQUEST_ACCEPTED)
                .build();
        TimelineElementInternal timelineElement4 = TimelineElementInternal.builder()
                .elementId("el4")
                .timestamp(Instant.parse("2021-09-16T15:25:00.00Z"))
                .category(TimelineElementCategoryInt.SEND_DIGITAL_MESSAGE)
                .build();

        Set<TimelineElementInternal> timelineElementList = Set.of(timelineElement1, timelineElement2, timelineElement3, timelineElement4);

        // WHEN ask for status history
        Instant notificationCreatedAt = Instant.parse("2021-09-16T15:20:00.00Z");

        List<NotificationStatusHistoryElementInt> actualStatusHistory = informalTimelineStatusHistoryStrategy.getStatusHistory(
                timelineElementList, 1, notificationCreatedAt
        );

        printStatus(actualStatusHistory);

        // THEN status histories have same length
        Assertions.assertEquals(3, actualStatusHistory.size(), "Check length");

        Assertions.assertEquals(NotificationStatusHistoryElementInt.builder()
                        .status(NotificationStatusInt.IN_VALIDATION)
                        .activeFrom(notificationCreatedAt)
                        .relatedTimelineElements(List.of("el1", "el2"))
                        .build(),
                actualStatusHistory.get(0),
                "1st status wrong"
        );

        Assertions.assertEquals(NotificationStatusHistoryElementInt.builder()
                        .status(NotificationStatusInt.ACCEPTED)
                        .activeFrom(timelineElement1.getTimestamp())
                        .relatedTimelineElements(List.of("el3"))
                        .build(),
                actualStatusHistory.get(1),
                "2nd status wrong"
        );

        Assertions.assertEquals(NotificationStatusHistoryElementInt.builder()
                        .status(NotificationStatusInt.PROCESSING)
                        .activeFrom(timelineElement4.getTimestamp())
                        .relatedTimelineElements(List.of("el4"))
                        .build(),
                actualStatusHistory.get(2),
                "3rd status wrong"
        );
    }

    @Test
    void getTimelineHistoryForEmptyTimeline() {
        // GIVEN an empty timeline
        Set<TimelineElementInternal> timelineElementList = Collections.emptySet();

        // WHEN ask for status history
        Instant notificationCreatedAt = Instant.parse("2021-09-16T15:20:00.00Z");

        List<NotificationStatusHistoryElementInt> actualStatusHistory = informalTimelineStatusHistoryStrategy.getStatusHistory(
                timelineElementList, 1, notificationCreatedAt
        );

        printStatus(actualStatusHistory);

        // THEN status histories have same length
        Assertions.assertEquals(1, actualStatusHistory.size(), "Check length");

        //  ... 1st initial status
        Assertions.assertEquals(NotificationStatusHistoryElementInt.builder()
                        .status(NotificationStatusInt.IN_VALIDATION)
                        .activeFrom(notificationCreatedAt)
                        .relatedTimelineElements(Collections.emptyList())
                        .build(),
                actualStatusHistory.getFirst(),
                "1st status wrong"
        );
    }

    private void printStatus(List<NotificationStatusHistoryElementInt> notificationHistoryElements) {
        String methodName = Thread.currentThread().getStackTrace()[2].getMethodName();

        System.out.print(methodName + " - ");
        notificationHistoryElements.stream()
                .map(NotificationStatusHistoryElementInt::getStatus)
                .forEach(notificationStatusInt -> System.out.print(notificationStatusInt + " "));
        System.out.println();
    }

}