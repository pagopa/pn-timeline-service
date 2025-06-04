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

public class CheckInternalExternalDetailsTest {

    private static List<TestErrors> testErrors = new ArrayList<>();
    int counter = 0;

    @Test
    void testAllDetailClasses() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        List<? extends Class<? extends TimelineElementDetailsInt>> internalImplementations = Arrays.stream(TimelineElementCategoryInt.values())
                .map(TimelineElementCategoryInt::getDetailsJavaClass)
                .toList();

        testClassPair(SenderAckCreationRequestDetailsInt.class, SenderAckCreationRequestDetails.class);
        testClassPair(ValidateF24Int.class, ValidateF24RequestDetails.class);
        testClassPair(ValidateNormalizeAddressDetailsInt.class, ValidateNormalizeAddressDetails.class);
        testClassPair(ValidatedF24DetailInt.class, ValidatedF24Details.class);
        testClassPair(NormalizedAddressDetailsInt.class, NormalizedAddressDetails.class);
        testClassPair(NotificationRequestAcceptedDetailsInt.class, NotificationRequestAcceptedDetails.class);
        testClassPair(ValidateF24Int.class, GenerateF24RequestDetails.class);
        testClassPair(GeneratedF24DetailsInt.class, GeneratedF24Details.class);
        testClassPair(SendCourtesyMessageDetailsInt.class, SendCourtesyMessageDetails.class);
        testClassPair(GetAddressInfoDetailsInt.class, GetAddressInfoDetails.class);
        testClassPair(PublicRegistryCallDetailsInt.class, PublicRegistryCallDetails.class);
        testClassPair(PublicRegistryResponseDetailsInt.class, PublicRegistryResponseDetails.class);
        testClassPair(ScheduleAnalogWorkflowDetailsInt.class, ScheduleAnalogWorkflowDetails.class);
        testClassPair(ScheduleDigitalWorkflowDetailsInt.class, ScheduleDigitalWorkflowDetails.class);
        testClassPair(PrepareDigitalDetailsInt.class, PrepareDigitalDetails.class);
        testClassPair(SendDigitalDetailsInt.class, SendDigitalDetails.class);
        testClassPair(SendDigitalFeedbackDetailsInt.class, SendDigitalFeedbackDetails.class);
        testClassPair(SendDigitalProgressDetailsInt.class, SendDigitalProgressDetails.class);
        testClassPair(RefinementDetailsInt.class, RefinementDetails.class);
        testClassPair(ScheduleRefinementDetailsInt.class, ScheduleRefinementDetails.class);
        testClassPair(DigitalDeliveryCreationRequestDetailsInt.class, DigitalDeliveryCreationRequestDetails.class);
        testClassPair(DigitalSuccessWorkflowDetailsInt.class, DigitalSuccessWorkflowDetails.class);
        testClassPair(DigitalFailureWorkflowDetailsInt.class, DigitalFailureWorkflowDetails.class);
        testClassPair(AnalogSuccessWorkflowDetailsInt.class, AnalogSuccessWorkflowDetails.class);
        testClassPair(AnalogFailureWorkflowDetailsInt.class, AnalogFailureWorkflowDetails.class);
        testClassPair(CompletelyUnreachableCreationRequestDetailsInt.class, CompletelyUnreachableCreationRequestDetails.class);
        testClassPair(BaseRegisteredLetterDetailsInt.class, BaseRegisteredLetterDetails.class);
        testClassPair(SimpleRegisteredLetterDetailsInt.class, SimpleRegisteredLetterDetails.class);
        testClassPair(NotificationViewedCreationRequestDetailsInt.class, NotificationViewedCreationRequestDetails.class);
        testClassPair(NotificationViewedDetailsInt.class, NotificationViewedDetails.class);
        testClassPair(BaseAnalogDetailsInt.class, BaseAnalogDetails.class);
        testClassPair(PrepareAnalogDomicileFailureDetailsInt.class, PrepareAnalogDomicileFailureDetails.class);
        testClassPair(SendAnalogDetailsInt.class, SendAnalogDetails.class);
        testClassPair(SendAnalogProgressDetailsInt.class, SendAnalogProgressDetails.class);
        testClassPair(SendAnalogFeedbackDetailsInt.class, SendAnalogFeedbackDetails.class);
        testClassPair(NotificationPaidDetailsInt.class, NotificationPaidDetails.class);
        testClassPair(CompletelyUnreachableDetailsInt.class, CompletelyUnreachableDetails.class);
        testClassPair(RequestRefusedDetailsInt.class, RequestRefusedDetails.class);
        testClassPair(AarCreationRequestDetailsInt.class, AarCreationRequestDetails.class);
        testClassPair(AarGenerationDetailsInt.class, AarGenerationDetails.class);
        testClassPair(NotHandledDetailsInt.class, NotHandledDetails.class);
        testClassPair(SimpleRegisteredLetterProgressDetailsInt.class, SimpleRegisteredLetterProgressDetails.class);
        testClassPair(ProbableDateAnalogWorkflowDetailsInt.class, ProbableDateAnalogWorkflowDetails.class);
        testClassPair(NotificationCancellationRequestDetailsInt.class, NotificationCancellationRequestDetails.class);
        testClassPair(NotificationCancelledDetailsInt.class, NotificationCancelledDetails.class);
        testClassPair(NotificationRADDRetrievedDetailsInt.class, NotificationRADDRetrievedDetails.class);
        testClassPair(NotificationCancelledDocumentCreationRequestDetailsInt.class, NotificationCancelledDocumentCreationRequestDetails.class);
        testClassPair(AnalogWorfklowRecipientDeceasedDetailsInt.class, AnalogWorkflowRecipientDeceasedDetails.class);
        testClassPair(PublicRegistryValidationCallDetailsInt.class, PublicRegistryValidationCallDetails.class);
        testClassPair(PublicRegistryValidationResponseDetailsInt.class, PublicRegistryValidationResponseDetails.class);

        Assertions.assertEquals(counter, internalImplementations.size());
        Assertions.assertEquals(0, testErrors.size(), "There are errors in the comparison of internal and generated classes: \n" +
                String.join("\n", testErrors.stream().map(error ->
                        "[" + error.getInternalClassName() + " - " + error.getGeneratedClassName() + "] --> " + String.join(",",error.getFieldName()))
                        .toList()));

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
}