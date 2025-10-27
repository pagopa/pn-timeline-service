package it.pagopa.pn.timelineservice.utils.extraction.extractor;

import it.pagopa.pn.timelineservice.dto.timeline.TimelineElementInternal;
import it.pagopa.pn.timelineservice.dto.timeline.details.NotificationCancellationRequestDetailsInt;
import it.pagopa.pn.timelineservice.dto.timeline.details.TimelineElementDetailsInt;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.mock;

public class IsCancelledExtractorTest {

    @Test
    void processReturnsTrueWhenDetailsAreNotificationCancellationRequestDetailsInt() {
        NotificationCancellationRequestDetailsInt details = mock(NotificationCancellationRequestDetailsInt.class);
        TimelineElementInternal element = TimelineElementInternal.builder()
                .details(details)
                .iun("testIun")
                .build();

        IsCancelledExtractor extractor = new IsCancelledExtractor();
        boolean processed = extractor.process(element);

        Assertions.assertTrue(processed);
        Assertions.assertEquals(Boolean.TRUE, extractor.getResult().orElse(false));
    }

    @Test
    void processReturnsFalseWhenDetailsAreNotNotificationCancellationRequestDetailsInt() {
        TimelineElementDetailsInt details = mock(TimelineElementDetailsInt.class);
        TimelineElementInternal element = TimelineElementInternal.builder()
                .details(details)
                .iun("testIun")
                .build();

        IsCancelledExtractor extractor = new IsCancelledExtractor();
        boolean processed = extractor.process(element);

        Assertions.assertFalse(processed);
        Assertions.assertEquals(Boolean.FALSE, extractor.getResult().orElse(true));
    }
}
