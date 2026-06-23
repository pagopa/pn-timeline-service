package it.pagopa.pn.timelineservice.dto.timeline.details.informal;

import it.pagopa.pn.timelineservice.dto.address.PhysicalAddressInt;
import it.pagopa.pn.timelineservice.dto.informalnotification.AnalogDeliveryTypeInt;
import it.pagopa.pn.timelineservice.dto.timeline.details.common.CategoryTypeTimelineElementDetailsInt;
import it.pagopa.pn.timelineservice.dto.timeline.details.common.PhysicalAddressRelatedTimelineElement;
import it.pagopa.pn.timelineservice.dto.timeline.details.common.RecipientRelatedTimelineElementDetails;
import it.pagopa.pn.timelineservice.dto.timeline.details.legal.ServiceLevelInt;
import it.pagopa.pn.timelineservice.utils.AuditLogUtils;
import lombok.*;
import lombok.experimental.SuperBuilder;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@SuperBuilder(toBuilder = true)
@EqualsAndHashCode(callSuper = true)
@ToString
public class PrepareAnalogDeliveryDetailsInt extends CategoryTypeTimelineElementDetailsInt implements RecipientRelatedTimelineElementDetails, PhysicalAddressRelatedTimelineElement {
    private int recIndex;
    private PhysicalAddressInt physicalAddress;
    private ServiceLevelInt serviceLevel;
    private AnalogDeliveryTypeInt deliveryType;
    private Integer sentAttemptMade;
    private String relatedRequestId;
    private String foreignState;

    @Override
    public String toLog() {
        return String.format(
                "recIndex=%d serviceLevel=%s deliveryType=%s sentAttemptMade=%s relatedRequestId=%s foreignState=%s physicalAddress=%s",
                recIndex,
                serviceLevel,
                deliveryType,
                sentAttemptMade,
                relatedRequestId,
                foreignState,
                AuditLogUtils.SENSITIVE
        );
    }
}
