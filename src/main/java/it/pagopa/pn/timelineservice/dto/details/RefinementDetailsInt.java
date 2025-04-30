package it.pagopa.pn.timelineservice.dto.details;

import lombok.*;

import java.time.Instant;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder(toBuilder = true)
@EqualsAndHashCode
@ToString
public class RefinementDetailsInt implements RecipientRelatedTimelineElementDetails {
    private int recIndex;
    private Integer notificationCost;
    private Instant eventTimestamp;

    public String toLog() {
        return String.format(
                "recIndex=%d",
                recIndex
        );
    }
}
