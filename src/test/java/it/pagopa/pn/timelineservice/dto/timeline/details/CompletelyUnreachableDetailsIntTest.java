package it.pagopa.pn.timelineservice.dto.timeline.details;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CompletelyUnreachableDetailsIntTest {

    private CompletelyUnreachableDetailsInt detailsInt;

    @BeforeEach
    void setup() {
        detailsInt = CompletelyUnreachableDetailsInt.builder()
                .recIndex(0)
                .build();
    }

    @Test
    void toLog() {
        String log = detailsInt.toLog();
        Assertions.assertEquals("recIndex=0", log);
    }

    @Test
    void getRecIndex() {
        int rec = detailsInt.getRecIndex();
        Assertions.assertEquals(0, rec);
    }
}