package it.pagopa.pn.timelineservice.operations.legal;

import it.pagopa.pn.timelineservice.dto.timeline.CommunicationType;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;

class LegalTimelineOperationsTest {
    private final LegalTimelineStatusHistoryCalculator statusHistoryStrategy = Mockito.mock(LegalTimelineStatusHistoryCalculator.class);
    private final LegalTimelineElementPersistenceStrategy persistenceStrategy = Mockito.mock(LegalTimelineElementPersistenceStrategy.class);
    private final LegalTimelineOperations bundle = new LegalTimelineOperations(persistenceStrategy, statusHistoryStrategy);

    @Test
    void persistenceStrategyReturnsDelegatedStrategy() {
        assertEquals(persistenceStrategy, bundle.persistenceStrategy());
    }

    @Test
    void statusHistoryCalculatorReturnsDelegatedStrategy() {
        assertEquals(statusHistoryStrategy, bundle.statusHistoryCalculator());
    }

    @Test
    void supportedTypeIsLegal() {
        assertEquals(CommunicationType.LEGAL, bundle.supportedType());
    }
}