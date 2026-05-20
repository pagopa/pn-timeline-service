package it.pagopa.pn.timelineservice.operations;

import it.pagopa.pn.commons.exceptions.PnInternalException;
import it.pagopa.pn.timelineservice.dto.timeline.CommunicationType;
import it.pagopa.pn.timelineservice.dto.timeline.TimelineElementInternal;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CommunicationTypeClassifierTest {

    private final CommunicationTypeClassifier classifier = new CommunicationTypeClassifier();

    @Test
    void returnsDefaultTypeWhenElementsIsNull() {
        CommunicationType result = classifier.resolveFromTimelineElements(null);
        assertEquals(CommunicationType.LEGAL, result);
    }

    @Test
    void returnsDefaultTypeWhenElementsIsEmpty() {
        CommunicationType result = classifier.resolveFromTimelineElements(Collections.emptyList());
        assertEquals(CommunicationType.LEGAL, result);
    }

    @Test
    void returnsFirstCommunicationTypeFromElements() {
        TimelineElementInternal element = TimelineElementInternal.builder()
                .communicationType(CommunicationType.INFORMAL)
                .build();

        CommunicationType result = classifier.resolveFromTimelineElements(List.of(element));
        assertEquals(CommunicationType.INFORMAL, result);
    }

    @Test
    void returnsFirstNonNullCommunicationType() {
        // Caso impossibile... i timeline element internal dovrebbero sempre avere un communication type quando letti da DB.
        TimelineElementInternal nullElement = TimelineElementInternal.builder()
                .communicationType(null)
                .build();
        TimelineElementInternal legalElement = TimelineElementInternal.builder()
                .communicationType(CommunicationType.LEGAL)
                .build();

        CommunicationType result = classifier.resolveFromTimelineElements(List.of(nullElement, legalElement));
        assertEquals(CommunicationType.LEGAL, result);
    }

    @Test
    void throwsExceptionWhenAllElementsHaveNullCommunicationType() {
        TimelineElementInternal element1 = TimelineElementInternal.builder()
                .communicationType(null)
                .build();
        TimelineElementInternal element2 = TimelineElementInternal.builder()
                .communicationType(null)
                .build();

        assertThrows(PnInternalException.class,
                () -> classifier.resolveFromTimelineElements(List.of(element1, element2)));
    }

    @Test
    void returnsFirstCommunicationTypeWhenMultipleElementsPresent() {
        TimelineElementInternal first = TimelineElementInternal.builder()
                .communicationType(CommunicationType.LEGAL)
                .build();
        TimelineElementInternal second = TimelineElementInternal.builder()
                .communicationType(CommunicationType.INFORMAL)
                .build();

        CommunicationType result = classifier.resolveFromTimelineElements(List.of(first, second));
        assertEquals(CommunicationType.LEGAL, result);
    }
}