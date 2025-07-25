package it.pagopa.pn.timelineservice.middleware.dao.dynamo.entity;

public enum ContactPhaseEntity {
    CHOOSE_DELIVERY("CHOOSE_DELIVERY"),

    SEND_ATTEMPT("SEND_ATTEMPT");

    private final String value;

    ContactPhaseEntity(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
