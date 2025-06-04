package it.pagopa.pn.timelineservice.middleware.dao.dynamo;

import it.pagopa.pn.commons.exceptions.PnIdConflictException;
import it.pagopa.pn.timelineservice.config.PnTimelineServiceConfigs;
import it.pagopa.pn.timelineservice.dto.timeline.TimelineElementInternal;
import it.pagopa.pn.timelineservice.middleware.dao.TimelineDao;
import it.pagopa.pn.timelineservice.middleware.dao.dynamo.entity.DigitalAddressEntity;
import it.pagopa.pn.timelineservice.middleware.dao.dynamo.entity.PhysicalAddressEntity;
import it.pagopa.pn.timelineservice.middleware.dao.dynamo.entity.TimelineElementDetailsEntity;
import it.pagopa.pn.timelineservice.middleware.dao.dynamo.entity.TimelineElementEntity;
import it.pagopa.pn.timelineservice.middleware.dao.dynamo.mapper.DtoToEntityTimelineMapper;
import it.pagopa.pn.timelineservice.middleware.dao.dynamo.mapper.EntityToDtoTimelineMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.enhanced.dynamodb.*;
import software.amazon.awssdk.enhanced.dynamodb.model.GetItemEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.model.PutItemEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;
import software.amazon.awssdk.services.dynamodb.model.ConditionalCheckFailedException;

import java.util.Collections;

import static it.pagopa.pn.commons.abstractions.impl.AbstractDynamoKeyValueStore.ATTRIBUTE_NOT_EXISTS;
import static it.pagopa.pn.timelineservice.exceptions.PnTimelineServiceExceptionCodes.ERROR_CODE_TIMELINESERVICE_DUPLICATED_ITEM;
import static software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional.keyEqualTo;
import static software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional.sortBeginsWith;

@Component
@Slf4j
public class TimelineDaoDynamo implements TimelineDao {

    private final DynamoDbAsyncTable<TimelineElementEntity> table;
    private final DtoToEntityTimelineMapper dto2entity;
    private final EntityToDtoTimelineMapper entity2dto;

    public TimelineDaoDynamo(DynamoDbEnhancedAsyncClient dynamoDbEnhancedClient, PnTimelineServiceConfigs cfg, DtoToEntityTimelineMapper dto2entity,
                             EntityToDtoTimelineMapper entity2dto) {
        this.dto2entity = dto2entity;
        this.entity2dto = entity2dto;
        this.table = dynamoDbEnhancedClient.table(cfg.getTimelineDao().getTableName(), TableSchema.fromBean(TimelineElementEntity.class));
    }

    @Override
    public Mono<TimelineElementInternal> getTimelineElement(String iun, String elementId, boolean strongly) {
        GetItemEnhancedRequest request = GetItemEnhancedRequest.builder()
                .key(key -> key.partitionValue(iun).sortValue(elementId))
                .consistentRead(strongly)
                .build();

        return Mono.fromFuture(table.getItem(request))
                .map(entity2dto::entityToDto);
    }

    @Override
    public Flux<TimelineElementInternal> getTimeline(String iun) {
       return getTimeline(iun, false);
    }

    @Override
    public Flux<TimelineElementInternal> getTimelineStrongly(String iun) {
        return getTimeline(iun, true);
    }

    private Flux<TimelineElementInternal> getTimeline(String iun, boolean strongly) {
        QueryEnhancedRequest request = QueryEnhancedRequest.builder()
                .queryConditional(keyEqualTo(Key.builder().partitionValue(iun).build()))
                .consistentRead(strongly)
                .build();

        return Flux.from(table.query(request))
                .flatMap(page -> Flux.fromIterable(page.items()))
                .map(entity2dto::entityToDto);
    }

    @Override
    public Mono<Void> addTimelineElementIfAbsent(TimelineElementInternal dto) throws PnIdConflictException {
        TimelineElementEntity entity = getTimelineElementEntity(dto);
        return putIfAbsent(entity);
    }

    private TimelineElementEntity getTimelineElementEntity(TimelineElementInternal dto) {
        TimelineElementEntity entity = dto2entity.dtoToEntity(dto);

        TimelineElementDetailsEntity details = entity.getDetails();
        if (details != null) {
            TimelineElementDetailsEntity newDetails = cloneWithoutSensitiveInformation(details);
            entity.setDetails(newDetails);
        }
        return entity;
    }

    private TimelineElementDetailsEntity cloneWithoutSensitiveInformation(TimelineElementDetailsEntity details) {
        TimelineElementDetailsEntity newDetails = details.toBuilder().build();

        PhysicalAddressEntity physicalAddress = newDetails.getPhysicalAddress();
        if (physicalAddress != null) {
            newDetails.setPhysicalAddress(physicalAddress.toBuilder()
                    .at(null)
                    .municipalityDetails(null)
                    .addressDetails(null)
                    .province(null)
                    .municipality(null)
                    .address(null)
                    // NBBBB: zip e foreignState NON vanno eliminati, in quanto servono per la fatturazione
                    // li esplicito volutamente anche se non serve
                    .zip(physicalAddress.getZip())
                    .foreignState(physicalAddress.getForeignState())
                    .build());
        }

        PhysicalAddressEntity newAddress = newDetails.getNewAddress();
        if (newAddress != null) {
            newDetails.setNewAddress(newAddress.toBuilder()
                    .at(null)
                    .municipalityDetails(null)
                    .zip(null)
                    .addressDetails(null)
                    .province(null)
                    .municipality(null)
                    .foreignState(null)
                    .address(null)
                    .build());
        }

        DigitalAddressEntity digitalAddress = newDetails.getDigitalAddress();
        if (digitalAddress != null) {
            newDetails.setDigitalAddress(digitalAddress.toBuilder()
                    .address(null)
                    .build());
        }
        return newDetails;
    }

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
                            ERROR_CODE_TIMELINESERVICE_DUPLICATED_ITEM,
                            Collections.singletonMap("timelineElementId", value.getTimelineElementId()),
                            ex
                    );
                });
    }

    @Override
    public Flux<TimelineElementInternal> getTimelineFilteredByElementId(String iun, String elementId) {
        return searchByIunAndElementId(iun, elementId)
                .map(entity2dto::entityToDto);
    }

    public Flux<TimelineElementEntity> searchByIunAndElementId(String iun, String elementId) {
        Key hashKey = Key.builder().partitionValue(iun).sortValue(elementId).build();
        QueryConditional queryByHashKey = sortBeginsWith(hashKey);
        return Flux.from(table.query(queryByHashKey))
                .flatMap(page -> Flux.fromIterable(page.items()));
    }
}
