package it.pagopa.pn.timelineservice.dto.timeline.details;

import it.pagopa.pn.timelineservice.dto.address.PhysicalAddressInt;
import it.pagopa.pn.timelineservice.dto.ext.externalchannel.AttachmentDetailsInt;
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
public class SendAnalogFeedbackDetailsInt extends CategoryTypeTimelineElementDetailsInt implements RecipientRelatedTimelineElementDetails,
        NewAddressRelatedTimelineElement, PhysicalAddressRelatedTimelineElement, ElementTimestampTimelineElementDetails {
    private int recIndex;
    private PhysicalAddressInt physicalAddress;
    private ServiceLevelInt serviceLevel;
    private Integer sentAttemptMade;
    private PhysicalAddressInt newAddress;
    private String deliveryFailureCause;
    private ResponseStatusInt responseStatus;
    private List<SendingReceipt> sendingReceipts;
    private String requestTimelineId;
    private String deliveryDetailCode;
    private Instant notificationDate;
    private List<AttachmentDetailsInt> attachments;
    private String sendRequestId;
    private String registeredLetterCode;

    public String toLog() {
        return String.format(
                "recIndex=%d sentAttemptMade=%d responseStatus=%s deliveryFailureCause=%s " +
                        "physicalAddress=%s requestTimelineId=%s deliveryDetailCode=%s attachments=%s",
                recIndex,
                sentAttemptMade,
                responseStatus,
                deliveryFailureCause,
                AuditLogUtils.SENSITIVE,
                requestTimelineId,
                deliveryDetailCode,
                attachments
        );
    }

    @Override
    public Instant getElementTimestamp() {
        return notificationDate;
    }
}
