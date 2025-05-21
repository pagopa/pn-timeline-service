package it.pagopa.pn.timelineservice.dto.ext.notification;

import lombok.*;

import java.time.Instant;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder(toBuilder = true)
@EqualsAndHashCode
@ToString
public class NotificationInt {
    private String iun;
    private int recipientsCount;
    private Instant sentAt;
    private String paProtocolNumber;
}
