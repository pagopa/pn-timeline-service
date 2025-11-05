package it.pagopa.pn.timelineservice.utils.extraction.extractor;

import it.pagopa.pn.timelineservice.dto.timeline.TimelineElementInternal;
import it.pagopa.pn.timelineservice.dto.timeline.details.TimelineElementCategoryInt;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class IsCancelledExtractor implements  TimelineDataExtractor<Boolean> {
    public static final ExtractorKey<Boolean> KEY = ExtractorKey.of("isCancelled", Boolean.class);
    private Boolean result = false;

    @Override
    public ExtractorKey<Boolean> getKey() {
        return KEY;
    }

    @Override
    public boolean process(TimelineElementInternal element) {
        TimelineElementCategoryInt category = element.getCategory();

        if (category == TimelineElementCategoryInt.NOTIFICATION_CANCELLATION_REQUEST) {
            log.debug("IsCancelledExtractor - NotificationCancellationRequestDetailsInt found for iun: {}", element.getIun());
            this.result = true;
            return true;
        }

        return false;
    }

    @Override
    public void postProcess() {
        // nothing to do
    }

    @Override
    public java.util.Optional<Boolean> getResult() {
        return java.util.Optional.ofNullable(result);
    }
}
