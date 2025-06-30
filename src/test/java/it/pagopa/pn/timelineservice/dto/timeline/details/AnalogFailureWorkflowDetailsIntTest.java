package it.pagopa.pn.timelineservice.dto.timeline.details;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AnalogFailureWorkflowDetailsIntTest {

    AnalogFailureWorkflowDetailsInt analogFailureWorkflowDetailsInt;

    @BeforeEach
    void setup() {

        analogFailureWorkflowDetailsInt = new AnalogFailureWorkflowDetailsInt();
    }

    @Test
    void toLog() {
        String actual = analogFailureWorkflowDetailsInt.toLog();

        Assertions.assertEquals("recIndex=0", actual);
    }
}