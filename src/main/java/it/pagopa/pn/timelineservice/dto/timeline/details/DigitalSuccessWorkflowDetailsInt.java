package it.pagopa.pn.timelineservice.dto.timeline.details;

import it.pagopa.pn.timelineservice.dto.address.LegalDigitalAddressInt;
import it.pagopa.pn.timelineservice.utils.AuditLogUtils;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder(toBuilder = true)
@EqualsAndHashCode
@ToString
public class DigitalSuccessWorkflowDetailsInt implements RecipientRelatedTimelineElementDetails, DigitalAddressRelatedTimelineElement{
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