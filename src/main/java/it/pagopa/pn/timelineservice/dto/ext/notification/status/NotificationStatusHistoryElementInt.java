package it.pagopa.pn.timelineservice.dto.ext.notification.status;

import lombok.*;

import java.time.Instant;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder(toBuilder = true)
@EqualsAndHashCode
@ToString
public class NotificationStatusHistoryElementInt {
    private NotificationStatusInt status;
    private Instant activeFrom;
    private List<String> relatedTimelineElements;
}
