package it.pagopa.pn.timelineservice.service.impl;

import it.pagopa.pn.commons.exceptions.PnInternalException;
import it.pagopa.pn.timelineservice.dto.notification.status.NotificationStatusHistoryElementInt;
import it.pagopa.pn.timelineservice.dto.timeline.CommunicationType;
import it.pagopa.pn.timelineservice.dto.timeline.TimelineElementInternal;
import it.pagopa.pn.timelineservice.operations.TimelineOperations;
import it.pagopa.pn.timelineservice.operations.TimelineOperationsResolver;
import it.pagopa.pn.timelineservice.operations.common.StatusHistoryCalculator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StatusHistoryServiceImplTest {

    @Mock
    private TimelineOperationsResolver timelineOperationsResolver;

    @Mock
    private TimelineOperations timelineOperations;

    @Mock
    private StatusHistoryCalculator statusHistoryCalculator;

    @InjectMocks
    private StatusHistoryServiceImpl statusHistoryService;

    private final Instant notificationCreatedAt = Instant.now();

    @Test
    void getStatusHistoryReturnsCalculatedHistory() {
        Set<TimelineElementInternal> timelineElements = Set.of(TimelineElementInternal.builder().build());
        int numberOfRecipients = 2;
        CommunicationType communicationType = CommunicationType.LEGAL;
        List<NotificationStatusHistoryElementInt> expected = List.of(mock(NotificationStatusHistoryElementInt.class));

        when(timelineOperationsResolver.resolve(communicationType)).thenReturn(timelineOperations);
        when(timelineOperations.statusHistoryCalculator()).thenReturn(statusHistoryCalculator);
        when(statusHistoryCalculator.getStatusHistory(timelineElements, numberOfRecipients, notificationCreatedAt)).thenReturn(expected);

        List<NotificationStatusHistoryElementInt> result = statusHistoryService.getStatusHistory(timelineElements, numberOfRecipients, notificationCreatedAt, communicationType);

        assertEquals(expected, result);
    }

    @Test
    void getStatusHistoryPropagatesExceptionWhenResolverThrows() {
        Set<TimelineElementInternal> timelineElements = Set.of(TimelineElementInternal.builder().build());
        CommunicationType communicationType = CommunicationType.LEGAL;

        when(timelineOperationsResolver.resolve(communicationType)).thenThrow(PnInternalException.class);

        assertThrows(PnInternalException.class,
                () -> statusHistoryService.getStatusHistory(timelineElements, 1, notificationCreatedAt, communicationType));
    }

    @Test
    void getStatusHistoryWithEmptyTimelineElements() {
        Set<TimelineElementInternal> timelineElements = Collections.emptySet();
        int numberOfRecipients = 1;
        CommunicationType communicationType = CommunicationType.LEGAL;
        List<NotificationStatusHistoryElementInt> expected = Collections.emptyList();

        when(timelineOperationsResolver.resolve(communicationType)).thenReturn(timelineOperations);
        when(timelineOperations.statusHistoryCalculator()).thenReturn(statusHistoryCalculator);
        when(statusHistoryCalculator.getStatusHistory(timelineElements, numberOfRecipients, notificationCreatedAt)).thenReturn(expected);

        List<NotificationStatusHistoryElementInt> result = statusHistoryService.getStatusHistory(timelineElements, numberOfRecipients, notificationCreatedAt, communicationType);

        assertEquals(expected, result);
    }
}