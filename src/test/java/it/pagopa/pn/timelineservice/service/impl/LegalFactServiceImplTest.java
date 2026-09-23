package it.pagopa.pn.timelineservice.service.impl;

import it.pagopa.pn.timelineservice.dto.legalfacts.LegalFactCategoryInt;
import it.pagopa.pn.timelineservice.dto.legalfacts.LegalFactsIdInt;
import it.pagopa.pn.timelineservice.dto.timeline.TimelineElementInternal;
import it.pagopa.pn.timelineservice.dto.timeline.details.legal.NotificationViewedDetailsInt;
import it.pagopa.pn.timelineservice.exceptions.PnNotFoundException;
import it.pagopa.pn.timelineservice.generated.openapi.server.v1.dto.LegalFactWithRecIndex;
import it.pagopa.pn.timelineservice.middleware.dao.TimelineDao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.Instant;
import java.util.List;

import static it.pagopa.pn.timelineservice.dto.timeline.details.TimelineElementCategoryInt.VALIDATED_F24;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class LegalFactServiceImplTest {
    private TimelineDao timelineDao;
    private LegalFactServiceImpl service;

    @BeforeEach
    void setup() {
        timelineDao = mock(TimelineDao.class);
        service = new LegalFactServiceImpl(timelineDao);
    }

    private static final String IUN = "IUN123";

    @Test
    void getLegalFacts_ShouldReturnSortedFacts_WhenTimelineHasFacts() {
        // Given
        var olderElement = createTimelineElement("factKey1", Instant.now().minusSeconds(10));
        var newerElement = createTimelineElement("factKey2", Instant.now());

        // Simulo ritorno non ordinato dal DAO per verificare che il service ordini
        when(timelineDao.getTimeline(IUN)).thenReturn(Flux.just(newerElement, olderElement));

        // When & Then
        StepVerifier.create(service.getLegalFacts(IUN, null))
                .assertNext(response -> {
                    assertThat(response.getLegalFacts()).hasSize(2);
                    assertThat(response.getLegalFacts().get(0).getKey()).isEqualTo("factKey1");
                    assertThat(response.getLegalFacts().get(1).getKey()).isEqualTo("factKey2");
                })
                .verifyComplete();
    }

    @Test
    void getLegalFacts_ShouldFilterByRecIndex_WhenIndexIsProvided() {
        // Given
        int targetIndex = 0;
        var senderFact = createTimelineElement("senderKey", Instant.now());

        var targetRecipientFact = createTimelineElementWithIndex("targetKey", Instant.now(), targetIndex);
        var otherRecipientFact = createTimelineElementWithIndex("otherKey", Instant.now(), 2);

        when(timelineDao.getTimeline(IUN)).thenReturn(Flux.just(senderFact, targetRecipientFact, otherRecipientFact));

        // When & Then
        StepVerifier.create(service.getLegalFacts(IUN, targetIndex))
                .assertNext(response -> {
                    assertThat(response.getLegalFacts()).hasSize(2);
                    assertThat(response.getLegalFacts().stream().map(LegalFactWithRecIndex::getKey))
                            .containsExactlyInAnyOrder("senderKey", "targetKey");
                })
                .verifyComplete();
    }

    @Test
    void getLegalFacts_handlesNullCategoryGracefully() {
        String iun = "IUN123";
        Integer recIndex = null;

        LegalFactsIdInt factId = LegalFactsIdInt.builder()
                .key("factKey")
                .category(null)
                .build();
        TimelineElementInternal element = mock(TimelineElementInternal.class);
        when(element.getLegalFactsIds()).thenReturn(List.of(factId));
        when(element.getTimestamp()).thenReturn(Instant.now());
        when(element.getDetails()).thenReturn(null);

        when(timelineDao.getTimeline(iun)).thenReturn(Flux.just(element));

        StepVerifier.create(service.getLegalFacts(iun, recIndex))
                .expectNextMatches(response -> response.getLegalFacts().getFirst().getCategory() == null)
                .verifyComplete();
    }

    @Test
    void getLegalFacts_throwsError_whenTimelineIsEmpty() {
        String iun = "IUN123";
        Integer recIndex = null;

        when(timelineDao.getTimeline(iun)).thenReturn(Flux.empty());

        StepVerifier.create(service.getLegalFacts(iun, recIndex))
                .expectError(PnNotFoundException.class)
                .verify();
    }

    private TimelineElementInternal createTimelineElement(String key, Instant timestamp) {
        var factId = LegalFactsIdInt.builder()
                .key(key)
                .category(LegalFactCategoryInt.PEC_RECEIPT)
                .build();

        TimelineElementInternal element = new TimelineElementInternal();
        element.setLegalFactsIds(List.of(factId));
        element.setElementId("elementId");
        element.setCategory(VALIDATED_F24);
        element.setTimestamp(timestamp);
        return element;
    }

    private TimelineElementInternal createTimelineElementWithIndex(String key, Instant timestamp, int index) {
        TimelineElementInternal element = createTimelineElement(key, timestamp);

        NotificationViewedDetailsInt details = new NotificationViewedDetailsInt();
        details.setRecIndex(index);
        element.setDetails(details);

        return element;
    }
}