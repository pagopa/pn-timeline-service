package it.pagopa.pn.timelineservice.dto.timeline.details.legal;

import it.pagopa.pn.timelineservice.dto.timeline.details.common.CategoryTypeTimelineElementDetailsInt;
import it.pagopa.pn.timelineservice.dto.timeline.details.common.TimelineElementDetailsInt;
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
