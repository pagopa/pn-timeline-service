package it.pagopa.pn.timelineservice.dto.legalfacts;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import javax.annotation.Generated;

/**
 * Tipi di atti opponibili a terzi che Piattaforma Notifiche mette a disposizione dei suoi utenti. <br/>   - SENDER_ACK  <br/>   - DIGITAL_DELIVERY  <br/>   - ANALOG_DELIVERY  <br/>   - RECIPIENT_ACCESS <br/>   - PEC_RECEIPT  <br/>   - ANALOG_FAILURE_DELIVERY  <br/>   - NOTIFICATION_CANCELLED  <br/>
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-04-23T15:16:43.168944600+02:00[Europe/Rome]")
public enum LegalFactCategoryV20 {
  
  SENDER_ACK("SENDER_ACK"),
  
  DIGITAL_DELIVERY("DIGITAL_DELIVERY"),
  
  ANALOG_DELIVERY("ANALOG_DELIVERY"),
  
  RECIPIENT_ACCESS("RECIPIENT_ACCESS"),
  
  PEC_RECEIPT("PEC_RECEIPT"),
  
  ANALOG_FAILURE_DELIVERY("ANALOG_FAILURE_DELIVERY"),
  
  NOTIFICATION_CANCELLED("NOTIFICATION_CANCELLED");

  private String value;

  LegalFactCategoryV20(String value) {
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
  public static LegalFactCategoryV20 fromValue(String value) {
    for (LegalFactCategoryV20 b : LegalFactCategoryV20.values()) {
      if (b.value.equals(value)) {
        return b;
      }
    }
    throw new IllegalArgumentException("Unexpected value '" + value + "'");
  }
}

