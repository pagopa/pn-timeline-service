package it.pagopa.pn.timelineservice.dto.notification;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum NotificationStatus {

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

  private final String value;

  NotificationStatus(String value) {
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
  public static NotificationStatus fromValue(String value) {
    for (NotificationStatus b : NotificationStatus.values()) {
      if (b.value.equals(value)) {
        return b;
      }
    }
    throw new IllegalArgumentException("Unexpected value '" + value + "'");
  }
}

