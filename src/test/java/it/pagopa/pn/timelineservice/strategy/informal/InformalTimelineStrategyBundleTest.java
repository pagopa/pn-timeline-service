package it.pagopa.pn.timelineservice.strategy.informal;

import it.pagopa.pn.timelineservice.dto.timeline.CommunicationType;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;

class InformalTimelineStrategyBundleTest {
    private final InformalTimelineStatusHistoryStrategy statusHistoryStrategy = Mockito.mock(InformalTimelineStatusHistoryStrategy.class);
    private final InformalTimelineElementPersistenceStrategy persistenceStrategy = Mockito.mock(InformalTimelineElementPersistenceStrategy.class);
    private final InformalTimelineStrategyBundle bundle = new InformalTimelineStrategyBundle(persistenceStrategy, statusHistoryStrategy);

    @Test
    void persistenceReturnsDelegatedStrategy() {
        assertEquals(persistenceStrategy, bundle.persistence());
    }

    @Test
    void statusHistoryReturnsDelegatedStrategy() {
        assertEquals(statusHistoryStrategy, bundle.statusHistory());
    }

    @Test
    void supportedTypeIsInformal() {
        assertEquals(CommunicationType.INFORMAL, bundle.supportedType());
    }
}