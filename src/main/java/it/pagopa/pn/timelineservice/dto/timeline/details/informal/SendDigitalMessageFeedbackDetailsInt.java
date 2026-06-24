package it.pagopa.pn.timelineservice.dto.timeline.details.informal;

import it.pagopa.pn.timelineservice.dto.address.DigitalAddressSourceInt;
import it.pagopa.pn.timelineservice.dto.address.InformalDigitalAddressInt;
import it.pagopa.pn.timelineservice.dto.ext.externalchannel.ResponseStatusInt;
import it.pagopa.pn.timelineservice.dto.informalnotification.DigitalChannelsInt;
import it.pagopa.pn.timelineservice.dto.informalnotification.DigitalDeliveryDetailsInt;
import it.pagopa.pn.timelineservice.dto.timeline.details.common.CategoryTypeTimelineElementDetailsInt;
import it.pagopa.pn.timelineservice.dto.timeline.details.common.ElementTimestampTimelineElementDetails;
import it.pagopa.pn.timelineservice.dto.timeline.details.common.RecipientRelatedTimelineElementDetails;
import it.pagopa.pn.timelineservice.dto.timeline.details.common.DigitalAddressSourceRelatedTimelineElement;
import it.pagopa.pn.timelineservice.dto.timeline.details.legal.SendingReceipt;
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
public class SendDigitalMessageFeedbackDetailsInt extends CategoryTypeTimelineElementDetailsInt implements RecipientRelatedTimelineElementDetails,
        InformalDigitalAddressRelatedTimelineElement, DigitalAddressSourceRelatedTimelineElement, ElementTimestampTimelineElementDetails {
    private int recIndex;
    private InformalDigitalAddressInt digitalAddress;
    private DigitalAddressSourceInt digitalAddressSource;
    private ResponseStatusInt responseStatus;
    private Instant notificationDate;
    private DigitalChannelsInt channel;
    private DigitalDeliveryDetailsInt deliveryDetail;
    private List<SendingReceipt> sendingReceipts;
    private String requestId;

    @Override
    public String toLog() {
        return String.format(
                "recIndex=%d responseStatus=%s requestId=%s channel=%s digitalAddressSource=%s deliveryDetail=%s digitalAddress=%s",
                recIndex,
                responseStatus,
                requestId,
                channel,
                digitalAddressSource,
                deliveryDetail,
                AuditLogUtils.SENSITIVE
        );
    }

    @Override
    public Instant getElementTimestamp() {
        return notificationDate;
    }
}
