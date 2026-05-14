package it.pagopa.pn.timelineservice.strategy;

import it.pagopa.pn.timelineservice.dto.timeline.CommunicationType;
import it.pagopa.pn.timelineservice.strategy.common.StatusHistoryStrategy;
import it.pagopa.pn.timelineservice.strategy.common.TimelineElementPersistenceStrategy;

public interface TimelineStrategyBundle {
    TimelineElementPersistenceStrategy persistence();
    StatusHistoryStrategy statusHistory();
    CommunicationType supportedType();
}
