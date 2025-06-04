package it.pagopa.pn.timelineservice.dto.timeline.details;

import it.pagopa.pn.timelineservice.dto.address.PhysicalAddressInt;
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
public class BaseRegisteredLetterDetailsInt extends CategoryTypeTimelineElementDetailsInt implements RecipientRelatedTimelineElementDetails, PhysicalAddressRelatedTimelineElement {
    protected int recIndex;
    protected PhysicalAddressInt physicalAddress;
    protected String foreignState;

    public String toLog() {
        return String.format(
                "recIndex=%d physicalAddress=%s",
                recIndex,
                AuditLogUtils.SENSITIVE
        );
    }
}
