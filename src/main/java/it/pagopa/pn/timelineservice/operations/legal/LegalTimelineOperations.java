package it.pagopa.pn.timelineservice.operations.legal;

import it.pagopa.pn.timelineservice.dto.timeline.CommunicationType;
import it.pagopa.pn.timelineservice.operations.common.StatusHistoryCalculator;
import it.pagopa.pn.timelineservice.operations.common.TimelineElementPersistenceStrategy;
import it.pagopa.pn.timelineservice.operations.TimelineOperations;
import it.pagopa.pn.timelineservice.operations.common.TimelineTimestampMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LegalTimelineOperations implements TimelineOperations {
    private final LegalTimelineElementPersistenceStrategy legalTimelineElementPersistenceStrategy;
    private final LegalTimelineStatusHistoryCalculator legalTimelineStatusHistoryStrategy;
    private final LegalTimelineTimestampMapper legalTimelineTimestampMapper;

    @Override
    public TimelineElementPersistenceStrategy persistenceStrategy() {
        return legalTimelineElementPersistenceStrategy;
    }

    @Override
    public StatusHistoryCalculator statusHistoryCalculator() {
        return legalTimelineStatusHistoryStrategy;
    }

    @Override
    public TimelineTimestampMapper timelineTimestampMapper() {
        return legalTimelineTimestampMapper;
    }

    @Override
    public CommunicationType supportedType() {
        return CommunicationType.LEGAL;
    }
}
