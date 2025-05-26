package it.pagopa.pn.timelineservice.service;

import it.pagopa.pn.timelineservice.dto.notification.NotificationHistoryInt;
import it.pagopa.pn.timelineservice.dto.notification.NotificationInfoInt;
import it.pagopa.pn.timelineservice.dto.notification.ProbableSchedulingAnalogDateInt;
import it.pagopa.pn.timelineservice.dto.timeline.TimelineElementInternal;
import it.pagopa.pn.timelineservice.dto.timeline.details.TimelineElementCategoryInt;
import it.pagopa.pn.timelineservice.generated.openapi.server.v1.dto.NotificationHistoryResponse;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.Optional;
import java.util.Set;

public interface TimelineService {

    boolean addTimelineElement(TimelineElementInternal element, NotificationInfoInt notification);

    Long retrieveAndIncrementCounterForTimelineEvent(String timelineId);

    Optional<TimelineElementInternal> getTimelineElement(String iun, String timelineId, boolean strongly);

    Set<TimelineElementInternal> getTimeline(String iun, String timelineId, boolean confidentialInfoRequired, boolean strongly);

    <T> Optional<T> getTimelineElementDetails(String iun, String timelineId, Class<T> timelineDetailsClass);

    <T> Optional<T> getTimelineElementDetailForSpecificRecipient(String iun, int recIndex, boolean confidentialInfoRequired, TimelineElementCategoryInt category, Class<T> timelineDetailsClass);

    Optional<TimelineElementInternal> getTimelineElementForSpecificRecipient(String iun, int recIndex, TimelineElementCategoryInt category);

    Mono<ProbableSchedulingAnalogDateInt> getSchedulingAnalogDate(String iun, int recIndex);

    NotificationHistoryInt getTimelineAndStatusHistory(String iun, int numberOfRecipients, Instant createdAt);

}
