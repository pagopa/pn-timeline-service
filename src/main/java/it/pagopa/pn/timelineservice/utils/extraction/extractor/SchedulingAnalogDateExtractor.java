package it.pagopa.pn.timelineservice.utils.extraction.extractor;

import it.pagopa.pn.timelineservice.dto.timeline.TimelineElementInternal;
import it.pagopa.pn.timelineservice.dto.timeline.details.*;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.util.Optional;

import static it.pagopa.pn.timelineservice.utils.extraction.extractor.ExtractorUtils.isRelatedToRecipient;

@Slf4j
public class SchedulingAnalogDateExtractor implements TimelineDataExtractor<Instant> {
    public final static ExtractorKey<Instant> KEY = ExtractorKey.of("schedulingAnalogDate", Instant.class);
    private Instant result;
    private final int recIndex;
    public SchedulingAnalogDateExtractor(int recIndex) {
        this.recIndex = recIndex;
    }


    @Override
    public ExtractorKey<Instant> getKey() {
        return KEY;
    }

    @Override
    public boolean process(TimelineElementInternal element) {
        if (!isRelatedToRecipient(element, this.recIndex)) {
            return false;
        }

        TimelineElementDetailsInt detailsInt = element.getDetails();


        if (detailsInt instanceof ScheduleAnalogWorkflowDetailsInt scheduleAnalogDetails) {
            log.debug("SchedulingAnalogDateExtractor - found ScheduleAnalogWorkflowDetailsInt for iun={}", element.getIun());
            this.result = scheduleAnalogDetails.getSchedulingDate();
            return true;
        }

        if(detailsInt instanceof ProbableDateAnalogWorkflowDetailsInt probableAnalogDetails) {
            log.debug("SchedulingAnalogDateExtractor - found ProbableDateAnalogWorkflowDetailsInt for iun={}", element.getIun());
            this.result = probableAnalogDetails.getSchedulingAnalogDate();
            return true;
        }

        return false;
    }

    @Override
    public void postProcess() {
        // nothing to do
    }

    @Override
    public Optional<Instant> getResult() {
        return Optional.ofNullable(result);
    }
}
