package it.pagopa.pn.timelineservice.strategy.legal;

import it.pagopa.pn.timelineservice.dto.timeline.CommunicationType;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;

class LegalTimelineStrategyBundleTest {
    @Test
    void persistenceReturnsDelegatedStrategy() {
        LegalTimelineElementPersistenceStrategy strategy = Mockito.mock(LegalTimelineElementPersistenceStrategy.class);
        LegalTimelineStrategyBundle bundle = new LegalTimelineStrategyBundle(strategy);

        assertEquals(strategy, bundle.persistence());
    }

    @Test
    void supportedTypeIsLegal() {
        LegalTimelineElementPersistenceStrategy strategy = Mockito.mock(LegalTimelineElementPersistenceStrategy.class);
        LegalTimelineStrategyBundle bundle = new LegalTimelineStrategyBundle(strategy);

        assertEquals(CommunicationType.LEGAL, bundle.supportedType());
    }
}