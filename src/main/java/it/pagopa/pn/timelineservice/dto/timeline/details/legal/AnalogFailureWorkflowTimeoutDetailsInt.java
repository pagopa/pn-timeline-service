package it.pagopa.pn.timelineservice.dto.timeline.details.legal;

import it.pagopa.pn.timelineservice.dto.timeline.details.common.CategoryTypeTimelineElementDetailsInt;
import it.pagopa.pn.timelineservice.dto.timeline.details.common.ElementTimestampTimelineElementDetails;
import it.pagopa.pn.timelineservice.dto.timeline.details.common.RecipientRelatedTimelineElementDetails;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.Instant;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@SuperBuilder(toBuilder = true)
@EqualsAndHashCode(callSuper = true)
@ToString
public class AnalogFailureWorkflowTimeoutDetailsInt extends CategoryTypeTimelineElementDetailsInt implements ElementTimestampTimelineElementDetails, RecipientRelatedTimelineElementDetails {
    private int recIndex;
    private String generatedAarUrl;
    private Integer notificationCost;
    private Instant timeoutDate;

    public String toLog() {
        return String.format(
                "recIndex=%d, cost=%d timeoutDate=%s",
                recIndex,
                notificationCost,
                timeoutDate
        );
    }

    @Override
    public Instant getElementTimestamp() {
        return timeoutDate;
    }
}
