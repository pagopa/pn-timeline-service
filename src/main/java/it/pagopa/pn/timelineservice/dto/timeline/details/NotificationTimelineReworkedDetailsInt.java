package it.pagopa.pn.timelineservice.dto.timeline.details;

import it.pagopa.pn.timelineservice.generated.openapi.server.v1.dto.NotificationStatusHistoryElement;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@SuperBuilder(toBuilder = true)
@ToString(callSuper = true)
public class NotificationTimelineReworkedDetailsInt implements RecipientRelatedTimelineElementDetails, TimelineElementDetailsInt {
    private int recIndex;
    private Integer sentAttemptMade;
    private List<NotificationStatusHistoryElement> invalidatedTimelineAndStatusHistory;
    private String reason;
    private String categoryType;

    @Override
    public String toLog() {
        return String.format(
                "NotificationTimelineReworkedDetailsInt{recIndex=%d, sentAttemptMade=%d, reason='%s', categoryType='%s'}",
                recIndex,
                sentAttemptMade,
                reason,
                categoryType
        );
    }

}
