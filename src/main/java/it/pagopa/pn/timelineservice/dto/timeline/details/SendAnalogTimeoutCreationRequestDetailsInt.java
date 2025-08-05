package it.pagopa.pn.timelineservice.dto.timeline.details;

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
public class SendAnalogTimeoutCreationRequestDetailsInt extends CategoryTypeTimelineElementDetailsInt implements ElementTimestampTimelineElementDetails {

    private Instant timeoutDate;
    private Integer recIndex;
    private Integer sentAttemptMade;
    private String relatedRequestId;
    private String legalFactId;

    public String toLog() {
        return String.format(
                "recIndex=%d sentAttemptMade=%d relatedRequestId=%s timeoutDate=%s legalFactId=%s",
                recIndex,
                sentAttemptMade,
                relatedRequestId,
                timeoutDate,
                legalFactId
        );
    }

    @Override
    public Instant getElementTimestamp() {
        return timeoutDate;
    }

}

