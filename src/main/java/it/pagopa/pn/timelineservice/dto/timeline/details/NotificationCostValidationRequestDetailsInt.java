package it.pagopa.pn.timelineservice.dto.timeline.details;

import lombok.*;
import lombok.experimental.SuperBuilder;

@NoArgsConstructor
@Getter
@Setter
@SuperBuilder(toBuilder = true)
@ToString
@EqualsAndHashCode(callSuper = true)
public class NotificationCostValidationRequestDetailsInt extends CategoryTypeTimelineElementDetailsInt implements TimelineElementDetailsInt{

    public String toLog() {
        return String.format("categoryType=%s", getCategoryType());
    }
}
