package it.pagopa.pn.timelineservice.dto.timeline.details.informal;

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
public class WorkflowEndedReachedDetailsInt extends CategoryTypeTimelineElementDetailsInt implements ElementTimestampTimelineElementDetails, RecipientRelatedTimelineElementDetails {
    private int recIndex;
    private Instant notificationDate;
    private String sourceElementId;

    @Override
    public String toLog() {
        return String.format(
                "recIndex=%d notificationDate=%s sourceElementId=%s",
                recIndex,
                notificationDate,
                sourceElementId
        );
    }

    @Override
    public Instant getElementTimestamp() {
        return notificationDate;
    }
}
