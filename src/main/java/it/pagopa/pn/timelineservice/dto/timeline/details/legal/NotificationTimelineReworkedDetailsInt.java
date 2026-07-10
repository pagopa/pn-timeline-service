package it.pagopa.pn.timelineservice.dto.timeline.details.legal;

import it.pagopa.pn.timelineservice.dto.notification.status.NotificationStatusHistoryInvalidatedElementInt;
import it.pagopa.pn.timelineservice.dto.timeline.details.common.CategoryTypeTimelineElementDetailsInt;
import it.pagopa.pn.timelineservice.dto.timeline.details.common.RecipientRelatedTimelineElementDetails;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@SuperBuilder(toBuilder = true)
@ToString(callSuper = true)
public class NotificationTimelineReworkedDetailsInt extends CategoryTypeTimelineElementDetailsInt implements RecipientRelatedTimelineElementDetails {
    private int recIndex;
    private Integer sentAttemptMade;
    private List<NotificationStatusHistoryInvalidatedElementInt> invalidatedTimelineAndStatusHistory;
    private String categoryType;

    @Override
    public String toLog() {
        return String.format(
                "NotificationTimelineReworkedDetailsInt{recIndex=%d, sentAttemptMade=%d, categoryType='%s'}",
                recIndex,
                sentAttemptMade,
                categoryType
        );
    }

}
