package it.pagopa.pn.timelineservice.dto.notification;


import lombok.*;

import java.time.Instant;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder(toBuilder = true)
@EqualsAndHashCode
@ToString
public class ProbableSchedulingAnalogDateInt {
    private String iun;
    private Integer recIndex;
    private Instant schedulingAnalogDate;
}
