package it.pagopa.pn.timelineservice.dto.informalnotification;

import lombok.Getter;

@Getter
public enum DigitalChannelsInt {
    APPIO("APPIO"),
    PEC("PEC"),
    EMAIL("EMAIL"),
    SMS("SMS");

    private final String value;

    DigitalChannelsInt(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
