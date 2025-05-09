package it.pagopa.pn.timelineservice.service;

import it.pagopa.pn.timelineservice.dto.ext.notification.NotificationInt;
import reactor.core.publisher.Mono;

public interface NotificationService {
    Mono<NotificationInt> getNotificationByIunReactive(String iun);
}
