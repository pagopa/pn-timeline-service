package it.pagopa.pn.timelineservice.dto.timeline.details;

import it.pagopa.pn.timelineservice.dto.address.LegalDigitalAddressInt;
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
public class DigitalDeliveryCreationRequestDetailsInt extends CategoryTypeTimelineElementDetailsInt implements RecipientRelatedTimelineElementDetails, DigitalAddressRelatedTimelineElement{
    private int recIndex;
    private EndWorkflowStatus endWorkflowStatus;
    private Instant completionWorkflowDate;
    private LegalDigitalAddressInt digitalAddress;
    private String legalFactId;

    public String toLog() {
        return String.format(
                "recIndex=%d endWorkflowStatus%s completionWorkflowDate=%s digitalAddress=%s legalFactId=%s",
                recIndex,
                endWorkflowStatus,
                completionWorkflowDate,
                AuditLogUtils.SENSITIVE,
                legalFactId
        );
    }
}