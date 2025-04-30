package it.pagopa.pn.timelineservice.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import javax.annotation.Generated;

/**
 * Livello Servizio
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-04-23T15:16:39.518831600+02:00[Europe/Rome]")
public enum ServiceLevel {
  
  AR_REGISTERED_LETTER("AR_REGISTERED_LETTER"),
  
  REGISTERED_LETTER_890("REGISTERED_LETTER_890");

  private String value;

  ServiceLevel(String value) {
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
  public static ServiceLevel fromValue(String value) {
    for (ServiceLevel b : ServiceLevel.values()) {
      if (b.value.equals(value)) {
        return b;
      }
    }
    throw new IllegalArgumentException("Unexpected value '" + value + "'");
  }
}

