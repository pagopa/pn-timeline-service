package it.pagopa.pn.timelineservice.operations.legal;

import it.pagopa.pn.timelineservice.dto.timeline.TimelineElementInternal;
import it.pagopa.pn.timelineservice.operations.common.TimelineTimestampBaseMapper;
import it.pagopa.pn.timelineservice.operations.common.TimelineTimestampMapper;
import it.pagopa.pn.timelineservice.service.mapper.TimelineMapper;
import it.pagopa.pn.timelineservice.service.mapper.TimelineMapperFactory;
import it.pagopa.pn.timelineservice.utils.FeatureEnabledUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class LegalTimelineTimestampMapper implements TimelineTimestampMapper {
    private final TimelineTimestampBaseMapper timelineTimestampBaseMapper;
    private final FeatureEnabledUtils featureEnabledUtils;
    private final TimelineMapperFactory timelineMapperFactory;

    @Override
    public TimelineElementInternal mapTimelineTimestamps(TimestampMapperPayload payload) {
        TimelineElementInternal source = payload.timelineElementInternal();
        Set<TimelineElementInternal> timelineElementInternalSet = payload.timelineElementInternalSet();

        //Viene recuperato il timestamp originale, prima di effettuare un qualsiasi remapping
        Instant ingestionTimestamp = source.getTimestamp();

        //Viene effettuato un primo remapping degli elementi di timeline e dei relativi timestamp in particolare viene effettuato il remapping di tutti
        // i timestamp che non dipendono da ulteriori elementi di timeline, cioè hanno l'eventTimestamp già storicizzato nei details
        TimelineElementInternal result = timelineTimestampBaseMapper.mapTimelineInternal(source);

        TimelineMapper timelineMapper = timelineMapperFactory.getTimelineMapper(source.getNotificationSentAt());
        boolean isPfNewWorkflowEnabled = featureEnabledUtils.isPfNewWorkflowEnabled(source.getNotificationSentAt());
        timelineMapper.remapSpecificTimelineElementData(timelineElementInternalSet, result, ingestionTimestamp, isPfNewWorkflowEnabled);

        return result;
    }
}
