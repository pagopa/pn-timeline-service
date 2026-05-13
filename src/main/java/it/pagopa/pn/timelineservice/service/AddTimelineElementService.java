package it.pagopa.pn.timelineservice.service;

import it.pagopa.pn.timelineservice.dto.notification.NotificationInfoInt;
import it.pagopa.pn.timelineservice.dto.timeline.TimelineElementInternal;
import reactor.core.publisher.Mono;

public interface AddTimelineElementService {
    Mono<String> addTimelineElement(TimelineElementInternal dto, NotificationInfoInt notification);
}
