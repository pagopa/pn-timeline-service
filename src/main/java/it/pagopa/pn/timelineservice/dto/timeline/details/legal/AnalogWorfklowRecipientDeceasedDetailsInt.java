package it.pagopa.pn.timelineservice.dto.timeline.details.legal;

import it.pagopa.pn.timelineservice.dto.address.PhysicalAddressInt;
import it.pagopa.pn.timelineservice.dto.timeline.details.common.CategoryTypeTimelineElementDetailsInt;
import it.pagopa.pn.timelineservice.dto.timeline.details.common.ElementTimestampTimelineElementDetails;
import it.pagopa.pn.timelineservice.dto.timeline.details.common.PhysicalAddressRelatedTimelineElement;
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
public class AnalogWorfklowRecipientDeceasedDetailsInt extends CategoryTypeTimelineElementDetailsInt implements RecipientRelatedTimelineElementDetails, PhysicalAddressRelatedTimelineElement, ElementTimestampTimelineElementDetails {
    private int recIndex;
    private PhysicalAddressInt physicalAddress;
    private Integer notificationCost;
    private Instant notificationDate;

    @Override
    public String toLog() {
        return String.format(
                "recIndex=%d notificationCost=%d notificationDate=%s physicalAddress=%s",
                recIndex,
                notificationCost,
                notificationDate,
                AuditLogUtils.SENSITIVE
        );
    }

    @Override
    public Instant getElementTimestamp() {
        return notificationDate;
    }

    @Override
    public void setPhysicalAddress(PhysicalAddressInt physicalAddressInt) {
        this.physicalAddress = physicalAddressInt;
    }
}