package it.pagopa.pn.timelineservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.format.annotation.DateTimeFormat;

import javax.annotation.Generated;
import javax.validation.Valid;
import javax.validation.constraints.Size;
import java.util.Objects;

/**
 * AttachmentDetails
 */
@lombok.Builder(toBuilder = true)
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-04-23T15:16:39.518831600+02:00[Europe/Rome]")
@lombok.ToString
public class AttachmentDetails {

  @JsonProperty("id")
  private String id;

  @JsonProperty("documentType")
  private String documentType;

  @JsonProperty("url")
  private String url;

  @JsonProperty("date")
  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private java.time.Instant date;

  public AttachmentDetails id(String id) {
    this.id = id;
    return this;
  }

  /**
   * Get id
   * @return id
  */
  @Size(max = 128) 
  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public AttachmentDetails documentType(String documentType) {
    this.documentType = documentType;
    return this;
  }

  /**
   * Codici documentType: - Plico: Indica il plico cartaceo - AR: Indica la ricevuta di ritorno - Indagine: Indica la ricevuta dell'analisi dell'indagine - 23L: Indica la ricevuta 23L 
   * @return documentType
  */
  @Size(max = 20) 
  public String getDocumentType() {
    return documentType;
  }

  public void setDocumentType(String documentType) {
    this.documentType = documentType;
  }

  public AttachmentDetails url(String url) {
    this.url = url;
    return this;
  }

  /**
   * Get url
   * @return url
  */
  @Size(max = 128) 
  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public AttachmentDetails date(java.time.Instant date) {
    this.date = date;
    return this;
  }

  /**
   * Get date
   * @return date
  */
  @Valid 
  public java.time.Instant getDate() {
    return date;
  }

  public void setDate(java.time.Instant date) {
    this.date = date;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    AttachmentDetails attachmentDetails = (AttachmentDetails) o;
    return Objects.equals(this.id, attachmentDetails.id) &&
        Objects.equals(this.documentType, attachmentDetails.documentType) &&
        Objects.equals(this.url, attachmentDetails.url) &&
        Objects.equals(this.date, attachmentDetails.date);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, documentType, url, date);
  }
}
