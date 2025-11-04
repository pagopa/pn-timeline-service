package it.pagopa.pn.timelineservice.utils.extraction.extractor;

import it.pagopa.pn.timelineservice.dto.timeline.TimelineElementInternal;
import it.pagopa.pn.timelineservice.dto.timeline.details.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Optional;

class DeliveryModeExtractorTest {

    @Test
    void returnsDigitalWhenSendDigitalDomicileIsPresentAndRelatedToRecipient() {
        int recIndex = 0;
        var details = new SendDigitalDetailsInt();
        details.setRecIndex(recIndex);
        TimelineElementInternal element = TimelineElementInternal.builder()
                .details(details)
                .category(TimelineElementCategoryInt.SEND_DIGITAL_DOMICILE)
                .build();

        DeliveryModeExtractor extractor = new DeliveryModeExtractor(recIndex);
        extractor.process(element);
        extractor.postProcess();

        Assertions.assertEquals(Optional.of(ExtendedDeliveryModeInt.DIGITAL), extractor.getResult());
    }

    @Test
    void returnsAnalogWhenScheduleAnalogWorkflowIsPresentAndRelatedToRecipient() {
        int recIndex = 0;
        var details = new ScheduleAnalogWorkflowDetailsInt();
        details.setRecIndex(recIndex);
        TimelineElementInternal element = TimelineElementInternal.builder()
                .details(details)
                .category(TimelineElementCategoryInt.SCHEDULE_ANALOG_WORKFLOW)
                .build();

        DeliveryModeExtractor extractor = new DeliveryModeExtractor(recIndex);
        extractor.process(element);
        extractor.postProcess();

        Assertions.assertEquals(Optional.of(ExtendedDeliveryModeInt.ANALOG), extractor.getResult());
    }

    @Test
    void returnsAnalogWhenProbableDateAnalogWorkflowIsPresentAndRelatedToRecipient() {
        int recIndex = 0;
        var details = new ProbableDateAnalogWorkflowDetailsInt();
        details.setRecIndex(recIndex);
        TimelineElementInternal element = TimelineElementInternal.builder()
                .details(details)
                .category(TimelineElementCategoryInt.PROBABLE_SCHEDULING_ANALOG_DATE)
                .build();

        DeliveryModeExtractor extractor = new DeliveryModeExtractor(recIndex);
        extractor.process(element);
        extractor.postProcess();

        Assertions.assertEquals(Optional.of(ExtendedDeliveryModeInt.ANALOG), extractor.getResult());
    }

    @Test
    void returnsDigitalWhenBothDigitalAndAnalogArePresentAndRelatedToRecipient() {
        int recIndex = 0;
        var sendDigitalDetailsInt = new SendDigitalDetailsInt();
        sendDigitalDetailsInt.setRecIndex(recIndex);

        var scheduleAnalogDetailsInt = new ScheduleAnalogWorkflowDetailsInt();
        scheduleAnalogDetailsInt.setRecIndex(recIndex);
        TimelineElementInternal digitalElement = TimelineElementInternal.builder()
                .category(TimelineElementCategoryInt.SEND_DIGITAL_DOMICILE)
                .details(sendDigitalDetailsInt)
                .build();
        TimelineElementInternal analogElement = TimelineElementInternal.builder()
                .category(TimelineElementCategoryInt.PROBABLE_SCHEDULING_ANALOG_DATE)
                .details(scheduleAnalogDetailsInt)
                .build();

        DeliveryModeExtractor extractor = new DeliveryModeExtractor(recIndex);
        extractor.process(analogElement);
        extractor.process(digitalElement);
        extractor.postProcess();

        Assertions.assertEquals(Optional.of(ExtendedDeliveryModeInt.DIGITAL), extractor.getResult());
    }

    @Test
    void returnsUnknownWhenElementIsNotRelatedToRecipient() {
        int recIndex = 4;
        TimelineElementInternal element = TimelineElementInternal.builder()
                .details(new SendDigitalDetailsInt())
                .build();

        DeliveryModeExtractor extractor = new DeliveryModeExtractor(recIndex);
        extractor.process(element);
        extractor.postProcess();

        Assertions.assertEquals(Optional.of(ExtendedDeliveryModeInt.UNKNOWN), extractor.getResult());
    }
}
