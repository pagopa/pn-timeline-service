package it.pagopa.pn.timelineservice.dto.timeline.details;

import it.pagopa.pn.timelineservice.dto.address.DigitalAddressSourceInt;
import it.pagopa.pn.timelineservice.dto.address.LegalDigitalAddressInt;
import it.pagopa.pn.timelineservice.utils.AuditLogUtils;
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
public class PrepareDigitalDetailsInt extends CategoryTypeTimelineElementDetailsInt implements DigitalSendTimelineElementDetails {
    private int recIndex;

    // info relative a lastAddress
    private Integer retryNumber;
    private LegalDigitalAddressInt digitalAddress;
    private DigitalAddressSourceInt digitalAddressSource;
    private Instant attemptDate;

    // info relative a nextAddress
    private DigitalAddressSourceInt nextDigitalAddressSource;
    private int nextSourceAttemptsMade;
    private Instant nextLastAttemptMadeForSource;
    private Boolean isFirstSendRetry;
    private String relatedFeedbackTimelineId;
    
    public String toLog() {
        return String.format(
                "recIndex=%d source=%s retryNumber=%s digitalAddress=%s nextDigitalAddressSource=%s nextSourceAttemptsMade=%d lastAttemptMadeForSource=%s",
                recIndex,
                digitalAddressSource,
                retryNumber,
                AuditLogUtils.SENSITIVE,
                nextDigitalAddressSource,
                nextSourceAttemptsMade,
                nextLastAttemptMadeForSource
        );
    }
}
