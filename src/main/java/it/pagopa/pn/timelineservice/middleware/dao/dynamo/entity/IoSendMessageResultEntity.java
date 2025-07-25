package it.pagopa.pn.timelineservice.middleware.dao.dynamo.entity;

public enum IoSendMessageResultEntity {
    NOT_SENT_OPTIN_ALREADY_SENT("NOT_SENT_OPTIN_ALREADY_SENT"),

    SENT_COURTESY("SENT_COURTESY"),

    SENT_OPTIN("SENT_OPTIN");

    private final String value;

    IoSendMessageResultEntity(String value) {
        this.value = value;
    }
}
