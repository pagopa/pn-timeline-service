package it.pagopa.pn.timelineservice.middleware.dao;

import it.pagopa.pn.commons.abstractions.KeyValueStore;
import it.pagopa.pn.timelineservice.middleware.dao.dynamo.entity.TimelineElementEntity;
import software.amazon.awssdk.enhanced.dynamodb.Key;

import java.util.Optional;
import java.util.Set;

public interface TimelineEntityDao extends KeyValueStore<Key, TimelineElementEntity> {

    Set<TimelineElementEntity> findByIun(String iun );

    Set<TimelineElementEntity> findByIunStrongly(String iun );

    Optional<TimelineElementEntity> getTimelineElementStrongly(String iun, String timelineId);

    /**
     * Ricerca le timeline per IUN e per elementId con ricerca "INIZIA PER"
     * @param iun iun della notifica
     * @param elementId elementId (anche parziale) da ricercare tramite "inizia per"
     * @return insieme di timeline
     */
    Set<TimelineElementEntity> searchByIunAndElementId(String iun, String elementId );

    void deleteByIun(String iun);
}
