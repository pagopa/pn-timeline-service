package it.pagopa.pn.timelineservice.dto.timeline.details;

import it.pagopa.pn.timelineservice.utils.AuditLogUtils;
import lombok.*;
import lombok.experimental.SuperBuilder;


@NoArgsConstructor
@Getter
@Setter
@SuperBuilder(toBuilder = true)
@EqualsAndHashCode(callSuper = true)
@ToString
public class ValidateNormalizeAddressDetailsInt extends CategoryTypeTimelineElementDetailsInt implements TimelineElementDetailsInt{
    
    public String toLog() {
        return AuditLogUtils.EMPTY;
    }
}

