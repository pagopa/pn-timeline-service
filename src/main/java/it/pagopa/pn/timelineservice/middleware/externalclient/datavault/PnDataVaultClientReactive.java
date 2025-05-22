package it.pagopa.pn.timelineservice.middleware.externalclient.datavault;

import it.pagopa.pn.commons.log.PnLogger;
import it.pagopa.pn.timelineservice.generated.openapi.msclient.datavault.model.ConfidentialTimelineElementDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface PnDataVaultClientReactive {
    String CLIENT_NAME = PnLogger.EXTERNAL_SERVICES.PN_DATA_VAULT;
    String UPDATE_TIMELINE_ELEMENT_CONF_INFORMATION = "UPDATE TIMELINE ELEMENT CONFIDENTIAL INFORMATION";
    String GET_TIMELINE_ELEMENT_CONF_INFORMATION = "GET TIMELINE ELEMENT CONFIDENTIAL INFORMATION";
    String GET_TIMELINE_CONF_INFORMATION = "GET TIMELINE CONFIDENTIAL INFORMATION";

    Mono<Void> updateNotificationTimelineByIunAndTimelineElementId(String iun, ConfidentialTimelineElementDto dto);

    Mono<ConfidentialTimelineElementDto> getNotificationTimelineByIunAndTimelineElementId(String iun, String timelineElementId);

    Flux<ConfidentialTimelineElementDto> getNotificationTimelineByIun(String iun);
}
