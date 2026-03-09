package it.pagopa.pn.timelineservice.dto.timeline;

import lombok.Getter;

@Getter
public enum ElementIdPrefix {
    REQUEST_REFUSED,
    NOTIFICATION_CANCELLATION_REQUEST;

    private final String value;

    ElementIdPrefix() {
        this.value = this.name();
    }
}
