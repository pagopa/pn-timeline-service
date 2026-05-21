package it.pagopa.pn.timelineservice.operations.informal;

import it.pagopa.pn.commons.log.PnAuditLogBuilder;
import it.pagopa.pn.commons.log.PnAuditLogEvent;
import it.pagopa.pn.commons.log.PnAuditLogEventType;
import it.pagopa.pn.timelineservice.dto.notification.NotificationInfoInt;
import it.pagopa.pn.timelineservice.dto.timeline.TimelineElementInternal;
import it.pagopa.pn.timelineservice.operations.common.TimelineElementPersistenceStrategy;
import it.pagopa.pn.timelineservice.service.mapper.SmartMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class InformalTimelineElementPersistenceStrategy implements TimelineElementPersistenceStrategy {
    private final SmartMapper smartMapper;

    @Override
    public PnAuditLogEvent buildAuditLogEvent(TimelineElementInternal dto, PnAuditLogBuilder builder) {
        String auditLog = String.format("Timeline event inserted with: CATEGORY=%s IUN=%s {DETAILS: %s} TIMELINEID=%s paId=%s TIMESTAMP=%s communicationType=%s",
                dto.getCategory(),
                dto.getIun(),
                dto.getDetails() != null ? dto.getDetails().toLog() : null,
                dto.getElementId(),
                dto.getPaId(),
                dto.getTimestamp(),
                dto.getCommunicationType()
        );
        return builder
                .before(PnAuditLogEventType.AUD_COM_TIMELINE, auditLog)
                .iun(dto.getIun())
                .build();
    }

    @Override
    public TimelineElementInternal enrichWithRework(TimelineElementInternal dto, Set<TimelineElementInternal> currentTimeline) {
        return dto; // Per gli eventi informali non è prevista l'arricchimento con i dati di rework.
    }

    @Override
    public TimelineElementInternal applyBusinessTimestamp(TimelineElementInternal dto, Set<TimelineElementInternal> currentTimeline) {
        // calcolo e aggiungo il businessTimestamp
        return smartMapper.mapTimelineInternalWithEventTimestamp(dto);
    }

    @Override
    public boolean requiresCriticalPath(TimelineElementInternal dto, NotificationInfoInt notification) {
        return false; // Le notifiche bonarie (INFORMAL) non richiedono il pessimistic lock.
    }
}
