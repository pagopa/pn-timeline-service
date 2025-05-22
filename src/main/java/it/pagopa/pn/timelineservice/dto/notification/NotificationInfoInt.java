package it.pagopa.pn.timelineservice.dto.notification;

import lombok.*;

import java.time.Instant;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder(toBuilder = true)
@EqualsAndHashCode
@ToString
public class NotificationInfoInt {
    private String iun;
    private int recipientsCount;
    private Instant sentAt;
    private String paProtocolNumber;
}
