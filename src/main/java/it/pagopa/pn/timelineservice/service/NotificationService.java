package it.pagopa.pn.timelineservice.service;

import it.pagopa.pn.timelineservice.dto.ext.notification.NotificationInt;
import it.pagopa.pn.timelineservice.dto.ext.notification.status.NotificationStatusInt;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.Map;

public interface NotificationService {
    NotificationInt getNotificationByIun(String iun);

    Map<String, String> getRecipientsQuickAccessLinkToken(String iun);
    
    Mono<NotificationInt> getNotificationByIunReactive(String iun);

    Mono<Void> updateStatus(String iun, NotificationStatusInt notificationStatusInt, Instant updateStatusTimestamp);

    Mono<Void> removeAllNotificationCostsByIun(String iun);
}
