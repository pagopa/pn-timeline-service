package it.pagopa.pn.timelineservice.dto.timeline.details;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AnalogSuccessWorkflowDetailsIntTest {

    private AnalogSuccessWorkflowDetailsInt analogSuccessWorkflowDetailsInt;

    @BeforeEach
    void setup() {
        analogSuccessWorkflowDetailsInt = new AnalogSuccessWorkflowDetailsInt();
    }

    @Test
    void toLog() {
        String expected = "recIndex=0 physicalAddress='Sensitive information'";

        String actual = analogSuccessWorkflowDetailsInt.toLog();

        Assertions.assertEquals(expected, actual);
    }
}