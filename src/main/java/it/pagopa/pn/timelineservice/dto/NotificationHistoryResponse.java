package it.pagopa.pn.timelineservice.dto;

import it.pagopa.pn.timelineservice.dto.timeline.TimelineElementInternal;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@lombok.Builder(toBuilder = true)
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor
@lombok.ToString
public class NotificationHistoryResponse {

  private NotificationStatusV26 notificationStatus;

  private List<NotificationStatusHistoryElement> notificationStatusHistory = null;

  private List<TimelineElementInternal> timeline = null;

  public NotificationHistoryResponse notificationStatus(NotificationStatusV26 notificationStatus) {
    this.notificationStatus = notificationStatus;
    return this;
  }

  public NotificationStatusV26 getNotificationStatus() {
    return notificationStatus;
  }

  public void setNotificationStatus(NotificationStatusV26 notificationStatus) {
    this.notificationStatus = notificationStatus;
  }

  public NotificationHistoryResponse notificationStatusHistory(List<NotificationStatusHistoryElement> notificationStatusHistory) {
    this.notificationStatusHistory = notificationStatusHistory;
    return this;
  }

  public NotificationHistoryResponse addNotificationStatusHistoryItem(NotificationStatusHistoryElement notificationStatusHistoryItem) {
    if (this.notificationStatusHistory == null) {
      this.notificationStatusHistory = new ArrayList<>();
    }
    this.notificationStatusHistory.add(notificationStatusHistoryItem);
    return this;
  }

  public List<NotificationStatusHistoryElement> getNotificationStatusHistory() {
    return notificationStatusHistory;
  }

  public void setNotificationStatusHistory(List<NotificationStatusHistoryElement> notificationStatusHistory) {
    this.notificationStatusHistory = notificationStatusHistory;
  }

  public NotificationHistoryResponse timeline(List<TimelineElementInternal> timeline) {
    this.timeline = timeline;
    return this;
  }

  public NotificationHistoryResponse addTimelineItem(TimelineElementInternal timelineItem) {
    if (this.timeline == null) {
      this.timeline = new ArrayList<>();
    }
    this.timeline.add(timelineItem);
    return this;
  }

  public List<TimelineElementInternal> getTimeline() {
    return timeline;
  }

  public void setTimeline(List<TimelineElementInternal> timeline) {
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
