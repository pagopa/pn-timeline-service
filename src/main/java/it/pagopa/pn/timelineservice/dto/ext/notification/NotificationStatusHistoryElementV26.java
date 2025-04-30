package it.pagopa.pn.timelineservice.dto.ext.notification;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.format.annotation.DateTimeFormat;

import javax.annotation.Generated;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * elenco degli avanzamenti effettuati dal processo di notifica
 */
@lombok.Builder(toBuilder = true)
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-04-23T15:16:39.518831600+02:00[Europe/Rome]")
@lombok.ToString
public class NotificationStatusHistoryElementV26   {

  @JsonProperty("status")
  private NotificationStatusV26 status;

  @JsonProperty("activeFrom")
  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private java.time.Instant activeFrom;

  @JsonProperty("relatedTimelineElements")
  @Valid
  private List<String> relatedTimelineElements = new ArrayList<>();

  public NotificationStatusHistoryElementV26 status(NotificationStatusV26 status) {
    this.status = status;
    return this;
  }

  /**
   * Get status
   * @return status
  */
  @NotNull @Valid 
  public NotificationStatusV26 getStatus() {
    return status;
  }

  public void setStatus(NotificationStatusV26 status) {
    this.status = status;
  }

  public NotificationStatusHistoryElementV26 activeFrom(java.time.Instant activeFrom) {
    this.activeFrom = activeFrom;
    return this;
  }

  /**
   * data e ora di raggiungimento dello stato di avanzamento
   * @return activeFrom
  */
  @NotNull @Valid 
  public java.time.Instant getActiveFrom() {
    return activeFrom;
  }

  public void setActiveFrom(java.time.Instant activeFrom) {
    this.activeFrom = activeFrom;
  }

  public NotificationStatusHistoryElementV26 relatedTimelineElements(List<String> relatedTimelineElements) {
    this.relatedTimelineElements = relatedTimelineElements;
    return this;
  }

  public NotificationStatusHistoryElementV26 addRelatedTimelineElementsItem(String relatedTimelineElementsItem) {
    if (this.relatedTimelineElements == null) {
      this.relatedTimelineElements = new ArrayList<>();
    }
    this.relatedTimelineElements.add(relatedTimelineElementsItem);
    return this;
  }

  /**
   * Eventi avvenuti nello stato
   * @return relatedTimelineElements
  */
  @NotNull 
  public List<String> getRelatedTimelineElements() {
    return relatedTimelineElements;
  }

  public void setRelatedTimelineElements(List<String> relatedTimelineElements) {
    this.relatedTimelineElements = relatedTimelineElements;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    NotificationStatusHistoryElementV26 notificationStatusHistoryElementV26 = (NotificationStatusHistoryElementV26) o;
    return Objects.equals(this.status, notificationStatusHistoryElementV26.status) &&
        Objects.equals(this.activeFrom, notificationStatusHistoryElementV26.activeFrom) &&
        Objects.equals(this.relatedTimelineElements, notificationStatusHistoryElementV26.relatedTimelineElements);
  }

  @Override
  public int hashCode() {
    return Objects.hash(status, activeFrom, relatedTimelineElements);
  }
}
