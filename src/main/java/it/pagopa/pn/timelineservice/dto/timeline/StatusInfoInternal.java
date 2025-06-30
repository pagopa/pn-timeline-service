package it.pagopa.pn.timelineservice.dto.timeline;

import lombok.*;

import java.time.Instant;

@Getter
@Setter
@Builder(toBuilder = true)
@EqualsAndHashCode
@ToString
public class StatusInfoInternal {

    private final String actual;
    private final Instant statusChangeTimestamp;
    private final boolean statusChanged;
}
