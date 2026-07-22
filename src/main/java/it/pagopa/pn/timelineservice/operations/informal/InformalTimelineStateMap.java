package it.pagopa.pn.timelineservice.operations.informal;

import it.pagopa.pn.timelineservice.dto.notification.status.NotificationStatusInt;
import it.pagopa.pn.timelineservice.dto.timeline.details.TimelineElementCategoryInt;
import it.pagopa.pn.timelineservice.operations.common.AbstractStateMap;

public class InformalTimelineStateMap extends AbstractStateMap {

    @Override
    protected void configureTransitions() {
        fromStatusInValidation();
        fromStatusAccepted();
        fromStatusProcessing();
        fromStatusCompletedUnreached();
        fromStatusUndeliverable();
        fromStatusCompletedReached();

        this.fromState(NotificationStatusInt.REFUSED);
    }

    private void fromStatusInValidation() {
        this.fromState(NotificationStatusInt.IN_VALIDATION)
                // STATE UNCHANGE
                .withTimelineGoToState(TimelineElementCategoryInt.VALIDATE_NORMALIZE_ADDRESSES_REQUEST, NotificationStatusInt.IN_VALIDATION, SINGLE_RECIPIENT)
                .withTimelineGoToState(TimelineElementCategoryInt.NORMALIZED_ADDRESS, NotificationStatusInt.IN_VALIDATION, SINGLE_RECIPIENT)
                .withTimelineGoToState(TimelineElementCategoryInt.PUBLIC_REGISTRY_VALIDATION_CALL, NotificationStatusInt.IN_VALIDATION, SINGLE_RECIPIENT)
                .withTimelineGoToState(TimelineElementCategoryInt.PUBLIC_REGISTRY_VALIDATION_RESPONSE, NotificationStatusInt.IN_VALIDATION, SINGLE_RECIPIENT)

                //STATE CHANGE
                .withTimelineGoToState(TimelineElementCategoryInt.REQUEST_ACCEPTED, NotificationStatusInt.ACCEPTED, SINGLE_RECIPIENT)
                .withTimelineGoToState(TimelineElementCategoryInt.REQUEST_REFUSED, NotificationStatusInt.REFUSED, SINGLE_RECIPIENT);
    }

    private void fromStatusAccepted() {
        this.fromState(NotificationStatusInt.ACCEPTED)
                .withTimelineGoToState(TimelineElementCategoryInt.SEND_DIGITAL_MESSAGE, NotificationStatusInt.PROCESSING, SINGLE_RECIPIENT)
                .withTimelineGoToState(TimelineElementCategoryInt.SEND_ANALOG_MESSAGE, NotificationStatusInt.PROCESSING, SINGLE_RECIPIENT);
    }

    private void fromStatusProcessing() {
        this.fromState(NotificationStatusInt.PROCESSING)
                //STATE UNCHANGE
                .withTimelineGoToState(TimelineElementCategoryInt.SEND_DIGITAL_MESSAGE, NotificationStatusInt.PROCESSING, SINGLE_RECIPIENT)
                .withTimelineGoToState(TimelineElementCategoryInt.SEND_DIGITAL_MESSAGE_SKIP, NotificationStatusInt.PROCESSING, SINGLE_RECIPIENT)
                .withTimelineGoToState(TimelineElementCategoryInt.SEND_DIGITAL_MESSAGE_PROGRESS, NotificationStatusInt.PROCESSING, SINGLE_RECIPIENT)
                .withTimelineGoToState(TimelineElementCategoryInt.SEND_DIGITAL_MESSAGE_FEEDBACK, NotificationStatusInt.PROCESSING, SINGLE_RECIPIENT)
                .withTimelineGoToState(TimelineElementCategoryInt.PREPARE_ANALOG_DELIVERY, NotificationStatusInt.PROCESSING, SINGLE_RECIPIENT)
                .withTimelineGoToState(TimelineElementCategoryInt.SEND_ANALOG_MESSAGE, NotificationStatusInt.PROCESSING, SINGLE_RECIPIENT)
                .withTimelineGoToState(TimelineElementCategoryInt.SEND_ANALOG_MESSAGE_PROGRESS, NotificationStatusInt.PROCESSING, SINGLE_RECIPIENT)
                .withTimelineGoToState(TimelineElementCategoryInt.SEND_ANALOG_MESSAGE_FEEDBACK, NotificationStatusInt.PROCESSING, SINGLE_RECIPIENT)
                .withTimelineGoToState(TimelineElementCategoryInt.DELIVERED, NotificationStatusInt.PROCESSING, SINGLE_RECIPIENT)
                .withTimelineGoToState(TimelineElementCategoryInt.PAYMENT, NotificationStatusInt.PROCESSING, SINGLE_RECIPIENT)
                .withTimelineGoToState(TimelineElementCategoryInt.INFORMAL_NOTIFICATION_VIEWED, NotificationStatusInt.PROCESSING, SINGLE_RECIPIENT)

                //STATE CHANGE
                .withTimelineGoToState(TimelineElementCategoryInt.WORKFLOW_ENDED_REACHED, NotificationStatusInt.COMPLETED_REACHED, SINGLE_RECIPIENT)
                .withTimelineGoToState(TimelineElementCategoryInt.WORKFLOW_ENDED_UNREACHED, NotificationStatusInt.COMPLETED_UNREACHED, SINGLE_RECIPIENT)
                .withTimelineGoToState(TimelineElementCategoryInt.WORKFLOW_ENDED_UNDELIVERABLE, NotificationStatusInt.UNDELIVERABLE, SINGLE_RECIPIENT)
                .withTimelineGoToState(TimelineElementCategoryInt.WORKFLOW_DONE_REACHED, NotificationStatusInt.COMPLETED_REACHED, SINGLE_RECIPIENT)
                .withTimelineGoToState(TimelineElementCategoryInt.WORKFLOW_DONE_UNREACHED, NotificationStatusInt.COMPLETED_UNREACHED, SINGLE_RECIPIENT);
    }

    private void fromStatusCompletedUnreached() {
        this.fromState(NotificationStatusInt.COMPLETED_UNREACHED)
                //STATE UNCHANGE
                .withTimelineGoToState(TimelineElementCategoryInt.SEND_DIGITAL_MESSAGE_PROGRESS, NotificationStatusInt.COMPLETED_UNREACHED, SINGLE_RECIPIENT)
                .withTimelineGoToState(TimelineElementCategoryInt.SEND_DIGITAL_MESSAGE_FEEDBACK, NotificationStatusInt.COMPLETED_UNREACHED, SINGLE_RECIPIENT)
                .withTimelineGoToState(TimelineElementCategoryInt.SEND_ANALOG_MESSAGE_PROGRESS, NotificationStatusInt.COMPLETED_UNREACHED, SINGLE_RECIPIENT)
                .withTimelineGoToState(TimelineElementCategoryInt.SEND_ANALOG_MESSAGE_FEEDBACK, NotificationStatusInt.COMPLETED_UNREACHED, SINGLE_RECIPIENT)
                .withTimelineGoToState(TimelineElementCategoryInt.PAYMENT, NotificationStatusInt.COMPLETED_UNREACHED, SINGLE_RECIPIENT)
                .withTimelineGoToState(TimelineElementCategoryInt.INFORMAL_NOTIFICATION_VIEWED, NotificationStatusInt.COMPLETED_UNREACHED, SINGLE_RECIPIENT)

                //STATE CHANGE
                .withTimelineGoToState(TimelineElementCategoryInt.WORKFLOW_ENDED_REACHED, NotificationStatusInt.COMPLETED_REACHED, SINGLE_RECIPIENT)
                .withTimelineGoToState(TimelineElementCategoryInt.WORKFLOW_DONE_REACHED, NotificationStatusInt.COMPLETED_REACHED, SINGLE_RECIPIENT);
    }

    private void fromStatusUndeliverable() {
        this.fromState(NotificationStatusInt.UNDELIVERABLE)
                //STATE CHANGE
                .withTimelineGoToState(TimelineElementCategoryInt.WORKFLOW_DONE_REACHED, NotificationStatusInt.COMPLETED_REACHED, SINGLE_RECIPIENT)
                .withTimelineGoToState(TimelineElementCategoryInt.WORKFLOW_ENDED_REACHED, NotificationStatusInt.COMPLETED_REACHED, SINGLE_RECIPIENT);
    }

    private void fromStatusCompletedReached() {
        this.fromState(NotificationStatusInt.COMPLETED_REACHED)
                //STATE UNCHANGE
                .withTimelineGoToState(TimelineElementCategoryInt.SEND_DIGITAL_MESSAGE_PROGRESS, NotificationStatusInt.COMPLETED_REACHED, SINGLE_RECIPIENT)
                .withTimelineGoToState(TimelineElementCategoryInt.SEND_DIGITAL_MESSAGE_FEEDBACK, NotificationStatusInt.COMPLETED_REACHED, SINGLE_RECIPIENT)
                .withTimelineGoToState(TimelineElementCategoryInt.SEND_ANALOG_MESSAGE_PROGRESS, NotificationStatusInt.COMPLETED_REACHED, SINGLE_RECIPIENT)
                .withTimelineGoToState(TimelineElementCategoryInt.SEND_ANALOG_MESSAGE_FEEDBACK, NotificationStatusInt.COMPLETED_REACHED, SINGLE_RECIPIENT)
                .withTimelineGoToState(TimelineElementCategoryInt.PAYMENT, NotificationStatusInt.COMPLETED_REACHED, SINGLE_RECIPIENT)
                .withTimelineGoToState(TimelineElementCategoryInt.INFORMAL_NOTIFICATION_VIEWED, NotificationStatusInt.COMPLETED_REACHED, SINGLE_RECIPIENT)
                .withTimelineGoToState(TimelineElementCategoryInt.WORKFLOW_ENDED_REACHED, NotificationStatusInt.COMPLETED_REACHED, SINGLE_RECIPIENT);
    }
}
