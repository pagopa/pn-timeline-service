package it.pagopa.pn.timelineservice.utils.extraction.extractor;

import it.pagopa.pn.timelineservice.dto.timeline.TimelineElementInternal;
import it.pagopa.pn.timelineservice.dto.timeline.details.legal.TimelineElementCategoryInt;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

@Slf4j
public class IsAcceptedExtractor implements TimelineDataExtractor<Boolean> {
    public static final ExtractorKey<Boolean> KEY = ExtractorKey.of("isAccepted", Boolean.class);
    private Boolean result = false;

    @Override
    public ExtractorKey<Boolean> getKey() {
        return KEY;
    }

    @Override
    public boolean process(TimelineElementInternal element) {
        TimelineElementCategoryInt category = element.getCategory();

        if (category == TimelineElementCategoryInt.REQUEST_ACCEPTED) {
            log.debug("isAcceptedExtractor - RequestAcceptedDetailsInt found for iun: {}", element.getIun());
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
    public Optional<Boolean> getResult() {
        return Optional.ofNullable(result);
    }
}
