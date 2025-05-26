package it.pagopa.pn.timelineservice.dto.transition;

import it.pagopa.pn.timelineservice.dto.notification.status.NotificationStatusInt;
import it.pagopa.pn.timelineservice.dto.timeline.details.TimelineElementCategoryInt;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TransitionRequest {

    private NotificationStatusInt fromStatus;
    private TimelineElementCategoryInt timelineRowType;
    private boolean multiRecipient;
}
