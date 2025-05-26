package it.pagopa.pn.timelineservice.rest;

import it.pagopa.pn.timelineservice.dto.notification.NotificationInfoInt;
import it.pagopa.pn.timelineservice.dto.timeline.TimelineElementInternal;
import it.pagopa.pn.timelineservice.dto.timeline.details.TimelineElementCategoryInt;
import it.pagopa.pn.timelineservice.generated.openapi.server.v1.api.TimelineControllerApi;
import it.pagopa.pn.timelineservice.generated.openapi.server.v1.dto.*;
import it.pagopa.pn.timelineservice.service.TimelineService;
import it.pagopa.pn.timelineservice.service.mapper.SmartMapper;
import it.pagopa.pn.timelineservice.service.mapper.TimelineElementMapper;
import lombok.CustomLog;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;

@RestController
@RequiredArgsConstructor
@CustomLog
public class TimelineController implements TimelineControllerApi {

    private final TimelineService timelineService;
    private final SmartMapper smartMapper;
    private final TimelineElementMapper timelineElementMapper;

    @Override
    public Mono<ResponseEntity<Boolean>> addTimelineElement(Mono<AddTimelineElementRequest> addTimelineElementRequest, final ServerWebExchange exchange) {
        return addTimelineElementRequest.flatMap(request -> timelineService.addTimelineElement(timelineElementMapper.externalToInternal(request.getTimelineElement()),
                        SmartMapper.mapToClass(request.getNotificationInfo(), NotificationInfoInt.class)))
                .map(ResponseEntity::ok);
    }

    @Override
    public Mono<ResponseEntity<ProbableSchedulingAnalogDate>> getSchedulingAnalogDate(String iun, Integer recIndex, final ServerWebExchange exchange) {
        return timelineService.getSchedulingAnalogDate(iun, recIndex)
                .map(probableSchedulingAnalogDateInt -> SmartMapper.mapToClass(probableSchedulingAnalogDateInt, ProbableSchedulingAnalogDate.class))
                .map(ResponseEntity::ok);
    }

   @Override
    public Mono<ResponseEntity<Flux<TimelineElement>>> getTimeline(String iun, Boolean confidentialInfoRequired, Boolean strongly, String timelineId, final ServerWebExchange exchange) {
        return Mono.fromSupplier(() -> ResponseEntity.ok(timelineService.getTimeline(iun, timelineId, confidentialInfoRequired, strongly)
                .map(timelineElementInternal -> smartMapper.mapToClassWithObjectMapper(timelineElementInternal, TimelineElement.class))));
    }

    @Override
    public Mono<ResponseEntity<NotificationHistoryResponse>> getTimelineAndStatusHistory(String iun, Integer numberOfRecipients, Instant createdAt, final ServerWebExchange exchange) {
        return timelineService.getTimelineAndStatusHistory(iun, numberOfRecipients, createdAt)
                .map(notificationHistoryInt -> smartMapper.mapToClassWithObjectMapper(notificationHistoryInt, NotificationHistoryResponse.class))
                .map(ResponseEntity::ok);
    }

    @Override
    public Mono<ResponseEntity<TimelineElement>> getTimelineElement(String iun, String timelineId, Boolean strongly,  final ServerWebExchange exchange) {
        return timelineService.getTimelineElement(iun, timelineId, strongly)
                .map(timelineElementInternal -> smartMapper.mapToClassWithObjectMapper(timelineElementInternal, TimelineElement.class))
                .map(ResponseEntity::ok);
    }

    @Override
    public Mono<ResponseEntity<TimelineElementDetails>> getTimelineElementDetailForSpecificRecipient(String iun, Integer recIndex, Boolean confidentialInfoRequired, TimelineCategory category, final ServerWebExchange exchange) {
        return timelineService.getTimelineElementDetailForSpecificRecipient(iun, recIndex, confidentialInfoRequired, TimelineElementCategoryInt.valueOf(category.name()))
                .map(timelineElementDetailsInt -> smartMapper.mapToClassWithObjectMapper(timelineElementDetailsInt, TimelineElementDetails.class))
                .map(ResponseEntity::ok);
    }

    @Override
    public Mono<ResponseEntity<TimelineElementDetails>> getTimelineElementDetails(String iun, String timelineId,  final ServerWebExchange exchange) {
        return timelineService.getTimelineElementDetails(iun, timelineId)
                .map(timelineElementDetailsInt -> smartMapper.mapToClassWithObjectMapper(timelineElementDetailsInt, TimelineElementDetails.class))
                .map(ResponseEntity::ok);
    }


    @Override
    public Mono<ResponseEntity<TimelineElement>> getTimelineElementForSpecificRecipient(String iun, Integer recIndex, TimelineCategory category,  final ServerWebExchange exchange) {
        return timelineService.getTimelineElementForSpecificRecipient(iun, recIndex, TimelineElementCategoryInt.valueOf(category.name()))
                .map(timelineElementInternal -> smartMapper.mapToClassWithObjectMapper(timelineElementInternal, TimelineElement.class))
                .map(ResponseEntity::ok);
    }

    @Override
    public Mono<ResponseEntity<Long>> retrieveAndIncrementCounterForTimelineEvent(String timelineId,  final ServerWebExchange exchange) {
        return timelineService.retrieveAndIncrementCounterForTimelineEvent(timelineId)
                .map(ResponseEntity::ok);
    }
}
