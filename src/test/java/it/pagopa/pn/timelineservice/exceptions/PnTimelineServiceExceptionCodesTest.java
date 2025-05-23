package it.pagopa.pn.timelineservice.exceptions;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


class PnTimelineServiceExceptionCodesTest {

    private PnTimelineServiceExceptionCodes code;

    @Test
    void checkAll() {
        Assertions.assertAll(
                () -> Assertions.assertEquals("PN_TIMELINESERVICE_STATUSNOTFOUND", code.ERROR_CODE_TIMELINESERVICE_STATUSNOTFOUND),
                () -> Assertions.assertEquals("PN_TIMELINESERVICE_ADDTIMELINEFAILED", code.ERROR_CODE_TIMELINESERVICE_ADDTIMELINEFAILED),
                () -> Assertions.assertEquals("PN_TIMELINESERVICE_NOTIFICATION_STATUS_FAILED", code.ERROR_CODE_TIMELINESERVICE_NOTIFICATIONSTATUSFAILED),
                () -> Assertions.assertEquals("PN_TIMELINESERVICE_DUPLICATED_ITEM", code.ERROR_CODE_TIMELINESERVICE_DUPLICATED_ITEM),
                () -> Assertions.assertEquals("ERROR_CODE_TIMELINESERVICE_TIMELINEELEMENTNOTPRESENT", code.ERROR_CODE_TIMELINESERVICE_TIMELINE_ELEMENT_NOT_PRESENT)
        );
    }

}