package it.pagopa.pn.timelineservice.service;

import it.pagopa.pn.timelineservice.dto.notification.status.NotificationStatusHistoryElementInt;
import it.pagopa.pn.timelineservice.dto.timeline.CommunicationType;
import it.pagopa.pn.timelineservice.dto.timeline.TimelineElementInternal;

import java.time.Instant;
import java.util.List;
import java.util.Set;

public interface StatusHistoryService {
    List<NotificationStatusHistoryElementInt> getStatusHistory(
        Set<TimelineElementInternal> timelineElementList,
        int numberOfRecipients,
        Instant notificationCreatedAt,
        CommunicationType communicationType
    );
}
