package it.pagopa.pn.timelineservice.dto.details;

import it.pagopa.pn.timelineservice.dto.ext.notification.NotificationRefusedErrorInt;
import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder(toBuilder = true)
@EqualsAndHashCode
@ToString
public class RequestRefusedDetailsInt implements TimelineElementDetailsInt {
    private List<NotificationRefusedErrorInt> refusalReasons;
    private Integer numberOfRecipients;
    private Integer notificationCost;

    public String toLog() {
        return String.format(
                "errors=%s",
                refusalReasons
        );
    }
}
