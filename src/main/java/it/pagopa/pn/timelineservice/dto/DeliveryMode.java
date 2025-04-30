package it.pagopa.pn.timelineservice.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import javax.annotation.Generated;

/**
 * Tipologia Domiciliazione
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-04-23T15:16:39.518831600+02:00[Europe/Rome]")
public enum DeliveryMode {
  
  DIGITAL("DIGITAL"),
  
  ANALOG("ANALOG");

  private String value;

  DeliveryMode(String value) {
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
  public static DeliveryMode fromValue(String value) {
    for (DeliveryMode b : DeliveryMode.values()) {
      if (b.value.equals(value)) {
        return b;
      }
    }
    throw new IllegalArgumentException("Unexpected value '" + value + "'");
  }
}

