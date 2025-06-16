package it.pagopa.pn.timelineservice.exceptions;

import it.pagopa.pn.commons.exceptions.PnRuntimeException;
import org.springframework.http.HttpStatus;

public class PnLockReserved extends PnRuntimeException {
    public PnLockReserved(String description, String detail) {
        super("Unable to acquire lock", description, HttpStatus.INTERNAL_SERVER_ERROR.value(), "LOCK_RESERVED", null, detail);
    }
}
