package it.pagopa.pn.timelineservice.utils.extraction.extractor;

import it.pagopa.pn.timelineservice.dto.timeline.TimelineElementInternal;
import it.pagopa.pn.timelineservice.dto.timeline.details.NotificationViewedCreationRequestDetailsInt;
import it.pagopa.pn.timelineservice.dto.timeline.details.RefinementDetailsInt;
import it.pagopa.pn.timelineservice.dto.timeline.details.TimelineElementDetailsInt;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.util.Optional;

import static it.pagopa.pn.timelineservice.utils.extraction.extractor.ExtractorUtils.isRelatedToRecipient;

@Slf4j
public class RefinementOrViewDateExtractor implements TimelineDataExtractor<Instant> {
    public final static ExtractorKey<Instant> KEY = ExtractorKey.of("refinementOrViewDate", Instant.class);
    private final int recIndex;
    private Instant result;
    private Instant refinementDate;
    private Instant viewDate;


    public RefinementOrViewDateExtractor(int recIndex) {
        this.recIndex = recIndex;
    }

    @Override
    public ExtractorKey<Instant> getKey() {
        return KEY;
    }

    @Override
    public boolean process(TimelineElementInternal element) {
        if (!isRelatedToRecipient(element, recIndex)) {
            return false;
        }

        TimelineElementDetailsInt detailsInt = element.getDetails();
        if(detailsInt instanceof NotificationViewedCreationRequestDetailsInt viewedCreationRequestDetailsInt) {
            log.debug("RefinementOrViewDateExtractor - found NotificationViewedCreationRequestDetailsInt for iun={}", element.getIun());
            this.viewDate = viewedCreationRequestDetailsInt.getEventTimestamp();
        }

        if(detailsInt instanceof RefinementDetailsInt refinementDetailsInt) {
            log.debug("RefinementOrViewDateExtractor - found RefinementDetailsInt for iun={}", element.getIun());
            this.refinementDate = refinementDetailsInt.getEventTimestamp();
        }

        return false; // ALWAYS Continue processing other elements
    }

    @Override
    public void postProcess() {
        this.result = lowestDate(viewDate, refinementDate);
    }

    @Override
    public Optional<Instant> getResult() {
        return Optional.ofNullable(this.result);
    }

    private Instant lowestDate(Instant viewDate, Instant refinementDate) {
        if(viewDate == null && refinementDate == null) {
            log.debug("Both viewDate and refinementDate are null");
            return null;
        }

        if(viewDate == null) {
            log.debug("viewDate is null, returning refinementDate");
            return refinementDate;
        }
        if(refinementDate == null) {
            log.debug("refinementDate is null, returning viewDate");
            return viewDate;
        }
        return viewDate.isBefore(refinementDate) ? viewDate : refinementDate;
    }
}
