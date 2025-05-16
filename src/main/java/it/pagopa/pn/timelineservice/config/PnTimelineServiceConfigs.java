package it.pagopa.pn.timelineservice.config;

import it.pagopa.pn.commons.conf.SharedAutoConfiguration;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.time.Duration;
import java.time.Instant;

@Configuration
@ConfigurationProperties( prefix = "pn.timeline-service")
@Data
@Import({SharedAutoConfiguration.class})
public class PnTimelineServiceConfigs {

    private String dataVaultBaseUrl;

    private TimelineDao timelineDao;

    private TimelinecounterDao timelinecounterDao;

    private LastPollForFutureActionDao lastPollForFutureActionDao;

    private Instant featureUnreachableRefinementPostAARStartDate;

    private Instant startWriteBusinessTimestamp;

    private Instant stopWriteBusinessTimestamp;

    private String pfNewWorkflowStart;

    private String pfNewWorkflowStop;

    private Duration timelineLockDuration;

    @Data
    public static class TimelineDao {
        private String tableName;
    }

    @Data
    public static class TimelinecounterDao {
        private String tableName;
    }

    @Data
    public static class LastPollForFutureActionDao {
        private String tableName;
        private String lockTableName;
    }

    @PostConstruct
    public void init() {
        System.out.println(this);
    }

}
