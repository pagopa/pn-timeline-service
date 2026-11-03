package it.pagopa.pn.timelineservice.middleware.dao.dynamo.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
public enum ReceiverAddressSourceTypeEntity {

    MITTENTE("MITTENTE"),
    ANPR("ANPR"),
    REGISTRO_IMPRESE("REGISTRO_IMPRESE"),
    INDAGINE("INDAGINE");

    private final String value;
}
