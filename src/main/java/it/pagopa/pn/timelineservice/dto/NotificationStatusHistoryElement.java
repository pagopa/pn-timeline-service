package it.pagopa.pn.timelineservice.dto;

import it.pagopa.pn.timelineservice.generated.openapi.msclient.delivery.model.NotificationStatusV26;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@lombok.Builder(toBuilder = true)
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor
@lombok.ToString
public class NotificationStatusHistoryElement {

  private NotificationStatusV26 status;

  private java.time.Instant activeFrom;

  private List<String> relatedTimelineElements = new ArrayList<>();

  public NotificationStatusHistoryElement status(NotificationStatusV26 status) {
    this.status = status;
    return this;
  }

  public NotificationStatusV26 getStatus() {
    return status;
  }

  public void setStatus(NotificationStatusV26 status) {
    this.status = status;
  }

  public NotificationStatusHistoryElement activeFrom(java.time.Instant activeFrom) {
    this.activeFrom = activeFrom;
    return this;
  }

  public java.time.Instant getActiveFrom() {
    return activeFrom;
  }

  public void setActiveFrom(java.time.Instant activeFrom) {
    this.activeFrom = activeFrom;
  }

  public NotificationStatusHistoryElement relatedTimelineElements(List<String> relatedTimelineElements) {
    this.relatedTimelineElements = relatedTimelineElements;
    return this;
  }

  public NotificationStatusHistoryElement addRelatedTimelineElementsItem(String relatedTimelineElementsItem) {
    if (this.relatedTimelineElements == null) {
      this.relatedTimelineElements = new ArrayList<>();
    }
    this.relatedTimelineElements.add(relatedTimelineElementsItem);
    return this;
  }

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
    NotificationStatusHistoryElement notificationStatusHistoryElement = (NotificationStatusHistoryElement) o;
    return Objects.equals(this.status, notificationStatusHistoryElement.status) &&
        Objects.equals(this.activeFrom, notificationStatusHistoryElement.activeFrom) &&
        Objects.equals(this.relatedTimelineElements, notificationStatusHistoryElement.relatedTimelineElements);
  }

  @Override
  public int hashCode() {
    return Objects.hash(status, activeFrom, relatedTimelineElements);
  }
}
