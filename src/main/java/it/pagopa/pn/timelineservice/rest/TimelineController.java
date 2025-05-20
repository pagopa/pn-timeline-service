package it.pagopa.pn.timelineservice.rest;

import it.pagopa.pn.timelineservice.generated.openapi.server.v1.api.TimelineControllerApi;
import it.pagopa.pn.timelineservice.generated.openapi.server.v1.dto.*;
import lombok.AllArgsConstructor;
import lombok.CustomLog;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;

@RestController
@AllArgsConstructor
@CustomLog
public class TimelineController implements TimelineControllerApi {

    @Override
    public Mono<ResponseEntity<Boolean>> addTimelineElement(Mono<AddTimelineElementRequest> addTimelineElementRequest, final ServerWebExchange exchange) {
        return Mono.empty();
    }

    @Override
    public Mono<ResponseEntity<ProbableSchedulingAnalogDateDto>> getSchedulingAnalogDate(String iun, String recIndex, final ServerWebExchange exchange) {
        return Mono.empty();
    }

    @Override
    public Mono<ResponseEntity<Flux<TimelineElement>>> getTimeline(String iun, Boolean confidentialInfoRequired, Boolean strongly, String timelineId, final ServerWebExchange exchange) {
        return Mono.empty();
    }

    @Override
    public Mono<ResponseEntity<NotificationHistoryResponse>> getTimelineAndStatusHistory(String iun, Integer numberOfRecipients, Instant createdAt, final ServerWebExchange exchange) {
        return Mono.empty();
    }

    @Override
    public Mono<ResponseEntity<TimelineElement>> getTimelineElement(String iun, String timelineId, Boolean strongly,  final ServerWebExchange exchange) {
        return Mono.empty();
    }

    @Override
    public Mono<ResponseEntity<TimelineElementDetails>> getTimelineElementDetailForSpecificRecipient(String iun, String recIndex, Boolean confidentialInfoRequired, String category, final ServerWebExchange exchange) {
        return Mono.empty();
    }

    @Override
    public Mono<ResponseEntity<TimelineElementDetails>> getTimelineElementDetails(String iun, String timelineId,  final ServerWebExchange exchange) {
        return Mono.empty();
    }

    @Override
    public Mono<ResponseEntity<TimelineElement>> getTimelineElementForSpecificRecipient(String iun, String recIndex, String category,  final ServerWebExchange exchange) {
        return Mono.empty();
    }

    @Override
    public Mono<ResponseEntity<Long>> retrieveAndIncrementCounterForTimelineEvent(String timelineId,  final ServerWebExchange exchange) {
        return Mono.empty();
    }
}
