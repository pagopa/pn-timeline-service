package it.pagopa.pn.timelineservice.utils.extraction.extractor;

import it.pagopa.pn.timelineservice.dto.timeline.TimelineElementInternal;
import it.pagopa.pn.timelineservice.dto.timeline.details.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Optional;

public class DeliveryModeExtractorTest {

    @Test
    void returnsDigitalWhenSendDigitalDetailsIntIsPresentAndRelatedToRecipient() {
        int recIndex = 0;
        TimelineElementInternal element = TimelineElementInternal.builder()
                .details(new SendDigitalDetailsInt())
                .build();

        DeliveryModeExtractor extractor = new DeliveryModeExtractor(recIndex);
        extractor.process(element);
        extractor.postProcess();

        Assertions.assertEquals(Optional.of(DeliveryModeInt.DIGITAL), extractor.getResult());
    }

    @Test
    void returnsAnalogWhenScheduleAnalogWorkflowDetailsIntIsPresentAndRelatedToRecipient() {
        int recIndex = 0;
        TimelineElementInternal element = TimelineElementInternal.builder()
                .details(new ScheduleAnalogWorkflowDetailsInt())
                .build();

        DeliveryModeExtractor extractor = new DeliveryModeExtractor(recIndex);
        extractor.process(element);
        extractor.postProcess();

        Assertions.assertEquals(Optional.of(DeliveryModeInt.ANALOG), extractor.getResult());
    }

    @Test
    void returnsAnalogWhenProbableDateAnalogWorkflowDetailsIntIsPresentAndRelatedToRecipient() {
        int recIndex = 0;
        TimelineElementInternal element = TimelineElementInternal.builder()
                .details(new ProbableDateAnalogWorkflowDetailsInt())
                .build();

        DeliveryModeExtractor extractor = new DeliveryModeExtractor(recIndex);
        extractor.process(element);
        extractor.postProcess();

        Assertions.assertEquals(Optional.of(DeliveryModeInt.ANALOG), extractor.getResult());
    }

    @Test
    void returnsDigitalWhenBothDigitalAndAnalogDetailsArePresentAndRelatedToRecipient() {
        int recIndex = 0;
        TimelineElementInternal digitalElement = TimelineElementInternal.builder()
                .details(new SendDigitalDetailsInt())
                .build();
        TimelineElementInternal analogElement = TimelineElementInternal.builder()
                .details(new ProbableDateAnalogWorkflowDetailsInt())
                .build();

        DeliveryModeExtractor extractor = new DeliveryModeExtractor(recIndex);
        extractor.process(analogElement);
        extractor.process(digitalElement);
        extractor.postProcess();

        Assertions.assertEquals(Optional.of(DeliveryModeInt.DIGITAL), extractor.getResult());
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

        Assertions.assertEquals(Optional.of(DeliveryModeInt.UNKNOWN), extractor.getResult());
    }

    @Test
    void returnsUnknownWhenDetailsTypeIsNotRecognized() {
        int recIndex = 0;
        TimelineElementInternal element = TimelineElementInternal.builder()
                .details(new AarCreationRequestDetailsInt())
                .build();

        DeliveryModeExtractor extractor = new DeliveryModeExtractor(recIndex);
        extractor.process(element);
        extractor.postProcess();

        Assertions.assertEquals(Optional.of(DeliveryModeInt.UNKNOWN), extractor.getResult());
    }
}
