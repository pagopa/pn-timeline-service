package it.pagopa.pn.timelineservice.service.impl;


import it.pagopa.pn.timelineservice.dto.ext.notification.NotificationInt;
import it.pagopa.pn.timelineservice.exceptions.PnNotFoundException;
import it.pagopa.pn.timelineservice.middleware.externalclient.delivery.PnDeliveryClientReactive;
import it.pagopa.pn.timelineservice.service.NotificationService;
import it.pagopa.pn.timelineservice.service.mapper.NotificationMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import static it.pagopa.pn.timelineservice.exceptions.PnDeliveryPushExceptionCodes.ERROR_CODE_DELIVERYPUSH_NOTIFICATIONFAILED;


@Service
@Slf4j
public class NotificationServiceImpl implements NotificationService {
    private final PnDeliveryClientReactive pnDeliveryClientReactive;

    public NotificationServiceImpl(PnDeliveryClientReactive pnDeliveryClientReactive) {
        this.pnDeliveryClientReactive = pnDeliveryClientReactive;
    }

    @Override
    public Mono<NotificationInt> getNotificationByIunReactive(String iun) {
        return pnDeliveryClientReactive.getSentNotification(iun)
                .switchIfEmpty(
                    Mono.error(new PnNotFoundException("Not found", "Get notification is not valid for - iun " + iun,
                            ERROR_CODE_DELIVERYPUSH_NOTIFICATIONFAILED))
                )
                .map(NotificationMapper::externalToInternal);
    }
}
