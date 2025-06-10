package it.pagopa.pn.timelineservice.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import it.pagopa.pn.timelineservice.dto.notification.NotificationHistoryInt;
import it.pagopa.pn.timelineservice.dto.notification.NotificationInfoInt;
import it.pagopa.pn.timelineservice.dto.notification.ProbableSchedulingAnalogDateInt;
import it.pagopa.pn.timelineservice.dto.notification.status.NotificationStatusHistoryElementInt;
import it.pagopa.pn.timelineservice.dto.notification.status.NotificationStatusInt;
import it.pagopa.pn.timelineservice.dto.timeline.TimelineElementInternal;
import it.pagopa.pn.timelineservice.dto.timeline.details.AarCreationRequestDetailsInt;
import it.pagopa.pn.timelineservice.dto.timeline.details.SendAnalogProgressDetailsInt;
import it.pagopa.pn.timelineservice.dto.timeline.details.TimelineElementCategoryInt;
import it.pagopa.pn.timelineservice.generated.openapi.server.v1.dto.*;
import it.pagopa.pn.timelineservice.legalfacts.AarTemplateType;
import it.pagopa.pn.timelineservice.service.TimelineService;
import it.pagopa.pn.timelineservice.service.mapper.SmartMapper;
import it.pagopa.pn.timelineservice.service.mapper.TimelineElementMapper;
import it.pagopa.pn.timelineservice.service.mapper.TimelineMapperFactory;
import it.pagopa.pn.timelineservice.utils.FeatureEnabledUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TimelineControllerTest {

    private static TimelineService timelineService;

    private static TimelineController timelineController;

    private static ObjectMapper objectMapper;

    @BeforeAll
    static void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        timelineService = mock(TimelineService.class);
        TimelineElementMapper timelineElementMapper = new TimelineElementMapper();
        SmartMapper smartMapper = new SmartMapper(mock(TimelineMapperFactory.class), objectMapper, mock(FeatureEnabledUtils.class));
        timelineController = new TimelineController(timelineService, smartMapper, timelineElementMapper);
    }


    @Test
    void addTimelineElementReturnsOkWhenRequestIsValid() throws JsonProcessingException {
        NewTimelineElement request = new NewTimelineElement();
        String timelineElement = "{\n" +
                " \"iun\": \"LNWV-GRMV-KPWV-202503-W-1\",\n" +
                " \"elementId\": \"AAR_CREATION_REQUEST.IUN_LNWV-GRMV-KPWV-202503-W-1.RECINDEX_0\",\n" +
                " \"category\": \"AAR_CREATION_REQUEST\",\n" +
                " \"details\": {\n" +
                "  \"aarKey\": \"safestorage://PN_AAR-e12466f63b8e49a49150383ad3d2a009.pdf\",\n" +
                "  \"aarTemplateType\": \"AAR_NOTIFICATION\",\n" +
                " \"categoryType\": \"AAR_CREATION_REQUEST\",\n" +
                "  \"numberOfPages\": 2,\n" +
                "  \"recIndex\": 0\n" +
                " },\n" +
                " \"legalFactsIds\": [\n" +
                " ],\n" +
                " \"notificationSentAt\": \"2025-03-03T17:27:04.443460926Z\",\n" +
                " \"paId\": \"5b994d4a-0fa8-47ac-9c7b-354f1d44a1ce\" }";
        request.setTimelineElement(objectMapper.readValue(timelineElement, TimelineElement.class));

        NotificationInfo info = new NotificationInfo();
        info.setIun("LNWV-GRMV-KPWV-202503-W-1");
        info.setNumberOfRecipients(1);
        info.setSentAt(Instant.parse("2025-03-03T17:27:04.443460926Z"));
        info.setPaProtocolNumber("12345678985");
        request.setNotificationInfo(info);

        when(timelineService.addTimelineElement(any(TimelineElementInternal.class),any(NotificationInfoInt.class))).thenReturn(Mono.empty());

        Mono<ResponseEntity<Void>> response = timelineController.addTimelineElement(Mono.just(request), null);

        StepVerifier.create(response)
                .verifyComplete();
    }

    @Test
    void getSchedulingAnalogDateReturnsMappedResponse() {
        String iun = "testIun";
        int recIndex = 1;
        ProbableSchedulingAnalogDateInt schedulingAnalogDateInt = ProbableSchedulingAnalogDateInt.builder()
                .iun("testIun")
                .schedulingAnalogDate(Instant.now())
                .recIndex(1)
                .build();
        ProbableSchedulingAnalogDate schedulingAnalogDate = new ProbableSchedulingAnalogDate();
        schedulingAnalogDate.setIun("testIun");
        schedulingAnalogDate.setRecIndex(1);
        schedulingAnalogDate.setSchedulingAnalogDate(schedulingAnalogDateInt.getSchedulingAnalogDate());

        when(timelineService.getSchedulingAnalogDate(iun, recIndex)).thenReturn(Mono.just(schedulingAnalogDateInt));

        Mono<ResponseEntity<ProbableSchedulingAnalogDate>> response = timelineController.getSchedulingAnalogDate(iun, recIndex, null);

        StepVerifier.create(response)
                .expectNext(ResponseEntity.ok(schedulingAnalogDate))
                .verifyComplete();
    }

    @Test
    void getTimelineReturnsFluxOfTimelineElementsWithTimelineId () {
        String iun = "testIun";
        String timelineId = "testTimelineId";
        boolean confidentialInfoRequired = true;
        boolean strongly = false;

        TimelineElementInternal timelineElementInternal = new TimelineElementInternal();
        timelineElementInternal.setIun("testIun");
        timelineElementInternal.setElementId("testElementId");
        timelineElementInternal.setCategory(TimelineElementCategoryInt.AAR_CREATION_REQUEST);
        AarCreationRequestDetailsInt aarCreationRequestDetailsInt = new AarCreationRequestDetailsInt();
        aarCreationRequestDetailsInt.setAarKey("safestorage://PN_AAR-e12466f63b8e49a49150383ad3d2a009.pdf");
        aarCreationRequestDetailsInt.setAarTemplateType(AarTemplateType.AAR_NOTIFICATION);
        aarCreationRequestDetailsInt.setCategoryType("AAR_CREATION_REQUEST");
        aarCreationRequestDetailsInt.setNumberOfPages(2);
        aarCreationRequestDetailsInt.setRecIndex(0);
        timelineElementInternal.setDetails(aarCreationRequestDetailsInt);
        timelineElementInternal.setTimestamp(Instant.now());
        timelineElementInternal.setIngestionTimestamp(Instant.now().plus(1, ChronoUnit.DAYS));
        timelineElementInternal.setEventTimestamp(Instant.now());
        timelineElementInternal.setNotificationSentAt(Instant.now().minus(1, ChronoUnit.DAYS));

        TimelineElement timelineElement = new TimelineElement();
        timelineElement.setIun("testIun");
        timelineElement.setElementId("testElementId");
        timelineElement.setCategory(TimelineCategory.AAR_CREATION_REQUEST);
        AarCreationRequestDetails aarCreationRequestDetails = new AarCreationRequestDetails();
        aarCreationRequestDetails.setAarKey("safestorage://PN_AAR-e12466f63b8e49a49150383ad3d2a009.pdf");
        aarCreationRequestDetails.setAarTemplateType(AarCreationRequestDetails.AarTemplateTypeEnum.AAR_NOTIFICATION);
        aarCreationRequestDetails.setCategoryType("AAR_CREATION_REQUEST");
        aarCreationRequestDetails.setNumberOfPages(2);
        aarCreationRequestDetails.setRecIndex(0);
        timelineElement.setDetails(aarCreationRequestDetails);
        timelineElement.setTimestamp(timelineElementInternal.getTimestamp());
        timelineElement.setIngestionTimestamp(timelineElementInternal.getIngestionTimestamp());
        timelineElement.setEventTimestamp(timelineElementInternal.getEventTimestamp());
        timelineElement.setNotificationSentAt(timelineElementInternal.getNotificationSentAt());

        when(timelineService.getTimeline(iun, timelineId, confidentialInfoRequired, strongly))
                .thenReturn(Flux.just(timelineElementInternal));

        Mono<ResponseEntity<Flux<TimelineElement>>> response = timelineController.getTimeline(iun, confidentialInfoRequired, strongly, timelineId, null);

        StepVerifier.create(response)
                .assertNext(entity -> {
                    assertNotNull(entity.getBody());
                    StepVerifier.create(entity.getBody())
                            .expectNextMatches(body ->
                                body.getIun().equalsIgnoreCase(timelineElement.getIun()) &&
                                body.getElementId().equalsIgnoreCase(timelineElement.getElementId()) &&
                                body.getCategory().equals(timelineElement.getCategory()) &&
                                body.getDetails().getCategoryType().equalsIgnoreCase(timelineElement.getDetails().getCategoryType()) &&
                                body.getTimestamp().equals(timelineElement.getTimestamp()) &&
                                body.getIngestionTimestamp().equals(timelineElement.getIngestionTimestamp()) &&
                                body.getEventTimestamp().equals(timelineElement.getEventTimestamp()) &&
                                body.getNotificationSentAt().equals(timelineElement.getNotificationSentAt()) &&
                                CollectionUtils.isEmpty(timelineElement.getLegalFactsIds()))
                            .verifyComplete();
                })
                .verifyComplete();
    }

    @Test
    void getTimelineReturnsFluxOfTimelineElementsWithoutTimelineId () {
        String iun = "testIun";
        String timelineId = "testTimelineId";
        boolean confidentialInfoRequired = true;
        boolean strongly = false;

        AarCreationRequestDetailsInt aarCreationRequestDetailsInt = new AarCreationRequestDetailsInt();
        aarCreationRequestDetailsInt.setAarKey("safestorage://PN_AAR-e12466f63b8e49a49150383ad3d2a009.pdf");
        aarCreationRequestDetailsInt.setAarTemplateType(AarTemplateType.AAR_NOTIFICATION);
        aarCreationRequestDetailsInt.setCategoryType("AAR_CREATION_REQUEST");
        aarCreationRequestDetailsInt.setNumberOfPages(2);
        aarCreationRequestDetailsInt.setRecIndex(0);

        TimelineElementInternal timelineElementInternal = new TimelineElementInternal();
        timelineElementInternal.setIun("testIun");
        timelineElementInternal.setElementId("testElementId");
        timelineElementInternal.setCategory(TimelineElementCategoryInt.AAR_CREATION_REQUEST);
        timelineElementInternal.setDetails(aarCreationRequestDetailsInt);
        timelineElementInternal.setTimestamp(Instant.now());
        timelineElementInternal.setIngestionTimestamp(Instant.now().plus(1, ChronoUnit.DAYS));
        timelineElementInternal.setEventTimestamp(Instant.now());
        timelineElementInternal.setNotificationSentAt(Instant.now().minus(1, ChronoUnit.DAYS));

        SendAnalogProgressDetailsInt sendAnalogProgressDetailsInt = new SendAnalogProgressDetailsInt();
        sendAnalogProgressDetailsInt.setCategoryType("SEND_ANALOG_PROGRESS");
        sendAnalogProgressDetailsInt.setRecIndex(1);
        sendAnalogProgressDetailsInt.setSendRequestId("sendRequestId");

        TimelineElementInternal timelineElementInternal2 = new TimelineElementInternal();
        timelineElementInternal2.setIun("testIun2");
        timelineElementInternal2.setElementId("testElementId2");
        timelineElementInternal2.setCategory(TimelineElementCategoryInt.SEND_ANALOG_PROGRESS);
        timelineElementInternal2.setDetails(sendAnalogProgressDetailsInt);
        timelineElementInternal2.setTimestamp(Instant.now());
        timelineElementInternal2.setIngestionTimestamp(Instant.now().plus(1, ChronoUnit.DAYS));
        timelineElementInternal2.setEventTimestamp(Instant.now());
        timelineElementInternal2.setNotificationSentAt(Instant.now().minus(1, ChronoUnit.DAYS));

        when(timelineService.getTimeline(iun, timelineId, confidentialInfoRequired, strongly))
                .thenReturn(Flux.fromIterable(List.of(timelineElementInternal, timelineElementInternal2)));

        Mono<ResponseEntity<Flux<TimelineElement>>> response = timelineController.getTimeline(iun, confidentialInfoRequired, strongly, timelineId, null);

        StepVerifier.create(response)
                .assertNext(entity -> {
                    assertNotNull(entity.getBody());
                    StepVerifier.create(entity.getBody())
                            .expectNextMatches(body ->
                                    body.getIun().equalsIgnoreCase("testIun") &&
                                            body.getElementId().equalsIgnoreCase("testElementId") &&
                                            body.getCategory().equals(TimelineCategory.AAR_CREATION_REQUEST) &&
                                            body.getDetails().getCategoryType().equalsIgnoreCase("AAR_CREATION_REQUEST") &&
                                            body.getTimestamp().equals(timelineElementInternal.getTimestamp()) &&
                                            body.getIngestionTimestamp().equals(timelineElementInternal.getIngestionTimestamp()) &&
                                            body.getEventTimestamp().equals(timelineElementInternal.getEventTimestamp()) &&
                                            body.getNotificationSentAt().equals(timelineElementInternal.getNotificationSentAt()) &&
                                            CollectionUtils.isEmpty(body.getLegalFactsIds()))
                            .expectNextMatches(body ->
                                    body.getIun().equalsIgnoreCase("testIun2") &&
                                            body.getElementId().equalsIgnoreCase("testElementId2") &&
                                            body.getCategory().equals(TimelineCategory.SEND_ANALOG_PROGRESS) &&
                                            body.getDetails().getCategoryType().equalsIgnoreCase("SEND_ANALOG_PROGRESS") &&
                                            body.getTimestamp().equals(timelineElementInternal2.getTimestamp()) &&
                                            body.getIngestionTimestamp().equals(timelineElementInternal2.getIngestionTimestamp()) &&
                                            body.getEventTimestamp().equals(timelineElementInternal2.getEventTimestamp()) &&
                                            body.getNotificationSentAt().equals(timelineElementInternal2.getNotificationSentAt()) &&
                                            CollectionUtils.isEmpty(body.getLegalFactsIds()))
                            .verifyComplete();
                })
                .verifyComplete();
    }

    @Test
    void retrieveAndIncrementCounterForTimelineEventReturnsIncrementedValue() {
        String timelineId = "testTimelineId";
        long incrementedValue = 42L;

        when(timelineService.retrieveAndIncrementCounterForTimelineEvent(timelineId)).thenReturn(Mono.just(incrementedValue));

        Mono<ResponseEntity<Long>> response = timelineController.retrieveAndIncrementCounterForTimelineEvent(timelineId, null);

        StepVerifier.create(response)
                .expectNext(ResponseEntity.ok(incrementedValue))
                .verifyComplete();
    }

    @Test
    void getTimelineAndStatusHistory(){
        Integer numberOfRecipients = 1;
        String iun = "testIun";
        Instant now = Instant.now();

        AarCreationRequestDetailsInt aarCreationRequestDetailsInt = new AarCreationRequestDetailsInt();
        aarCreationRequestDetailsInt.setAarKey("safestorage://PN_AAR-e12466f63b8e49a49150383ad3d2a009.pdf");
        aarCreationRequestDetailsInt.setAarTemplateType(AarTemplateType.AAR_NOTIFICATION);
        aarCreationRequestDetailsInt.setCategoryType("AAR_CREATION_REQUEST");
        aarCreationRequestDetailsInt.setNumberOfPages(2);
        aarCreationRequestDetailsInt.setRecIndex(0);

        TimelineElementInternal timelineElementInternal = new TimelineElementInternal();
        timelineElementInternal.setIun("testIun");
        timelineElementInternal.setElementId("testElementId");
        timelineElementInternal.setCategory(TimelineElementCategoryInt.AAR_CREATION_REQUEST);
        timelineElementInternal.setDetails(aarCreationRequestDetailsInt);
        timelineElementInternal.setTimestamp(Instant.now());
        timelineElementInternal.setIngestionTimestamp(Instant.now().plus(1, ChronoUnit.DAYS));
        timelineElementInternal.setEventTimestamp(Instant.now());
        timelineElementInternal.setNotificationSentAt(Instant.now().minus(1, ChronoUnit.DAYS));

        NotificationHistoryInt notificationHistoryInt = new NotificationHistoryInt();
        notificationHistoryInt.setTimeline(List.of(timelineElementInternal));
        notificationHistoryInt.setNotificationStatus(NotificationStatusInt.CANCELLED);
        notificationHistoryInt.setNotificationStatusHistory(List.of(NotificationStatusHistoryElementInt.builder()
                .status(NotificationStatusInt.CANCELLED)
                .activeFrom(now)
                .relatedTimelineElements(List.of("element1"))
                .build()));

        when(timelineService.getTimelineAndStatusHistory(iun, numberOfRecipients,now))
                .thenReturn(Mono.just(notificationHistoryInt));

        var response = timelineController.getTimelineAndStatusHistory(iun, numberOfRecipients, now, null);

        StepVerifier.create(response)
                .assertNext(entity -> {
                    var body = entity.getBody();
                    assertNotNull(body);
                    var status = body.getNotificationStatus();
                    var statusHistory = body.getNotificationStatusHistory();
                    var timeline = body.getTimeline();
                    Assertions.assertEquals(NotificationStatus.CANCELLED, status);
                    Assertions.assertEquals(NotificationStatus.CANCELLED, statusHistory.getFirst().getStatus());
                    Assertions.assertEquals(statusHistory.getFirst().getActiveFrom(), now);
                    Assertions.assertEquals(1, statusHistory.getFirst().getRelatedTimelineElements().size());
                    Assertions.assertEquals("element1", statusHistory.getFirst().getRelatedTimelineElements().getFirst());
                    Assertions.assertEquals("testIun", timeline.getFirst().getIun());
                    Assertions.assertEquals("testElementId", timeline.getFirst().getElementId());
                    Assertions.assertEquals(TimelineCategory.AAR_CREATION_REQUEST, timeline.getFirst().getCategory());
                    Assertions.assertEquals("AAR_CREATION_REQUEST", Objects.requireNonNull(timeline.getFirst().getDetails()).getCategoryType());
                    Assertions.assertEquals(timeline.getFirst().getTimestamp(), timelineElementInternal.getTimestamp());
                    Assertions.assertEquals(timeline.getFirst().getIngestionTimestamp(), timelineElementInternal.getIngestionTimestamp());
                    Assertions.assertEquals(timeline.getFirst().getEventTimestamp(), timelineElementInternal.getEventTimestamp());
                    Assertions.assertEquals(timeline.getFirst().getNotificationSentAt(), timelineElementInternal.getNotificationSentAt());
                    Assertions.assertTrue(CollectionUtils.isEmpty(timeline.getFirst().getLegalFactsIds()));
                })
                .verifyComplete();
    }

    @Test
    void getTimelineElement(){
        String timelineElementId = "timelineElementId";
        String iun = "testIun";

        AarCreationRequestDetailsInt aarCreationRequestDetailsInt = new AarCreationRequestDetailsInt();
        aarCreationRequestDetailsInt.setAarKey("safestorage://PN_AAR-e12466f63b8e49a49150383ad3d2a009.pdf");
        aarCreationRequestDetailsInt.setAarTemplateType(AarTemplateType.AAR_NOTIFICATION);
        aarCreationRequestDetailsInt.setCategoryType("AAR_CREATION_REQUEST");
        aarCreationRequestDetailsInt.setNumberOfPages(2);
        aarCreationRequestDetailsInt.setRecIndex(0);

        TimelineElementInternal timelineElementInternal = new TimelineElementInternal();
        timelineElementInternal.setIun("testIun");
        timelineElementInternal.setElementId("testElementId");
        timelineElementInternal.setCategory(TimelineElementCategoryInt.AAR_CREATION_REQUEST);
        timelineElementInternal.setDetails(aarCreationRequestDetailsInt);
        timelineElementInternal.setTimestamp(Instant.now());
        timelineElementInternal.setIngestionTimestamp(Instant.now().plus(1, ChronoUnit.DAYS));
        timelineElementInternal.setEventTimestamp(Instant.now());
        timelineElementInternal.setNotificationSentAt(Instant.now().minus(1, ChronoUnit.DAYS));

        when(timelineService.getTimelineElement(iun, timelineElementId, false))
                .thenReturn(Mono.just(timelineElementInternal));

        var response = timelineController.getTimelineElement(iun, timelineElementId, false, null);

        StepVerifier.create(response)
                .assertNext(entity -> {
                    var body = entity.getBody();
                    assertNotNull(entity.getBody());
                    Assertions.assertEquals(body.getIun(), "testIun");
                    Assertions.assertEquals(body.getElementId(), "testElementId");
                    Assertions.assertEquals(body.getCategory(), TimelineCategory.AAR_CREATION_REQUEST);
                    Assertions.assertEquals( body.getDetails().getCategoryType(), "AAR_CREATION_REQUEST");
                    Assertions.assertEquals(body.getTimestamp(), timelineElementInternal.getTimestamp());
                    Assertions.assertEquals(body.getIngestionTimestamp(), timelineElementInternal.getIngestionTimestamp());
                    Assertions.assertEquals(body.getEventTimestamp(), timelineElementInternal.getEventTimestamp());
                    Assertions.assertEquals( body.getNotificationSentAt(), timelineElementInternal.getNotificationSentAt());
                    Assertions.assertTrue(CollectionUtils.isEmpty(body.getLegalFactsIds()));
                })
                .verifyComplete();
    }

    @Test
    void getTimelineElementDetailForSpecificRecipient(){
        String iun = "testIun";
        Integer recIndex = 1;

        AarCreationRequestDetailsInt aarCreationRequestDetailsInt = new AarCreationRequestDetailsInt();
        aarCreationRequestDetailsInt.setAarKey("safestorage://PN_AAR-e12466f63b8e49a49150383ad3d2a009.pdf");
        aarCreationRequestDetailsInt.setAarTemplateType(AarTemplateType.AAR_NOTIFICATION);
        aarCreationRequestDetailsInt.setCategoryType("AAR_CREATION_REQUEST");
        aarCreationRequestDetailsInt.setNumberOfPages(2);
        aarCreationRequestDetailsInt.setRecIndex(0);

        when(timelineService.getTimelineElementDetailForSpecificRecipient(iun, recIndex, false, TimelineElementCategoryInt.AAR_CREATION_REQUEST))
                .thenReturn(Mono.just(aarCreationRequestDetailsInt));

        var response = timelineController.getTimelineElementDetailForSpecificRecipient(iun, recIndex, false, TimelineCategory.AAR_CREATION_REQUEST, null);

        StepVerifier.create(response)
                .assertNext(entity -> {
                    assertNotNull(entity.getBody());
                    var detail = entity.getBody();
                    Assertions.assertTrue(detail.getCategoryType().equalsIgnoreCase("AAR_CREATION_REQUEST"));
                    Assertions.assertInstanceOf(AarCreationRequestDetails.class, detail);
                    AarCreationRequestDetails aarCreationRequestDetails = ((AarCreationRequestDetails) detail);
                    Assertions.assertEquals(2, aarCreationRequestDetails.getNumberOfPages());
                    Assertions.assertEquals(0, aarCreationRequestDetails.getRecIndex());
                    Assertions.assertEquals(AarCreationRequestDetails.AarTemplateTypeEnum.AAR_NOTIFICATION, aarCreationRequestDetails.getAarTemplateType());
                    Assertions.assertEquals("safestorage://PN_AAR-e12466f63b8e49a49150383ad3d2a009.pdf", aarCreationRequestDetails.getAarKey());
                })
                .verifyComplete();
    }

    @Test
    void getTimelineElementDetails(){
        String iun = "testIun";
        String timelineId = "timelineId";

        AarCreationRequestDetailsInt aarCreationRequestDetailsInt = new AarCreationRequestDetailsInt();
        aarCreationRequestDetailsInt.setAarKey("safestorage://PN_AAR-e12466f63b8e49a49150383ad3d2a009.pdf");
        aarCreationRequestDetailsInt.setAarTemplateType(AarTemplateType.AAR_NOTIFICATION);
        aarCreationRequestDetailsInt.setCategoryType("AAR_CREATION_REQUEST");
        aarCreationRequestDetailsInt.setNumberOfPages(2);
        aarCreationRequestDetailsInt.setRecIndex(0);

        when(timelineService.getTimelineElementDetails(iun, timelineId))
                .thenReturn(Mono.just(aarCreationRequestDetailsInt));

        var response = timelineController.getTimelineElementDetails(iun, timelineId,  null);

        StepVerifier.create(response)
                .assertNext(entity -> {
                    assertNotNull(entity.getBody());
                    var detail = entity.getBody();
                    Assertions.assertTrue(detail.getCategoryType().equalsIgnoreCase("AAR_CREATION_REQUEST"));
                    Assertions.assertInstanceOf(AarCreationRequestDetails.class, detail);
                    AarCreationRequestDetails aarCreationRequestDetails = ((AarCreationRequestDetails) detail);
                    Assertions.assertEquals(2, aarCreationRequestDetails.getNumberOfPages());
                    Assertions.assertEquals(0, aarCreationRequestDetails.getRecIndex());
                    Assertions.assertEquals(AarCreationRequestDetails.AarTemplateTypeEnum.AAR_NOTIFICATION, aarCreationRequestDetails.getAarTemplateType());
                    Assertions.assertEquals("safestorage://PN_AAR-e12466f63b8e49a49150383ad3d2a009.pdf", aarCreationRequestDetails.getAarKey());
                })
                .verifyComplete();
    }


    @Test
    void getTimelineElementForSpecificRecipient(){

        Integer recIndex = 0;
        String iun = "testIun";

        AarCreationRequestDetailsInt aarCreationRequestDetailsInt = new AarCreationRequestDetailsInt();
        aarCreationRequestDetailsInt.setAarKey("safestorage://PN_AAR-e12466f63b8e49a49150383ad3d2a009.pdf");
        aarCreationRequestDetailsInt.setAarTemplateType(AarTemplateType.AAR_NOTIFICATION);
        aarCreationRequestDetailsInt.setCategoryType("AAR_CREATION_REQUEST");
        aarCreationRequestDetailsInt.setNumberOfPages(2);
        aarCreationRequestDetailsInt.setRecIndex(0);

        TimelineElementInternal timelineElementInternal = new TimelineElementInternal();
        timelineElementInternal.setIun("testIun");
        timelineElementInternal.setElementId("testElementId");
        timelineElementInternal.setCategory(TimelineElementCategoryInt.AAR_CREATION_REQUEST);
        timelineElementInternal.setDetails(aarCreationRequestDetailsInt);
        timelineElementInternal.setTimestamp(Instant.now());
        timelineElementInternal.setIngestionTimestamp(Instant.now().plus(1, ChronoUnit.DAYS));
        timelineElementInternal.setEventTimestamp(Instant.now());
        timelineElementInternal.setNotificationSentAt(Instant.now().minus(1, ChronoUnit.DAYS));

        when(timelineService.getTimelineElementForSpecificRecipient(iun, recIndex, TimelineElementCategoryInt.AAR_CREATION_REQUEST))
                .thenReturn(Mono.just(timelineElementInternal));

        var response = timelineController.getTimelineElementForSpecificRecipient(iun, recIndex, TimelineCategory.AAR_CREATION_REQUEST, null);

        StepVerifier.create(response)
                .assertNext(entity -> {
                    var body = entity.getBody();
                    assertNotNull(entity.getBody());
                    Assertions.assertEquals(body.getIun(), "testIun");
                    Assertions.assertEquals(body.getElementId(), "testElementId");
                    Assertions.assertEquals(body.getCategory(), TimelineCategory.AAR_CREATION_REQUEST);
                    Assertions.assertEquals( body.getDetails().getCategoryType(), "AAR_CREATION_REQUEST");
                    Assertions.assertEquals(body.getTimestamp(), timelineElementInternal.getTimestamp());
                    Assertions.assertEquals(body.getIngestionTimestamp(), timelineElementInternal.getIngestionTimestamp());
                    Assertions.assertEquals(body.getEventTimestamp(), timelineElementInternal.getEventTimestamp());
                    Assertions.assertEquals( body.getNotificationSentAt(), timelineElementInternal.getNotificationSentAt());
                    Assertions.assertTrue(CollectionUtils.isEmpty(body.getLegalFactsIds()));
                })
                .verifyComplete();
    }
}
