package it.pagopa.pn.timelineservice.config.msclient;

import it.pagopa.pn.commons.pnclients.CommonBaseClient;
import it.pagopa.pn.timelineservice.config.PnTimelineServiceConfigs;
import it.pagopa.pn.timelineservice.generated.openapi.msclient.datavault_reactive.ApiClient;
import it.pagopa.pn.timelineservice.generated.openapi.msclient.datavault_reactive.api.NotificationsApi;
import it.pagopa.pn.timelineservice.generated.openapi.msclient.datavault_reactive.api.RecipientsApi;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataVaultApiReactiveConfigurator extends CommonBaseClient {

    @Bean
    public NotificationsApi notificationsApiReactive(PnTimelineServiceConfigs cfg){
        return new NotificationsApi(getNewApiClient(cfg));
    }

    @Bean
    public RecipientsApi recipientsApiReactive(PnTimelineServiceConfigs cfg){
        return new RecipientsApi(getNewApiClient(cfg));
    }
    
    @NotNull
    private ApiClient getNewApiClient(PnTimelineServiceConfigs cfg) {
        ApiClient newApiClient = new ApiClient( initWebClient(ApiClient.buildWebClientBuilder()) );
        newApiClient.setBasePath( cfg.getDataVaultBaseUrl() );
        return newApiClient;
    }

}
