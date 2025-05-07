package it.pagopa.pn.timelineservice.config;

import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.core.LockProvider;
import net.javacrumbs.shedlock.provider.dynamodb2.DynamoDBLockProvider;
import net.javacrumbs.shedlock.spring.annotation.EnableSchedulerLock;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.scheduling.annotation.EnableScheduling;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

@EnableScheduling
@EnableSchedulerLock(defaultLockAtMostFor = "10m")
@Configuration
@Slf4j
public class PnTimelineServiceSchedulingConfiguration {

    @Bean
    @Primary
    public LockProvider lockProvider(DynamoDbClient dynamoDB, PnTimelineServiceConfigs cfg) {
        String lockTableName = cfg.getLastPollForFutureActionDao().getLockTableName();
        log.info("Shared Lock tableName={}", lockTableName);
        return new DynamoDBLockProvider(dynamoDB, lockTableName);
    }
}
