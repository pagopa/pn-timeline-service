package it.pagopa.pn.timelineservice.service;

import it.pagopa.pn.timelineservice.dto.timeline.TimelineElementInternal;
import it.pagopa.pn.timelineservice.dto.ext.datavault.ConfidentialTimelineElementDtoInt;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface ConfidentialInformationService {
    
    void saveTimelineConfidentialInformation(TimelineElementInternal timelineElementInternal);

    Flux<ConfidentialTimelineElementDtoInt> getTimelineConfidentialInformation(List<TimelineElementInternal> timelineElementInternal);
    
    Optional<ConfidentialTimelineElementDtoInt> getTimelineElementConfidentialInformation(String iun, String timelineElementId);

    Optional<Map<String, ConfidentialTimelineElementDtoInt>> getTimelineConfidentialInformation(String iun);
}
