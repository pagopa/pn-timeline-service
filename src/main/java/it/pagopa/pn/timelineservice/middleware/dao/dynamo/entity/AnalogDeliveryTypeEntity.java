package it.pagopa.pn.timelineservice.middleware.dao.dynamo.entity;

import lombok.Getter;

@Getter
public enum AnalogDeliveryTypeEntity {
    RS("RS");

    private final String value;

    AnalogDeliveryTypeEntity(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
