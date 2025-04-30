package it.pagopa.pn.timelineservice.middleware.dao;

import it.pagopa.pn.timelineservice.middleware.dao.dynamo.entity.TimelineCounterEntity;

public interface TimelineCounterEntityDao {


    String IMPLEMENTATION_TYPE_PROPERTY_NAME = "pn.middleware.impl.timeline-counter-dao";

    TimelineCounterEntity getCounter(String timelineElementId);
}
