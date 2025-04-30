package it.pagopa.pn.timelineservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.annotation.Generated;
import javax.validation.constraints.Size;
import java.util.Objects;

/**
 * NotificationRefusedErrorV25
 */
@lombok.Builder(toBuilder = true)
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-04-23T15:16:39.518831600+02:00[Europe/Rome]")
@lombok.ToString
public class NotificationRefusedErrorV25 {

  @JsonProperty("errorCode")
  private String errorCode;

  @JsonProperty("detail")
  private String detail;

  public NotificationRefusedErrorV25 errorCode(String errorCode) {
    this.errorCode = errorCode;
    return this;
  }

  /**
   * Errori di rifiuto della notifica.   - FILE_NOTFOUND - I riferimenti dei documenti nella notifica non sono corretti o i documenti non sono stati caricati con successo.   - FILE_GONE - Allegato non disponibile: superati i termini di conservazione   - FILE_SHA_ERROR - Il digest calcolato con algoritmi sha256 dei documenti inseriti non è corrispondete a quello del file referenziato.   - FILE_PDF_INVALID_ERROR - Il file inserito non è in formato pdf.   - FILE_PDF_TOOBIG_ERROR - Il file pdf ha una dimensione eccedente il limite di 200MB.   - F24_METADATA_NOT_VALID - I dati contenuti del file dei metadati F24 non rispettano lo schema di validazione.   - TAXID_NOT_VALID - Codice fiscale del destinatario non corretto (non ancora implementato).   - RECIPIENT_ID_NOT_VALID - Destinatario non Valido (utilizzato solo per funzionalità non ancora implementate)   - NOT_VALID_ADDRESS - L'indirizzo di invio cartaceo inserito per il destinatario non ha superato i controlli di correttezza.   - PAYMENT_NOT_VALID - I dati del pagamento non sono sono registrati sulla piattaforma GPD (modalità asincrona di integrazione pagoPA)   - SERVICE_UNAVAILABLE - Il sistema non è attualmente disponibile.   ...
   * @return errorCode
  */
  @Size(max = 128) 
  public String getErrorCode() {
    return errorCode;
  }

  public void setErrorCode(String errorCode) {
    this.errorCode = errorCode;
  }

  public NotificationRefusedErrorV25 detail(String detail) {
    this.detail = detail;
    return this;
  }

  /**
   * Get detail
   * @return detail
  */
  @Size(max = 2048) 
  public String getDetail() {
    return detail;
  }

  public void setDetail(String detail) {
    this.detail = detail;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    NotificationRefusedErrorV25 notificationRefusedErrorV25 = (NotificationRefusedErrorV25) o;
    return Objects.equals(this.errorCode, notificationRefusedErrorV25.errorCode) &&
        Objects.equals(this.detail, notificationRefusedErrorV25.detail);
  }

  @Override
  public int hashCode() {
    return Objects.hash(errorCode, detail);
  }
}
