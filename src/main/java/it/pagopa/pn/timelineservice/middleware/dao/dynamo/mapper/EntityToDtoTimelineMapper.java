package it.pagopa.pn.timelineservice.middleware.dao.dynamo.mapper;

import it.pagopa.pn.timelineservice.dto.legalfacts.LegalFactCategoryInt;
import it.pagopa.pn.timelineservice.dto.legalfacts.LegalFactsIdInt;
import it.pagopa.pn.timelineservice.dto.timeline.CommunicationType;
import it.pagopa.pn.timelineservice.dto.timeline.ReworkRequestTypeEnum;
import it.pagopa.pn.timelineservice.dto.timeline.StatusInfoInternal;
import it.pagopa.pn.timelineservice.dto.timeline.TimelineElementInternal;
import it.pagopa.pn.timelineservice.dto.timeline.details.TimelineElementCategoryInt;
import it.pagopa.pn.timelineservice.dto.timeline.details.common.TimelineElementDetailsInt;
import it.pagopa.pn.timelineservice.dto.timeline.details.legal.NotificationTimelineReworkedDetailsInt;
import it.pagopa.pn.timelineservice.middleware.dao.dynamo.entity.LegalFactsIdEntity;
import it.pagopa.pn.timelineservice.middleware.dao.dynamo.entity.StatusInfoEntity;
import it.pagopa.pn.timelineservice.middleware.dao.dynamo.entity.TimelineElementDetailsEntity;
import it.pagopa.pn.timelineservice.middleware.dao.dynamo.entity.TimelineElementEntity;
import it.pagopa.pn.timelineservice.service.mapper.SmartMapper;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class EntityToDtoTimelineMapper {
    
    public TimelineElementInternal entityToDto(TimelineElementEntity entity, Map<String,TimelineElementInternal> invalidatedTimelineElements ) {
        TimelineElementCategoryInt category = entity.getCategory() != null ? TimelineElementCategoryInt.valueOf(entity.getCategory().getValue()) : null;

        assert category != null;
        return TimelineElementInternal.builder()
                .iun(entity.getIun())
                .reworkId(entity.getReworkId())
                .reworkRequestType(Optional.ofNullable(entity.getReworkRequestType())
                        .map(ReworkRequestTypeEnum::valueOf)
                        .orElse(null))
                .campaignId(entity.getCampaignId())
                .elementId( entity.getTimelineElementId() )
                .category( category )
                .timestamp( entity.getTimestamp() )
                .details(parseDetailsFromEntity(entity.getDetails(), category, invalidatedTimelineElements))
                .legalFactsIds( convertLegalFactsFromEntity( entity.getLegalFactIds() ) )
                .statusInfo(entityToStatusInfoInternal(entity.getStatusInfo()))
                .notificationSentAt(entity.getNotificationSentAt())
                .paId(entity.getPaId())
                .eventTimestamp(entity.getBusinessTimestamp())
                // Nel caso in cui communicationType sia null, assumo che si tratti di un elemento legale
                .communicationType(entity.getCommunicationType() == null ? CommunicationType.LEGAL : entity.getCommunicationType())
                .build();
    }

    private List<LegalFactsIdInt> convertLegalFactsFromEntity(List<LegalFactsIdEntity>  entity ) {
        List<LegalFactsIdInt> legalFactsIds = null;
        
        if (entity != null){
            legalFactsIds = entity.stream().map( this::mapOneLegalFact ).toList();
        }
        
        return legalFactsIds;
    }

    private LegalFactsIdInt mapOneLegalFact(LegalFactsIdEntity legalFactsIdEntity) {
        String legalFactCategoryName = legalFactsIdEntity.getCategory().getValue();
        return LegalFactsIdInt.builder()
                .key(legalFactsIdEntity.getKey())
                .category( LegalFactCategoryInt.valueOf( legalFactCategoryName ) )
                .build();
    }

    private TimelineElementDetailsInt parseDetailsFromEntity(TimelineElementDetailsEntity entity, TimelineElementCategoryInt category, Map<String, TimelineElementInternal> invalidatedTimelineElements) {
        TimelineElementDetailsInt timelineElementDetailsInt = SmartMapper.mapToClass(entity, category.getDetailsJavaClass());
        if(timelineElementDetailsInt == null) {
            return null;
        }
        timelineElementDetailsInt.setCategoryType(category.name());
        if(category.equals(TimelineElementCategoryInt.NOTIFICATION_TIMELINE_REWORKED) && !CollectionUtils.isEmpty(invalidatedTimelineElements)){
            remapTimelineReworkDetails((NotificationTimelineReworkedDetailsInt) timelineElementDetailsInt, invalidatedTimelineElements);
        }
        return timelineElementDetailsInt;
    }

    private void remapTimelineReworkDetails(NotificationTimelineReworkedDetailsInt timelineElementDetailsInt, Map<String, TimelineElementInternal> invalidatedTimelineElements) {
        timelineElementDetailsInt.getInvalidatedTimelineAndStatusHistory()
                .forEach(notificationStatusHistoryElementInt -> notificationStatusHistoryElementInt.getRelatedTimelineElementIds()
                        .forEach(elementId -> Optional.ofNullable(invalidatedTimelineElements.get(elementId))
                                .map(timelineElementInternal -> notificationStatusHistoryElementInt.getRelatedTimelineElements().add(timelineElementInternal))));
    }

    private StatusInfoInternal entityToStatusInfoInternal(StatusInfoEntity entity) {
        if(entity == null) return null;

        return StatusInfoInternal.builder()
                .actual(entity.getActual())
                .statusChanged(entity.isStatusChanged())
                .statusChangeTimestamp(entity.getStatusChangeTimestamp())
                .build();
    }
}
