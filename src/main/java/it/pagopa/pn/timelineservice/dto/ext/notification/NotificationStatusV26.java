package it.pagopa.pn.timelineservice.dto.ext.notification;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import javax.annotation.Generated;

/**
 * stato di avanzamento del processo di notifica:   * `IN_VALIDATION` - notifica depositata in attesa di validazione   * `ACCEPTED` - L'ente ha depositato la notifica con successo   * `REFUSED` - Notifica rifiutata a seguito della validazione   * `DELIVERING` - L'invio della notifica è in corso   * `DELIVERED` - La notifica è stata consegnata a tutti i destinatari   * `VIEWED` - Il destinatario ha letto la notifica entro il termine stabilito   * `EFFECTIVE_DATE` - Il destinatario non ha letto la notifica entro il termine stabilito   * `UNREACHABLE` - Il destinatario non è reperibile   * `CANCELLED` - L'ente ha annullato l'invio della notifica   * `PAID` - [DEPRECATO] Uno dei destinatari ha pagato la notifica   * `RETURNED_TO_SENDER` - La notifica è stata restituita al mittente 
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-04-23T15:16:39.518831600+02:00[Europe/Rome]")
public enum NotificationStatusV26 {
  
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

  private String value;

  NotificationStatusV26(String value) {
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
  public static NotificationStatusV26 fromValue(String value) {
    for (NotificationStatusV26 b : NotificationStatusV26.values()) {
      if (b.value.equals(value)) {
        return b;
      }
    }
    throw new IllegalArgumentException("Unexpected value '" + value + "'");
  }
}

