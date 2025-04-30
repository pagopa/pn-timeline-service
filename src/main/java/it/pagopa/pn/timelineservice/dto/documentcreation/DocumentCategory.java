package it.pagopa.pn.timelineservice.dto.documentcreation;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import javax.annotation.Generated;

/**
 * Tipologie di documenti
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-04-23T15:16:43.168944600+02:00[Europe/Rome]")
public enum DocumentCategory {
  
  AAR("AAR");

  private String value;

  DocumentCategory(String value) {
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
  public static DocumentCategory fromValue(String value) {
    for (DocumentCategory b : DocumentCategory.values()) {
      if (b.value.equals(value)) {
        return b;
      }
    }
    throw new IllegalArgumentException("Unexpected value '" + value + "'");
  }
}

