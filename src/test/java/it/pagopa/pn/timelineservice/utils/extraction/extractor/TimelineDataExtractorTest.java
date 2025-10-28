package it.pagopa.pn.timelineservice.utils.extraction.extractor;

import it.pagopa.pn.timelineservice.dto.timeline.TimelineElementInternal;
import it.pagopa.pn.timelineservice.dto.timeline.details.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Optional;

class TimelineDataExtractorTest {

    @Test
    void extractsDigitalDeliveryModeForRecipient() {
        int recIndex = 0;
        TimelineDataExtractor<DeliveryModeInt> extractor = new DeliveryModeExtractor(recIndex);
        SendDigitalDetailsInt details = new SendDigitalDetailsInt();
        details.setRecIndex(recIndex);

        TimelineElementInternal digitalElement = TimelineElementInternal.builder()
                .details(details)
                .build();

        extractor.process(digitalElement);
        extractor.postProcess();

        Assertions.assertEquals(Optional.of(DeliveryModeInt.DIGITAL), extractor.getResult());
    }

    @Test
    void extractsAnalogDeliveryModeForRecipient() {
        int recIndex = 0;
        TimelineDataExtractor<DeliveryModeInt> extractor = new DeliveryModeExtractor(recIndex);
        ScheduleAnalogWorkflowDetailsInt details = new ScheduleAnalogWorkflowDetailsInt();
        details.setRecIndex(recIndex);

        TimelineElementInternal analogElement = TimelineElementInternal.builder()
                .details(details)
                .build();

        extractor.process(analogElement);
        extractor.postProcess();

        Assertions.assertEquals(Optional.of(DeliveryModeInt.ANALOG), extractor.getResult());
    }

    @Test
    void returnsUnknownDeliveryModeWhenNoRelevantDetails() {
        int recIndex = 0;
        TimelineDataExtractor<DeliveryModeInt> extractor = new DeliveryModeExtractor(recIndex);
        AarCreationRequestDetailsInt details = new AarCreationRequestDetailsInt();
        details.setRecIndex(recIndex);
        TimelineElementInternal unrelatedElement = TimelineElementInternal.builder()
                .details(details)
                .build();

        extractor.process(unrelatedElement);
        extractor.postProcess();

        Assertions.assertEquals(Optional.of(DeliveryModeInt.UNKNOWN), extractor.getResult());
    }

    @Test
    void extractsSchedulingDateForRecipient() {
        int recIndex = 0;
        TimelineDataExtractor<Instant> extractor = new SchedulingAnalogDateExtractor(recIndex);
        Instant expectedDate = Instant.parse("2024-06-01T10:00:00Z");
        ScheduleAnalogWorkflowDetailsInt details = new ScheduleAnalogWorkflowDetailsInt();
        details.setSchedulingDate(expectedDate);

        TimelineElementInternal element = TimelineElementInternal.builder()
                .details(details)
                .build();

        extractor.process(element);
        extractor.postProcess();

        Assertions.assertEquals(Optional.of(expectedDate), extractor.getResult());
    }

    @Test
    void returnsEmptySchedulingDateWhenNoAnalogDetails() {
        int recIndex = 0;
        TimelineDataExtractor<Instant> extractor = new SchedulingAnalogDateExtractor(recIndex);
        SendDigitalDetailsInt details = new SendDigitalDetailsInt();
        TimelineElementInternal element = TimelineElementInternal.builder()
                .details(details)
                .build();

        extractor.process(element);
        extractor.postProcess();

        Assertions.assertEquals(Optional.empty(), extractor.getResult());
    }

    @Test
    void extractsRefinementDetailsForRecipient() {
        int recIndex = 0;
        TimelineDataExtractor<Instant> extractor = new RefinementOrViewDateExtractor(recIndex);
        Instant expectedDate = Instant.parse("2024-06-01T10:00:00Z");
        RefinementDetailsInt details = new RefinementDetailsInt();
        details.setEventTimestamp(expectedDate);
        TimelineElementInternal element = TimelineElementInternal.builder()
                .details(details)
                .build();

        extractor.process(element);
        extractor.postProcess();

        Assertions.assertEquals(Optional.of(expectedDate), extractor.getResult());
    }

    @Test
    void extractsNotificationViewedCreationRequestDetailsForRecipient() {
        int recIndex = 0;
        TimelineDataExtractor<Instant> extractor = new RefinementOrViewDateExtractor(recIndex);
        Instant expectedDate = Instant.parse("2024-06-01T10:00:00Z");
        NotificationViewedCreationRequestDetailsInt details = new NotificationViewedCreationRequestDetailsInt();
        details.setEventTimestamp(expectedDate);
        TimelineElementInternal element = TimelineElementInternal.builder()
                .details(details)
                .build();

        extractor.process(element);
        extractor.postProcess();

        Assertions.assertEquals(Optional.of(expectedDate), extractor.getResult());
    }

    @Test
    void extractsNotificationCancellationRequestDetailsForRecipient() {
        TimelineDataExtractor<Boolean> extractor = new IsCancelledExtractor();
        NotificationCancellationRequestDetailsInt details = new NotificationCancellationRequestDetailsInt();

        TimelineElementInternal element = TimelineElementInternal.builder()
                .details(details)
                .build();

        extractor.process(element);
        extractor.postProcess();

        Assertions.assertEquals(Optional.of(true), extractor.getResult());
    }
}
