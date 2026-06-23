package it.pagopa.pn.timelineservice.middleware.timelinedao.entity;

import it.pagopa.pn.timelineservice.dto.timeline.details.TimelineElementCategoryInt;
import it.pagopa.pn.timelineservice.middleware.dao.dynamo.entity.TimelineElementCategoryEntity;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class TimelineElementCategoryEntityTest {
    @Test
    void checkElement(){
        //Nota il test fallisce probabilmente ci si potrebbe essere dimenticati di aggiungere nell'entity un nuovo valore presente nel Dto interno
        assertDoesNotThrow( ()  ->{
            for (TimelineElementCategoryInt timelineElementCategoryInt : TimelineElementCategoryInt.values()) {
                TimelineElementCategoryEntity.valueOf(timelineElementCategoryInt.name());
            }
        });
    }
}