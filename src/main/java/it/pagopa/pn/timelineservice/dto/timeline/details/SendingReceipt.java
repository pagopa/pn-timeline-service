package it.pagopa.pn.timelineservice.dto.timeline.details;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder(toBuilder = true)
@EqualsAndHashCode
@ToString
public class SendingReceipt {
    private String id;
    private String system;
}
