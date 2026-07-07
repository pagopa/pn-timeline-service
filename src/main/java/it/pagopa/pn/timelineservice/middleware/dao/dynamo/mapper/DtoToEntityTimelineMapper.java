package it.pagopa.pn.timelineservice.middleware.dao.dynamo.mapper;

import it.pagopa.pn.timelineservice.dto.legalfacts.LegalFactsIdInt;
import it.pagopa.pn.timelineservice.dto.timeline.CommunicationType;
import it.pagopa.pn.timelineservice.dto.timeline.StatusInfoInternal;
import it.pagopa.pn.timelineservice.dto.timeline.TimelineElementInternal;
import it.pagopa.pn.timelineservice.dto.timeline.details.TimelineElementDetailsInt;
import it.pagopa.pn.timelineservice.middleware.dao.dynamo.entity.*;
import it.pagopa.pn.timelineservice.service.mapper.SmartMapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DtoToEntityTimelineMapper {
    
    public TimelineElementEntity dtoToEntity(TimelineElementInternal dto) {
        return TimelineElementEntity.builder()
                .iun( dto.getIun() )
                .timelineElementId( dto.getElementId() )
                .paId( dto.getPaId() )
                .category( TimelineElementCategoryEntity.valueOf( dto.getCategory().name() ) )
                .timestamp( dto.getTimestamp() )
                .reworkId(dto.getReworkId())
                .reworkRequestType(dto.getReworkRequestType())
                .details( dtoToDetailsEntity( dto.getDetails() ) )
                .legalFactIds( convertLegalFactsToEntity( dto.getLegalFactsIds() ) )
                .statusInfo(dtoToStatusInfoEntity(dto.getStatusInfo()))
                .notificationSentAt(dto.getNotificationSentAt())
                .businessTimestamp(dto.getEventTimestamp())
                .communicationType(mapCommunicationType(dto.getCommunicationType()))
                .build();
    }



    private List<LegalFactsIdEntity> convertLegalFactsToEntity(List<LegalFactsIdInt>  dto ) {
        List<LegalFactsIdEntity> legalFactsIds = null;

        if (dto != null){
            legalFactsIds = dto.stream().map( this::mapOneLegalFact ).toList();
        }

        return legalFactsIds;
    }

    private LegalFactsIdEntity mapOneLegalFact(LegalFactsIdInt legalFactsId) {
        LegalFactsIdEntity entity = new LegalFactsIdEntity();
        entity.setKey( legalFactsId.getKey() );
        entity.setCategory(LegalFactCategoryEntity.valueOf( legalFactsId.getCategory().getValue()));
        return entity;
    }
    
    private TimelineElementDetailsEntity dtoToDetailsEntity(TimelineElementDetailsInt details) {
        return SmartMapper.mapToClass(details, TimelineElementDetailsEntity.class );
    }

    private StatusInfoEntity dtoToStatusInfoEntity(StatusInfoInternal statusInfoInternal) {
        if(statusInfoInternal == null) return null;
        return StatusInfoEntity.builder()
                .statusChangeTimestamp(statusInfoInternal.getStatusChangeTimestamp())
                .statusChanged(statusInfoInternal.isStatusChanged())
                .actual(statusInfoInternal.getActual())
                .build();
    }

    /**
     * Mappatura che evita di salvare il campo communicationType per gli eventi di categoria LEGAL,
     * Invece se il communicationType è definito e ha un altro valore allora viene persistito normalmente.
     * @param communicationType il communicationType da mappare
     * @return null se communicationType è null o LEGAL, altrimenti communicationType stesso
     */
    private CommunicationType mapCommunicationType(CommunicationType communicationType) {
        if(communicationType == null || communicationType == CommunicationType.LEGAL) return null;

        return communicationType;
    }
}
