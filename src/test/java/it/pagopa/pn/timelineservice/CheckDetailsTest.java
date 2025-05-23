package it.pagopa.pn.timelineservice;

import it.pagopa.pn.timelineservice.dto.notification.status.NotificationStatusInt;
import it.pagopa.pn.timelineservice.dto.timeline.details.TimelineElementCategoryInt;
import it.pagopa.pn.timelineservice.generated.openapi.server.v1.dto.NotificationStatus;
import it.pagopa.pn.timelineservice.generated.openapi.server.v1.dto.TimelineCategory;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class CheckDetailsTest {

    @Test
    void checkExternalCategory(){
        //Nota il test fallisce probabilmente ci si potrebbe essere dimenticati di aggiungere nell'openapi un nuovo valore presente nel Dto interno
        assertDoesNotThrow( ()  ->{
            for (TimelineElementCategoryInt timelineElementCategoryInt : TimelineElementCategoryInt.values()) {
                TimelineCategory.valueOf(timelineElementCategoryInt.name());
            }
        });
    }

    @Test
    void checkInternalCategory(){
        //Nota il test fallisce probabilmente ci si potrebbe essere dimenticati di aggiungere nel Dto interno una category presente nell'openapi
        assertDoesNotThrow( ()  ->{
            for (TimelineCategory timelineCategory : TimelineCategory.values()) {
                TimelineElementCategoryInt.valueOf(timelineCategory.name());
            }
        });
    }

    @Test
    void checkExternalStatus(){
        //Nota il test fallisce probabilmente ci si potrebbe essere dimenticati di aggiungere nell'openapi un nuovo valore presente nel Dto interno
        assertDoesNotThrow( ()  ->{
            for (NotificationStatusInt notificationStatusInt : NotificationStatusInt.values()) {
                NotificationStatus.valueOf(notificationStatusInt.name());
            }
        });
    }

    @Test
    void checkInternalStatus(){
        //Nota il test fallisce probabilmente ci si potrebbe essere dimenticati di aggiungere nel Dto interno uno stato presente nell'openapi
        assertDoesNotThrow( ()  ->{
            for (NotificationStatus notificationStatus : NotificationStatus.values()) {
                NotificationStatusInt.valueOf(notificationStatus.name());
            }
        });
    }
}
