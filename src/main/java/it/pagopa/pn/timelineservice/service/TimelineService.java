package it.pagopa.pn.timelineservice.service;

import it.pagopa.pn.timelineservice.dto.ProbableSchedulingAnalogDateDto;
import it.pagopa.pn.timelineservice.dto.timeline.TimelineElementInternal;
import it.pagopa.pn.timelineservice.dto.timeline.details.TimelineElementCategoryInt;
import it.pagopa.pn.timelineservice.dto.timeline.details.TimelineElementDetailsInt;
import it.pagopa.pn.timelineservice.dto.ext.datavault.ConfidentialTimelineElementDtoInt;
import it.pagopa.pn.timelineservice.dto.ext.notification.NotificationInt;
import reactor.core.publisher.Mono;

import java.util.Optional;
import java.util.Set;

public interface TimelineService {

    boolean addTimelineElement(TimelineElementInternal element, NotificationInt notification);

    Long retrieveAndIncrementCounterForTimelineEvent(String timelineId);

    Optional<TimelineElementInternal> getTimelineElement(String iun, String timelineId);

    Optional<TimelineElementInternal> getTimelineElementStrongly(String iun, String timelineId);

    <T> Optional<T> getTimelineElementDetails(String iun, String timelineId, Class<T> timelineDetailsClass);

    <T> Optional<T> getTimelineElementDetailForSpecificRecipient(String iun, int recIndex, boolean confidentialInfoRequired, TimelineElementCategoryInt category, Class<T> timelineDetailsClass);

    Optional<TimelineElementInternal> getTimelineElementForSpecificRecipient(String iun, int recIndex, TimelineElementCategoryInt category);
    
    Set<TimelineElementInternal> getTimeline(String iun, boolean confidentialInfoRequired);

    Set<TimelineElementInternal> getTimelineStrongly(String iun, boolean confidentialInfoRequired);
    
    Set<TimelineElementInternal> getTimelineByIunTimelineId(String iun, String timelineId, boolean confidentialInfoRequired);

//    NotificationHistoryResponse getTimelineAndStatusHistory(String iun, int numberOfRecipients, Instant createdAt);

    Mono<ProbableSchedulingAnalogDateDto> getSchedulingAnalogDate(String iun, String recipientId);

    void enrichTimelineElementWithConfidentialInformation(TimelineElementDetailsInt details,
                                                          ConfidentialTimelineElementDtoInt confidentialDto);

}
