package it.pagopa.pn.timelineservice.dto.timeline;

import lombok.Getter;

@Getter
public enum ElementIdPrefix {
    NOTIFICATION_CANCELLATION_REQUEST;

    private final String value;

    ElementIdPrefix() {
        this.value = this.name();
    }
}
