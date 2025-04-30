package it.pagopa.pn.timelineservice.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import javax.annotation.Generated;

/**
 * Gets or Sets CxTypeAuthFleet
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-04-23T15:16:46.485080400+02:00[Europe/Rome]")
public enum CxTypeAuthFleet {
  
  PA("PA"),
  
  PF("PF"),
  
  PG("PG");

  private String value;

  CxTypeAuthFleet(String value) {
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
  public static CxTypeAuthFleet fromValue(String value) {
    for (CxTypeAuthFleet b : CxTypeAuthFleet.values()) {
      if (b.value.equals(value)) {
        return b;
      }
    }
    throw new IllegalArgumentException("Unexpected value '" + value + "'");
  }
}

