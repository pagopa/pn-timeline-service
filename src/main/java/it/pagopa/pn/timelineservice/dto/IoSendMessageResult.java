package it.pagopa.pn.timelineservice.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import javax.annotation.Generated;

/**
 * Risultato invio messaggio su IO
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-04-23T15:16:39.518831600+02:00[Europe/Rome]")
public enum IoSendMessageResult {
  
  NOT_SENT_OPTIN_ALREADY_SENT("NOT_SENT_OPTIN_ALREADY_SENT"),
  
  SENT_COURTESY("SENT_COURTESY"),
  
  SENT_OPTIN("SENT_OPTIN");

  private String value;

  IoSendMessageResult(String value) {
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
  public static IoSendMessageResult fromValue(String value) {
    for (IoSendMessageResult b : IoSendMessageResult.values()) {
      if (b.value.equals(value)) {
        return b;
      }
    }
    throw new IllegalArgumentException("Unexpected value '" + value + "'");
  }
}

