package it.pagopa.pn.timelineservice.config;

import net.javacrumbs.shedlock.core.LockProvider;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

class PnTimelineServiceSchedulingConfigurationTest {

    private PnTimelineServiceSchedulingConfiguration configuration;

    @BeforeEach
    void setUp() {
        configuration = new PnTimelineServiceSchedulingConfiguration();
    }

    @Test
    void lockProvider() {
        DynamoDbClient dynamoDB = DynamoDbClient.builder().build();
        PnTimelineServiceConfigs cfg = new PnTimelineServiceConfigs();
        PnTimelineServiceConfigs.LastPollForFutureActionDao dao = new PnTimelineServiceConfigs.LastPollForFutureActionDao();
        dao.setLockTableName("Lock");
        cfg.setLastPollForFutureActionDao(dao);
        LockProvider provider = configuration.lockProvider(dynamoDB, cfg);
        Assertions.assertNotNull(provider);
    }

}