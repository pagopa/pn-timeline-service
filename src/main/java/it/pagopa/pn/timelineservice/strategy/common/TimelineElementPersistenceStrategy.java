package it.pagopa.pn.timelineservice.strategy.common;

import it.pagopa.pn.commons.log.PnAuditLogBuilder;
import it.pagopa.pn.commons.log.PnAuditLogEvent;
import it.pagopa.pn.timelineservice.dto.notification.NotificationInfoInt;
import it.pagopa.pn.timelineservice.dto.timeline.TimelineElementInternal;

import java.util.Set;

public interface TimelineElementPersistenceStrategy {
    PnAuditLogEvent buildAuditLogEvent(TimelineElementInternal dto, PnAuditLogBuilder builder);

    TimelineElementInternal enrichWithRework(
            TimelineElementInternal dto,
            Set<TimelineElementInternal> currentTimeline);

    TimelineElementInternal applyBusinessTimestamp(
            TimelineElementInternal dto,
            Set<TimelineElementInternal> currentTimeline);

    /**
     * Indica se per questa strategia è richiesto il lock pessimistico
     * (path "critico" multi-recipient).
     */
    boolean requiresCriticalPath(TimelineElementInternal dto, NotificationInfoInt notification);

}
