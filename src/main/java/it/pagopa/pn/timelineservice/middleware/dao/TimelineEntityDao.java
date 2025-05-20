package it.pagopa.pn.timelineservice.middleware.dao;

import it.pagopa.pn.timelineservice.middleware.dao.dynamo.entity.TimelineElementEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface TimelineEntityDao {

    Flux<TimelineElementEntity> findByIun(String iun );

    Flux<TimelineElementEntity> findByIunStrongly(String iun );

    Mono<TimelineElementEntity> getTimelineElementStrongly(String iun, String timelineId);

    Mono<TimelineElementEntity> getTimelineElement(String iun, String timelineId);

    Mono<Void> putIfAbsent(TimelineElementEntity entity);

    /**
     * Ricerca le timeline per IUN e per elementId con ricerca "INIZIA PER"
     * @param iun iun della notifica
     * @param elementId elementId (anche parziale) da ricercare tramite "inizia per"
     * @return insieme di timeline
     */
    Flux<TimelineElementEntity> searchByIunAndElementId(String iun, String elementId );

    Mono<Void> deleteByIun(String iun);
}
