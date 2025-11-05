package it.pagopa.pn.timelineservice.dto.timeline.details;

import lombok.Getter;

@Getter
public enum ExtendedDeliveryModeInt {

    DIGITAL("DIGITAL"),

    ANALOG("ANALOG"),

    UNKNOWN("UNKNOWN");

    private final String value;

    ExtendedDeliveryModeInt(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
