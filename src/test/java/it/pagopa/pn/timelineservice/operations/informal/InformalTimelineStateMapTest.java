package it.pagopa.pn.timelineservice.operations.informal;

import it.pagopa.pn.timelineservice.dto.notification.status.NotificationStatusInt;
import it.pagopa.pn.timelineservice.dto.timeline.details.TimelineElementCategoryInt;
import it.pagopa.pn.timelineservice.dto.transition.TransitionRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class InformalTimelineStateMapTest {

    private final InformalTimelineStateMap stateMap = new InformalTimelineStateMap();

    @Test
    void requestAcceptedMovesFromInValidationToProcessing() {
        assertTransition(NotificationStatusInt.IN_VALIDATION, TimelineElementCategoryInt.REQUEST_ACCEPTED, NotificationStatusInt.ACCEPTED);
    }

    @Test
    void processingKeepsStateForReachedAndDeliveryProgressEvents() {
        assertTransition(NotificationStatusInt.PROCESSING, TimelineElementCategoryInt.SEND_DIGITAL_MESSAGE, NotificationStatusInt.PROCESSING);
        assertTransition(NotificationStatusInt.PROCESSING, TimelineElementCategoryInt.SEND_DIGITAL_MESSAGE_SKIP, NotificationStatusInt.PROCESSING);
        assertTransition(NotificationStatusInt.PROCESSING, TimelineElementCategoryInt.SEND_DIGITAL_MESSAGE_PROGRESS, NotificationStatusInt.PROCESSING);
        assertTransition(NotificationStatusInt.PROCESSING, TimelineElementCategoryInt.SEND_DIGITAL_MESSAGE_FEEDBACK, NotificationStatusInt.PROCESSING);
        assertTransition(NotificationStatusInt.PROCESSING, TimelineElementCategoryInt.PREPARE_ANALOG_DELIVERY, NotificationStatusInt.PROCESSING);
        assertTransition(NotificationStatusInt.PROCESSING, TimelineElementCategoryInt.SEND_ANALOG_MESSAGE, NotificationStatusInt.PROCESSING);
        assertTransition(NotificationStatusInt.PROCESSING, TimelineElementCategoryInt.SEND_ANALOG_MESSAGE_PROGRESS, NotificationStatusInt.PROCESSING);
        assertTransition(NotificationStatusInt.PROCESSING, TimelineElementCategoryInt.SEND_ANALOG_MESSAGE_FEEDBACK, NotificationStatusInt.PROCESSING);
        assertTransition(NotificationStatusInt.PROCESSING, TimelineElementCategoryInt.DELIVERED, NotificationStatusInt.PROCESSING);
        assertTransition(NotificationStatusInt.PROCESSING, TimelineElementCategoryInt.PAYMENT, NotificationStatusInt.PROCESSING);
        assertTransition(NotificationStatusInt.PROCESSING, TimelineElementCategoryInt.INFORMAL_NOTIFICATION_VIEWED, NotificationStatusInt.PROCESSING);
        assertTransition(NotificationStatusInt.PROCESSING, TimelineElementCategoryInt.COVERPAGE_CREATION_REQUEST, NotificationStatusInt.PROCESSING);
    }

    @Test
    void processingMovesToExpectedTerminalStates() {
        assertTransition(NotificationStatusInt.PROCESSING, TimelineElementCategoryInt.WORKFLOW_ENDED_REACHED, NotificationStatusInt.COMPLETED_REACHED);
        assertTransition(NotificationStatusInt.PROCESSING, TimelineElementCategoryInt.WORKFLOW_ENDED_UNREACHED, NotificationStatusInt.COMPLETED_UNREACHED);
        assertTransition(NotificationStatusInt.PROCESSING, TimelineElementCategoryInt.WORKFLOW_ENDED_UNDELIVERABLE, NotificationStatusInt.UNDELIVERABLE);
        assertTransition(NotificationStatusInt.PROCESSING, TimelineElementCategoryInt.WORKFLOW_DONE_REACHED, NotificationStatusInt.COMPLETED_REACHED);
        assertTransition(NotificationStatusInt.PROCESSING, TimelineElementCategoryInt.WORKFLOW_DONE_UNREACHED, NotificationStatusInt.COMPLETED_UNREACHED);
    }

    @Test
    void completedUnreachedMovesToCompletedReachedWhenWorkflowRecovers() {
        assertTransition(NotificationStatusInt.COMPLETED_UNREACHED, TimelineElementCategoryInt.SEND_DIGITAL_MESSAGE_PROGRESS, NotificationStatusInt.COMPLETED_UNREACHED);
        assertTransition(NotificationStatusInt.COMPLETED_UNREACHED, TimelineElementCategoryInt.SEND_DIGITAL_MESSAGE_FEEDBACK, NotificationStatusInt.COMPLETED_UNREACHED);
        assertTransition(NotificationStatusInt.COMPLETED_UNREACHED, TimelineElementCategoryInt.SEND_ANALOG_MESSAGE_PROGRESS, NotificationStatusInt.COMPLETED_UNREACHED);
        assertTransition(NotificationStatusInt.COMPLETED_UNREACHED, TimelineElementCategoryInt.SEND_ANALOG_MESSAGE_FEEDBACK, NotificationStatusInt.COMPLETED_UNREACHED);
        assertTransition(NotificationStatusInt.COMPLETED_UNREACHED, TimelineElementCategoryInt.PAYMENT, NotificationStatusInt.COMPLETED_UNREACHED);
        assertTransition(NotificationStatusInt.COMPLETED_UNREACHED, TimelineElementCategoryInt.INFORMAL_NOTIFICATION_VIEWED, NotificationStatusInt.COMPLETED_UNREACHED);
        assertTransition(NotificationStatusInt.COMPLETED_UNREACHED, TimelineElementCategoryInt.WORKFLOW_ENDED_REACHED, NotificationStatusInt.COMPLETED_REACHED);
        assertTransition(NotificationStatusInt.COMPLETED_UNREACHED, TimelineElementCategoryInt.WORKFLOW_DONE_REACHED, NotificationStatusInt.COMPLETED_REACHED);
    }

    @Test
    void undeliverableMovesToCompletedReachedWhenWorkflowDoneReachedArrives() {
        assertTransition(NotificationStatusInt.UNDELIVERABLE, TimelineElementCategoryInt.PAYMENT, NotificationStatusInt.UNDELIVERABLE);
        assertTransition(NotificationStatusInt.UNDELIVERABLE, TimelineElementCategoryInt.INFORMAL_NOTIFICATION_VIEWED, NotificationStatusInt.UNDELIVERABLE);

        assertTransition(NotificationStatusInt.UNDELIVERABLE, TimelineElementCategoryInt.WORKFLOW_DONE_REACHED, NotificationStatusInt.COMPLETED_REACHED);
        assertTransition(NotificationStatusInt.UNDELIVERABLE, TimelineElementCategoryInt.WORKFLOW_ENDED_REACHED, NotificationStatusInt.COMPLETED_REACHED);
    }

    @Test
    void completedReachedKeepsStateForAllowedEvents() {
        assertTransition(NotificationStatusInt.COMPLETED_REACHED, TimelineElementCategoryInt.SEND_DIGITAL_MESSAGE_PROGRESS, NotificationStatusInt.COMPLETED_REACHED);
        assertTransition(NotificationStatusInt.COMPLETED_REACHED, TimelineElementCategoryInt.SEND_DIGITAL_MESSAGE_FEEDBACK, NotificationStatusInt.COMPLETED_REACHED);
        assertTransition(NotificationStatusInt.COMPLETED_REACHED, TimelineElementCategoryInt.SEND_ANALOG_MESSAGE_PROGRESS, NotificationStatusInt.COMPLETED_REACHED);
        assertTransition(NotificationStatusInt.COMPLETED_REACHED, TimelineElementCategoryInt.SEND_ANALOG_MESSAGE_FEEDBACK, NotificationStatusInt.COMPLETED_REACHED);
        assertTransition(NotificationStatusInt.COMPLETED_REACHED, TimelineElementCategoryInt.PAYMENT, NotificationStatusInt.COMPLETED_REACHED);
        assertTransition(NotificationStatusInt.COMPLETED_REACHED, TimelineElementCategoryInt.INFORMAL_NOTIFICATION_VIEWED, NotificationStatusInt.COMPLETED_REACHED);
        assertTransition(NotificationStatusInt.COMPLETED_REACHED, TimelineElementCategoryInt.WORKFLOW_ENDED_REACHED, NotificationStatusInt.COMPLETED_REACHED);
    }

    @Test
    void acceptedKeepsStateForAllowedEventsAndMovesToUndeliverable() {
        assertTransition(NotificationStatusInt.ACCEPTED, TimelineElementCategoryInt.SEND_DIGITAL_MESSAGE_SKIP, NotificationStatusInt.ACCEPTED);
        assertTransition(NotificationStatusInt.ACCEPTED, TimelineElementCategoryInt.COVERPAGE_CREATION_REQUEST, NotificationStatusInt.ACCEPTED);
        assertTransition(NotificationStatusInt.ACCEPTED, TimelineElementCategoryInt.PREPARE_ANALOG_DELIVERY, NotificationStatusInt.ACCEPTED);
        assertTransition(NotificationStatusInt.ACCEPTED, TimelineElementCategoryInt.INFORMAL_NOTIFICATION_VIEWED, NotificationStatusInt.ACCEPTED);

        assertTransition(NotificationStatusInt.ACCEPTED, TimelineElementCategoryInt.WORKFLOW_ENDED_UNDELIVERABLE, NotificationStatusInt.UNDELIVERABLE);
    }

    private void assertTransition(NotificationStatusInt fromStatus, TimelineElementCategoryInt elementCategory, NotificationStatusInt expectedStatus) {
        NotificationStatusInt actualStatus = stateMap.getStateTransition(TransitionRequest.builder()
                .fromStatus(fromStatus)
                .timelineRowType(elementCategory)
                .multiRecipient(false)
                .build());

        Assertions.assertEquals(expectedStatus, actualStatus);
    }
}
