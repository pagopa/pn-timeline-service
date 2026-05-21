package it.pagopa.pn.timelineservice.operations.legal;

import it.pagopa.pn.timelineservice.dto.timeline.CommunicationType;
import it.pagopa.pn.timelineservice.operations.common.TimelineElementPersistenceStrategy;
import it.pagopa.pn.timelineservice.operations.TimelineOperations;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LegalTimelineOperations implements TimelineOperations {
    private final LegalTimelineElementPersistenceStrategy legalTimelineElementPersistenceStrategy;
    private final LegalTimelineStatusHistoryCalculator legalTimelineStatusHistoryStrategy;

    @Override
    public TimelineElementPersistenceStrategy persistenceStrategy() {
        return legalTimelineElementPersistenceStrategy;
    }

    @Override
    public LegalTimelineStatusHistoryCalculator statusHistoryCalculator() {
        return legalTimelineStatusHistoryStrategy;
    }

    @Override
    public CommunicationType supportedType() {
        return CommunicationType.LEGAL;
    }
}
