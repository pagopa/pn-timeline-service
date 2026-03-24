package it.pagopa.pn.timelineservice.dto.timeline.details;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.Instant;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@SuperBuilder(toBuilder = true)
@ToString
@EqualsAndHashCode(callSuper = true)
public class NotificationCostValidationRequestDetailsInt extends CategoryTypeTimelineElementDetailsInt implements ElementTimestampTimelineElementDetails {
    private String categoryType;

    public String toLog() {
        return String.format("categoryType=%s", categoryType);
    }

    @Override
    public Instant getElementTimestamp() {
        return null;
    }
}
