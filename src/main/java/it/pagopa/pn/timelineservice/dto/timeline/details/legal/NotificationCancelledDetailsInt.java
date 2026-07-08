package it.pagopa.pn.timelineservice.dto.timeline.details.legal;

import it.pagopa.pn.timelineservice.dto.timeline.details.common.CategoryTypeTimelineElementDetailsInt;
import it.pagopa.pn.timelineservice.dto.timeline.details.common.TimelineElementDetailsInt;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
@EqualsAndHashCode(callSuper = true)
@Getter
@Setter
@ToString
public class NotificationCancelledDetailsInt extends CategoryTypeTimelineElementDetailsInt implements TimelineElementDetailsInt {

    private int notificationCost;
    private List<Integer> notRefinedRecipientIndexes;

    public String toLog() {
        return String.format(
            "notificationCost=%d notRefinedRecipientIndexes=%s",
            notificationCost,
            notRefinedRecipientIndexes.toString()
        );
    }
}