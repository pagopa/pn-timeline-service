package it.pagopa.pn.timelineservice.dto.ext.externalchannel;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Gets or Sets ResultFilterEnum
 */
public enum ResultFilterEnum {
  
  SUCCESS("SUCCESS"),
  
  DISCARD("DISCARD"),
  
  NEXT("NEXT");

  private String value;

  ResultFilterEnum(String value) {
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
  public static ResultFilterEnum fromValue(String value) {
    for (ResultFilterEnum b : ResultFilterEnum.values()) {
      if (b.value.equals(value)) {
        return b;
      }
    }
    throw new IllegalArgumentException("Unexpected value '" + value + "'");
  }
}

