package it.pagopa.pn.timelineservice.dto.ext.notification;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder(toBuilder = true)
@EqualsAndHashCode
@ToString
public class NotificationRefusedErrorInt {
    private String errorCode;
    private String detail;
    private Integer recIndex;
}
