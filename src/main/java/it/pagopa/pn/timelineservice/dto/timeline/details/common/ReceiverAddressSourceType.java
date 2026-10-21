package it.pagopa.pn.timelineservice.dto.timeline.details.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
public enum ReceiverAddressSourceType {

    MITTENTE("MITTENTE"),
    ANPR("ANPR"),
    REGISTRO_IMPRESE("REGISTRO_IMPRESE"),
    INDAGINE("INDAGINE");

    private final String value;
}
