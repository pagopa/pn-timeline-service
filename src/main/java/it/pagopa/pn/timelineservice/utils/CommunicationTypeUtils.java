package it.pagopa.pn.timelineservice.utils;

import it.pagopa.pn.commons.exceptions.PnInternalException;
import it.pagopa.pn.timelineservice.dto.timeline.CommunicationType;
import it.pagopa.pn.timelineservice.dto.timeline.TimelineElementInternal;

import java.util.Collection;

import static it.pagopa.pn.timelineservice.exceptions.PnTimelineServiceExceptionCodes.ERROR_CODE_TIMELINESERVICE_INCONSISTENT_COMMUNICATION_TYPE;
import static it.pagopa.pn.timelineservice.exceptions.PnTimelineServiceExceptionCodes.ERROR_CODE_TIMELINESERVICE_UNKNOWN_COMMUNICATION_TYPE;

public class CommunicationTypeUtils {
    /**
     * Verifica che un nuovo elemento da aggiungere alla timeline abbia
     * un {@link CommunicationType} compatibile con quello degli elementi
     * già presenti.
     *
     * @param newElement       l'elemento candidato all'inserimento
     * @param existingElements gli elementi già presenti nella timeline
     *                         (può essere vuota: in tal caso nessun vincolo)
     * @throws PnInternalException se newElement ha communication type null
     *                             o incompatibile con gli esistenti
     */
    public static void validateCommunicationTypeConsistency(TimelineElementInternal newElement, Collection<TimelineElementInternal> existingElements) {
        CommunicationType expected = newElement.getCommunicationType();

        if (expected == null) {
            throw new PnInternalException("Communication type cannot be null", ERROR_CODE_TIMELINESERVICE_UNKNOWN_COMMUNICATION_TYPE);
        }
        for (TimelineElementInternal e : existingElements) {
            CommunicationType actual = e.getCommunicationType();
            if (actual == null) {
                throw new PnInternalException(
                        String.format("Null communication type on element=%s", e.getElementId()),
                        ERROR_CODE_TIMELINESERVICE_UNKNOWN_COMMUNICATION_TYPE);
            }
            if (actual != expected) {
                throw new PnInternalException(
                        String.format(
                                "Inconsistent communication type: expected=%s, found=%s on element=%s",
                                expected, actual, e.getElementId()),
                        ERROR_CODE_TIMELINESERVICE_INCONSISTENT_COMMUNICATION_TYPE);
            }
        }
    }
}
