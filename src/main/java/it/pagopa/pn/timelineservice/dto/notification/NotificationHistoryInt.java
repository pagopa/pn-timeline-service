package it.pagopa.pn.timelineservice.dto.notification;

import it.pagopa.pn.timelineservice.dto.notification.status.NotificationStatusHistoryElementInt;
import it.pagopa.pn.timelineservice.dto.notification.status.NotificationStatusInt;
import it.pagopa.pn.timelineservice.dto.timeline.TimelineElementInternal;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Setter
@Getter
@lombok.Builder(toBuilder = true)
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor
@lombok.ToString
public class NotificationHistoryInt{

    private NotificationStatusInt notificationStatus;

    private List<NotificationStatusHistoryElementInt> notificationStatusHistory = null;

    private List<TimelineElementInternal> timeline = null;

}
