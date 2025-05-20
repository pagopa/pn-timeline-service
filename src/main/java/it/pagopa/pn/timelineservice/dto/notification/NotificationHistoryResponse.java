package it.pagopa.pn.timelineservice.dto.notification;

import it.pagopa.pn.timelineservice.dto.timeline.TimelineElementInternal;
import lombok.Getter;
import lombok.Setter;
import java.util.List;
import java.util.Objects;

@Setter
@Getter
@lombok.Builder(toBuilder = true)
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor
@lombok.ToString
public class NotificationHistoryResponse {

  private NotificationStatus notificationStatus;

  private List<NotificationStatusHistoryElement> notificationStatusHistory = null;

  private List<TimelineElementInternal> timeline = null;

    public NotificationHistoryResponse timeline(List<TimelineElementInternal> timeline) {
    this.timeline = timeline;
    return this;
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
