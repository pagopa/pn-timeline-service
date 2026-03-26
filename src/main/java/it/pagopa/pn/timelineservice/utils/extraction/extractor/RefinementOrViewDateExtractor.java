package it.pagopa.pn.timelineservice.utils.extraction.extractor;

import it.pagopa.pn.timelineservice.dto.timeline.TimelineElementInternal;
import it.pagopa.pn.timelineservice.dto.timeline.details.NotificationViewedCreationRequestDetailsInt;
import it.pagopa.pn.timelineservice.dto.timeline.details.RefinementDetailsInt;
import it.pagopa.pn.timelineservice.dto.timeline.details.TimelineElementCategoryInt;
import it.pagopa.pn.timelineservice.dto.timeline.details.TimelineElementDetailsInt;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.util.Optional;

import static it.pagopa.pn.timelineservice.utils.extraction.extractor.ExtractorUtils.isRelatedToRecipient;

@Slf4j
public class RefinementOrViewDateExtractor implements TimelineDataExtractor<RefinementOrViewDateExtractor.Result> {
    public static final ExtractorKey<Result> KEY = ExtractorKey.of("refinementOrViewDate", Result.class);
    private final int recIndex;
    private Instant refinementDate;
    private Instant viewDate;
    private Result result;

    public RefinementOrViewDateExtractor(int recIndex) {
        this.recIndex = recIndex;
    }

    @Override
    public ExtractorKey<Result> getKey() {
        return KEY;
    }

    @Override
    public boolean process(TimelineElementInternal element) {
        if (!isRelatedToRecipient(element, recIndex)) {
            return false;
        }

        TimelineElementDetailsInt detailsInt = element.getDetails();
        TimelineElementCategoryInt category = element.getCategory();
        if (category == TimelineElementCategoryInt.NOTIFICATION_VIEWED_CREATION_REQUEST && detailsInt instanceof NotificationViewedCreationRequestDetailsInt viewedCreationRequestDetailsInt) {
            log.debug("RefinementOrViewDateExtractor - found NotificationViewedCreationRequestDetailsInt for iun={}", element.getIun());
            this.viewDate = viewedCreationRequestDetailsInt.getEventTimestamp();
        }

        if (category == TimelineElementCategoryInt.REFINEMENT && detailsInt instanceof RefinementDetailsInt refinementDetailsInt) {
            log.debug("RefinementOrViewDateExtractor - found RefinementDetailsInt for iun={}", element.getIun());
            this.refinementDate = refinementDetailsInt.getEventTimestamp();
        }

        return false; // ALWAYS Continue processing other elements
    }

    @Override
    public void postProcess() {
        Instant lowest = lowestDate(viewDate, refinementDate);
        if (lowest == null) {
            this.result = null;
        } else {
            this.result = new Result(lowest, refinementDate, viewDate);
        }
    }

    @Override
    public Optional<Result> getResult() {
        return Optional.ofNullable(this.result);
    }

    private Instant lowestDate(Instant viewDate, Instant refinementDate) {
        if (viewDate == null && refinementDate == null) {
            log.debug("Both viewDate and refinementDate are null");
            return null;
        }
        if (viewDate == null) {
            log.debug("viewDate is null, returning refinementDate");
            return refinementDate;
        }
        if (refinementDate == null) {
            log.debug("refinementDate is null, returning viewDate");
            return viewDate;
        }
        return viewDate.isBefore(refinementDate) ? viewDate : refinementDate;
    }

    @Getter
    public static class Result {
        private final Instant lowestDate;
        private final Instant refinementDate;
        private final Instant viewDate;

        public Result(Instant lowestDate, Instant refinementDate, Instant viewDate) {
            this.lowestDate = lowestDate;
            this.refinementDate = refinementDate;
            this.viewDate = viewDate;
        }
    }
}