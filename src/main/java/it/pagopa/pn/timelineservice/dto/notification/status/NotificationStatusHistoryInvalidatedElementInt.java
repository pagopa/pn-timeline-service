package it.pagopa.pn.timelineservice.dto.notification.status;

import it.pagopa.pn.timelineservice.dto.timeline.TimelineElementInternal;
import lombok.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder(toBuilder = true)
@EqualsAndHashCode
@ToString
public class NotificationStatusHistoryInvalidatedElementInt {
    private NotificationStatusInt status;
    private Instant activeFrom;
    private List<String> relatedTimelineElementIds;
    private List<TimelineElementInternal> relatedTimelineElement = new ArrayList<>();
}
