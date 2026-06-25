package it.pagopa.pn.timelineservice.dto.timeline.details.informal;

import it.pagopa.pn.timelineservice.dto.address.DigitalAddressSourceInt;
import it.pagopa.pn.timelineservice.dto.address.InformalDigitalAddressInt;
import it.pagopa.pn.timelineservice.dto.informalnotification.DigitalChannelsInt;
import it.pagopa.pn.timelineservice.dto.informalnotification.DigitalDeliveryDetailsInt;
import it.pagopa.pn.timelineservice.dto.timeline.details.common.CategoryTypeTimelineElementDetailsInt;
import it.pagopa.pn.timelineservice.dto.timeline.details.common.ElementTimestampTimelineElementDetails;
import it.pagopa.pn.timelineservice.dto.timeline.details.common.RecipientRelatedTimelineElementDetails;
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
public class SendDigitalMessageProgressDetailsInt extends CategoryTypeTimelineElementDetailsInt implements InformalDigitalAddressRelatedTimelineElement, RecipientRelatedTimelineElementDetails, ElementTimestampTimelineElementDetails {
    private int recIndex;
    private String requestId;
    private DigitalDeliveryDetailsInt deliveryDetail;
    private InformalDigitalAddressInt digitalAddress;
    private DigitalAddressSourceInt digitalAddressSource;
    private DigitalChannelsInt channel;
    private Integer retryNumber;
    private Instant eventTimestamp;

    @Override
    public String toLog() {
        return String.format(
                "recIndex=%d requestId=%s channel=%s digitalAddressSource=%s retryNumber=%s deliveryDetail=%s digitalAddress=%s eventTimestamp=%s",
                recIndex,
                requestId,
                channel,
                digitalAddressSource,
                retryNumber,
                deliveryDetail,
                AuditLogUtils.SENSITIVE,
                eventTimestamp
        );
    }

    @Override
    public Instant getElementTimestamp() {
        return eventTimestamp;
    }
}
