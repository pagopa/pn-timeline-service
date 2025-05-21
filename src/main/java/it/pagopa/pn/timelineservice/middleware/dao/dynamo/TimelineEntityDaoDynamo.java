package it.pagopa.pn.timelineservice.middleware.dao.dynamo;

import it.pagopa.pn.commons.exceptions.PnIdConflictException;
import it.pagopa.pn.timelineservice.config.PnTimelineServiceConfigs;
import it.pagopa.pn.timelineservice.middleware.dao.TimelineEntityDao;
import it.pagopa.pn.timelineservice.middleware.dao.dynamo.entity.TimelineElementEntity;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.enhanced.dynamodb.*;
import software.amazon.awssdk.enhanced.dynamodb.model.*;
import software.amazon.awssdk.services.dynamodb.model.ConditionalCheckFailedException;

import java.util.Collections;

import static it.pagopa.pn.commons.abstractions.impl.AbstractDynamoKeyValueStore.ATTRIBUTE_NOT_EXISTS;
import static it.pagopa.pn.timelineservice.exceptions.PnDeliveryPushExceptionCodes.ERROR_CODE_DELIVERYPUSH_DUPLICATED_ITEMD;
import static software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional.keyEqualTo;
import static software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional.sortBeginsWith;

@Component
@Slf4j
public class TimelineEntityDaoDynamo implements TimelineEntityDao {

    private final DynamoDbAsyncTable<TimelineElementEntity> table;

    public TimelineEntityDaoDynamo(DynamoDbEnhancedAsyncClient dynamoDbEnhancedClient, PnTimelineServiceConfigs cfg) {
        this.table = dynamoDbEnhancedClient.table(tableName(cfg), TableSchema.fromBean(TimelineElementEntity.class));
    }

    private static String tableName( PnTimelineServiceConfigs cfg ) {
        return cfg.getTimelineDao().getTableName();
    }

    @Override
    public Flux<TimelineElementEntity> findByIun(String iun) {
        Key hashKey = Key.builder().partitionValue(iun).build();
        QueryConditional queryByHashKey = keyEqualTo(hashKey);
        return Flux.from(table.query(queryByHashKey))
                .flatMap(page -> Flux.fromIterable(page.items()));
    }

    @Override
    public Flux<TimelineElementEntity> findByIunStrongly(String iun) {
        Key hashKey = Key.builder().partitionValue(iun).build();
        QueryConditional queryByHashKey = keyEqualTo(hashKey);
        QueryEnhancedRequest enhancedRequest = QueryEnhancedRequest.builder()
                .queryConditional(queryByHashKey)
                .consistentRead(true)
                .build();
        return Flux.from(table.query(enhancedRequest))
                .flatMap(page -> Flux.fromIterable(page.items()));
    }

    @Override
    public Mono<TimelineElementEntity> getTimelineElementStrongly(String iun, String timelineId) {
        GetItemEnhancedRequest request = GetItemEnhancedRequest.builder()
                .key(key -> key
                    .partitionValue(iun)
                    .sortValue(timelineId))
                .consistentRead(true)
                .build();

        return Mono.fromFuture(table.getItem(request));
    }

    @Override
    public Mono<TimelineElementEntity> getTimelineElement(String iun, String timelineId) {
        GetItemEnhancedRequest request = GetItemEnhancedRequest.builder()
                .key(key -> key
                        .partitionValue(iun)
                        .sortValue(timelineId))
                .build();

        return Mono.fromFuture(table.getItem(request));
    }

    @Override
    public Flux<TimelineElementEntity> searchByIunAndElementId(String iun, String elementId) {
        Key hashKey = Key.builder().partitionValue(iun).sortValue(elementId).build();
        QueryConditional queryByHashKey = sortBeginsWith(hashKey);
        return Flux.from(table.query(queryByHashKey))
                .flatMap(page -> Flux.fromIterable(page.items()));
    }

    @Override
    public Mono<Void> deleteByIun(String iun) {
        return findByIunStrongly(iun)
                .flatMap(entity -> deleteTimelineElement(iun, entity.getTimelineElementId())).then();
    }

    private @NotNull Mono<TimelineElementEntity> deleteTimelineElement(String iun, String elementId) {
        return Mono.fromFuture(table.deleteItem(Key.builder()
                .partitionValue(iun)
                .sortValue(elementId)
                .build()));
    }

    @Override
    public Mono<Void> putIfAbsent(TimelineElementEntity value) throws PnIdConflictException {
        String expression = String.format(
                "%s(%s) AND %s(%s)",
                ATTRIBUTE_NOT_EXISTS,
                TimelineElementEntity.FIELD_IUN,
                ATTRIBUTE_NOT_EXISTS,
                TimelineElementEntity.FIELD_TIMELINE_ELEMENT_ID
        );

        Expression conditionExpressionPut = Expression.builder()
                .expression(expression)
                .build();

        PutItemEnhancedRequest<TimelineElementEntity> request = PutItemEnhancedRequest.builder(TimelineElementEntity.class)
                .item(value)
                .conditionExpression(conditionExpressionPut)
                .build();

        return Mono.fromFuture(table.putItem(request))
                .onErrorMap(ConditionalCheckFailedException.class, ex -> {
                    log.warn("Conditional check exception on TimelineEntityDaoDynamo putIfAbsent timelineId={} exmessage={}", value.getTimelineElementId(), ex.getMessage());
                    return new PnIdConflictException(
                        ERROR_CODE_DELIVERYPUSH_DUPLICATED_ITEMD,
                        Collections.singletonMap("timelineElementId", value.getTimelineElementId()),
                        ex
                    );
                });
    }
}

