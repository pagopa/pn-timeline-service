package it.pagopa.pn.timelineservice.operations.informal;

import it.pagopa.pn.commons.log.PnAuditLogBuilder;
import it.pagopa.pn.commons.log.PnAuditLogEvent;
import it.pagopa.pn.timelineservice.dto.notification.NotificationInfoInt;
import it.pagopa.pn.timelineservice.dto.timeline.TimelineElementInternal;
import it.pagopa.pn.timelineservice.dto.timeline.details.legal.AarGenerationDetailsInt;
import it.pagopa.pn.timelineservice.dto.timeline.details.TimelineElementCategoryInt;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

import static org.mockito.Mockito.when;

class InformalTimelineElementPersistenceStrategyTest {

    private InformalTimelineElementPersistenceStrategy strategy;
    private InformalTimelineTimestampMapper timelineTimestampMapper;

    @BeforeEach
    void setup() {
        timelineTimestampMapper = Mockito.mock(InformalTimelineTimestampMapper.class);
        strategy = new InformalTimelineElementPersistenceStrategy(timelineTimestampMapper);
    }

    @Test
    void buildAuditLogEventReturnsEventWithIun() {
        TimelineElementInternal dto = TimelineElementInternal.builder()
                .iun("iun_123")
                .elementId("elementId_123")
                .category(TimelineElementCategoryInt.AAR_GENERATION)
                .timestamp(Instant.now())
                .build();

        PnAuditLogEvent event = strategy.buildAuditLogEvent(dto, new PnAuditLogBuilder());

        Assertions.assertNotNull(event);
    }

    @Test
    void buildAuditLogEventWithNullDetailsDoesNotThrow() {
        TimelineElementInternal dto = TimelineElementInternal.builder()
                .iun("iun_123")
                .elementId("elementId_123")
                .category(TimelineElementCategoryInt.AAR_GENERATION)
                .timestamp(Instant.now())
                .details(null)
                .build();

        Assertions.assertDoesNotThrow(() -> strategy.buildAuditLogEvent(dto, new PnAuditLogBuilder()));
    }

    @Test
    void buildAuditLogEventWithDetailsDoesNotThrow() {
        AarGenerationDetailsInt details = AarGenerationDetailsInt.builder()
                .recIndex(0)
                .generatedAarUrl("url")
                .numberOfPages(1)
                .build();
        TimelineElementInternal dto = TimelineElementInternal.builder()
                .iun("iun_123")
                .elementId("elementId_123")
                .category(TimelineElementCategoryInt.AAR_GENERATION)
                .timestamp(Instant.now())
                .details(details)
                .build();

        Assertions.assertDoesNotThrow(() -> strategy.buildAuditLogEvent(dto, new PnAuditLogBuilder()));
    }

    @Test
    void enrichWithReworkReturnsUnmodifiedElement() {
        TimelineElementInternal dto = TimelineElementInternal.builder()
                .iun("iun_123")
                .elementId("elementId_123")
                .category(TimelineElementCategoryInt.AAR_GENERATION)
                .timestamp(Instant.now())
                .build();
        Set<TimelineElementInternal> currentTimeline = new HashSet<>();

        TimelineElementInternal result = strategy.enrichWithRework(dto, currentTimeline);

        Assertions.assertEquals(dto, result);
    }

    @Test
    void applyBusinessTimestampPreservesOriginalTimestampAndSetEventTimestamp() {
        Instant originalTimestamp = Instant.now();
        TimelineElementInternal dto = TimelineElementInternal.builder()
                .iun("iun_123")
                .elementId("elementId_123")
                .category(TimelineElementCategoryInt.AAR_GENERATION)
                .timestamp(originalTimestamp)
                .build();
        Instant eventTimestamp = originalTimestamp.plusSeconds(10);
        TimelineElementInternal mappedDto = TimelineElementInternal.builder()
                .iun("iun_123")
                .elementId("elementId_123")
                .category(TimelineElementCategoryInt.AAR_GENERATION)
                .timestamp(originalTimestamp)
                .eventTimestamp(eventTimestamp)
                .build();
        when(timelineTimestampMapper.mapTimelineTimestamps(Mockito.any())).thenReturn(mappedDto);
        Set<TimelineElementInternal> currentTimeline = new HashSet<>();

        TimelineElementInternal result = strategy.applyBusinessTimestamp(dto, currentTimeline);

        Mockito.verify(timelineTimestampMapper).mapTimelineTimestamps(Mockito.any());
        Assertions.assertEquals(originalTimestamp, result.getTimestamp());
        Assertions.assertEquals(eventTimestamp, result.getEventTimestamp());
    }

    @Test
    void requiresCriticalPathReturnsFalse() {
        TimelineElementInternal dto = TimelineElementInternal.builder()
                .iun("iun_123")
                .elementId("elementId_123")
                .build();
        NotificationInfoInt notification = NotificationInfoInt.builder().iun("iun_123").build();

        boolean result = strategy.requiresCriticalPath(dto, notification);

        Assertions.assertFalse(result);
    }
}