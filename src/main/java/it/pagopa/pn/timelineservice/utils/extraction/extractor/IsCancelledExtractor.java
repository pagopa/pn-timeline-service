package it.pagopa.pn.timelineservice.utils.extraction.extractor;

import it.pagopa.pn.timelineservice.dto.timeline.TimelineElementInternal;
import it.pagopa.pn.timelineservice.dto.timeline.details.NotificationCancellationRequestDetailsInt;
import it.pagopa.pn.timelineservice.dto.timeline.details.TimelineElementDetailsInt;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class IsCancelledExtractor implements  TimelineDataExtractor<Boolean> {
    public final static ExtractorKey<Boolean> KEY = ExtractorKey.of("isCancelled", Boolean.class);
    private Boolean result = false;

    @Override
    public ExtractorKey<Boolean> getKey() {
        return KEY;
    }

    @Override
    public boolean process(TimelineElementInternal element) {
        TimelineElementDetailsInt detailsInt = element.getDetails();

        if (detailsInt instanceof NotificationCancellationRequestDetailsInt) {
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
