package it.pagopa.pn.timelineservice.strategy;

import it.pagopa.pn.timelineservice.dto.timeline.CommunicationType;
import it.pagopa.pn.timelineservice.strategy.common.TimelineElementPersistenceStrategy;

public interface TimelineStrategyBundle {
    TimelineElementPersistenceStrategy persistence();
    CommunicationType supportedType();
}
