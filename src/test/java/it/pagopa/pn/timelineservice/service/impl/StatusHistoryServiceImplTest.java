package it.pagopa.pn.timelineservice.service.impl;

import it.pagopa.pn.commons.exceptions.PnInternalException;
import it.pagopa.pn.timelineservice.dto.notification.status.NotificationStatusHistoryElementInt;
import it.pagopa.pn.timelineservice.dto.timeline.CommunicationType;
import it.pagopa.pn.timelineservice.dto.timeline.TimelineElementInternal;
import it.pagopa.pn.timelineservice.operations.TimelineOperations;
import it.pagopa.pn.timelineservice.operations.TimelineOperationsResolver;
import it.pagopa.pn.timelineservice.operations.common.StatusHistoryCalculator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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

    private Instant notificationCreatedAt;

    @BeforeEach
    void setUp() {
        notificationCreatedAt = Instant.now();
    }

    @Test
    void getStatusHistory_returnsStatusHistoryFromStrategy() {
        TimelineElementInternal element = mock(TimelineElementInternal.class);
        when(element.getCommunicationType()).thenReturn(CommunicationType.INFORMAL);

        List<NotificationStatusHistoryElementInt> expected = List.of(mock(NotificationStatusHistoryElementInt.class));
        when(timelineOperationsResolver.resolve(CommunicationType.INFORMAL)).thenReturn(timelineOperations);
        when(timelineOperations.statusHistoryCalculator()).thenReturn(statusHistoryCalculator);
        when(statusHistoryCalculator.getStatusHistory(Set.of(element), 1, notificationCreatedAt)).thenReturn(expected);

        List<NotificationStatusHistoryElementInt> result = statusHistoryService.getStatusHistory(Set.of(element), 1, notificationCreatedAt);

        assertEquals(expected, result);
    }

    @Test
    void getStatusHistory_usesFirstNonNullCommunicationType() {
        TimelineElementInternal elementWithNull = mock(TimelineElementInternal.class);
        TimelineElementInternal elementWithType = mock(TimelineElementInternal.class);
        when(elementWithNull.getCommunicationType()).thenReturn(null);
        when(elementWithType.getCommunicationType()).thenReturn(CommunicationType.INFORMAL);

        Set<TimelineElementInternal> elements = new java.util.LinkedHashSet<>(List.of(elementWithNull, elementWithType));

        when(timelineOperationsResolver.resolve(CommunicationType.INFORMAL)).thenReturn(timelineOperations);
        when(timelineOperations.statusHistoryCalculator()).thenReturn(statusHistoryCalculator);
        when(statusHistoryCalculator.getStatusHistory(elements, 2, notificationCreatedAt)).thenReturn(List.of());

        assertDoesNotThrow(() -> statusHistoryService.getStatusHistory(elements, 2, notificationCreatedAt));
        verify(timelineOperationsResolver).resolve(CommunicationType.INFORMAL);
    }

    @Test
    void getStatusHistory_throwsExceptionWhenAllCommunicationTypesAreNull() {
        TimelineElementInternal element = mock(TimelineElementInternal.class);
        when(element.getCommunicationType()).thenReturn(null);
        Set<TimelineElementInternal> elements = Set.of(element);
        assertThrows(PnInternalException.class,
                () -> statusHistoryService.getStatusHistory(elements, 1, notificationCreatedAt));
    }

    @Test
    void getStatusHistory_usesLegalCommunicationStrategyWhenTimelineElementListIsEmpty() {
        when(timelineOperationsResolver.resolve(CommunicationType.LEGAL)).thenReturn(timelineOperations);
        when(timelineOperations.statusHistoryCalculator()).thenReturn(statusHistoryCalculator);
        when(statusHistoryCalculator.getStatusHistory(Set.of(), 1, notificationCreatedAt)).thenReturn(List.of());

        assertDoesNotThrow(() -> statusHistoryService.getStatusHistory(Set.of(), 1, notificationCreatedAt));
        verify(timelineOperationsResolver).resolve(CommunicationType.LEGAL);
    }

    private static Stream<Arguments> provideTimelineElementLists() {
        return Stream.of(
                null,
                Arguments.of(Collections.emptySet())
        );
    }

    @ParameterizedTest
    @MethodSource("provideTimelineElementLists")
    void getStatusHistory_usesLegalCommunicationStrategyWhenTimelineElementListIsEmpty(Set<TimelineElementInternal> timelineElementList) {
        when(timelineOperationsResolver.resolve(CommunicationType.LEGAL)).thenReturn(timelineOperations);
        when(timelineOperations.statusHistoryCalculator()).thenReturn(statusHistoryCalculator);
        when(statusHistoryCalculator.getStatusHistory(timelineElementList, 1, notificationCreatedAt)).thenReturn(List.of());

        assertDoesNotThrow(() -> statusHistoryService.getStatusHistory(timelineElementList, 1, notificationCreatedAt));
        verify(timelineOperationsResolver).resolve(CommunicationType.LEGAL);
    }
}