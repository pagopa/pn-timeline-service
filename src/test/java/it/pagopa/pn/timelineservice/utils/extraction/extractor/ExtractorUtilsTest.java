package it.pagopa.pn.timelineservice.utils.extraction.extractor;

import it.pagopa.pn.timelineservice.dto.timeline.TimelineElementInternal;
import it.pagopa.pn.timelineservice.dto.timeline.details.common.RecipientRelatedTimelineElementDetails;
import it.pagopa.pn.timelineservice.dto.timeline.details.common.TimelineElementDetailsInt;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ExtractorUtilsTest {

    private TimelineElementDetailsInt details;
    private RecipientRelatedTimelineElementDetails recipientRelatedTimelineElementDetails;

    @BeforeEach
    void setUp() {
        details = mock(TimelineElementDetailsInt.class);
        recipientRelatedTimelineElementDetails = mock(RecipientRelatedTimelineElementDetails.class);
    }

    @Test
    void isRelatedToRecipientReturnsTrueWhenRecIndexMatchesAndTypeIsCorrect() {
        int recIndex = 0;
        when(recipientRelatedTimelineElementDetails.getRecIndex()).thenReturn(recIndex);

        TimelineElementInternal element = TimelineElementInternal.builder()
                .details(recipientRelatedTimelineElementDetails)
                .build();

        boolean result = ExtractorUtils.isRelatedToRecipient(element, recIndex);
        Assertions.assertTrue(result);
    }

    @Test
    void isRelatedToRecipientReturnsFalseWhenRecIndexDoesNotMatch() {
        int recIndex = 2;

        when(recipientRelatedTimelineElementDetails.getRecIndex()).thenReturn(0);

        TimelineElementInternal element = TimelineElementInternal.builder()
                .details(recipientRelatedTimelineElementDetails)
                .build();

        boolean result = ExtractorUtils.isRelatedToRecipient(element, recIndex);
        Assertions.assertFalse(result);
    }

    @Test
    void isRelatedToRecipientReturnsFalseWhenDetailsAreNotRecipientRelated() {
        TimelineElementInternal element = TimelineElementInternal.builder()
                .details(details)
                .build();

        boolean result = ExtractorUtils.isRelatedToRecipient(element, 0);
        Assertions.assertFalse(result);
    }
}
