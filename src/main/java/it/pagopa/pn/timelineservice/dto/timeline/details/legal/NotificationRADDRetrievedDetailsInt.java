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
@SuperBuilder( toBuilder = true )
@EqualsAndHashCode(callSuper = true)
@ToString
public class NotificationRADDRetrievedDetailsInt extends CategoryTypeTimelineElementDetailsInt implements RecipientRelatedTimelineElementDetails, ElementTimestampTimelineElementDetails{
    private int recIndex;
    private String raddType;
    private String raddTransactionId;
    private Instant eventTimestamp;
    
    public String toLog() {
        return String.format(
                "recIndex=%d eventTimestamp=%s",
                recIndex,
                eventTimestamp
        );
    }

    @Override
    public Instant getElementTimestamp() {
        return eventTimestamp;
    }
}
