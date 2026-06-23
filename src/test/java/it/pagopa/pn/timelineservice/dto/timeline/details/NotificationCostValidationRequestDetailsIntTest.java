package it.pagopa.pn.timelineservice.dto.timeline.details;

import it.pagopa.pn.timelineservice.dto.timeline.details.legal.NotificationCostValidationRequestDetailsInt;
import it.pagopa.pn.timelineservice.dto.timeline.details.legal.TimelineElementCategoryInt;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class NotificationCostValidationRequestDetailsIntTest {

    @Test
    void testNoArgsConstructorAndSetters() {
        NotificationCostValidationRequestDetailsInt details = new NotificationCostValidationRequestDetailsInt();
        details.setCategoryType(TimelineElementCategoryInt.NOTIFICATION_COST_VALIDATION_REQUEST.name());

        assertEquals(TimelineElementCategoryInt.NOTIFICATION_COST_VALIDATION_REQUEST.name(), details.getCategoryType());
    }

    @Test
    void testBuilderAndToBuilder() {
        NotificationCostValidationRequestDetailsInt details = NotificationCostValidationRequestDetailsInt.builder()
                .categoryType(TimelineElementCategoryInt.NOTIFICATION_COST_VALIDATION_REQUEST.name())
                .build();

        assertEquals(TimelineElementCategoryInt.NOTIFICATION_COST_VALIDATION_REQUEST.name(), details.getCategoryType());

        NotificationCostValidationRequestDetailsInt clonedDetails = details.toBuilder()
                .categoryType(TimelineElementCategoryInt.NOTIFICATION_COST_VALIDATION_REQUEST.name())
                .build();

        assertEquals(details.getCategoryType(), clonedDetails.getCategoryType());
    }

    @Test
    void testToLog() {
        NotificationCostValidationRequestDetailsInt details = NotificationCostValidationRequestDetailsInt.builder()
                .categoryType(TimelineElementCategoryInt.NOTIFICATION_COST_VALIDATION_REQUEST.name())
                .build();

        assertEquals(
                "categoryType=NOTIFICATION_COST_VALIDATION_REQUEST",
                details.toLog()
        );
    }
}
