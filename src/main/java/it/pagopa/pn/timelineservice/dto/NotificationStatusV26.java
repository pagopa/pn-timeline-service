package it.pagopa.pn.timelineservice.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum NotificationStatusV26 {
    IN_VALIDATION("IN_VALIDATION"),

    ACCEPTED("ACCEPTED"),

    REFUSED("REFUSED"),

    DELIVERING("DELIVERING"),

    DELIVERED("DELIVERED"),

    VIEWED("VIEWED"),

    EFFECTIVE_DATE("EFFECTIVE_DATE"),

    PAID("PAID"),

    UNREACHABLE("UNREACHABLE"),

    CANCELLED("CANCELLED"),

    RETURNED_TO_SENDER("RETURNED_TO_SENDER");

    private String value;

    NotificationStatusV26(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }

    @JsonCreator
    public static NotificationStatusV26 fromValue(String value) {
        for (NotificationStatusV26 b : NotificationStatusV26.values()) {
            if (b.value.equals(value)) {
                return b;
            }
        }
        throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }
}
