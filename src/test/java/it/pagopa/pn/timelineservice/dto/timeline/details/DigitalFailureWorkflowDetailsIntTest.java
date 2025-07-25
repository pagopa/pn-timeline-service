package it.pagopa.pn.timelineservice.dto.timeline.details;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DigitalFailureWorkflowDetailsIntTest {

    private DigitalFailureWorkflowDetailsInt detailsInt;

    @BeforeEach
    void setUp() {
        detailsInt = new DigitalFailureWorkflowDetailsInt();
        detailsInt.setRecIndex(1);
    }

    @Test
    void toLog() {
        Assertions.assertEquals("recIndex=1", detailsInt.toLog());
    }
}