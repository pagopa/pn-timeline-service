package it.pagopa.pn.timelineservice.utils.extraction.extractor;

import it.pagopa.pn.timelineservice.dto.timeline.TimelineElementInternal;
import it.pagopa.pn.timelineservice.dto.timeline.details.legal.NotificationCancellationRequestDetailsInt;
import it.pagopa.pn.timelineservice.dto.timeline.details.TimelineElementCategoryInt;
import it.pagopa.pn.timelineservice.dto.timeline.details.common.TimelineElementDetailsInt;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.mock;

class IsCancelledExtractorTest {

    @Test
    void processReturnsTrueWhenDetailsAreNotificationCancellationRequestDetailsInt() {
        NotificationCancellationRequestDetailsInt details = mock(NotificationCancellationRequestDetailsInt.class);
        TimelineElementInternal element = TimelineElementInternal.builder()
                .details(details)
                .category(TimelineElementCategoryInt.NOTIFICATION_CANCELLATION_REQUEST)
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
