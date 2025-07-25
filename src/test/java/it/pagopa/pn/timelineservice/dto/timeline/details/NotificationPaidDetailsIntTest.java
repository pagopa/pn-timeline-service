package it.pagopa.pn.timelineservice.dto.timeline.details;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class NotificationPaidDetailsIntTest {

    private NotificationPaidDetailsInt details;

    @BeforeEach
    void setUp() {
        details = new NotificationPaidDetailsInt();
        details.setRecIndex(1);
        details.setAmount(1000);
        details.setNoticeCode("noticeCode");
        details.setCreditorTaxId("creditorTaxId");
        details.setRecipientType("PF");
        details.setPaymentSourceChannel("source");
    }

    @Test
    void toLog() {

        Assertions.assertEquals(details.toLog(), details.toLog());
    }

}