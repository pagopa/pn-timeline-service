package it.pagopa.pn.timelineservice.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import javax.annotation.Generated;

/**
 * sorgente indirizzo di invio della notifica
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-04-23T15:16:39.518831600+02:00[Europe/Rome]")
public enum DigitalAddressSource {
  
  PLATFORM("PLATFORM"),
  
  SPECIAL("SPECIAL"),
  
  GENERAL("GENERAL");

  private String value;

  DigitalAddressSource(String value) {
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
  public static DigitalAddressSource fromValue(String value) {
    for (DigitalAddressSource b : DigitalAddressSource.values()) {
      if (b.value.equals(value)) {
        return b;
      }
    }
    throw new IllegalArgumentException("Unexpected value '" + value + "'");
  }
}

