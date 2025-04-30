package it.pagopa.pn.timelineservice.dto.ext.notification;

import com.fasterxml.jackson.annotation.JsonProperty;
import it.pagopa.pn.timelineservice.dto.TimelineElementV26;
import it.pagopa.pn.timelineservice.dto.details.TimelineElementCategoryV26;

import javax.annotation.Generated;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * NotificationHistoryResponse
 */
@lombok.Builder(toBuilder = true)
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-04-23T15:16:39.518831600+02:00[Europe/Rome]")
@lombok.ToString
public class NotificationHistoryResponse {

  @JsonProperty("notificationStatus")
  private NotificationStatusV26 notificationStatus;

  @JsonProperty("notificationStatusHistory")
  @Valid
  private List<NotificationStatusHistoryElementV26> notificationStatusHistory = null;

  @JsonProperty("timeline")
  @Valid
  private List<TimelineElementV26> timeline = null;

  public NotificationHistoryResponse notificationStatus(NotificationStatusV26 notificationStatus) {
    this.notificationStatus = notificationStatus;
    return this;
  }

  /**
   * Get notificationStatus
   * @return notificationStatus
  */
  @Valid 
  public NotificationStatusV26 getNotificationStatus() {
    return notificationStatus;
  }

  public void setNotificationStatus(NotificationStatusV26 notificationStatus) {
    this.notificationStatus = notificationStatus;
  }

  public NotificationHistoryResponse notificationStatusHistory(List<NotificationStatusHistoryElementV26> notificationStatusHistory) {
    this.notificationStatusHistory = notificationStatusHistory;
    return this;
  }

  public NotificationHistoryResponse addNotificationStatusHistoryItem(NotificationStatusHistoryElementV26 notificationStatusHistoryItem) {
    if (this.notificationStatusHistory == null) {
      this.notificationStatusHistory = new ArrayList<>();
    }
    this.notificationStatusHistory.add(notificationStatusHistoryItem);
    return this;
  }

  /**
   * elenco degli avanzamenti effettuati dal processo di notifica
   * @return notificationStatusHistory
  */
  @Valid 
  public List<NotificationStatusHistoryElementV26> getNotificationStatusHistory() {
    return notificationStatusHistory;
  }

  public void setNotificationStatusHistory(List<NotificationStatusHistoryElementV26> notificationStatusHistory) {
    this.notificationStatusHistory = notificationStatusHistory;
  }

  public NotificationHistoryResponse timeline(List<TimelineElementV26> timeline) {
    this.timeline = timeline;
    return this;
  }

  public NotificationHistoryResponse addTimelineItem(TimelineElementV26 timelineItem) {
    if (this.timeline == null) {
      this.timeline = new ArrayList<>();
    }
    this.timeline.add(timelineItem);
    return this;
  }

  /**
   * elenco dettagliato di tutto ciò che è accaduto durante il processo di notifica
   * @return timeline
  */
  @Valid 
  public List<TimelineElementV26> getTimeline() {
    return timeline;
  }

  public void setTimeline(List<TimelineElementV26> timeline) {
    this.timeline = timeline;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    NotificationHistoryResponse notificationHistoryResponse = (NotificationHistoryResponse) o;
    return Objects.equals(this.notificationStatus, notificationHistoryResponse.notificationStatus) &&
        Objects.equals(this.notificationStatusHistory, notificationHistoryResponse.notificationStatusHistory) &&
        Objects.equals(this.timeline, notificationHistoryResponse.timeline);
  }

  @Override
  public int hashCode() {
    return Objects.hash(notificationStatus, notificationStatusHistory, timeline);
  }
}
