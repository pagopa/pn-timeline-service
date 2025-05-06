package it.pagopa.pn.timelineservice.dto.timeline.details;

import java.time.Instant;

public interface ElementTimestampTimelineElementDetails extends TimelineElementDetailsInt {

    Instant getElementTimestamp();
}
