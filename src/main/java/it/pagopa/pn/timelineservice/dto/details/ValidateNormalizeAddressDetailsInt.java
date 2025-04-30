package it.pagopa.pn.timelineservice.dto.details;

import it.pagopa.pn.timelineservice.utils.AuditLogUtils;
import lombok.*;


@NoArgsConstructor
@Getter
@Setter
@Builder(toBuilder = true)
@EqualsAndHashCode
@ToString
public class ValidateNormalizeAddressDetailsInt implements TimelineElementDetailsInt{
    
    public String toLog() {
        return AuditLogUtils.EMPTY;
    }
}

