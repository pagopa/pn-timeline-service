package it.pagopa.pn.timelineservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import it.pagopa.pn.timelineservice.dto.details.TimelineElementCategoryV26;
import it.pagopa.pn.timelineservice.dto.legalfacts.LegalFactsIdV20;
import org.springframework.format.annotation.DateTimeFormat;

import javax.annotation.Generated;
import javax.validation.Valid;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * TimelineElementV26
 */
@lombok.Builder(toBuilder = true)
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-04-23T15:16:39.518831600+02:00[Europe/Rome]")
@lombok.ToString
public class TimelineElementV26 {

  @JsonProperty("elementId")
  private String elementId;

  @JsonProperty("timestamp")
  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private java.time.Instant timestamp;

  @JsonProperty("ingestionTimestamp")
  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private java.time.Instant ingestionTimestamp;

  @JsonProperty("eventTimestamp")
  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private java.time.Instant eventTimestamp;

  @JsonProperty("notificationSentAt")
  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private java.time.Instant notificationSentAt;

  @JsonProperty("legalFactsIds")
  @Valid
  private List<LegalFactsIdV20> legalFactsIds = null;

  @JsonProperty("category")
  private TimelineElementCategoryV26 category;

  @JsonProperty("details")
  private TimelineElementDetailsV26 details;

  public TimelineElementV26 elementId(String elementId) {
    this.elementId = elementId;
    return this;
  }

  /**
   * Identificativo dell'elemento di timeline: insieme allo IUN della notifica definisce in maniera univoca l'elemento di timeline
   * @return elementId
  */
  @Size(max = 512) 
  public String getElementId() {
    return elementId;
  }

  public void setElementId(String elementId) {
    this.elementId = elementId;
  }

  public TimelineElementV26 timestamp(java.time.Instant timestamp) {
    this.timestamp = timestamp;
    return this;
  }

  /**
   * Istante in cui avviene l'evento descritto in questo elemento di timeline (deprecato, fare riferimento al campo eventTimestamp)
   * @return timestamp
  */
  @Valid 
  public java.time.Instant getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(java.time.Instant timestamp) {
    this.timestamp = timestamp;
  }

  public TimelineElementV26 ingestionTimestamp(java.time.Instant ingestionTimestamp) {
    this.ingestionTimestamp = ingestionTimestamp;
    return this;
  }

  /**
   * Istante in cui l'evento descritto in questo elemento di timeline è gestito da SEND
   * @return ingestionTimestamp
  */
  @Valid 
  public java.time.Instant getIngestionTimestamp() {
    return ingestionTimestamp;
  }

  public void setIngestionTimestamp(java.time.Instant ingestionTimestamp) {
    this.ingestionTimestamp = ingestionTimestamp;
  }

  public TimelineElementV26 eventTimestamp(java.time.Instant eventTimestamp) {
    this.eventTimestamp = eventTimestamp;
    return this;
  }

  /**
   * Istante in cui avviene l'evento descritto in questo elemento di timeline
   * @return eventTimestamp
  */
  @Valid 
  public java.time.Instant getEventTimestamp() {
    return eventTimestamp;
  }

  public void setEventTimestamp(java.time.Instant eventTimestamp) {
    this.eventTimestamp = eventTimestamp;
  }

  public TimelineElementV26 notificationSentAt(java.time.Instant notificationSentAt) {
    this.notificationSentAt = notificationSentAt;
    return this;
  }

  /**
   * Momento di ricezione della richiesta di notifica da parte di SEND
   * @return notificationSentAt
  */
  @Valid 
  public java.time.Instant getNotificationSentAt() {
    return notificationSentAt;
  }

  public void setNotificationSentAt(java.time.Instant notificationSentAt) {
    this.notificationSentAt = notificationSentAt;
  }

  public TimelineElementV26 legalFactsIds(List<LegalFactsIdV20> legalFactsIds) {
    this.legalFactsIds = legalFactsIds;
    return this;
  }

  public TimelineElementV26 addLegalFactsIdsItem(LegalFactsIdV20 legalFactsIdsItem) {
    if (this.legalFactsIds == null) {
      this.legalFactsIds = new ArrayList<>();
    }
    this.legalFactsIds.add(legalFactsIdsItem);
    return this;
  }

  /**
   * Chiavi dei documenti che provano l'effettivo accadimento dell'evento descritto in timeline. Questo elemento
   * @return legalFactsIds
  */
  @Valid 
  public List<LegalFactsIdV20> getLegalFactsIds() {
    return legalFactsIds;
  }

  public void setLegalFactsIds(List<LegalFactsIdV20> legalFactsIds) {
    this.legalFactsIds = legalFactsIds;
  }

  public TimelineElementV26 category(TimelineElementCategoryV26 category) {
    this.category = category;
    return this;
  }

  /**
   * Get category
   * @return category
  */
  @Valid 
  public TimelineElementCategoryV26 getCategory() {
    return category;
  }

  public void setCategory(TimelineElementCategoryV26 category) {
    this.category = category;
  }

  public TimelineElementV26 details(TimelineElementDetailsV26 details) {
    this.details = details;
    return this;
  }

  /**
   * Get details
   * @return details
  */
  @Valid 
  public TimelineElementDetailsV26 getDetails() {
    return details;
  }

  public void setDetails(TimelineElementDetailsV26 details) {
    this.details = details;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    TimelineElementV26 timelineElementV26 = (TimelineElementV26) o;
    return Objects.equals(this.elementId, timelineElementV26.elementId) &&
        Objects.equals(this.timestamp, timelineElementV26.timestamp) &&
        Objects.equals(this.ingestionTimestamp, timelineElementV26.ingestionTimestamp) &&
        Objects.equals(this.eventTimestamp, timelineElementV26.eventTimestamp) &&
        Objects.equals(this.notificationSentAt, timelineElementV26.notificationSentAt) &&
        Objects.equals(this.legalFactsIds, timelineElementV26.legalFactsIds) &&
        Objects.equals(this.category, timelineElementV26.category) &&
        Objects.equals(this.details, timelineElementV26.details);
  }

  @Override
  public int hashCode() {
    return Objects.hash(elementId, timestamp, ingestionTimestamp, eventTimestamp, notificationSentAt, legalFactsIds, category, details);
  }
}
