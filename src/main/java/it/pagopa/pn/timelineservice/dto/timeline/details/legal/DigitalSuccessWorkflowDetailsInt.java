package it.pagopa.pn.timelineservice.dto.timeline.details.legal;

import it.pagopa.pn.timelineservice.dto.address.LegalDigitalAddressInt;
import it.pagopa.pn.timelineservice.dto.timeline.details.common.CategoryTypeTimelineElementDetailsInt;
import it.pagopa.pn.timelineservice.dto.timeline.details.common.RecipientRelatedTimelineElementDetails;
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
public class DigitalSuccessWorkflowDetailsInt extends CategoryTypeTimelineElementDetailsInt implements RecipientRelatedTimelineElementDetails, DigitalAddressRelatedTimelineElement{
    private int recIndex;
    private LegalDigitalAddressInt digitalAddress;

    public String toLog() {
        return String.format(
                "recIndex=%d digitalAddress=%s",
                recIndex,
                AuditLogUtils.SENSITIVE
        );
    }
}