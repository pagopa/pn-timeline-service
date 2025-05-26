package it.pagopa.pn.timelineservice.dto.timeline.details;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder(toBuilder = true)
@EqualsAndHashCode
@ToString
public class DigitalFailureWorkflowDetailsInt implements RecipientRelatedTimelineElementDetails {
    private int recIndex;

    public String toLog() {
        return String.format(
                "recIndex=%d",
                recIndex
        );
    }
}
