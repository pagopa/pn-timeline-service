package it.pagopa.pn.timelineservice;

import it.pagopa.pn.timelineservice.dto.timeline.details.*;
import it.pagopa.pn.timelineservice.generated.openapi.server.v1.dto.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.stream.Stream;

class CheckInternalExternalDetailsTest {

    private static final List<TestErrors> testErrors = new ArrayList<>();
    private static int counter = 0;
    private static final List<? extends Class<? extends TimelineElementDetailsInt>> internalImplementations = Arrays.stream(TimelineElementCategoryInt.values())
            .map(TimelineElementCategoryInt::getDetailsJavaClass)
            .toList();

    @Test
    void testAllDetailClasses()  {

        classPairsProvider().forEach(pair -> {
            try {
                testClassPair(pair.internalClass(), pair.generatedClass());
            } catch (InvocationTargetException | InstantiationException | NoSuchMethodException |
                     IllegalAccessException e) {
                throw new CheckInternalExternalDetailsTestException("Error while testing class pair: " + pair.internalClass().getSimpleName() + " and " + pair.generatedClass().getSimpleName(), e);
            }
        });

        Assertions.assertEquals(counter, internalImplementations.size());
        Assertions.assertEquals(0, testErrors.size(), "There are errors in the comparison of internal and generated classes: \n" +
                String.join("\n", testErrors.stream().map(error ->
                                "[" + error.getInternalClassName() + " - " + error.getGeneratedClassName() + "] --> " + String.join(",",error.getFieldName()))
                        .toList()));
    }

    // Aggiungere qui le coppie di classi interne e generate da confrontare
    static Stream<Pair> classPairsProvider() {
        return Stream.of(
            new Pair(SenderAckCreationRequestDetailsInt.class, SenderAckCreationRequestDetails.class),
            new Pair(ValidateF24Int.class, ValidateF24RequestDetails.class),
            new Pair(ValidateNormalizeAddressDetailsInt.class, ValidateNormalizeAddressDetails.class),
            new Pair(ValidatedF24DetailInt.class, ValidatedF24Details.class),
            new Pair(NormalizedAddressDetailsInt.class, NormalizedAddressDetails.class),
            new Pair(NotificationRequestAcceptedDetailsInt.class, NotificationRequestAcceptedDetails.class),
            new Pair(ValidateF24Int.class, GenerateF24RequestDetails.class),
            new Pair(GeneratedF24DetailsInt.class, GeneratedF24Details.class),
            new Pair(SendCourtesyMessageDetailsInt.class, SendCourtesyMessageDetails.class),
            new Pair(GetAddressInfoDetailsInt.class, GetAddressInfoDetails.class),
            new Pair(PublicRegistryCallDetailsInt.class, PublicRegistryCallDetails.class),
            new Pair(PublicRegistryResponseDetailsInt.class, PublicRegistryResponseDetails.class),
            new Pair(ScheduleAnalogWorkflowDetailsInt.class, ScheduleAnalogWorkflowDetails.class),
            new Pair(ScheduleDigitalWorkflowDetailsInt.class, ScheduleDigitalWorkflowDetails.class),
            new Pair(PrepareDigitalDetailsInt.class, PrepareDigitalDetails.class),
            new Pair(SendDigitalDetailsInt.class, SendDigitalDetails.class),
            new Pair(SendDigitalFeedbackDetailsInt.class, SendDigitalFeedbackDetails.class),
            new Pair(SendDigitalProgressDetailsInt.class, SendDigitalProgressDetails.class),
            new Pair(RefinementDetailsInt.class, RefinementDetails.class),
            new Pair(ScheduleRefinementDetailsInt.class, ScheduleRefinementDetails.class),
            new Pair(DigitalDeliveryCreationRequestDetailsInt.class, DigitalDeliveryCreationRequestDetails.class),
            new Pair(DigitalSuccessWorkflowDetailsInt.class, DigitalSuccessWorkflowDetails.class),
            new Pair(DigitalFailureWorkflowDetailsInt.class, DigitalFailureWorkflowDetails.class),
            new Pair(AnalogSuccessWorkflowDetailsInt.class, AnalogSuccessWorkflowDetails.class),
            new Pair(AnalogFailureWorkflowDetailsInt.class, AnalogFailureWorkflowDetails.class),
            new Pair(CompletelyUnreachableCreationRequestDetailsInt.class, CompletelyUnreachableCreationRequestDetails.class),
            new Pair(BaseRegisteredLetterDetailsInt.class, BaseRegisteredLetterDetails.class),
            new Pair(SimpleRegisteredLetterDetailsInt.class, SimpleRegisteredLetterDetails.class),
            new Pair(NotificationViewedCreationRequestDetailsInt.class, NotificationViewedCreationRequestDetails.class),
            new Pair(NotificationViewedDetailsInt.class, NotificationViewedDetails.class),
            new Pair(BaseAnalogDetailsInt.class, BaseAnalogDetails.class),
            new Pair(PrepareAnalogDomicileFailureDetailsInt.class, PrepareAnalogDomicileFailureDetails.class),
            new Pair(SendAnalogDetailsInt.class, SendAnalogDetails.class),
            new Pair(SendAnalogProgressDetailsInt.class, SendAnalogProgressDetails.class),
            new Pair(SendAnalogFeedbackDetailsInt.class, SendAnalogFeedbackDetails.class),
            new Pair(NotificationPaidDetailsInt.class, NotificationPaidDetails.class),
            new Pair(CompletelyUnreachableDetailsInt.class, CompletelyUnreachableDetails.class),
            new Pair(RequestRefusedDetailsInt.class, RequestRefusedDetails.class),
            new Pair(AarCreationRequestDetailsInt.class, AarCreationRequestDetails.class),
            new Pair(AarGenerationDetailsInt.class, AarGenerationDetails.class),
            new Pair(NotHandledDetailsInt.class, NotHandledDetails.class),
            new Pair(SimpleRegisteredLetterProgressDetailsInt.class, SimpleRegisteredLetterProgressDetails.class),
            new Pair(ProbableDateAnalogWorkflowDetailsInt.class, ProbableDateAnalogWorkflowDetails.class),
            new Pair(NotificationCancellationRequestDetailsInt.class, NotificationCancellationRequestDetails.class),
            new Pair(NotificationCancelledDetailsInt.class, NotificationCancelledDetails.class),
            new Pair(NotificationRADDRetrievedDetailsInt.class, NotificationRADDRetrievedDetails.class),
            new Pair(NotificationCancelledDocumentCreationRequestDetailsInt.class, NotificationCancelledDocumentCreationRequestDetails.class),
            new Pair(AnalogWorfklowRecipientDeceasedDetailsInt.class, AnalogWorkflowRecipientDeceasedDetails.class),
            new Pair(PublicRegistryValidationCallDetailsInt.class, PublicRegistryValidationCallDetails.class),
            new Pair(PublicRegistryValidationResponseDetailsInt.class, PublicRegistryValidationResponseDetails.class),
            new Pair(SendAnalogTimeoutCreationRequestDetailsInt.class, SendAnalogTimeoutCreationRequestDetails.class),
            new Pair(SendAnalogTimeoutDetailsInt.class, SendAnalogTimeoutDetails.class),
            new Pair(AnalogFailureWorkflowTimeoutDetailsInt.class, AnalogFailureWorkflowTimeoutDetails.class)
        );
    }

    private void testClassPair(Class<? extends TimelineElementDetailsInt> internalClass, Class<? extends TimelineElementDetails> generatedClass) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        counter++;
        TestErrors error = compareFields(internalClass, generatedClass);
        if(!CollectionUtils.isEmpty(error.getFieldName())) {
            testErrors.add(error);
        }
    }

    private TestErrors compareFields(Class<? extends TimelineElementDetailsInt> internalClass, Class<? extends TimelineElementDetails> generatedClass) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {

        Object internal = internalClass.getDeclaredConstructor().newInstance();
        Object generated = generatedClass.getDeclaredConstructor().newInstance();

        Set<String> fields1 = getFieldsInfo(internal.getClass());
        Set<String> fields2 = getFieldsInfo(generated.getClass());

        Set<String> missingInFirst = new HashSet<>(fields2);
        missingInFirst.removeAll(fields1);

        Set<String> missingInSecond = new HashSet<>(fields1);
        missingInSecond.removeAll(fields2);

        TestErrors error = TestErrors.builder()
                .internalClassName(internal.getClass().getSimpleName())
                .generatedClassName(generated.getClass().getSimpleName())
                .build();

        if(!CollectionUtils.isEmpty(missingInFirst)){
            error.setFieldName(missingInFirst);
        }
        if(!CollectionUtils.isEmpty(missingInSecond)) {
            error.setFieldName(missingInSecond);
        }
        return error;
    }

    private Set<String> getFieldsInfo(Class<?> clazz) {
        Set<String> fieldsInfo = new HashSet<>();

        List<Field> allFields = new ArrayList<>();
        Class<?> currentClass = clazz;

        while (currentClass != null) {
            Field[] fields = currentClass.getDeclaredFields();
            allFields.addAll(Arrays.asList(fields));
            currentClass = currentClass.getSuperclass();
        }

        for (Field field : allFields) {
            if (!Modifier.isStatic(field.getModifiers()) &&
                    !Modifier.isFinal(field.getModifiers()) &&
                    !field.isSynthetic() &&
                        !field.getName().equalsIgnoreCase("categoryType")) {
                fieldsInfo.add(field.getName());
            }
        }

        return fieldsInfo;
    }

    @Builder
    @Getter
    @Setter
    public static class TestErrors {
        private Set<String> fieldName;
        private String internalClassName;
        private String generatedClassName;
    }


    public record Pair(Class<? extends TimelineElementDetailsInt> internalClass, Class<? extends TimelineElementDetails> generatedClass) {

    }

    private static class CheckInternalExternalDetailsTestException extends RuntimeException {
        public CheckInternalExternalDetailsTestException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}