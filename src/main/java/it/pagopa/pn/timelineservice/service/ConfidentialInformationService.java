package it.pagopa.pn.timelineservice.service;

import it.pagopa.pn.timelineservice.dto.ext.datavault.ConfidentialTimelineElementDtoInt;
import it.pagopa.pn.timelineservice.dto.timeline.TimelineElementInternal;
import reactor.core.publisher.Mono;

import java.util.Map;

public interface ConfidentialInformationService {
    
    Mono<Void> saveTimelineConfidentialInformation(TimelineElementInternal timelineElementInternal);

    Mono<ConfidentialTimelineElementDtoInt> getTimelineElementConfidentialInformation(String iun, String timelineElementId);

    Mono<Map<String, ConfidentialTimelineElementDtoInt>> getTimelineConfidentialInformation(String iun);
}
