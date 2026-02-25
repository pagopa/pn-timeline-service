package it.pagopa.pn.timelineservice.service.impl;

import it.pagopa.pn.timelineservice.dto.legalfacts.LegalFactsIdInt;
import it.pagopa.pn.timelineservice.dto.timeline.TimelineElementInternal;
import it.pagopa.pn.timelineservice.dto.timeline.details.RecipientRelatedTimelineElementDetails;
import it.pagopa.pn.timelineservice.dto.timeline.details.TimelineElementDetailsInt;
import it.pagopa.pn.timelineservice.generated.openapi.server.v1.dto.LegalFactWithRecIndex;
import it.pagopa.pn.timelineservice.generated.openapi.server.v1.dto.LegalFactsResponse;
import it.pagopa.pn.timelineservice.middleware.dao.TimelineDao;
import it.pagopa.pn.timelineservice.service.LegalFactService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import reactor.core.publisher.Mono;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor
public class LegalFactServiceImpl implements LegalFactService {
    private final TimelineDao timelineDao;

    public Mono<LegalFactsResponse> getLegalFacts(String iun, Integer recIndex) {
        log.debug("getLegalFacts - IUN={} recIndex={}", iun, recIndex);
        return timelineDao.getTimeline(iun)
                .filter(element -> hasLegalFacts(element, recIndex))
                .collectSortedList(Comparator.comparing(TimelineElementInternal::getTimestamp))
                .map(this::mapToLegalFactsResponse)
                .doOnError(e -> log.error("getLegalFacts error - IUN={} recIndex={}", iun, recIndex, e));
    }

    private boolean hasLegalFacts(TimelineElementInternal element, Integer recIndex) {
        if (CollectionUtils.isEmpty(element.getLegalFactsIds())) {
            return false;
        }
        // Se recIndex è fornito, deve corrispondere. Se non è fornito (null), passa tutto.
        if (recIndex != null && element.getDetails() instanceof RecipientRelatedTimelineElementDetails details) {
            return Objects.equals(details.getRecIndex(), recIndex);
        }
        return true;
    }

    private LegalFactsResponse mapToLegalFactsResponse(List<TimelineElementInternal> timelineElements) {
        List<LegalFactWithRecIndex> legalFacts = timelineElements.stream()
                .flatMap(element -> element.getLegalFactsIds().stream()
                        .map(legalFact -> createLegalFact(legalFact, element.getDetails())))
                .toList();

        LegalFactsResponse response = new LegalFactsResponse();
        response.setLegalFacts(legalFacts);
        return response;
    }

    private LegalFactWithRecIndex createLegalFact(LegalFactsIdInt legalFactId, TimelineElementDetailsInt details) {
        LegalFactWithRecIndex fact = new LegalFactWithRecIndex();
        fact.setKey(legalFactId.getKey());

        if (legalFactId.getCategory() != null) {
            fact.setCategory(LegalFactWithRecIndex.CategoryEnum.fromValue(legalFactId.getCategory().getValue()));
        }

        if (details instanceof RecipientRelatedTimelineElementDetails recipientDetails) {
            fact.setRecIndex(recipientDetails.getRecIndex());
        }
        return fact;
    }
}
