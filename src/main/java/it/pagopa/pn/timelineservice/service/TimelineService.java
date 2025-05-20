package it.pagopa.pn.timelineservice.service;

import it.pagopa.pn.timelineservice.dto.notification.NotificationHistoryResponse;
import it.pagopa.pn.timelineservice.dto.ProbableSchedulingAnalogDateDto;
import it.pagopa.pn.timelineservice.dto.ext.notification.NotificationInt;
import it.pagopa.pn.timelineservice.dto.timeline.TimelineElementInternal;
import it.pagopa.pn.timelineservice.dto.timeline.details.TimelineElementCategoryInt;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.time.Instant;

public interface TimelineService {

    Mono<Boolean> addTimelineElement(TimelineElementInternal element, NotificationInt notification);

    Long retrieveAndIncrementCounterForTimelineEvent(String timelineId);

    Mono<TimelineElementInternal> getTimelineElement(String iun, String timelineId, boolean strongly);

    Flux<TimelineElementInternal> getTimeline(String iun, String timelineId, boolean confidentialInfoRequired, boolean strongly);

    <T> Mono<T> getTimelineElementDetails(String iun, String timelineId, Class<T> timelineDetailsClass);

    <T> Mono<T> getTimelineElementDetailForSpecificRecipient(String iun, int recIndex, boolean confidentialInfoRequired, TimelineElementCategoryInt category, Class<T> timelineDetailsClass);

    Mono<TimelineElementInternal> getTimelineElementForSpecificRecipient(String iun, int recIndex, TimelineElementCategoryInt category);

    Mono<ProbableSchedulingAnalogDateDto> getSchedulingAnalogDate(String iun, int recIndex);

    NotificationHistoryResponse getTimelineAndStatusHistory(String iun, int numberOfRecipients, Instant createdAt);

}
