package it.pagopa.pn.timelineservice.service;

import it.pagopa.pn.timelineservice.generated.openapi.server.v1.dto.LegalFactsResponse;
import reactor.core.publisher.Mono;

public interface LegalFactService {
    Mono<LegalFactsResponse> getLegalFacts(String iun, Integer recIndex);
}
