package it.pagopa.pn.timelineservice.utils;

import it.pagopa.pn.commons.exceptions.PnInternalException;
import it.pagopa.pn.timelineservice.dto.timeline.CommunicationType;
import it.pagopa.pn.timelineservice.dto.timeline.TimelineElementInternal;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CommunicationTypeUtilsTest {
    @Test
    void doesNotThrowWhenExistingElementsIsEmpty() {
        TimelineElementInternal newElement = TimelineElementInternal.builder()
                .communicationType(CommunicationType.LEGAL)
                .build();

        assertDoesNotThrow(() -> CommunicationTypeUtils.validateCommunicationTypeConsistency(newElement, Collections.emptyList()));
    }

    @Test
    void doesNotThrowWhenAllElementsHaveSameCommunicationType() {
        TimelineElementInternal newElement = TimelineElementInternal.builder()
                .communicationType(CommunicationType.LEGAL)
                .build();
        TimelineElementInternal existing = TimelineElementInternal.builder()
                .communicationType(CommunicationType.LEGAL)
                .build();

        assertDoesNotThrow(() -> CommunicationTypeUtils.validateCommunicationTypeConsistency(newElement, List.of(existing)));
    }

    @Test
    void throwsExceptionWhenNewElementHasNullCommunicationType() {
        TimelineElementInternal newElement = TimelineElementInternal.builder()
                .communicationType(null)
                .build();

        assertThrows(PnInternalException.class,
                () -> CommunicationTypeUtils.validateCommunicationTypeConsistency(newElement, Collections.emptyList()));
    }

    @Test
    void throwsExceptionWhenExistingElementHasNullCommunicationType() {
        TimelineElementInternal newElement = TimelineElementInternal.builder()
                .communicationType(CommunicationType.LEGAL)
                .build();
        TimelineElementInternal existing = TimelineElementInternal.builder()
                .communicationType(null)
                .build();

        assertThrows(PnInternalException.class,
                () -> CommunicationTypeUtils.validateCommunicationTypeConsistency(newElement, List.of(existing)));
    }

    @Test
    void throwsExceptionWhenCommunicationTypeMismatch() {
        TimelineElementInternal newElement = TimelineElementInternal.builder()
                .communicationType(CommunicationType.LEGAL)
                .build();
        TimelineElementInternal existing = TimelineElementInternal.builder()
                .communicationType(CommunicationType.INFORMAL)
                .build();

        assertThrows(PnInternalException.class,
                () -> CommunicationTypeUtils.validateCommunicationTypeConsistency(newElement, List.of(existing)));
    }

    @Test
    void throwsExceptionOnFirstInconsistentElementAmongMultiple() {
        TimelineElementInternal newElement = TimelineElementInternal.builder()
                .communicationType(CommunicationType.LEGAL)
                .build();
        TimelineElementInternal consistent = TimelineElementInternal.builder()
                .communicationType(CommunicationType.LEGAL)
                .build();
        TimelineElementInternal inconsistent = TimelineElementInternal.builder()
                .communicationType(CommunicationType.INFORMAL)
                .build();

        assertThrows(PnInternalException.class,
                () -> CommunicationTypeUtils.validateCommunicationTypeConsistency(newElement, List.of(consistent, inconsistent)));
    }
}