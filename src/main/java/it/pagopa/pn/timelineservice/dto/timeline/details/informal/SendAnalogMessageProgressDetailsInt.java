package it.pagopa.pn.timelineservice.dto.timeline.details.informal;

import it.pagopa.pn.timelineservice.dto.ext.externalchannel.AttachmentDetailsInt;
import it.pagopa.pn.timelineservice.dto.informalnotification.AnalogDeliveryDetailsInt;
import it.pagopa.pn.timelineservice.dto.informalnotification.AnalogDeliveryTypeInt;
import it.pagopa.pn.timelineservice.dto.timeline.details.common.CategoryTypeTimelineElementDetailsInt;
import it.pagopa.pn.timelineservice.dto.timeline.details.common.ElementTimestampTimelineElementDetails;
import it.pagopa.pn.timelineservice.dto.timeline.details.common.RecipientRelatedTimelineElementDetails;
import it.pagopa.pn.timelineservice.dto.timeline.details.legal.ServiceLevelInt;
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
public class SendAnalogMessageProgressDetailsInt extends CategoryTypeTimelineElementDetailsInt implements RecipientRelatedTimelineElementDetails, ElementTimestampTimelineElementDetails {
    private int recIndex;
    private Instant notificationDate;
    private AnalogDeliveryDetailsInt deliveryDetail;
    private AnalogDeliveryTypeInt deliveryType;
    private List<AttachmentDetailsInt> attachments;
    private String sendRequestId;
    private String registeredLetterCode;
    private ServiceLevelInt serviceLevel;

    @Override
    public String toLog() {
        return String.format(
                "recIndex=%d notificationDate=%s deliveryType=%s deliveryDetail=%s attachments=%s sendRequestId=%s registeredLetterCode=%s serviceLevel=%s",
                recIndex,
                notificationDate,
                deliveryType,
                deliveryDetail,
                attachments,
                sendRequestId,
                registeredLetterCode,
                serviceLevel
        );
    }

    @Override
    public Instant getElementTimestamp() {
        return notificationDate;
    }
}
