package it.pagopa.pn.timelineservice.middleware.dao.dynamo;


import it.pagopa.pn.commons.abstractions.impl.MiddlewareTypes;
import it.pagopa.pn.timelineservice.config.PnTimelineServiceConfigs;
import it.pagopa.pn.timelineservice.middleware.dao.TimelineCounterEntityDao;
import it.pagopa.pn.timelineservice.middleware.dao.dynamo.entity.TimelineCounterEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbAsyncTable;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.UpdateItemEnhancedRequest;

@Component
@ConditionalOnProperty(name = TimelineCounterEntityDao.IMPLEMENTATION_TYPE_PROPERTY_NAME, havingValue = MiddlewareTypes.DYNAMO)
@Slf4j
public class TimelineCounterEntityDaoDynamo implements TimelineCounterEntityDao {
    private final DynamoDbAsyncTable<TimelineCounterEntity> table;

    public TimelineCounterEntityDaoDynamo(DynamoDbEnhancedAsyncClient dynamoDbEnhancedAsyncClient, PnTimelineServiceConfigs cfg) {
        this.table = dynamoDbEnhancedAsyncClient.table( tableName(cfg), TableSchema.fromClass(TimelineCounterEntity.class));
    }
 
    private static String tableName( PnTimelineServiceConfigs cfg ) {
        return cfg.getTimelinecounterDao().getTableName();
    }


    @Override
    public Mono<TimelineCounterEntity> getCounter(String timelineElementId) {
        return Mono.fromFuture(table.updateItem(createUpdateItemEnhancedRequest(timelineElementId)));
    }

    protected UpdateItemEnhancedRequest<TimelineCounterEntity> createUpdateItemEnhancedRequest(String timelineElementId) {
        TimelineCounterEntity counterModel = new TimelineCounterEntity();
        counterModel.setTimelineElementId(timelineElementId);
        return UpdateItemEnhancedRequest
                .builder(TimelineCounterEntity.class)
                .item(counterModel)
                .build();
    }
}
