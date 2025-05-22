package it.pagopa.pn.timelineservice.middleware.dao.dynamo;

import it.pagopa.pn.commons.exceptions.PnIdConflictException;
import it.pagopa.pn.timelineservice.dto.timeline.TimelineElementInternal;
import it.pagopa.pn.timelineservice.middleware.dao.TimelineDao;
import it.pagopa.pn.timelineservice.middleware.dao.TimelineEntityDao;
import it.pagopa.pn.timelineservice.middleware.dao.dynamo.entity.DigitalAddressEntity;
import it.pagopa.pn.timelineservice.middleware.dao.dynamo.entity.PhysicalAddressEntity;
import it.pagopa.pn.timelineservice.middleware.dao.dynamo.entity.TimelineElementDetailsEntity;
import it.pagopa.pn.timelineservice.middleware.dao.dynamo.entity.TimelineElementEntity;
import it.pagopa.pn.timelineservice.middleware.dao.dynamo.mapper.DtoToEntityTimelineMapper;
import it.pagopa.pn.timelineservice.middleware.dao.dynamo.mapper.EntityToDtoTimelineMapper;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class TimelineDaoDynamo implements TimelineDao {
    private final TimelineEntityDao entityDao;
    private final DtoToEntityTimelineMapper dto2entity;
    private final EntityToDtoTimelineMapper entity2dto;

    public TimelineDaoDynamo(TimelineEntityDao entityDao, DtoToEntityTimelineMapper dto2entity,
                             EntityToDtoTimelineMapper entity2dto) {
        this.entityDao = entityDao;
        this.dto2entity = dto2entity;
        this.entity2dto = entity2dto;
    }

    @Override
    public Mono<TimelineElementInternal> getTimelineElement(String iun, String elementId, boolean strong) {
        if (strong) {
            return entityDao.getTimelineElementStrongly(iun, elementId)
                    .map(entity2dto::entityToDto);
        } else {
            return entityDao.getTimelineElement(iun, elementId)
                    .map(entity2dto::entityToDto);
        }
    }

    @Override
    public Flux<TimelineElementInternal> getTimeline(String iun) {
        return entityDao.findByIun(iun)
                .map(entity2dto::entityToDto);
    }

    @Override
    public Mono<Void> addTimelineElementIfAbsent(TimelineElementInternal dto) throws PnIdConflictException {
        TimelineElementEntity entity = getTimelineElementEntity(dto);
        return entityDao.putIfAbsent(entity);
    }
    
    @NotNull
    private TimelineElementEntity getTimelineElementEntity(TimelineElementInternal dto) {
        TimelineElementEntity entity = dto2entity.dtoToEntity(dto);

        TimelineElementDetailsEntity details = entity.getDetails();
        if (details != null) {

            TimelineElementDetailsEntity newDetails = cloneWithoutSensitiveInformation(details);
            entity.setDetails(newDetails);
        }
        return entity;
    }
    
    @NotNull
    private TimelineElementDetailsEntity cloneWithoutSensitiveInformation(TimelineElementDetailsEntity details) {
        TimelineElementDetailsEntity newDetails = details.toBuilder().build();
        
        PhysicalAddressEntity physicalAddress = newDetails.getPhysicalAddress();
        if( physicalAddress != null ) {
            newDetails.setPhysicalAddress( physicalAddress.toBuilder()
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
        if( newAddress != null ) {
            newDetails.setNewAddress( newAddress.toBuilder()
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
        if( digitalAddress != null ) {
            newDetails.setDigitalAddress( digitalAddress.toBuilder()
                    .address( null )
                    .build());
        }

        
        return newDetails;
    }


    @Override
    public Flux<TimelineElementInternal> getTimelineStrongly(String iun) {
        return entityDao.findByIunStrongly(iun)
                .map(entity2dto::entityToDto);
    }

    @Override
    public Flux<TimelineElementInternal> getTimelineFilteredByElementId(String iun, String elementId) {
        return entityDao.searchByIunAndElementId(iun, elementId)
                .map(entity2dto::entityToDto);
    }

    @Override
    public Mono<Void> deleteTimeline(String iun) {
        return entityDao.deleteByIun(iun);
    }

}
