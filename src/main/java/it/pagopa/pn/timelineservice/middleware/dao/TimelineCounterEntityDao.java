package it.pagopa.pn.timelineservice.middleware.dao;

import it.pagopa.pn.timelineservice.middleware.dao.dynamo.entity.TimelineCounterEntity;
import reactor.core.publisher.Mono;

public interface TimelineCounterEntityDao {


    String IMPLEMENTATION_TYPE_PROPERTY_NAME = "pn.middleware.impl.timeline-counter-dao";

    Mono<TimelineCounterEntity> getCounter(String timelineElementId);
}
