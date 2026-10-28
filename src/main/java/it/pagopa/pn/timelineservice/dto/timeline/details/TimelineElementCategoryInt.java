package it.pagopa.pn.timelineservice.dto.timeline.details;

import it.pagopa.pn.timelineservice.dto.timeline.details.common.*;
import it.pagopa.pn.timelineservice.dto.timeline.details.informal.*;
import it.pagopa.pn.timelineservice.dto.timeline.details.legal.*;
import lombok.Getter;

@Getter
public enum TimelineElementCategoryInt {
    SENDER_ACK_CREATION_REQUEST(SenderAckCreationRequestDetailsInt.class),
    VALIDATE_F24_REQUEST(ValidateF24Int.class),
    VALIDATE_NORMALIZE_ADDRESSES_REQUEST(ValidateNormalizeAddressDetailsInt.class),
    VALIDATED_F24(ValidatedF24DetailInt.class),
    NORMALIZED_ADDRESS(NormalizedAddressDetailsInt.class),
    REQUEST_ACCEPTED(NotificationRequestAcceptedDetailsInt.class),
    GENERATE_F24_REQUEST(ValidateF24Int.class),
    GENERATED_F24(GeneratedF24DetailsInt.class),
    SEND_COURTESY_MESSAGE(SendCourtesyMessageDetailsInt.class),
    GET_ADDRESS(GetAddressInfoDetailsInt.class),
    PUBLIC_REGISTRY_CALL(PublicRegistryCallDetailsInt.class),
    PUBLIC_REGISTRY_RESPONSE(PublicRegistryResponseDetailsInt.class),
    SCHEDULE_ANALOG_WORKFLOW(ScheduleAnalogWorkflowDetailsInt.class),
    SCHEDULE_DIGITAL_WORKFLOW(ScheduleDigitalWorkflowDetailsInt.class),
    PREPARE_DIGITAL_DOMICILE(PrepareDigitalDetailsInt.class),
    SEND_DIGITAL_DOMICILE(SendDigitalDetailsInt.class),
    SEND_DIGITAL_FEEDBACK(SendDigitalFeedbackDetailsInt.class, TimelineElementCategoryInt.PRIORITY_AFTER),
    SEND_DIGITAL_PROGRESS(SendDigitalProgressDetailsInt.class),
    REFINEMENT(RefinementDetailsInt.class),
    SCHEDULE_REFINEMENT(ScheduleRefinementDetailsInt.class, TimelineElementCategoryInt.PRIORITY_SCHEDULE_REFINEMENT),
    DIGITAL_DELIVERY_CREATION_REQUEST(DigitalDeliveryCreationRequestDetailsInt.class),
    DIGITAL_SUCCESS_WORKFLOW(DigitalSuccessWorkflowDetailsInt.class),
    DIGITAL_FAILURE_WORKFLOW(DigitalFailureWorkflowDetailsInt.class),
    ANALOG_SUCCESS_WORKFLOW(AnalogSuccessWorkflowDetailsInt.class, TimelineElementCategoryInt.PRIORITY_ANALOG_SUCCESS_WORKFLOW),
    ANALOG_FAILURE_WORKFLOW(AnalogFailureWorkflowDetailsInt.class, TimelineElementCategoryInt.PRIORITY_ANALOG_FAILURE_WORKFLOW),
    COMPLETELY_UNREACHABLE_CREATION_REQUEST(CompletelyUnreachableCreationRequestDetailsInt.class, TimelineElementCategoryInt.PRIORITY_COMPLETELY_UNREACHABLE_CREATION_REQUEST),
    PREPARE_SIMPLE_REGISTERED_LETTER(BaseRegisteredLetterDetailsInt.class),
    SEND_SIMPLE_REGISTERED_LETTER(SimpleRegisteredLetterDetailsInt.class),
    NOTIFICATION_VIEWED_CREATION_REQUEST(NotificationViewedCreationRequestDetailsInt.class),
    NOTIFICATION_VIEWED(NotificationViewedDetailsInt.class),
    PREPARE_ANALOG_DOMICILE(BaseAnalogDetailsInt.class),
    PREPARE_ANALOG_DOMICILE_FAILURE(PrepareAnalogDomicileFailureDetailsInt.class),
    SEND_ANALOG_DOMICILE(SendAnalogDetailsInt.class),
    SEND_ANALOG_PROGRESS(SendAnalogProgressDetailsInt.class),
    SEND_ANALOG_FEEDBACK(SendAnalogFeedbackDetailsInt.class, TimelineElementCategoryInt.PRIORITY_SEND_ANALOG_FEEDBACK),
    PAYMENT(NotificationPaidDetailsInt.class),
    COMPLETELY_UNREACHABLE(CompletelyUnreachableDetailsInt.class, TimelineElementCategoryInt.PRIORITY_COMPLETELY_UNREACHABLE),
    REQUEST_REFUSED(RequestRefusedDetailsInt.class),
    AAR_CREATION_REQUEST(AarCreationRequestDetailsInt.class),
    AAR_GENERATION(AarGenerationDetailsInt.class),
    NOT_HANDLED(NotHandledDetailsInt.class),
    SEND_SIMPLE_REGISTERED_LETTER_PROGRESS(SimpleRegisteredLetterProgressDetailsInt.class),
    PROBABLE_SCHEDULING_ANALOG_DATE(ProbableDateAnalogWorkflowDetailsInt.class),
    NOTIFICATION_CANCELLATION_REQUEST(NotificationCancellationRequestDetailsInt.class),
    NOTIFICATION_CANCELLED(NotificationCancelledDetailsInt.class),
    NOTIFICATION_RADD_RETRIEVED(NotificationRADDRetrievedDetailsInt.class),
    NOTIFICATION_CANCELLED_DOCUMENT_CREATION_REQUEST(NotificationCancelledDocumentCreationRequestDetailsInt.class),
    ANALOG_WORKFLOW_RECIPIENT_DECEASED(AnalogWorfklowRecipientDeceasedDetailsInt.class, TimelineElementCategoryInt.PRIORITY_ANALOG_WORKFLOW_RECIPIENT_DECEASED),
    PUBLIC_REGISTRY_VALIDATION_CALL(PublicRegistryValidationCallDetailsInt.class),
    PUBLIC_REGISTRY_VALIDATION_RESPONSE(PublicRegistryValidationResponseDetailsInt.class),
    SEND_ANALOG_TIMEOUT_CREATION_REQUEST(SendAnalogTimeoutCreationRequestDetailsInt.class),
    SEND_ANALOG_TIMEOUT(SendAnalogTimeoutDetailsInt.class),
    ANALOG_FAILURE_WORKFLOW_TIMEOUT(AnalogFailureWorkflowTimeoutDetailsInt.class, TimelineElementCategoryInt.PRIORITY_ANALOG_FAILURE_WORKFLOW_TIMEOUT),
    NOTIFICATION_TIMELINE_REWORKED(NotificationTimelineReworkedDetailsInt.class, TimelineElementCategoryInt.PRIORITY_AFTER),
    NOTIFICATION_COST_VALIDATION_REQUEST(NotificationCostValidationRequestDetailsInt.class),
    NOTIFICATION_COST_VALIDATION_RESPONSE(NotificationCostValidationResponseDetailsInt.class),
    COURTESY_CHANNEL_FAILED(CourtesyChannelFailedDetailsInt.class),
    //Timeline Element for Informal Notification
    SEND_DIGITAL_MESSAGE(SendDigitalMessageDetailsInt.class, TimelineElementCategoryInt.PRIORITY_SEND_DIGITAL_MESSAGE),
    SEND_DIGITAL_MESSAGE_SKIP(SendDigitalMessageSkipDetailsInt.class),
    SEND_DIGITAL_MESSAGE_PROGRESS(SendDigitalMessageProgressDetailsInt.class),
    SEND_DIGITAL_MESSAGE_FEEDBACK(SendDigitalMessageFeedbackDetailsInt.class, TimelineElementCategoryInt.PRIORITY_SEND_DIGITAL_MESSAGE_FEEDBACK),
    PREPARE_ANALOG_DELIVERY(PrepareAnalogDeliveryDetailsInt.class),
    SEND_ANALOG_MESSAGE(SendAnalogMessageDetailsInt.class),
    SEND_ANALOG_MESSAGE_PROGRESS(SendAnalogMessageProgressDetailsInt.class),
    SEND_ANALOG_MESSAGE_FEEDBACK(SendAnalogMessageFeedbackDetailsInt.class, TimelineElementCategoryInt.PRIORITY_SEND_ANALOG_MESSAGE_FEEDBACK),
    DELIVERED(DeliveredDetailsInt.class, TimelineElementCategoryInt.PRIORITY_DELIVERED),
    WORKFLOW_ENDED_REACHED(WorkflowEndedReachedDetailsInt.class),
    WORKFLOW_ENDED_UNREACHED(WorkflowEndedUnreachedDetailsInt.class),
    WORKFLOW_ENDED_UNDELIVERABLE(WorkflowEndedUndeliverableDetailsInt.class),
    WORKFLOW_DONE_REACHED(WorkflowDoneReachedDetailsInt.class),
    WORKFLOW_DONE_UNREACHED(WorkflowDoneUnreachedDetailsInt.class),
    INFORMAL_NOTIFICATION_VIEWED(InformalNotificationViewedDetailsInt.class),
    PAYMENT_NOTICE_PAID(PaymentNoticePaidDetailsInt.class),
    COVERPAGE_CREATION_REQUEST(CoverpageCreationRequestDetailsInt.class);

    private final Class<? extends TimelineElementDetailsInt> detailsJavaClass;
    private final int priority;

    public static final int PRIORITY_SEND_ANALOG_FEEDBACK = 30;
    public static final int PRIORITY_ANALOG_SUCCESS_WORKFLOW = 40;
    public static final int PRIORITY_ANALOG_FAILURE_WORKFLOW = 40;
    public static final int PRIORITY_ANALOG_WORKFLOW_RECIPIENT_DECEASED = 40;
    public static final int PRIORITY_COMPLETELY_UNREACHABLE_CREATION_REQUEST = 50;
    public static final int PRIORITY_COMPLETELY_UNREACHABLE = 60;
    public static final int PRIORITY_SCHEDULE_REFINEMENT = 70;
    public static final int PRIORITY_ANALOG_FAILURE_WORKFLOW_TIMEOUT = 70;
    public static final int PRIORITY_DELIVERED = 40;
    public static final int PRIORITY_SEND_ANALOG_MESSAGE_FEEDBACK = 30;
    public static final int PRIORITY_SEND_DIGITAL_MESSAGE = 20;
    public static final int PRIORITY_SEND_DIGITAL_MESSAGE_FEEDBACK = 30;

    public static final int PRIORITY_BEFORE = 10;
    public static final int PRIORITY_AFTER = 20;

    TimelineElementCategoryInt(Class<? extends TimelineElementDetailsInt> detailsJavaClass) {
        this(detailsJavaClass, PRIORITY_BEFORE);
    }


    TimelineElementCategoryInt(Class<? extends TimelineElementDetailsInt> detailsJavaClass, int priority) {
        this.detailsJavaClass = detailsJavaClass;
        this.priority = priority;
    }

}
