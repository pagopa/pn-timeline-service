package it.pagopa.pn.timelineservice.operations.common;

import it.pagopa.pn.timelineservice.dto.timeline.TimelineElementInternal;

import java.util.Set;

public interface TimelineTimestampMapper {
    TimelineElementInternal mapTimelineTimestamps(TimestampMapperPayload payload);

    record TimestampMapperPayload(
            TimelineElementInternal timelineElementInternal,
            Set<TimelineElementInternal> timelineElementInternalSet
    ) { }
}
