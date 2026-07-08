package it.pagopa.pn.timelineservice.utils.extraction.extractor;

import it.pagopa.pn.timelineservice.dto.timeline.TimelineElementInternal;
import it.pagopa.pn.timelineservice.dto.timeline.details.legal.AarCreationRequestDetailsInt;
import it.pagopa.pn.timelineservice.dto.timeline.details.legal.ProbableDateAnalogWorkflowDetailsInt;
import it.pagopa.pn.timelineservice.dto.timeline.details.legal.ScheduleAnalogWorkflowDetailsInt;
import it.pagopa.pn.timelineservice.dto.timeline.details.TimelineElementCategoryInt;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Optional;

class SchedulingAnalogDateExtractorTest {

    @Test
    void returnsSchedulingDateFromScheduleAnalogWorkflowDetailsInt() {
        int recIndex = 0;
        Instant expectedDate = Instant.parse("2024-06-01T10:15:30.00Z");
        ScheduleAnalogWorkflowDetailsInt details = new ScheduleAnalogWorkflowDetailsInt();
        details.setSchedulingDate(expectedDate);

        TimelineElementInternal element = TimelineElementInternal.builder()
                .details(details)
                .category(TimelineElementCategoryInt.SCHEDULE_ANALOG_WORKFLOW)
                .build();

        SchedulingAnalogDateExtractor extractor = new SchedulingAnalogDateExtractor(recIndex);
        boolean processed = extractor.process(element);

        Assertions.assertTrue(processed);
        Assertions.assertEquals(Optional.of(expectedDate), extractor.getResult());
    }

    @Test
    void returnsSchedulingAnalogDateFromProbableDateAnalogWorkflowDetailsInt() {
        int recIndex = 0;
        Instant expectedDate = Instant.parse("2024-06-02T12:00:00.00Z");
        ProbableDateAnalogWorkflowDetailsInt details = new ProbableDateAnalogWorkflowDetailsInt();
        details.setSchedulingAnalogDate(expectedDate);

        TimelineElementInternal element = TimelineElementInternal.builder()
                .details(details)
                .category(TimelineElementCategoryInt.PROBABLE_SCHEDULING_ANALOG_DATE)
                .build();

        SchedulingAnalogDateExtractor extractor = new SchedulingAnalogDateExtractor(recIndex);
        boolean processed = extractor.process(element);

        Assertions.assertTrue(processed);
        Assertions.assertEquals(Optional.of(expectedDate), extractor.getResult());
    }

    @Test
    void returnsEmptyWhenElementIsNotRelatedToRecipient() {
        int recIndex = 4;
        ScheduleAnalogWorkflowDetailsInt details = new ScheduleAnalogWorkflowDetailsInt();
        details.setSchedulingDate(Instant.parse("2024-06-03T08:00:00.00Z"));

        TimelineElementInternal element = TimelineElementInternal.builder()
                .details(details)
                .build();

        SchedulingAnalogDateExtractor extractor = new SchedulingAnalogDateExtractor(recIndex);
        boolean processed = extractor.process(element);

        Assertions.assertFalse(processed);
        Assertions.assertEquals(Optional.empty(), extractor.getResult());
    }

    @Test
    void returnsEmptyWhenDetailsTypeIsNotRecognized() {
        int recIndex = 0;
        AarCreationRequestDetailsInt details = new AarCreationRequestDetailsInt();

        TimelineElementInternal element = TimelineElementInternal.builder()
                .details(details)
                .build();

        SchedulingAnalogDateExtractor extractor = new SchedulingAnalogDateExtractor(recIndex);
        boolean processed = extractor.process(element);

        Assertions.assertFalse(processed);
        Assertions.assertEquals(Optional.empty(), extractor.getResult());
    }
}
