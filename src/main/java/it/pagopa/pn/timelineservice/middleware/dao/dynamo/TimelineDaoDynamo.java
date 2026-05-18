package it.pagopa.pn.timelineservice.middleware.dao.dynamo;

import it.pagopa.pn.commons.exceptions.PnIdConflictException;
import it.pagopa.pn.commons.exceptions.PnInternalException;
import it.pagopa.pn.timelineservice.config.PnTimelineServiceConfigs;
import it.pagopa.pn.timelineservice.dto.timeline.TimelineElementInternal;
import it.pagopa.pn.timelineservice.dto.timeline.TimelineEventIdParser;
import it.pagopa.pn.timelineservice.middleware.dao.TimelineDao;
import it.pagopa.pn.timelineservice.middleware.dao.dynamo.entity.DigitalAddressEntity;
import it.pagopa.pn.timelineservice.middleware.dao.dynamo.entity.PhysicalAddressEntity;
import it.pagopa.pn.timelineservice.middleware.dao.dynamo.entity.TimelineElementDetailsEntity;
import it.pagopa.pn.timelineservice.middleware.dao.dynamo.entity.TimelineElementEntity;
import it.pagopa.pn.timelineservice.middleware.dao.dynamo.mapper.DtoToEntityTimelineMapper;
import it.pagopa.pn.timelineservice.middleware.dao.dynamo.mapper.EntityToDtoTimelineMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.enhanced.dynamodb.*;
import software.amazon.awssdk.enhanced.dynamodb.model.GetItemEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.model.PutItemEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;
import software.amazon.awssdk.services.dynamodb.model.ConditionalCheckFailedException;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static it.pagopa.pn.commons.abstractions.impl.AbstractDynamoKeyValueStore.ATTRIBUTE_NOT_EXISTS;
import static it.pagopa.pn.timelineservice.exceptions.PnTimelineServiceExceptionCodes.ERROR_CODE_TIMELINESERVICE_DUPLICATED_ITEM;
import static it.pagopa.pn.timelineservice.middleware.dao.dynamo.entity.TimelineElementCategoryEntity.NOTIFICATION_TIMELINE_REWORKED;
import static it.pagopa.pn.timelineservice.utils.NotificationReworkUtils.*;
import static software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional.keyEqualTo;
import static software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional.sortBeginsWith;

@Component
@Slf4j
public class TimelineDaoDynamo implements TimelineDao {

    private final DynamoDbAsyncTable<TimelineElementEntity> table;
    private final DtoToEntityTimelineMapper dto2entity;
    private final EntityToDtoTimelineMapper entity2dto;
    private final PnTimelineServiceConfigs cfg;

    public TimelineDaoDynamo(DynamoDbEnhancedAsyncClient dynamoDbEnhancedClient, PnTimelineServiceConfigs cfg, DtoToEntityTimelineMapper dto2entity,
                             EntityToDtoTimelineMapper entity2dto) {
        this.dto2entity = dto2entity;
        this.entity2dto = entity2dto;
        this.cfg = cfg;
        this.table = dynamoDbEnhancedClient.table(cfg.getTimelineDao().getTableName(), TableSchema.fromBean(TimelineElementEntity.class));
    }

    @Override
    public Mono<TimelineElementInternal> getTimelineElement(String iun, String elementId, boolean strongly) {
        if (isReworkedId(elementId)) {
            return getTimelineAndFilterWithElementId(iun, elementId).next();
        }
        return retrieveCorrectElementIdIfReworked(iun, elementId, strongly)
                .switchIfEmpty(Mono.just(elementId))
                .doOnNext(timelineId ->  log.info("Call getTimeline with timelineId {} ", timelineId))
                .map(updatedElementId -> GetItemEnhancedRequest.builder()
                        .key(key -> key.partitionValue(iun).sortValue(updatedElementId))
                        .consistentRead(strongly)
                        .build())
                .flatMap(getItemEnhancedRequest -> Mono.fromFuture(table.getItem(getItemEnhancedRequest)))
                .map(timelineElementEntity -> entity2dto.entityToDto(timelineElementEntity, null));

    }

    @Override
    public Flux<TimelineElementInternal> getTimeline(String iun) {
        return getTimeline(iun, false);
    }

    @Override
    public Flux<TimelineElementInternal> getTimelineStrongly(String iun) {
        return getTimeline(iun, true);
    }

    @Override
    public Flux<TimelineElementInternal> getTimelineFilteredByElementId(String iun, String elementId, boolean strongly) {
        if (isReworkedId(elementId)) {
            return getTimelineAndFilterWithElementId(iun, elementId);
        }

        return searchByIunAndElementId(iun, elementId)
                .collectList()
                .flatMapMany(timelineElementEntities -> filterForReworkedElementIdIfExists(iun, timelineElementEntities, strongly, elementId))
                .map(timelineElementEntity -> entity2dto.entityToDto(timelineElementEntity, null));
    }

    private Flux<TimelineElementInternal> getTimelineAndFilterWithElementId(String iun, String elementId) {
        return getTimeline(iun)
                .filter(timelineElementInternal -> timelineElementInternal.getElementId().startsWith(elementId));
    }

    private boolean isReworkedId(String elementId) {
        return StringUtils.hasText(elementId) && elementId.startsWith(NOTIFICATION_TIMELINE_REWORKED.name());
    }


    @Override
    public Mono<Void> addTimelineElementIfAbsent(TimelineElementInternal dto) throws PnIdConflictException {
        TimelineElementEntity entity = getTimelineElementEntity(dto);
        return putIfAbsent(entity);
    }


    private Mono<String> retrieveCorrectElementIdIfReworked(String iun, String timelineId, boolean strongly) {
        TimelineEventIdParser parser = TimelineEventIdParser.parse(timelineId);
        if(parser.reworkIndexFull().isPresent()){
            return Mono.just(timelineId);
        }

        return parser.category()
                .filter(cfg.getInvalidableCategories()::contains)
                .map(cat -> getReworkTimelineElementsIfExists(iun, strongly, timelineId)
                        .collectList()
                        .filter(reworkElementsInternal -> !CollectionUtils.isEmpty(reworkElementsInternal))
                        .map(reworkElementsInternal -> checkReworkAttemptAndReturnSuffix(reworkElementsInternal, timelineId).getTimelineElementId()))
                .orElse(Mono.just(timelineId));
    }

    private Flux<TimelineElementInternal> getTimeline(String iun, boolean strongly) {
        Map<String,TimelineElementInternal> invalidatedTimelineElements = new HashMap<>();
        QueryEnhancedRequest request = QueryEnhancedRequest.builder()
                .queryConditional(keyEqualTo(Key.builder().partitionValue(iun).build()))
                .consistentRead(strongly)
                .build();

        return Flux.from(table.query(request))
                .flatMap(page -> Flux.fromIterable(page.items()))
                .collectList()
                .doOnNext(entities -> {
                    Map<String,TimelineElementInternal> invalidatedElementMap = checkIfReworksArePresentAndRetrieveInvalidatedElements(entities);
                    invalidatedTimelineElements.putAll(invalidatedElementMap);
                })
                .flatMapMany(Flux::fromIterable)
                .map(entity -> entity2dto.entityToDto(entity, invalidatedTimelineElements))
                .filter(timelineElementInternal -> isNotInvalidated(timelineElementInternal, invalidatedTimelineElements));
    }

    private Map<String,TimelineElementInternal> checkIfReworksArePresentAndRetrieveInvalidatedElements(List<TimelineElementEntity> entities) {
        if(!CollectionUtils.isEmpty(entities) &&
                entities.stream().anyMatch(timelineElementEntity -> timelineElementEntity.getCategory().equals(NOTIFICATION_TIMELINE_REWORKED))) {
            return getInvalidatedTimelineElementIds(entities);
        }
        return Map.of();
    }

    private Map<String,TimelineElementInternal> getInvalidatedTimelineElementIds(List<TimelineElementEntity> entities) {
        List<String> invalidatedTimelineElementIds = getInvalidatedTimelineElementsIds(entities);
        return entities.stream()
                .filter(elem -> invalidatedTimelineElementIds.contains(elem.getTimelineElementId()))
                .map(timelineElementEntity -> entity2dto.entityToDto(timelineElementEntity, null))
                .collect(Collectors.toMap(TimelineElementInternal::getElementId, Function.identity()));
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
                    .municipality(physicalAddress.getMunicipality())
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
                    .zip(newAddress.getZip())
                    .addressDetails(null)
                    .province(null)
                    .municipality(newAddress.getMunicipality())
                    .foreignState(newAddress.getForeignState())
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



    public Flux<TimelineElementEntity> searchByIunAndElementId(String iun, String elementId) {
        Key hashKey = Key.builder().partitionValue(iun).sortValue(elementId).build();
        QueryConditional queryByHashKey = sortBeginsWith(hashKey);
        return Flux.from(table.query(queryByHashKey))
                .flatMap(page -> Flux.fromIterable(page.items()));
    }

    private Flux<TimelineElementEntity> filterForReworkedElementIdIfExists(String iun, List<TimelineElementEntity> timelineElementEntities, boolean strongly, String elementId) {
        return getReworkTimelineElementsIfExists(iun, strongly, elementId)
                .collectList()
                .map(reworkElementsInternal -> removeInvalidatedElement(reworkElementsInternal,timelineElementEntities))
                .defaultIfEmpty(timelineElementEntities)
                .flatMapIterable(timelineElementInternals -> timelineElementInternals);
    }

    private Flux<TimelineElementInternal> getReworkTimelineElementsIfExists(String iun, boolean strongly, String timelineElementId) {
        QueryEnhancedRequest request = QueryEnhancedRequest.builder()
                .queryConditional(QueryConditional.sortBeginsWith(
                        Key.builder().partitionValue(iun).sortValue(NOTIFICATION_TIMELINE_REWORKED.getValue()).build()))
                .scanIndexForward(false)
                .consistentRead(strongly)
                .build();

        return Flux.from(table.query(request))
                .flatMap(page -> Flux.fromIterable(page.items()))
                .filter(timelineElementEntity -> TimelineEventIdParser.parse(timelineElementEntity.getTimelineElementId()).recIndex()
                        .orElseThrow(() -> new PnInternalException("RecIndex not present in timelineElementId " + timelineElementId, "ERROR_CODE_TIMELINESERVICE_INVALID_TIMELINE_ID"))
                        .equals(TimelineEventIdParser.parse(timelineElementId).recIndex().orElse(null)))
                .map(timelineElementEntity -> entity2dto.entityToDto(timelineElementEntity, null));
    }
}
