package it.pagopa.pn.timelineservice.utils.extraction.extractor;

import it.pagopa.pn.timelineservice.dto.timeline.TimelineElementInternal;
import it.pagopa.pn.timelineservice.dto.timeline.details.NotificationViewedCreationRequestDetailsInt;
import it.pagopa.pn.timelineservice.dto.timeline.details.RefinementDetailsInt;
import it.pagopa.pn.timelineservice.dto.timeline.details.TimelineElementDetailsInt;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Optional;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class RefinementOrViewDateExtractorTest {

    private TimelineElementDetailsInt timelineElementDetailsInt;
    private ExtractorUtils extractorUtils;

    @BeforeEach
    void setUp() {
        timelineElementDetailsInt = mock(TimelineElementDetailsInt.class);
        extractorUtils = mock(ExtractorUtils.class);
    }

    @Test
    void returnsViewDateWhenOnlyViewDatePresent() {
        int recIndex = 1;
        Instant viewDate = Instant.parse("2024-06-01T10:00:00Z");

        NotificationViewedCreationRequestDetailsInt details = mock(NotificationViewedCreationRequestDetailsInt.class);
        when(details.getEventTimestamp()).thenReturn(viewDate);
        when(details.getRecIndex()).thenReturn(recIndex);

        TimelineElementInternal element = TimelineElementInternal.builder()
                .details(details)
                .build();

        RefinementOrViewDateExtractor extractor = new RefinementOrViewDateExtractor(recIndex);
        extractor.process(element);
        extractor.postProcess();

        Optional<Instant> result = extractor.getResult();
        Assertions.assertTrue(result.isPresent());
        Assertions.assertEquals(viewDate, result.get());
    }

    @Test
    void returnsRefinementDateWhenOnlyRefinementDatePresent() {
        int recIndex = 1;
        Instant refinementDate = Instant.parse("2024-06-02T10:00:00Z");

        RefinementDetailsInt details = mock(RefinementDetailsInt.class);
        when(details.getEventTimestamp()).thenReturn(refinementDate);
        when(details.getRecIndex()).thenReturn(recIndex);

        TimelineElementInternal element = TimelineElementInternal.builder()
                .details(details)
                .build();

        RefinementOrViewDateExtractor extractor = new RefinementOrViewDateExtractor(recIndex);
        extractor.process(element);
        extractor.postProcess();

        Optional<Instant> result = extractor.getResult();
        Assertions.assertTrue(result.isPresent());
        Assertions.assertEquals(refinementDate, result.get());
    }

    @Test
    void returnsLowestDateWhenBothDatesPresent() {
        int recIndex = 1;
        Instant viewDate = Instant.parse("2024-06-01T10:00:00Z");
        Instant refinementDate = Instant.parse("2024-06-02T10:00:00Z");

        NotificationViewedCreationRequestDetailsInt viewDetails = mock(NotificationViewedCreationRequestDetailsInt.class);
        when(viewDetails.getEventTimestamp()).thenReturn(viewDate);
        when(viewDetails.getRecIndex()).thenReturn(recIndex);

        RefinementDetailsInt refinementDetails = mock(RefinementDetailsInt.class);
        when(refinementDetails.getEventTimestamp()).thenReturn(refinementDate);

        TimelineElementInternal viewElement = TimelineElementInternal.builder()
                .details(viewDetails)
                .build();
        TimelineElementInternal refinementElement = TimelineElementInternal.builder()
                .details(refinementDetails)
                .build();

        RefinementOrViewDateExtractor extractor = new RefinementOrViewDateExtractor(recIndex);
        extractor.process(viewElement);
        extractor.process(refinementElement);
        extractor.postProcess();

        Optional<Instant> result = extractor.getResult();
        Assertions.assertTrue(result.isPresent());
        Assertions.assertEquals(viewDate, result.get());
    }

    @Test
    void returnsEmptyWhenNoDatesPresent() {
        int recIndex = 1;

        TimelineElementInternal element = TimelineElementInternal.builder()
                .details(timelineElementDetailsInt)
                .build();

        RefinementOrViewDateExtractor extractor = new RefinementOrViewDateExtractor(recIndex);
        extractor.process(element);
        extractor.postProcess();

        Optional<Instant> result = extractor.getResult();
        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    void doesNotProcessIfNotRelatedToRecipient() {
        int recIndex = 1;
        NotificationViewedCreationRequestDetailsInt details = mock(NotificationViewedCreationRequestDetailsInt.class);

        TimelineElementInternal element = TimelineElementInternal.builder()
                .details(details)
                .build();

        RefinementOrViewDateExtractor extractor = new RefinementOrViewDateExtractor(recIndex);
        boolean processed = extractor.process(element);

        Assertions.assertFalse(processed);
        extractor.postProcess();
        Assertions.assertTrue(extractor.getResult().isEmpty());
    }


}
