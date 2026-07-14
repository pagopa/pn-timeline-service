package it.pagopa.pn.timelineservice.dto.timeline;

import lombok.Getter;

@Getter
public enum ReworkRequestTypeEnum {
    REWORK("REWORK"),
    RESTART("RESTART"),
    INVALIDATE_ELEMENTS("INVALIDATE_ELEMENTS");

    private final String value;

    ReworkRequestTypeEnum(String value) {
        this.value = value;
    }

}
