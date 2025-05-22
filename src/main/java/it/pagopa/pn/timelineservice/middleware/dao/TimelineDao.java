package it.pagopa.pn.timelineservice.middleware.dao;

import it.pagopa.pn.timelineservice.dto.timeline.TimelineElementInternal;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


public interface TimelineDao {

    Mono<Void> addTimelineElementIfAbsent(TimelineElementInternal dto);
    
    Mono<TimelineElementInternal> getTimelineElement(String iun, String timelineId, boolean strongly);

    Flux<TimelineElementInternal> getTimeline(String iun);

    Flux<TimelineElementInternal> getTimelineStrongly(String iun);

    Flux<TimelineElementInternal> getTimelineFilteredByElementId(String iun, String timelineId);

    Mono<Void> deleteTimeline(String iun);

}
