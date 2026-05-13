package it.pagopa.pn.timelineservice.strategy.legal;

import it.pagopa.pn.timelineservice.dto.timeline.CommunicationType;
import it.pagopa.pn.timelineservice.strategy.common.TimelineElementPersistenceStrategy;
import it.pagopa.pn.timelineservice.strategy.TimelineStrategyBundle;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LegalTimelineStrategyBundle implements TimelineStrategyBundle {
    private final LegalTimelineElementPersistenceStrategy legalTimelineElementPersistenceStrategy;

    @Override
    public TimelineElementPersistenceStrategy persistence() {
        return legalTimelineElementPersistenceStrategy;
    }

    @Override
    public CommunicationType supportedType() {
        return CommunicationType.LEGAL;
    }
}
