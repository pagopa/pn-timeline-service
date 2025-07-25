package it.pagopa.pn.timelineservice.dto.timeline.details;

import it.pagopa.pn.timelineservice.dto.address.DigitalAddressSourceInt;
import it.pagopa.pn.timelineservice.dto.address.LegalDigitalAddressInt;
import it.pagopa.pn.timelineservice.dto.ext.externalchannel.ResponseStatusInt;
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
public class SendDigitalFeedbackDetailsInt extends CategoryTypeTimelineElementDetailsInt implements DigitalAddressRelatedTimelineElement, DigitalAddressSourceRelatedTimelineElement, ElementTimestampTimelineElementDetails {
    private int recIndex;
    private LegalDigitalAddressInt digitalAddress;
    private DigitalAddressSourceInt digitalAddressSource;
    private ResponseStatusInt responseStatus;
    private Instant notificationDate; //Cambiare il nome del campo in extChannelsFeedbackDate
    private List<SendingReceipt> sendingReceipts;
    private String requestTimelineId;
    private String deliveryFailureCause;
    private String deliveryDetailCode;

    public String toLog() {
        return String.format(
                "recIndex=%d responseStatus=%s deliveryFailureCause=%s digitalAddress=%s requestTimelineId=%s deliveryDetailCode=%s",
                recIndex,
                responseStatus,
                deliveryFailureCause,
                AuditLogUtils.SENSITIVE,
                requestTimelineId,
                deliveryDetailCode
        );
    }

    @Override
    public Instant getElementTimestamp() {
        return notificationDate;
    }
}
