package it.pagopa.pn.timelineservice.operations.common;

import it.pagopa.pn.timelineservice.dto.notification.status.NotificationStatusHistoryElementInt;
import it.pagopa.pn.timelineservice.dto.timeline.TimelineElementInternal;

import java.time.Instant;
import java.util.List;
import java.util.Set;

public interface StatusHistoryCalculator {
    List<NotificationStatusHistoryElementInt> getStatusHistory(Set<TimelineElementInternal> timelineElementList,
                                                                      int numberOfRecipients,
                                                                      Instant notificationCreatedAt);
}
