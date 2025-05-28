package it.pagopa.pn.timelineservice.service.mapper;

import it.pagopa.pn.timelineservice.dto.legalfacts.LegalFactCategoryInt;
import it.pagopa.pn.timelineservice.dto.legalfacts.LegalFactsIdInt;
import it.pagopa.pn.timelineservice.dto.timeline.StatusInfoInternal;
import it.pagopa.pn.timelineservice.dto.timeline.TimelineElementInternal;
import it.pagopa.pn.timelineservice.dto.timeline.details.TimelineElementCategoryInt;
import it.pagopa.pn.timelineservice.dto.timeline.details.TimelineElementDetailsInt;
import it.pagopa.pn.timelineservice.generated.openapi.server.v1.dto.LegalFactsId;
import it.pagopa.pn.timelineservice.generated.openapi.server.v1.dto.StatusInfo;
import it.pagopa.pn.timelineservice.generated.openapi.server.v1.dto.TimelineElement;
import it.pagopa.pn.timelineservice.generated.openapi.server.v1.dto.TimelineElementDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class TimelineElementMapper {

    public TimelineElementInternal externalToInternal(TimelineElement timelineElement ) {
        TimelineElementCategoryInt category = timelineElement.getCategory() != null ? TimelineElementCategoryInt.valueOf(timelineElement.getCategory().getValue()) : null;

        assert category != null;
        return TimelineElementInternal.builder()
                .iun(timelineElement.getIun())
                .elementId( timelineElement.getElementId() )
                .category( category )
                .timestamp( timelineElement.getTimestamp() )
                .details( parseDetailsFromEntity( timelineElement.getDetails(), category) )
                .legalFactsIds( convertLegalFactsFromEntity( timelineElement.getLegalFactsIds() ) )
                .statusInfo(entityToStatusInfoInternal(timelineElement.getStatusInfo()))
                .notificationSentAt(timelineElement.getNotificationSentAt())
                .paId(timelineElement.getPaId())
                .build();
    }

    private List<LegalFactsIdInt> convertLegalFactsFromEntity(List<LegalFactsId>  entity ) {
        List<LegalFactsIdInt> legalFactsIds = null;

        if (entity != null){
            legalFactsIds = entity.stream().map( this::mapOneLegalFact ).toList();
        }

        return legalFactsIds;
    }

    private LegalFactsIdInt mapOneLegalFact(LegalFactsId legalFactsId) {
        assert legalFactsId.getCategory() != null;
        String legalFactCategoryName = legalFactsId.getCategory().getValue();
        return LegalFactsIdInt.builder()
                .key(legalFactsId.getKey())
                .category( LegalFactCategoryInt.valueOf( legalFactCategoryName ) )
                .build();
    }

    private TimelineElementDetailsInt parseDetailsFromEntity(TimelineElementDetails timelineElementDetails, TimelineElementCategoryInt category) {
        TimelineElementDetailsInt timelineElementDetailsInt = SmartMapper.mapToClass(timelineElementDetails, category.getDetailsJavaClass());
        timelineElementDetailsInt.setCategoryType(category.name());
        return timelineElementDetailsInt;
    }

    private StatusInfoInternal entityToStatusInfoInternal(StatusInfo statusInfo) {
        if(statusInfo == null) return null;

        return StatusInfoInternal.builder()
                .actual(statusInfo.getActual())
                .statusChanged(Boolean.TRUE.equals(statusInfo.getStatusChanged()))
                .statusChangeTimestamp(statusInfo.getStatusChangeTimestamp())
                .build();
    }


}
