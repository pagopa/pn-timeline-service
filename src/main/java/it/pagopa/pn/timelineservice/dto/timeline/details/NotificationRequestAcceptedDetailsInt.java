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
public class NotificationRequestAcceptedDetailsInt extends CategoryTypeTimelineElementDetailsInt implements TimelineElementDetailsInt{

    private String notificationRequestId;
    private String paProtocolNumber;
    private String idempotenceToken;

    public String toLog() {
        return String.format("notificationRequestId=%s, paProtocolNumber=%s, idempotenceToken=%s",
                notificationRequestId,
                paProtocolNumber,
                idempotenceToken
        );
    }
}

