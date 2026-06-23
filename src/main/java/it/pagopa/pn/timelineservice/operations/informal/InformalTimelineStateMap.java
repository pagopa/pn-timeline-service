package it.pagopa.pn.timelineservice.operations.informal;

import it.pagopa.pn.timelineservice.dto.notification.status.NotificationStatusInt;
import it.pagopa.pn.timelineservice.dto.timeline.details.legal.TimelineElementCategoryInt;
import it.pagopa.pn.timelineservice.operations.common.AbstractStateMap;

public class InformalTimelineStateMap extends AbstractStateMap {

    @Override
    protected void configureTransitions() {
        fromStatusInValidation();
        fromStatusProcessing();
        fromStatusReached();
        fromStatusUnreached();
        fromStatusUndeliverable();
        fromStatusCompleted();

        this.fromState(NotificationStatusInt.REFUSED);
    }

    private void fromStatusInValidation() {
        // Received state
        this.fromState(NotificationStatusInt.IN_VALIDATION)
                //STATE UNCHANGE
                .withTimelineGoToState(TimelineElementCategoryInt.VALIDATE_NORMALIZE_ADDRESSES_REQUEST, NotificationStatusInt.IN_VALIDATION, SINGLE_RECIPIENT)
                .withTimelineGoToState(TimelineElementCategoryInt.NORMALIZED_ADDRESS, NotificationStatusInt.IN_VALIDATION, SINGLE_RECIPIENT)
                .withTimelineGoToState(TimelineElementCategoryInt.PUBLIC_REGISTRY_VALIDATION_CALL, NotificationStatusInt.IN_VALIDATION, SINGLE_RECIPIENT)
                .withTimelineGoToState(TimelineElementCategoryInt.PUBLIC_REGISTRY_VALIDATION_RESPONSE, NotificationStatusInt.IN_VALIDATION, SINGLE_RECIPIENT)

                //STATE CHANGE
                .withTimelineGoToState(TimelineElementCategoryInt.REQUEST_ACCEPTED, NotificationStatusInt.PROCESSING, SINGLE_RECIPIENT)
                .withTimelineGoToState(TimelineElementCategoryInt.REQUEST_REFUSED, NotificationStatusInt.REFUSED, SINGLE_RECIPIENT)
        //Todo: gestire l'accepted, non in stato processing ma in stato accepted e da accepted a -> processing
        ;
    }

    private void fromStatusProcessing() {
        // Received state
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
                .withTimelineGoToState(TimelineElementCategoryInt.REACHED, NotificationStatusInt.PROCESSING, SINGLE_RECIPIENT)
                .withTimelineGoToState(TimelineElementCategoryInt.PAYMENT, NotificationStatusInt.PROCESSING, SINGLE_RECIPIENT)
                .withTimelineGoToState(TimelineElementCategoryInt.INFORMAL_NOTIFICATION_VIEWED, NotificationStatusInt.PROCESSING, SINGLE_RECIPIENT)

                //STATE CHANGE
                .withTimelineGoToState(TimelineElementCategoryInt.WORKFLOW_ENDED_REACHED, NotificationStatusInt.REACHED, SINGLE_RECIPIENT)
                .withTimelineGoToState(TimelineElementCategoryInt.WORKFLOW_ENDED_UNREACHED, NotificationStatusInt.UNREACHED, SINGLE_RECIPIENT)
                .withTimelineGoToState(TimelineElementCategoryInt.WORKFLOW_ENDED_UNDELIVERABLE, NotificationStatusInt.UNDELIVERABLE, SINGLE_RECIPIENT)
                .withTimelineGoToState(TimelineElementCategoryInt.WORKFLOW_DONE, NotificationStatusInt.COMPLETED, SINGLE_RECIPIENT)
        ;
    }

    private void fromStatusReached() {
        // Received state
        this.fromState(NotificationStatusInt.REACHED)
                //STATE UNCHANGE
                .withTimelineGoToState(TimelineElementCategoryInt.SEND_DIGITAL_MESSAGE_PROGRESS, NotificationStatusInt.REACHED, SINGLE_RECIPIENT)
                .withTimelineGoToState(TimelineElementCategoryInt.SEND_DIGITAL_MESSAGE_FEEDBACK, NotificationStatusInt.REACHED, SINGLE_RECIPIENT)
                .withTimelineGoToState(TimelineElementCategoryInt.SEND_ANALOG_MESSAGE_PROGRESS, NotificationStatusInt.REACHED, SINGLE_RECIPIENT)
                .withTimelineGoToState(TimelineElementCategoryInt.SEND_ANALOG_MESSAGE_FEEDBACK, NotificationStatusInt.REACHED, SINGLE_RECIPIENT)
                .withTimelineGoToState(TimelineElementCategoryInt.PAYMENT, NotificationStatusInt.REACHED, SINGLE_RECIPIENT)
                .withTimelineGoToState(TimelineElementCategoryInt.INFORMAL_NOTIFICATION_VIEWED, NotificationStatusInt.REACHED, SINGLE_RECIPIENT)

                //STATE CHANGE
                .withTimelineGoToState(TimelineElementCategoryInt.WORKFLOW_DONE, NotificationStatusInt.COMPLETED, SINGLE_RECIPIENT)
        ;
    }

    private void fromStatusUnreached() {
        // Received state
        this.fromState(NotificationStatusInt.UNREACHED)
                //STATE UNCHANGE
                .withTimelineGoToState(TimelineElementCategoryInt.SEND_DIGITAL_MESSAGE_PROGRESS, NotificationStatusInt.UNREACHED, SINGLE_RECIPIENT)
                .withTimelineGoToState(TimelineElementCategoryInt.SEND_DIGITAL_MESSAGE_FEEDBACK, NotificationStatusInt.UNREACHED, SINGLE_RECIPIENT)
                .withTimelineGoToState(TimelineElementCategoryInt.SEND_ANALOG_MESSAGE_PROGRESS, NotificationStatusInt.UNREACHED, SINGLE_RECIPIENT)
                .withTimelineGoToState(TimelineElementCategoryInt.SEND_ANALOG_MESSAGE_FEEDBACK, NotificationStatusInt.UNREACHED, SINGLE_RECIPIENT)
                .withTimelineGoToState(TimelineElementCategoryInt.PAYMENT, NotificationStatusInt.UNREACHED, SINGLE_RECIPIENT)
                .withTimelineGoToState(TimelineElementCategoryInt.INFORMAL_NOTIFICATION_VIEWED, NotificationStatusInt.UNREACHED, SINGLE_RECIPIENT)

                //STATE CHANGE
                .withTimelineGoToState(TimelineElementCategoryInt.WORKFLOW_ENDED_REACHED, NotificationStatusInt.REACHED, SINGLE_RECIPIENT)
                .withTimelineGoToState(TimelineElementCategoryInt.WORKFLOW_DONE, NotificationStatusInt.COMPLETED, SINGLE_RECIPIENT)
        ;
    }

    private void fromStatusUndeliverable() {
        // Received state
        this.fromState(NotificationStatusInt.UNDELIVERABLE)
                //STATE CHANGE
                .withTimelineGoToState(TimelineElementCategoryInt.WORKFLOW_DONE, NotificationStatusInt.COMPLETED, SINGLE_RECIPIENT)
        ;
    }

    private void fromStatusCompleted() {
        // Received state
        this.fromState(NotificationStatusInt.COMPLETED)
                //STATE UNCHANGE
                .withTimelineGoToState(TimelineElementCategoryInt.SEND_DIGITAL_MESSAGE_PROGRESS, NotificationStatusInt.COMPLETED, SINGLE_RECIPIENT)
                .withTimelineGoToState(TimelineElementCategoryInt.SEND_DIGITAL_MESSAGE_FEEDBACK, NotificationStatusInt.COMPLETED, SINGLE_RECIPIENT)
                .withTimelineGoToState(TimelineElementCategoryInt.SEND_ANALOG_MESSAGE_PROGRESS, NotificationStatusInt.COMPLETED, SINGLE_RECIPIENT)
                .withTimelineGoToState(TimelineElementCategoryInt.SEND_ANALOG_MESSAGE_FEEDBACK, NotificationStatusInt.COMPLETED, SINGLE_RECIPIENT)
                .withTimelineGoToState(TimelineElementCategoryInt.PAYMENT, NotificationStatusInt.COMPLETED, SINGLE_RECIPIENT)
                .withTimelineGoToState(TimelineElementCategoryInt.INFORMAL_NOTIFICATION_VIEWED, NotificationStatusInt.COMPLETED, SINGLE_RECIPIENT)
        ;
    }
}
