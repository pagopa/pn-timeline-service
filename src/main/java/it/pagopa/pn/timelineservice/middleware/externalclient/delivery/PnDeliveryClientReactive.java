package it.pagopa.pn.timelineservice.middleware.externalclient.delivery;

import it.pagopa.pn.commons.log.PnLogger;
import it.pagopa.pn.timelineservice.generated.openapi.msclient.delivery.model.SentNotificationV24;
import reactor.core.publisher.Mono;

public interface PnDeliveryClientReactive {
    String CLIENT_NAME = PnLogger.EXTERNAL_SERVICES.PN_DELIVERY;
    String GET_NOTIFICATION = "GET NOTIFICATION";

    Mono<SentNotificationV24> getSentNotification(String iun);
}
