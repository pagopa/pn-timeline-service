package it.pagopa.pn.timelineservice.service;

import it.pagopa.pn.timelineservice.dto.ext.notification.NotificationInt;
import it.pagopa.pn.timelineservice.dto.ext.notification.status.NotificationStatusInt;
import it.pagopa.pn.timelineservice.dto.timeline.TimelineElementInternal;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Set;

public interface StatusService {

    @Data
    @AllArgsConstructor
    class NotificationStatusUpdate{
        private NotificationStatusInt oldStatus;
        private NotificationStatusInt newStatus;
    }

    /**
     * calcola lo stato in base al dto e al set di timeline correnti
     *
     * @param dto nuova timeline
     * @param currentTimeline storico timeline attuale
     * @param notification notifica
     * @return entrambi i notificationstatus (old, new) - cambio di stato elaborato
     */
    NotificationStatusUpdate getStatus(TimelineElementInternal dto, Set<TimelineElementInternal> currentTimeline, NotificationInt notification);

    NotificationStatusUpdate computeStatusChange(TimelineElementInternal dto, Set<TimelineElementInternal> currentTimeline, NotificationInt notification);
}
