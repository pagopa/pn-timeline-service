package it.pagopa.pn.timelineservice.strategy.informal;

import it.pagopa.pn.timelineservice.dto.timeline.CommunicationType;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;

class InformalTimelineStrategyBundleTest {
    @Test
    void persistenceReturnsDelegatedStrategy() {
        InformalTimelineElementPersistenceStrategy strategy = Mockito.mock(InformalTimelineElementPersistenceStrategy.class);
        InformalTimelineStrategyBundle bundle = new InformalTimelineStrategyBundle(strategy);

        assertEquals(strategy, bundle.persistence());
    }

    @Test
    void supportedTypeIsInformal() {
        InformalTimelineElementPersistenceStrategy strategy = Mockito.mock(InformalTimelineElementPersistenceStrategy.class);
        InformalTimelineStrategyBundle bundle = new InformalTimelineStrategyBundle(strategy);

        assertEquals(CommunicationType.INFORMAL, bundle.supportedType());
    }
}