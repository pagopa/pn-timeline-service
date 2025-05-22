package it.pagopa.pn.timelineservice.middleware.dao;

import it.pagopa.pn.timelineservice.middleware.dao.dynamo.entity.TimelineCounterEntity;
import reactor.core.publisher.Mono;

public interface TimelineCounterEntityDao {
    Mono<TimelineCounterEntity> getCounter(String timelineElementId);
}
