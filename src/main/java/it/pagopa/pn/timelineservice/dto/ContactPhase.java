package it.pagopa.pn.timelineservice.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import javax.annotation.Generated;

/**
 * Fase in cui è avvenuta la richiesta
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-04-23T15:16:39.518831600+02:00[Europe/Rome]")
public enum ContactPhase {
  
  CHOOSE_DELIVERY("CHOOSE_DELIVERY"),
  
  SEND_ATTEMPT("SEND_ATTEMPT");

  private String value;

  ContactPhase(String value) {
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
  public static ContactPhase fromValue(String value) {
    for (ContactPhase b : ContactPhase.values()) {
      if (b.value.equals(value)) {
        return b;
      }
    }
    throw new IllegalArgumentException("Unexpected value '" + value + "'");
  }
}

