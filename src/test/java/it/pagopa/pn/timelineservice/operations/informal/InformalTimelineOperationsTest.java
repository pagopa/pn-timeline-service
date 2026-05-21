package it.pagopa.pn.timelineservice.operations.informal;

import it.pagopa.pn.timelineservice.dto.timeline.CommunicationType;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;

class InformalTimelineOperationsTest {
    private final InformalTimelineStatusHistoryCalculator statusHistoryStrategy = Mockito.mock(InformalTimelineStatusHistoryCalculator.class);
    private final InformalTimelineElementPersistenceStrategy persistenceStrategy = Mockito.mock(InformalTimelineElementPersistenceStrategy.class);
    private final InformalTimelineOperations bundle = new InformalTimelineOperations(persistenceStrategy, statusHistoryStrategy);

    @Test
    void persistenceStrategyReturnsDelegatedStrategy() {
        assertEquals(persistenceStrategy, bundle.persistenceStrategy());
    }

    @Test
    void statusHistoryCalculatorReturnsDelegatedStrategy() {
        assertEquals(statusHistoryStrategy, bundle.statusHistoryCalculator());
    }

    @Test
    void supportedTypeIsInformal() {
        assertEquals(CommunicationType.INFORMAL, bundle.supportedType());
    }
}