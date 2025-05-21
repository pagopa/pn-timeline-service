package it.pagopa.pn.timelineservice.middleware.externalclient.datavault;

import it.pagopa.pn.commons.log.PnLogger;
import it.pagopa.pn.timelineservice.generated.openapi.msclient.datavault.model.BaseRecipientDto;
import it.pagopa.pn.timelineservice.generated.openapi.msclient.datavault.model.ConfidentialTimelineElementDto;
import it.pagopa.pn.timelineservice.generated.openapi.msclient.datavault.model.ConfidentialTimelineElementId;
import it.pagopa.pn.timelineservice.generated.openapi.msclient.datavault.model.NotificationRecipientAddressesDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface PnDataVaultClientReactive {
    String CLIENT_NAME = PnLogger.EXTERNAL_SERVICES.PN_DATA_VAULT;

    Mono<Void> updateNotificationTimelineByIunAndTimelineElementId(String iun, ConfidentialTimelineElementDto dto);

    Mono<ConfidentialTimelineElementDto> getNotificationTimelineByIunAndTimelineElementId(String iun, String timelineElementId);

    Flux<ConfidentialTimelineElementDto> getNotificationTimelineByIun(String iun);
}
