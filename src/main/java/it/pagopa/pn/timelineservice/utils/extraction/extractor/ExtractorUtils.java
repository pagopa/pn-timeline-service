package it.pagopa.pn.timelineservice.utils.extraction.extractor;

import it.pagopa.pn.timelineservice.dto.timeline.TimelineElementInternal;
import it.pagopa.pn.timelineservice.dto.timeline.details.common.RecipientRelatedTimelineElementDetails;
import it.pagopa.pn.timelineservice.dto.timeline.details.common.TimelineElementDetailsInt;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ExtractorUtils {
    private ExtractorUtils() {
        // private constructor to prevent instantiation
    }

    public static boolean isRelatedToRecipient(TimelineElementInternal timelineElement, int recIndex) {
        TimelineElementDetailsInt detailsInt = timelineElement.getDetails();
        return detailsInt instanceof RecipientRelatedTimelineElementDetails recipientRelatedTimelineElementDetails &&
                recipientRelatedTimelineElementDetails.getRecIndex() == recIndex;
    }
}
