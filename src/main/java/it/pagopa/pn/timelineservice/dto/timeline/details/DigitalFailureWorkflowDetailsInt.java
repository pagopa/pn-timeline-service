package it.pagopa.pn.timelineservice.dto.timeline.details;

import lombok.*;
import lombok.experimental.SuperBuilder;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@SuperBuilder(toBuilder = true)
@EqualsAndHashCode(callSuper = true)
@ToString
public class DigitalFailureWorkflowDetailsInt extends CategoryTypeTimelineElementDetailsInt implements RecipientRelatedTimelineElementDetails {
    private int recIndex;

    public String toLog() {
        return String.format(
                "recIndex=%d",
                recIndex
        );
    }
}
