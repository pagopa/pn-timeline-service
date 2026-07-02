package it.pagopa.pn.timelineservice.middleware.dao.dynamo.entity;

import lombok.Getter;

@Getter
public enum CourtesyChannelFailureReasonEntity {
    EXPECTED_FAILURE("EXPECTED_FAILURE"),

    RETRIES_EXHAUSTED("RETRIES_EXHAUSTED");

    private final String value;

    CourtesyChannelFailureReasonEntity(String value) {
        this.value = value;
    }

}
