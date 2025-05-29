package it.pagopa.pn.timelineservice.service;

import it.pagopa.pn.timelineservice.dto.notification.NotificationHistoryInt;
import it.pagopa.pn.timelineservice.dto.notification.NotificationInfoInt;
import it.pagopa.pn.timelineservice.dto.notification.ProbableSchedulingAnalogDateInt;
import it.pagopa.pn.timelineservice.dto.timeline.TimelineElementInternal;
import it.pagopa.pn.timelineservice.dto.timeline.details.TimelineElementCategoryInt;
import it.pagopa.pn.timelineservice.dto.timeline.details.TimelineElementDetailsInt;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.time.Instant;

public interface TimelineService {

    Mono<Boolean> addTimelineElement(TimelineElementInternal element, NotificationInfoInt notification);

    Mono<Long> retrieveAndIncrementCounterForTimelineEvent(String timelineId);

    Mono<TimelineElementInternal> getTimelineElement(String iun, String timelineId, boolean strongly);

    Flux<TimelineElementInternal> getTimeline(String iun, String timelineId, boolean confidentialInfoRequired, boolean strongly);

    Mono<TimelineElementDetailsInt> getTimelineElementDetails(String iun, String timelineId);

    Mono<TimelineElementDetailsInt> getTimelineElementDetailForSpecificRecipient(String iun, int recIndex, boolean confidentialInfoRequired, TimelineElementCategoryInt category);

    Mono<TimelineElementInternal> getTimelineElementForSpecificRecipient(String iun, int recIndex, TimelineElementCategoryInt category);

    Mono<ProbableSchedulingAnalogDateInt> getSchedulingAnalogDate(String iun, int recIndex);

    Mono<NotificationHistoryInt> getTimelineAndStatusHistory(String iun, int numberOfRecipients, Instant createdAt);

}
