package it.pagopa.pn.timelineservice.strategy.legal;

import it.pagopa.pn.timelineservice.dto.timeline.CommunicationType;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;

class LegalTimelineStrategyBundleTest {
    private final LegalTimelineStatusHistoryStrategy statusHistoryStrategy = Mockito.mock(LegalTimelineStatusHistoryStrategy.class);
    private final LegalTimelineElementPersistenceStrategy persistenceStrategy = Mockito.mock(LegalTimelineElementPersistenceStrategy.class);
    private final LegalTimelineStrategyBundle bundle = new LegalTimelineStrategyBundle(persistenceStrategy, statusHistoryStrategy);

    @Test
    void persistenceReturnsDelegatedStrategy() {
        assertEquals(persistenceStrategy, bundle.persistence());
    }

    @Test
    void statusHistoryReturnsDelegatedStrategy() {
        assertEquals(statusHistoryStrategy, bundle.statusHistory());
    }

    @Test
    void supportedTypeIsLegal() {
        assertEquals(CommunicationType.LEGAL, bundle.supportedType());
    }
}