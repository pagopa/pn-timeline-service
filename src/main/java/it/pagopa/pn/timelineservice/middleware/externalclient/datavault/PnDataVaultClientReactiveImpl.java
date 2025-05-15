package it.pagopa.pn.timelineservice.middleware.externalclient.datavault;

import it.pagopa.pn.commons.pnclients.CommonBaseClient;
import it.pagopa.pn.timelineservice.generated.openapi.msclient.datavault.model.ConfidentialTimelineElementDto;
import it.pagopa.pn.timelineservice.generated.openapi.msclient.datavault_reactive.api.NotificationsApi;
import lombok.CustomLog;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import static it.pagopa.pn.timelineservice.middleware.externalclient.datavault.PnDataVaultClient.*;

@Component
@RequiredArgsConstructor
@CustomLog
public class PnDataVaultClientReactiveImpl extends CommonBaseClient implements PnDataVaultClientReactive {
    private final NotificationsApi pnDataVaultNotificationApi;

    public Mono<Void> updateNotificationTimelineByIunAndTimelineElementId(String iun, ConfidentialTimelineElementDto dto) {
        log.logInvokingExternalService(CLIENT_NAME, UPDATE_TIMELINE_ELEMENT_CONF_INFORMATION);

        return pnDataVaultNotificationApi.updateNotificationTimelineByIunAndTimelineElementId(iun, dto.getTimelineElementId(), dto);
    }

    public Mono<ConfidentialTimelineElementDto> getNotificationTimelineByIunAndTimelineElementId(String iun, String timelineElementId) {
        log.logInvokingExternalService(CLIENT_NAME, GET_TIMELINE_ELEMENT_CONF_INFORMATION);

        return pnDataVaultNotificationApi.getNotificationTimelineByIunAndTimelineElementId(iun, timelineElementId);
    }

    @Override
    public Flux<ConfidentialTimelineElementDto> getNotificationTimelineByIun(String iun) {
        log.logInvokingExternalService(CLIENT_NAME, GET_TIMELINE_CONF_INFORMATION);

        return pnDataVaultNotificationApi.getNotificationTimelineByIun(iun);
    }

}
