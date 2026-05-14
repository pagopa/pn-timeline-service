package it.pagopa.pn.timelineservice.strategy.informal;

import it.pagopa.pn.timelineservice.dto.timeline.CommunicationType;
import it.pagopa.pn.timelineservice.strategy.common.TimelineElementPersistenceStrategy;
import it.pagopa.pn.timelineservice.strategy.TimelineStrategyBundle;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class InformalTimelineStrategyBundle implements TimelineStrategyBundle {
    private final InformalTimelineElementPersistenceStrategy informalTimelineElementPersistenceStrategy;
    private final InformalTimelineStatusHistoryStrategy informalTimelineStatusHistoryStrategy;

    @Override
    public TimelineElementPersistenceStrategy persistence() {
        return informalTimelineElementPersistenceStrategy;
    }

    @Override
    public InformalTimelineStatusHistoryStrategy statusHistory() {
        return informalTimelineStatusHistoryStrategy;
    }

    @Override
    public CommunicationType supportedType() {
        return CommunicationType.INFORMAL;
    }
}
