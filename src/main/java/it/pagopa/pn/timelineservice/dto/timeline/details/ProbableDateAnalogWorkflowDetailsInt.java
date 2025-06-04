package it.pagopa.pn.timelineservice.dto.timeline.details;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.Instant;

@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
@EqualsAndHashCode(callSuper = true)
@Getter
@Setter
@ToString
public class ProbableDateAnalogWorkflowDetailsInt extends CategoryTypeTimelineElementDetailsInt implements RecipientRelatedTimelineElementDetails {
    private int recIndex;
    private Instant schedulingAnalogDate;

    public String toLog() {
       return this.toString();
    }
}
