package it.pagopa.pn.timelineservice.exceptions;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

class PnLockReservedTest {
    @Test
    void createsExceptionWithCorrectFields() {
        String description = "Lock is already held";
        String detail = "Resource: timeline-123";
        PnLockReserved ex = new PnLockReserved(description, detail);

        Assertions.assertEquals("Unable to acquire lock", ex.getMessage());
        Assertions.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getStatus());
        Assertions.assertNull(ex.getCause());
    }
}