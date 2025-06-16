package it.pagopa.pn.timelineservice.dto.timeline.details;

import lombok.Getter;

@Getter
public enum TimelineElementCategoryInt {
    SENDER_ACK_CREATION_REQUEST(SenderAckCreationRequestDetailsInt.class, TimelineElementCategoryInt.VERSION_10),
    VALIDATE_F24_REQUEST(ValidateF24Int.class, TimelineElementCategoryInt.VERSION_20, TimelineElementCategoryInt.DIAGNOSTIC_ELEMENT),
    VALIDATE_NORMALIZE_ADDRESSES_REQUEST(ValidateNormalizeAddressDetailsInt.class, TimelineElementCategoryInt.VERSION_10),
    VALIDATED_F24(ValidatedF24DetailInt.class, TimelineElementCategoryInt.VERSION_20, TimelineElementCategoryInt.DIAGNOSTIC_ELEMENT),
    NORMALIZED_ADDRESS(NormalizedAddressDetailsInt.class, TimelineElementCategoryInt.VERSION_10),
    REQUEST_ACCEPTED(NotificationRequestAcceptedDetailsInt.class, TimelineElementCategoryInt.VERSION_10),
    GENERATE_F24_REQUEST(ValidateF24Int.class, TimelineElementCategoryInt.VERSION_23, TimelineElementCategoryInt.DIAGNOSTIC_ELEMENT),
    GENERATED_F24(GeneratedF24DetailsInt.class, TimelineElementCategoryInt.VERSION_23, TimelineElementCategoryInt.DIAGNOSTIC_ELEMENT),
    SEND_COURTESY_MESSAGE(SendCourtesyMessageDetailsInt.class, TimelineElementCategoryInt.VERSION_10),
    GET_ADDRESS(GetAddressInfoDetailsInt.class, TimelineElementCategoryInt.VERSION_10),
    PUBLIC_REGISTRY_CALL(PublicRegistryCallDetailsInt.class, TimelineElementCategoryInt.VERSION_10),
    PUBLIC_REGISTRY_RESPONSE(PublicRegistryResponseDetailsInt.class, TimelineElementCategoryInt.VERSION_10),
    SCHEDULE_ANALOG_WORKFLOW(ScheduleAnalogWorkflowDetailsInt.class, TimelineElementCategoryInt.VERSION_10),
    SCHEDULE_DIGITAL_WORKFLOW(ScheduleDigitalWorkflowDetailsInt.class, TimelineElementCategoryInt.VERSION_10),
    PREPARE_DIGITAL_DOMICILE(PrepareDigitalDetailsInt.class, TimelineElementCategoryInt.VERSION_10),
    SEND_DIGITAL_DOMICILE(SendDigitalDetailsInt.class, TimelineElementCategoryInt.VERSION_10),
    SEND_DIGITAL_FEEDBACK(SendDigitalFeedbackDetailsInt.class, TimelineElementCategoryInt.PRIORITY_AFTER, TimelineElementCategoryInt.VERSION_10),
    SEND_DIGITAL_PROGRESS(SendDigitalProgressDetailsInt.class, TimelineElementCategoryInt.VERSION_10),
    REFINEMENT(RefinementDetailsInt.class, TimelineElementCategoryInt.VERSION_10),
    SCHEDULE_REFINEMENT(ScheduleRefinementDetailsInt.class, TimelineElementCategoryInt.PRIORITY_SCHEDULE_REFINEMENT, TimelineElementCategoryInt.VERSION_10),
    DIGITAL_DELIVERY_CREATION_REQUEST(DigitalDeliveryCreationRequestDetailsInt.class, TimelineElementCategoryInt.VERSION_10),
    DIGITAL_SUCCESS_WORKFLOW(DigitalSuccessWorkflowDetailsInt.class, TimelineElementCategoryInt.VERSION_10),
    DIGITAL_FAILURE_WORKFLOW(DigitalFailureWorkflowDetailsInt.class, TimelineElementCategoryInt.VERSION_10),
    ANALOG_SUCCESS_WORKFLOW(AnalogSuccessWorkflowDetailsInt.class, TimelineElementCategoryInt.PRIORITY_ANALOG_SUCCESS_WORKFLOW, TimelineElementCategoryInt.VERSION_10),
    ANALOG_FAILURE_WORKFLOW(AnalogFailureWorkflowDetailsInt.class, TimelineElementCategoryInt.PRIORITY_ANALOG_FAILURE_WORKFLOW, TimelineElementCategoryInt.VERSION_10),
    COMPLETELY_UNREACHABLE_CREATION_REQUEST(CompletelyUnreachableCreationRequestDetailsInt.class, TimelineElementCategoryInt.PRIORITY_COMPLETELY_UNREACHABLE_CREATION_REQUEST, TimelineElementCategoryInt.VERSION_10),
    PREPARE_SIMPLE_REGISTERED_LETTER(BaseRegisteredLetterDetailsInt.class, TimelineElementCategoryInt.VERSION_10),
    SEND_SIMPLE_REGISTERED_LETTER(SimpleRegisteredLetterDetailsInt.class, TimelineElementCategoryInt.VERSION_10),
    NOTIFICATION_VIEWED_CREATION_REQUEST(NotificationViewedCreationRequestDetailsInt.class, TimelineElementCategoryInt.VERSION_10),
    NOTIFICATION_VIEWED(NotificationViewedDetailsInt.class, TimelineElementCategoryInt.VERSION_10),
    PREPARE_ANALOG_DOMICILE(BaseAnalogDetailsInt.class, TimelineElementCategoryInt.VERSION_10),
    PREPARE_ANALOG_DOMICILE_FAILURE(PrepareAnalogDomicileFailureDetailsInt.class, TimelineElementCategoryInt.VERSION_20),
    SEND_ANALOG_DOMICILE(SendAnalogDetailsInt.class, TimelineElementCategoryInt.VERSION_10),
    SEND_ANALOG_PROGRESS(SendAnalogProgressDetailsInt.class, TimelineElementCategoryInt.VERSION_10),
    SEND_ANALOG_FEEDBACK(SendAnalogFeedbackDetailsInt.class, TimelineElementCategoryInt.PRIORITY_SEND_ANALOG_FEEDBACK, TimelineElementCategoryInt.VERSION_10),
    PAYMENT(NotificationPaidDetailsInt.class, TimelineElementCategoryInt.VERSION_10),
    COMPLETELY_UNREACHABLE(CompletelyUnreachableDetailsInt.class, TimelineElementCategoryInt.PRIORITY_COMPLETELY_UNREACHABLET, TimelineElementCategoryInt.VERSION_10),
    REQUEST_REFUSED(RequestRefusedDetailsInt.class, TimelineElementCategoryInt.VERSION_10),
    AAR_CREATION_REQUEST(AarCreationRequestDetailsInt.class, TimelineElementCategoryInt.VERSION_10),
    AAR_GENERATION(AarGenerationDetailsInt.class, TimelineElementCategoryInt.VERSION_10),
    NOT_HANDLED(NotHandledDetailsInt.class, TimelineElementCategoryInt.VERSION_10),
    SEND_SIMPLE_REGISTERED_LETTER_PROGRESS(SimpleRegisteredLetterProgressDetailsInt.class, TimelineElementCategoryInt.VERSION_10),
    PROBABLE_SCHEDULING_ANALOG_DATE(ProbableDateAnalogWorkflowDetailsInt.class, TimelineElementCategoryInt.VERSION_20),
    NOTIFICATION_CANCELLATION_REQUEST(NotificationCancellationRequestDetailsInt.class, TimelineElementCategoryInt.VERSION_20),
    NOTIFICATION_CANCELLED(NotificationCancelledDetailsInt.class, TimelineElementCategoryInt.VERSION_20),
    NOTIFICATION_RADD_RETRIEVED(NotificationRADDRetrievedDetailsInt.class, TimelineElementCategoryInt.VERSION_23),
    NOTIFICATION_CANCELLED_DOCUMENT_CREATION_REQUEST(NotificationCancelledDocumentCreationRequestDetailsInt.class, TimelineElementCategoryInt.VERSION_25, TimelineElementCategoryInt.DIAGNOSTIC_ELEMENT),
    ANALOG_WORKFLOW_RECIPIENT_DECEASED(AnalogWorfklowRecipientDeceasedDetailsInt.class, TimelineElementCategoryInt.PRIORITY_ANALOG_WORKFLOW_RECIPIENT_DECEASED, TimelineElementCategoryInt.VERSION_26),
    PUBLIC_REGISTRY_VALIDATION_CALL(PublicRegistryValidationCallDetailsInt.class, TimelineElementCategoryInt.VERSION_27),
    PUBLIC_REGISTRY_VALIDATION_RESPONSE(PublicRegistryValidationResponseDetailsInt.class, TimelineElementCategoryInt.VERSION_27);

    private final Class<? extends TimelineElementDetailsInt> detailsJavaClass;
    private final int priority;
    private final int version;
    private final boolean isDiagnostic;

    public static final int PRIORITY_SEND_ANALOG_FEEDBACK = 30;
    public static final int PRIORITY_ANALOG_SUCCESS_WORKFLOW = 40;
    public static final int PRIORITY_ANALOG_FAILURE_WORKFLOW = 40;
    public static final int PRIORITY_ANALOG_WORKFLOW_RECIPIENT_DECEASED = 40;
    public static final int PRIORITY_COMPLETELY_UNREACHABLE_CREATION_REQUEST = 50;
    public static final int PRIORITY_COMPLETELY_UNREACHABLET = 60;
    public static final int PRIORITY_SCHEDULE_REFINEMENT = 70;

    public static final int PRIORITY_BEFORE = 10;
    public static final int PRIORITY_AFTER = 20;

    public static final int VERSION_10 = 10;
    public static final int VERSION_20 = 20;
    public static final int VERSION_23 = 23;
    public static final int VERSION_25 = 25;
    public static final int VERSION_26 = 26;
    public static final int VERSION_27 = 27;

    public static final boolean DIAGNOSTIC_ELEMENT = true;

    TimelineElementCategoryInt(Class<? extends TimelineElementDetailsInt> detailsJavaClass, int version) {
        this(detailsJavaClass, PRIORITY_BEFORE, version, false);
    }

    TimelineElementCategoryInt(Class<? extends TimelineElementDetailsInt> detailsJavaClass, int priority, int version) {
        this(detailsJavaClass, priority, version, false);
    }

    TimelineElementCategoryInt(Class<? extends TimelineElementDetailsInt> detailsJavaClass, int version, boolean isDiagnostic) {
        this(detailsJavaClass, PRIORITY_BEFORE, version, isDiagnostic);
    }

    TimelineElementCategoryInt(Class<? extends TimelineElementDetailsInt> detailsJavaClass, int priority, int version, boolean isDiagnostic) {
        this.detailsJavaClass = detailsJavaClass;
        this.priority = priority;
        this.version = version;
        this.isDiagnostic = isDiagnostic;
    }

}
