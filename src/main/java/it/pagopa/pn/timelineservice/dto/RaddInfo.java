package it.pagopa.pn.timelineservice.dto;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Builder
@Getter
@EqualsAndHashCode
public class RaddInfo {
    private String type;
    private String transactionId;
}
