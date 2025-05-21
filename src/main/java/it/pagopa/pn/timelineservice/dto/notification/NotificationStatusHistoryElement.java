package it.pagopa.pn.timelineservice.dto.notification;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Setter
@Getter
@lombok.Builder(toBuilder = true)
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor
@lombok.ToString
public class NotificationStatusHistoryElement {

  private NotificationStatus status;

  private java.time.Instant activeFrom;

  private List<String> relatedTimelineElements = new ArrayList<>();

  public NotificationStatusHistoryElement status(NotificationStatus status) {
    this.status = status;
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
