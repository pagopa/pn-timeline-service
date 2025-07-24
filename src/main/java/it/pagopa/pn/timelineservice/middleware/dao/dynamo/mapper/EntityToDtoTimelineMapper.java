package it.pagopa.pn.timelineservice.middleware.dao.dynamo.mapper;

import it.pagopa.pn.timelineservice.dto.legalfacts.LegalFactCategoryInt;
import it.pagopa.pn.timelineservice.dto.legalfacts.LegalFactsIdInt;
import it.pagopa.pn.timelineservice.dto.timeline.StatusInfoInternal;
import it.pagopa.pn.timelineservice.dto.timeline.TimelineElementInternal;
import it.pagopa.pn.timelineservice.dto.timeline.details.TimelineElementCategoryInt;
import it.pagopa.pn.timelineservice.dto.timeline.details.TimelineElementDetailsInt;
import it.pagopa.pn.timelineservice.middleware.dao.dynamo.entity.LegalFactsIdEntity;
import it.pagopa.pn.timelineservice.middleware.dao.dynamo.entity.StatusInfoEntity;
import it.pagopa.pn.timelineservice.middleware.dao.dynamo.entity.TimelineElementDetailsEntity;
import it.pagopa.pn.timelineservice.middleware.dao.dynamo.entity.TimelineElementEntity;
import it.pagopa.pn.timelineservice.service.mapper.SmartMapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class EntityToDtoTimelineMapper {
    
    public TimelineElementInternal entityToDto(TimelineElementEntity entity ) {
        TimelineElementCategoryInt category = entity.getCategory() != null ? TimelineElementCategoryInt.valueOf(entity.getCategory().getValue()) : null;

        assert category != null;
        return TimelineElementInternal.builder()
                .iun(entity.getIun())
                .elementId( entity.getTimelineElementId() )
                .category( category )
                .timestamp( entity.getTimestamp() )
                .details( parseDetailsFromEntity( entity.getDetails(), category) )
                .legalFactsIds( convertLegalFactsFromEntity( entity.getLegalFactIds() ) )
                .statusInfo(entityToStatusInfoInternal(entity.getStatusInfo()))
                .notificationSentAt(entity.getNotificationSentAt())
                .paId(entity.getPaId())
                .eventTimestamp(entity.getBusinessTimestamp())
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

    private TimelineElementDetailsInt parseDetailsFromEntity(TimelineElementDetailsEntity entity, TimelineElementCategoryInt category) {
        TimelineElementDetailsInt timelineElementDetailsInt = SmartMapper.mapToClass(entity, category.getDetailsJavaClass());
        if(timelineElementDetailsInt == null) {
            return null;
        }
        timelineElementDetailsInt.setCategoryType(category.name());
        return timelineElementDetailsInt;
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
