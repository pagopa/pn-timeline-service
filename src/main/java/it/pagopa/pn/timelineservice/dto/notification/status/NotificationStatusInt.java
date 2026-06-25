package it.pagopa.pn.timelineservice.dto.notification.status;

import lombok.Getter;

@Getter
public enum NotificationStatusInt {
    IN_VALIDATION("IN_VALIDATION", NotificationStatusInt.VERSION_10),

    ACCEPTED("ACCEPTED", NotificationStatusInt.VERSION_10),

    DELIVERING("DELIVERING", NotificationStatusInt.VERSION_10),

    DELIVERED("DELIVERED", NotificationStatusInt.VERSION_10),

    VIEWED("VIEWED", NotificationStatusInt.VERSION_10),

    EFFECTIVE_DATE("EFFECTIVE_DATE", NotificationStatusInt.VERSION_10),

    PAID("PAID", NotificationStatusInt.VERSION_10),

    UNREACHABLE("UNREACHABLE", NotificationStatusInt.VERSION_10),

    REFUSED("REFUSED", NotificationStatusInt.VERSION_10),

    CANCELLED("CANCELLED", NotificationStatusInt.VERSION_10),

    RETURNED_TO_SENDER("RETURNED_TO_SENDER", NotificationStatusInt.VERSION_26),

    /* Nuovi stati per gli elementi di timeline delle notifiche bonarie*/

    PROCESSING("PROCESSING", NotificationStatusInt.VERSION_10),

    REACHED("REACHED", NotificationStatusInt.VERSION_10),

    UNREACHED("UNREACHED", NotificationStatusInt.VERSION_10),

    UNDELIVERABLE("UNDELIVERABLE", NotificationStatusInt.VERSION_10),

    COMPLETED("COMPLETED", NotificationStatusInt.VERSION_10);

    private final String value;
    private final int version;

    public static final int VERSION_10 = 10;
    public static final int VERSION_26 = 26;

    NotificationStatusInt(String value, int version) {
        this.value = value;
        this.version = version;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }

}
