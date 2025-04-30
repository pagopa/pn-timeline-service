package it.pagopa.pn.timelineservice.config;

import it.pagopa.pn.commons.conf.SharedAutoConfiguration;
import lombok.CustomLog;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.time.Duration;

@Configuration
@ConfigurationProperties( prefix = "pn.timeline-service")
@Data
@Import({SharedAutoConfiguration.class})
@CustomLog
public class PnTimelineServiceConfigs {

}
