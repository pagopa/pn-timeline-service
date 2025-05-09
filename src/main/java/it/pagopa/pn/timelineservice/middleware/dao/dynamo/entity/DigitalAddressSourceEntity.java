package it.pagopa.pn.timelineservice.middleware.dao.dynamo.entity;

public enum DigitalAddressSourceEntity {
    PLATFORM("PLATFORM"),

    SPECIAL("SPECIAL"),

    GENERAL("GENERAL");

    private final String value;

    DigitalAddressSourceEntity(String value) {
        this.value = value;
    }
    
    public String getValue() {
        return value;
    }
}
