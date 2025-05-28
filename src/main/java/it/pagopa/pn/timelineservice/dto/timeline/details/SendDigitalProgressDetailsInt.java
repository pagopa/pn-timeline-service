package it.pagopa.pn.timelineservice.dto.timeline.details;

import it.pagopa.pn.timelineservice.dto.address.DigitalAddressSourceInt;
import it.pagopa.pn.timelineservice.dto.address.LegalDigitalAddressInt;
import it.pagopa.pn.timelineservice.utils.AuditLogUtils;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.Instant;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@SuperBuilder(toBuilder = true)
@EqualsAndHashCode(callSuper = true)
@ToString
public class SendDigitalProgressDetailsInt extends CategoryTypeTimelineElementDetailsInt implements DigitalSendTimelineElementDetails, ElementTimestampTimelineElementDetails {
    private int recIndex;
    private LegalDigitalAddressInt  digitalAddress;
    private DigitalAddressSourceInt digitalAddressSource;
    private Integer retryNumber;
    private Instant notificationDate;
    private List<SendingReceipt> sendingReceipts;
    private String deliveryFailureCause;
    private String deliveryDetailCode;
    private boolean shouldRetry;
    private Boolean isFirstSendRetry;
    private String relatedFeedbackTimelineId;
    private Instant eventTimestamp;
    
    public String toLog() {
        return String.format(
                "recIndex=%d deliveryDetailCode=%s digitalAddress=%s shouldRetry=%b digitalAddressSource=%s retryNumber=%d isFirstSendRetry=%s relatedFeedbackTimelineId=%s eventTimestamp=%s",
                recIndex,
                deliveryDetailCode,
                AuditLogUtils.SENSITIVE,
                shouldRetry,
                digitalAddressSource.getValue(),
                retryNumber,
                isFirstSendRetry,
                relatedFeedbackTimelineId,
                eventTimestamp
        );
    }

    @Override
    public Instant getElementTimestamp() {
        return eventTimestamp;
    }
}
