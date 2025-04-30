package it.pagopa.pn.timelineservice.dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder(toBuilder = true)
@EqualsAndHashCode
@ToString
public class StatusDetailInt {

    private String code;
    private String level;
    private String detail;
}

