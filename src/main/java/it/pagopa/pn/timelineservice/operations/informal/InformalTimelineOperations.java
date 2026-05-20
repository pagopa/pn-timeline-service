package it.pagopa.pn.timelineservice.operations.informal;

import it.pagopa.pn.timelineservice.dto.timeline.CommunicationType;
import it.pagopa.pn.timelineservice.operations.common.StatusHistoryCalculator;
import it.pagopa.pn.timelineservice.operations.common.TimelineElementPersistenceStrategy;
import it.pagopa.pn.timelineservice.operations.TimelineOperations;
import it.pagopa.pn.timelineservice.operations.common.TimelineTimestampMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class InformalTimelineOperations implements TimelineOperations {
    private final InformalTimelineElementPersistenceStrategy informalTimelineElementPersistenceStrategy;
    private final InformalTimelineStatusHistoryCalculator informalTimelineStatusHistoryStrategy;
    private final InformalTimelineTimestampMapper informalTimelineTimestampMapper;

    @Override
    public TimelineElementPersistenceStrategy persistenceStrategy() {
        return informalTimelineElementPersistenceStrategy;
    }

    @Override
    public StatusHistoryCalculator statusHistoryCalculator() {
        return informalTimelineStatusHistoryStrategy;
    }

    @Override
    public TimelineTimestampMapper timelineTimestampMapper() {
        return informalTimelineTimestampMapper;
    }

    @Override
    public CommunicationType supportedType() {
        return CommunicationType.INFORMAL;
    }
}
