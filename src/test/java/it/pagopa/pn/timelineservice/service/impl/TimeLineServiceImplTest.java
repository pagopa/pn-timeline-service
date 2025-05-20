package it.pagopa.pn.timelineservice.service.impl;

import it.pagopa.pn.commons.exceptions.PnIdConflictException;
import it.pagopa.pn.commons.exceptions.PnInternalException;
import it.pagopa.pn.timelineservice.config.PnTimelineServiceConfigs;
import it.pagopa.pn.timelineservice.dto.*;
import it.pagopa.pn.timelineservice.dto.address.CourtesyDigitalAddressInt;
import it.pagopa.pn.timelineservice.dto.address.DigitalAddressSourceInt;
import it.pagopa.pn.timelineservice.dto.address.LegalDigitalAddressInt;
import it.pagopa.pn.timelineservice.dto.address.PhysicalAddressInt;
import it.pagopa.pn.timelineservice.dto.ext.datavault.ConfidentialTimelineElementDtoInt;
import it.pagopa.pn.timelineservice.dto.ext.notification.*;
import it.pagopa.pn.timelineservice.dto.ext.notification.status.NotificationStatusHistoryElementInt;
import it.pagopa.pn.timelineservice.dto.ext.notification.status.NotificationStatusInt;
import it.pagopa.pn.timelineservice.dto.notification.NotificationStatus;
import it.pagopa.pn.timelineservice.dto.notification.NotificationStatusHistoryElement;
import it.pagopa.pn.timelineservice.dto.timeline.StatusInfoInternal;
import it.pagopa.pn.timelineservice.dto.timeline.TimelineElementInternal;
import it.pagopa.pn.timelineservice.dto.timeline.details.*;
import it.pagopa.pn.timelineservice.exceptions.PnNotFoundException;
import it.pagopa.pn.timelineservice.middleware.dao.TimelineCounterEntityDao;
import it.pagopa.pn.timelineservice.middleware.dao.TimelineDao;
import it.pagopa.pn.timelineservice.middleware.dao.dynamo.entity.TimelineCounterEntity;
import it.pagopa.pn.timelineservice.service.ConfidentialInformationService;
import it.pagopa.pn.timelineservice.service.StatusService;
import it.pagopa.pn.timelineservice.service.mapper.SmartMapper;
import it.pagopa.pn.timelineservice.service.mapper.TimelineMapperFactory;
import it.pagopa.pn.timelineservice.utils.FeatureEnabledUtils;
import it.pagopa.pn.timelineservice.utils.StatusUtils;
import net.javacrumbs.shedlock.core.LockProvider;
import net.javacrumbs.shedlock.core.SimpleLock;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

class TimeLineServiceImplTest {
    private TimelineDao timelineDao;
    private TimelineCounterEntityDao timelineCounterDao;
    private StatusUtils statusUtils;
    private TimeLineServiceImpl timeLineService;
    private StatusService statusService;
    private ConfidentialInformationService confidentialInformationService;
    private SimpleLock simpleLock;
    private LockProvider lockProvider;
    private FeatureEnabledUtils featureEnabledUtils;

    private PnTimelineServiceConfigs pnTimelineServiceConfigs;
    private SmartMapper smartMapper;

    @BeforeEach
    void setup() {
        timelineDao = Mockito.mock( TimelineDao.class );
        timelineCounterDao = Mockito.mock( TimelineCounterEntityDao.class );
        statusUtils = Mockito.mock( StatusUtils.class );
        statusService = Mockito.mock( StatusService.class );
        featureEnabledUtils = Mockito.mock(FeatureEnabledUtils.class);
        confidentialInformationService = Mockito.mock( ConfidentialInformationService.class );
        pnTimelineServiceConfigs = Mockito.mock(PnTimelineServiceConfigs.class);

        Mockito.when(pnTimelineServiceConfigs.getStartWriteBusinessTimestamp()).thenReturn(Instant.now().plus(Duration.ofDays(1)));
        Mockito.when(pnTimelineServiceConfigs.getStopWriteBusinessTimestamp()).thenReturn(Instant.now().minus(Duration.ofDays(1)));
//        smartMapper = new SmartMapper(new TimelineMapperFactory(pnDeliveryPushConfigs));
        smartMapper= Mockito.spy(new SmartMapper(new TimelineMapperFactory(pnTimelineServiceConfigs), featureEnabledUtils));
//        timeLineService = new TimeLineServiceImpl(timelineDao , timelineCounterDao , statusUtils, confidentialInformationService, statusService, schedulerService, notificationService);
        simpleLock = Mockito.mock(SimpleLock.class);
        lockProvider = Mockito.mock(LockProvider.class);
        Mockito.when(pnTimelineServiceConfigs.getTimelineLockDuration()).thenReturn(Duration.ofSeconds(5));

        timeLineService = new TimeLineServiceImpl(timelineDao , timelineCounterDao , statusUtils, confidentialInformationService, statusService, smartMapper, lockProvider, pnTimelineServiceConfigs);
        //timeLineService.setSchedulerService(schedulerService);

    }

    @Test
    void addTimelineElement() {
        // GIVEN
        String iun = "iun_12345";
        String elementId = "elementId_12345";

        NotificationInt notification = NotificationInt.builder().iun(iun).build();
        StatusService.NotificationStatusUpdate notificationStatuses = new StatusService.NotificationStatusUpdate(NotificationStatusInt.ACCEPTED, NotificationStatusInt.ACCEPTED);
        Mockito.when(statusService.getStatus(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(notificationStatuses);
        Mockito.when(confidentialInformationService.saveTimelineConfidentialInformation(Mockito.any())).thenReturn(Mono.empty());
        Mockito.when(timelineDao.addTimelineElementIfAbsent(Mockito.any())).thenReturn(Mono.empty());
        Set<TimelineElementInternal> setTimelineElement = getSendPaperDetailsList(iun, elementId);
        Mockito.when(timelineDao.getTimeline(Mockito.anyString()))
                .thenReturn(Flux.fromIterable(setTimelineElement));

        TimelineElementInternal newElement = TimelineElementInternal.builder()
                .elementId(elementId)
                .iun(iun)
                .timestamp(Instant.now())
                .build();

        // WHEN
        Mono<Void> result = timeLineService.addTimelineElement(newElement, notification).then();

        // THEN
        StepVerifier.create(result)
                .verifyComplete();

        ArgumentCaptor<TimelineElementInternal> captor = ArgumentCaptor.forClass(TimelineElementInternal.class);
        verify(timelineDao).addTimelineElementIfAbsent(captor.capture());
        TimelineElementInternal dtoToPersist = captor.getValue();
        Assertions.assertEquals(dtoToPersist.getTimestamp(), newElement.getTimestamp());
    }

    @Test
        void addTimelineElementNoNotification() {
            String iun = "iun_12345";
            String elementId = "elementId_12345";

            TimelineElementInternal newElement = getAarGenerationTimelineElement(iun, elementId);
            StepVerifier.create(timeLineService.addTimelineElement(newElement, null))
                    .expectError(PnInternalException.class)
                    .verify();
        }

    @Test
    void addCriticalTimelineElement() {
        // GIVEN
        String iun = "iun_12345";
        String elementId = "elementId_12345";
        String elementId2 = "elementId2";

        NotificationInt notification = getNotificationWithMultipleRecipients(iun);
        StatusService.NotificationStatusUpdate notificationStatuses = new StatusService.NotificationStatusUpdate(NotificationStatusInt.ACCEPTED, NotificationStatusInt.ACCEPTED);

        Mockito.when(statusService.getStatus(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(notificationStatuses);
        Set<TimelineElementInternal> setTimelineElement = getSendPaperDetailsList(iun, elementId2);
        Mockito.when(timelineDao.getTimeline(Mockito.anyString())).thenReturn(Flux.fromIterable(setTimelineElement));
        Mockito.when(confidentialInformationService.saveTimelineConfidentialInformation(Mockito.any())).thenReturn(Mono.empty());
        Mockito.when(timelineDao.addTimelineElementIfAbsent(Mockito.any())).thenReturn(Mono.empty());
        Mockito.when(lockProvider.lock(Mockito.any())).thenReturn(Optional.of(simpleLock));
        Mockito.when(confidentialInformationService.getTimelineConfidentialInformation(Mockito.anyString()))
                .thenReturn(Mono.just(Map.of("key", ConfidentialTimelineElementDtoInt.builder().timelineElementId("1").build())));

        TimelineElementInternal newElement = getAnalogSuccessTimelineCriticalElement(iun, elementId);

        // WHEN
        Mono<Boolean> result = timeLineService.addTimelineElement(newElement, notification);

        // THEN
        StepVerifier.create(result)
                .expectNext(false)
                .verifyComplete();

        TimelineElementInternal timelineElement = setTimelineElement.iterator().next();
        Instant timestampLastElementInTimeline = timelineElement.getTimestamp();
        StatusInfoInternal expectedStatusInfo = StatusInfoInternal.builder()
                .actual(NotificationStatusInt.ACCEPTED.getValue())
                .statusChangeTimestamp(timestampLastElementInTimeline).build();

        StatusInfoInternal actualStatusInfo = timeLineService.buildStatusInfo(notificationStatuses, null);
        TimelineElementInternal dtoWithStatusInfo = newElement.toBuilder().statusInfo(actualStatusInfo).build();

        Assertions.assertEquals(expectedStatusInfo.getActual(), actualStatusInfo.getActual());
        Assertions.assertEquals(expectedStatusInfo.isStatusChanged(), actualStatusInfo.isStatusChanged());
        Assertions.assertNull(actualStatusInfo.getStatusChangeTimestamp());
        Mockito.verify(timelineDao).addTimelineElementIfAbsent(dtoWithStatusInfo);
        Mockito.verify(statusService).getStatus(newElement, setTimelineElement, notification);
        Mockito.verify(confidentialInformationService).saveTimelineConfidentialInformation(newElement);
    }

    @Test
    void addTimelineElementWithNullStatusInfo() {
        // GIVEN
        String iun = "iun_12345";
        String elementId = "elementId_12345";
        NotificationInt notification = getNotification(iun);
        TimelineElementInternal newElement = getAarGenerationTimelineElement(iun, elementId);

        // Simula una timeline vuota
        Mockito.when(timelineDao.getTimeline(Mockito.anyString()))
                .thenReturn(Flux.empty());
        // Simula un errore nella generazione dello status
        Mockito.when(statusService.getStatus(Mockito.any(), Mockito.any(), Mockito.any()))
                .thenThrow(new PnInternalException("Error", "test"));

        // WHEN & THEN
        StepVerifier.create(timeLineService.addTimelineElement(newElement, notification))
                .expectError(PnInternalException.class)
                .verify();

        Mockito.verify(statusService).getStatus(newElement, new HashSet<>(), notification);
    }

    @Test
    void addCriticalTimelineElementLockNotAcquired() {
        String iun = "iun_12345";
        String elementId = "elementId_12345";

        NotificationInt notification = getNotificationWithMultipleRecipients(iun);
        StatusService.NotificationStatusUpdate notificationStatuses = new StatusService.NotificationStatusUpdate(NotificationStatusInt.ACCEPTED, NotificationStatusInt.ACCEPTED);

        Mockito.when(statusService.getStatus(Mockito.any(), Mockito.any(), Mockito.any()))
                .thenReturn(notificationStatuses);
        String elementId2 = "elementId2";
        Set<TimelineElementInternal> setTimelineElement = getSendPaperDetailsList(iun, elementId2);
        Mockito.when(timelineDao.getTimeline(Mockito.anyString()))
                .thenReturn(Flux.fromIterable(setTimelineElement));
        Mockito.when(lockProvider.lock(Mockito.any())).thenReturn(Optional.empty());

        TimelineElementInternal newElement = getAnalogSuccessTimelineCriticalElement(iun, elementId);

        StepVerifier.create(timeLineService.addTimelineElement(newElement, notification))
                .expectError(PnInternalException.class)
                .verify();
    }

    @Test
    void addTimelineElementWithBusinessTimestampFeatureFlag() {
        // GIVEN
        String iun = "iun_12345";
        String elementId = "elementId_12345";

        NotificationInt notification = getNotification(iun);
        StatusService.NotificationStatusUpdate notificationStatuses = new StatusService.NotificationStatusUpdate(NotificationStatusInt.ACCEPTED, NotificationStatusInt.ACCEPTED);

        // Abilita la feature flag per il business timestamp
        Instant startWriteBusinessTimestamp = Instant.now().minus(Duration.ofDays(1));
        Instant stopWriteBusinessTimestamp = Instant.now().plus(Duration.ofDays(1));
        Mockito.when(pnTimelineServiceConfigs.getStartWriteBusinessTimestamp()).thenReturn(startWriteBusinessTimestamp);
        Mockito.when(pnTimelineServiceConfigs.getStopWriteBusinessTimestamp()).thenReturn(stopWriteBusinessTimestamp);

        Mockito.when(statusService.getStatus(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(notificationStatuses);

        String elementId2 = "elementId2";
        Set<TimelineElementInternal> setTimelineElement = getSendPaperDetailsList(iun, elementId2);
        Mockito.when(timelineDao.getTimeline(Mockito.anyString()))
                .thenReturn(Flux.fromIterable(setTimelineElement));
        Mockito.when(confidentialInformationService.saveTimelineConfidentialInformation(Mockito.any())).thenReturn(Mono.empty());
        Mockito.when(timelineDao.addTimelineElementIfAbsent(Mockito.any())).thenReturn(Mono.empty());
        Mockito.when(confidentialInformationService.getTimelineConfidentialInformation(Mockito.anyString()))
                .thenReturn(Mono.just(Map.of()));

        TimelineElementInternal newElement = getAarGenerationTimelineElement(iun, elementId);

        // WHEN
        Mono<Boolean> result = timeLineService.addTimelineElement(newElement, notification);

        // THEN
        StepVerifier.create(result)
                .expectNext(false)
                .verifyComplete();

        ArgumentCaptor<TimelineElementInternal> captor = ArgumentCaptor.forClass(TimelineElementInternal.class);
        verify(timelineDao).addTimelineElementIfAbsent(captor.capture());
        TimelineElementInternal dtoToPersist = captor.getValue();
        Assertions.assertEquals(dtoToPersist.getTimestamp(), newElement.getTimestamp());
        // Verifica che il campo eventTimestamp sia valorizzato (feature flag attiva)
        Assertions.assertNotNull(dtoToPersist.getEventTimestamp());

        Mockito.verify(smartMapper).mapTimelineInternal(Mockito.any(), Mockito.any());
        Mockito.verify(timelineDao).addTimelineElementIfAbsent(dtoToPersist);
        Mockito.verify(statusService).getStatus(newElement, setTimelineElement, notification);
        Mockito.verify(confidentialInformationService).saveTimelineConfidentialInformation(newElement);
    }

    @Test
    void addTimelineElementIdConflict(){
        // GIVEN
        String iun = "iun_12345";
        String elementId = "elementId_12345";

        NotificationInt notification = getNotification(iun);
        StatusService.NotificationStatusUpdate notificationStatuses = new StatusService.NotificationStatusUpdate(NotificationStatusInt.ACCEPTED, NotificationStatusInt.ACCEPTED);
        Mockito.when(statusService.getStatus(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(notificationStatuses);
        String elementId2 = "elementId2";
        Set<TimelineElementInternal> setTimelineElement = getSendPaperDetailsList(iun, elementId2);
        Mockito.when(timelineDao.getTimeline(Mockito.anyString()))
                .thenReturn(Flux.fromIterable(setTimelineElement));

        Instant timestampLastElementInTimeline = setTimelineElement.iterator().next().getTimestamp();
        StatusInfoInternal expectedStatusInfo = StatusInfoInternal.builder()
                .actual(NotificationStatusInt.ACCEPTED.getValue())
                .statusChangeTimestamp(timestampLastElementInTimeline).build();

        TimelineElementInternal newElement = getAarGenerationTimelineElement(iun, elementId);

        Mockito.when(timelineDao.addTimelineElementIfAbsent(Mockito.any())).thenReturn(Mono.error(new PnIdConflictException(Collections.emptyMap())));
        Mockito.when(confidentialInformationService.getTimelineConfidentialInformation(Mockito.anyString()))
                .thenReturn(Mono.just(Map.of("key", ConfidentialTimelineElementDtoInt.builder().timelineElementId("1").build())));
        Mockito.when(confidentialInformationService.saveTimelineConfidentialInformation(Mockito.any())).thenReturn(Mono.empty());

        // WHEN & THEN
        StepVerifier.create(timeLineService.addTimelineElement(newElement, notification))
                .expectNext(true)
                .verifyComplete();

        // mi aspetto che il timestampLastUpdateStatus sia null quando gli elementi già salvati non hanno valorizzato
        // lo statusInfo e non c'è stato un cambio di stato
        StatusInfoInternal actualStatusInfo = timeLineService.buildStatusInfo(notificationStatuses, null);
        TimelineElementInternal dtoWithStatusInfo = newElement.toBuilder().statusInfo(actualStatusInfo).build();
        Assertions.assertEquals(expectedStatusInfo.getActual(), actualStatusInfo.getActual());
        Assertions.assertEquals(expectedStatusInfo.isStatusChanged(), actualStatusInfo.isStatusChanged());
        Assertions.assertNull(actualStatusInfo.getStatusChangeTimestamp());
        Mockito.verify(timelineDao).addTimelineElementIfAbsent(dtoWithStatusInfo);
        Mockito.verify(statusService).getStatus(newElement, setTimelineElement, notification);
        Mockito.verify(confidentialInformationService).saveTimelineConfidentialInformation(newElement);
    }

    @Test
    void addCriticalTimelineElementException() {
        // GIVEN
        String iun = "iun_12345";
        String elementId = "elementId_12345";
        NotificationInt notification = getNotificationWithMultipleRecipients(iun);
        TimelineElementInternal newElement = getAnalogSuccessTimelineCriticalElement(iun, elementId);

        Mockito.when(lockProvider.lock(Mockito.any())).thenReturn(Optional.of(simpleLock));
        Mockito.when(statusService.getStatus(Mockito.any(), Mockito.any(), Mockito.any()))
                .thenReturn(new StatusService.NotificationStatusUpdate(NotificationStatusInt.ACCEPTED, NotificationStatusInt.ACCEPTED));
        Mockito.when(timelineDao.getTimeline(Mockito.anyString()))
                .thenReturn(Flux.empty());
        Mockito.when(confidentialInformationService.saveTimelineConfidentialInformation(Mockito.any()))
                .thenReturn(Mono.empty());
        Mockito.doThrow(new PnInternalException("error", "test"))
                .when(timelineDao).addTimelineElementIfAbsent(Mockito.any(TimelineElementInternal.class));

        StepVerifier.create(timeLineService.addTimelineElement(newElement, notification))
                .expectError(PnInternalException.class)
                .verify();

        Mockito.verify(simpleLock).unlock();
    }

    @Test
    void addTimelineElementError() {
        // GIVEN
        String iun = "iun";
        String elementId = "elementId";

        NotificationInt notification = getNotification(iun);

        String elementId2 = "elementId";
        Set<TimelineElementInternal> setTimelineElement = getSendPaperDetailsList(iun, elementId2);
        Mockito.when(timelineDao.getTimeline(Mockito.anyString()))
                .thenReturn(Flux.fromIterable(setTimelineElement));

        TimelineElementInternal newElement = getSendPaperFeedbackTimelineElement(iun, elementId, Instant.now());

        Mockito.doThrow(new PnInternalException("error", "test")).when(statusService)
                .getStatus(Mockito.any(TimelineElementInternal.class), Mockito.anySet(), Mockito.any(NotificationInt.class));

        // WHEN
        StepVerifier.create(timeLineService.addTimelineElement(newElement, notification))
                .expectError(PnInternalException.class)
                .verify();
    }

    @Test
    void addTimelineElementWithUnchangedStatus() {
        // GIVEN
        String iun = "iun";
        String elementId = "elementId";

        String expectedNewStatus = NotificationStatusInt.IN_VALIDATION.getValue();
        boolean expectedStatusChanged = false;

        NotificationInt notification = getNotification(iun);
        StatusService.NotificationStatusUpdate notificationStatuses = new StatusService.NotificationStatusUpdate(NotificationStatusInt.IN_VALIDATION, NotificationStatusInt.IN_VALIDATION);
        Mockito.when(statusService.getStatus(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(notificationStatuses);
        String elementId2 = "elementId2";
        Set<TimelineElementInternal> setTimelineElement = getSendPaperDetailsList(iun, elementId2);
        Flux<TimelineElementInternal> timelineElementsWithStatusInfo = Flux.fromIterable(setTimelineElement.stream().map(timelineElementInternal -> timelineElementInternal.toBuilder()
                .statusInfo(StatusInfoInternal.builder()
                        .statusChangeTimestamp(Instant.now().minusSeconds(5))
                        .actual(NotificationStatusInt.IN_VALIDATION.getValue())
                        .build())
                .build()).collect(Collectors.toSet()));
        Mockito.when(confidentialInformationService.saveTimelineConfidentialInformation(Mockito.any())).thenReturn(Mono.empty());
        Mockito.when(timelineDao.addTimelineElementIfAbsent(Mockito.any())).thenReturn(Mono.empty());
        Mockito.when(timelineDao.getTimeline(Mockito.anyString()))
                .thenReturn(timelineElementsWithStatusInfo);
        Mockito.when(confidentialInformationService.getTimelineConfidentialInformation(Mockito.anyString()))
                .thenReturn(Mono.just(Map.of("key", ConfidentialTimelineElementDtoInt.builder().timelineElementId("1").build())));
        Instant timestampLastElementInTimeline = timelineElementsWithStatusInfo.blockFirst().getStatusInfo().getStatusChangeTimestamp();

        TimelineElementInternal newElement = getAarGenerationTimelineElement(iun, elementId);

        // WHEN & THEN
        StepVerifier.create(timeLineService.addTimelineElement(newElement, notification))
                .expectNext(false)
                .verifyComplete();

        StepVerifier.create(Mono.just(timeLineService.buildStatusInfo(notificationStatuses, timestampLastElementInTimeline)))
                .assertNext(actualStatusInfo -> {
                    Assertions.assertEquals(expectedNewStatus, actualStatusInfo.getActual());
                    Assertions.assertEquals(expectedStatusChanged, actualStatusInfo.isStatusChanged());
                    Assertions.assertEquals(timestampLastElementInTimeline, actualStatusInfo.getStatusChangeTimestamp());
                })
                .verifyComplete();
    }

    @Test
    void getTimelineAndStatusHistory() {
        // GIVEN
        String iun = "iun";
        int numberOfRecipients1 = 1;
        Instant notificationCreatedAt = Instant.now();
        NotificationStatusInt currentStatus = NotificationStatusInt.DELIVERING;

        String elementId1 = "elementId1";
        Flux<TimelineElementInternal> setTimelineElement = Flux.fromIterable(getSendPaperDetailsList(iun, elementId1));
        Mockito.when(timelineDao.getTimeline(Mockito.anyString()))
                .thenReturn(setTimelineElement);

        Instant activeFromInValidation = Instant.now();

        NotificationStatusHistoryElementInt inValidationElement = NotificationStatusHistoryElementInt.builder()
                .status(NotificationStatusInt.IN_VALIDATION)
                .activeFrom(activeFromInValidation)
                .build();

        Instant activeFromAccepted = activeFromInValidation.plus(Duration.ofDays(1));

        NotificationStatusHistoryElementInt acceptedElementElement = NotificationStatusHistoryElementInt.builder()
                .status(NotificationStatusInt.ACCEPTED)
                .activeFrom(activeFromAccepted)
                .build();

        Instant activeFromDelivering = activeFromAccepted.plus(Duration.ofDays(1));

        NotificationStatusHistoryElementInt deliveringElement = NotificationStatusHistoryElementInt.builder()
                .status(NotificationStatusInt.DELIVERING)
                .activeFrom(activeFromDelivering)
                .build();

        List<NotificationStatusHistoryElementInt> notificationStatusHistoryElements = new ArrayList<>(List.of(inValidationElement, acceptedElementElement, deliveringElement));
        Mockito.when(confidentialInformationService.getTimelineConfidentialInformation(Mockito.anyString()))
                .thenReturn(Mono.just(Map.of("key", ConfidentialTimelineElementDtoInt.builder()
                        .timelineElementId("1")
                        .build())));
        Mockito.when(
                statusUtils.getStatusHistory(Mockito.anySet(), Mockito.anyInt(), Mockito.any(Instant.class))
        ).thenReturn(notificationStatusHistoryElements);

        Mockito.when(
                statusUtils.getCurrentStatus(Mockito.anyList())
        ).thenReturn(currentStatus);

        // WHEN & THEN
        StepVerifier.create(Mono.just(timeLineService.getTimelineAndStatusHistory(iun, numberOfRecipients1, notificationCreatedAt)))
                .assertNext(notificationHistoryResponse -> {
                    // Verifica che il numero di elementi restituiti sia 2
                    Assertions.assertEquals(2, notificationHistoryResponse.getNotificationStatusHistory().size());

                    NotificationStatusHistoryElement firstElement = notificationHistoryResponse.getNotificationStatusHistory().get(0);
                    Assertions.assertEquals(acceptedElementElement.getStatus(), NotificationStatusInt.valueOf(firstElement.getStatus().getValue()));
                    Assertions.assertEquals(inValidationElement.getActiveFrom(), firstElement.getActiveFrom());

                    NotificationStatusHistoryElement secondElement = notificationHistoryResponse.getNotificationStatusHistory().get(1);
                    Assertions.assertEquals(deliveringElement.getStatus(), NotificationStatusInt.valueOf(secondElement.getStatus().getValue()));
                    Assertions.assertEquals(deliveringElement.getActiveFrom(), secondElement.getActiveFrom());

                    // Verifica timeline
                    List<TimelineElementInternal> timelineElementList = setTimelineElement.toStream().toList();
                    TimelineElementInternal elementInt = timelineElementList.get(0);

                    Assertions.assertEquals(timelineElementList.size(), notificationHistoryResponse.getTimeline().size());

                    var firstElementReturned = notificationHistoryResponse.getTimeline().get(0);

                    Assertions.assertEquals(notificationHistoryResponse.getNotificationStatus(), NotificationStatus.valueOf(currentStatus.getValue()));
                    Assertions.assertEquals(elementInt.getElementId(), firstElementReturned.getElementId());

                    SendAnalogDetailsInt details = (SendAnalogDetailsInt) elementInt.getDetails();
                    Assertions.assertEquals(((BaseAnalogDetailsInt) firstElementReturned.getDetails()).getRecIndex(), details.getRecIndex());
                    Assertions.assertEquals(((BaseAnalogDetailsInt) firstElementReturned.getDetails()).getPhysicalAddress().getAddress(), details.getPhysicalAddress().getAddress());
                })
                .verifyComplete();
    }

    @Test
        void getSendPaperFeedbackTimelineElement() {
            // GIVEN
            String iun = "iun";
            String timelineId = "idTimeline";

            TimelineElementInternal daoElement = getSendDigitalTimelineElement(iun, timelineId);

            Mockito.when(timelineDao.getTimelineElement(Mockito.anyString(), Mockito.anyString(), Mockito.anyBoolean()))
                    .thenReturn(Mono.justOrEmpty(daoElement));

            ConfidentialTimelineElementDtoInt confidentialTimelineElementDtoInt = ConfidentialTimelineElementDtoInt.builder()
                    .timelineElementId(timelineId)
                    .digitalAddress("prova@prova.com")
                    .build();
            Mockito.when(confidentialInformationService.getTimelineElementConfidentialInformation(Mockito.anyString(), Mockito.anyString()))
                    .thenReturn(Mono.just(confidentialTimelineElementDtoInt));

            // WHEN
            Mono<TimelineElementInternal> retrievedElementMono = timeLineService.getTimelineElement(iun, timelineId, false);

            // THEN
            StepVerifier.create(retrievedElementMono)
                    .assertNext(retrievedElement -> {
                        Assertions.assertEquals(retrievedElement.getElementId(), daoElement.getElementId());
                        Assertions.assertEquals(retrievedElement.getDetails(), daoElement.getDetails());

                        SendDigitalDetailsInt details = (SendDigitalDetailsInt) retrievedElement.getDetails();
                        Assertions.assertEquals(details.getDigitalAddress().getAddress(), confidentialTimelineElementDtoInt.getDigitalAddress());
                    })
                    .verifyComplete();
        }

    @Test
    void getTimelineElementDetails(){
        //GIVEN
        String iun = "iun";
        String timelineId = "idTimeline";

        TimelineElementInternal daoElement = getSendDigitalTimelineElement(iun, timelineId);
        Mockito.when(timelineDao.getTimelineElement(Mockito.anyString(), Mockito.anyString(), Mockito.anyBoolean()))
                .thenReturn(Mono.justOrEmpty(daoElement));

        ConfidentialTimelineElementDtoInt confidentialTimelineElementDtoInt = ConfidentialTimelineElementDtoInt.builder()
                .timelineElementId(timelineId)
                .digitalAddress("prova@prova.com")
                .build();

        Mockito.when(confidentialInformationService.getTimelineElementConfidentialInformation(Mockito.anyString(), Mockito.anyString()))
                .thenReturn(Mono.just(confidentialTimelineElementDtoInt));

        //WHEN
        Mono<SendDigitalDetailsInt> detailsMono = timeLineService.getTimelineElementDetails(iun, timelineId, SendDigitalDetailsInt.class);

        //THEN
        StepVerifier.create(detailsMono)
                .assertNext(details -> {
                    Assertions.assertEquals(daoElement.getDetails(), details);
                    Assertions.assertEquals(confidentialTimelineElementDtoInt.getDigitalAddress(), details.getDigitalAddress().getAddress());
                })
                .verifyComplete();
    }

    @Test
    void getTimelineElementDetailsWithNullLegalDigitalAddress(){
        //GIVEN
        String iun = "iun";
        String timelineId = "idTimeline";

        TimelineElementInternal daoElement = getSendDigitalTimelineElement(iun, timelineId);
        Mockito.when(timelineDao.getTimelineElement(Mockito.anyString(), Mockito.anyString(), Mockito.anyBoolean()))
                .thenReturn(Mono.justOrEmpty(daoElement));

        ConfidentialTimelineElementDtoInt confidentialTimelineElementDtoInt = ConfidentialTimelineElementDtoInt.builder()
                .timelineElementId(timelineId)
                .digitalAddress(null) // Simulating null LegalDigitalAddressInt
                .build();
        Mockito.when(confidentialInformationService.getTimelineElementConfidentialInformation(Mockito.anyString(), Mockito.anyString()))
                .thenReturn(Mono.justOrEmpty(confidentialTimelineElementDtoInt));

        //WHEN
        Optional<SendDigitalDetailsInt> detailsOpt = timeLineService.getTimelineElementDetails(iun, timelineId, SendDigitalDetailsInt.class).blockOptional();

        //THEN
        Assertions.assertTrue(detailsOpt.isPresent());
        SendDigitalDetailsInt details = detailsOpt.get();
        Assertions.assertEquals(daoElement.getDetails(), details);
        Assertions.assertNull(details.getDigitalAddress().getAddress(), "Digital address should be null");
    }


   @Test
        void getTimelineElementDetails_SendCourtesyMessageDetailsInt() {
            // GIVEN
            String iun = "iun_12345";
            String timelineId = "idTimeline";

            TimelineElementInternal daoElement = TimelineElementInternal.builder()
                    .elementId(timelineId)
                    .iun(iun)
                    .details(SendCourtesyMessageDetailsInt.builder()
                            .digitalAddress(CourtesyDigitalAddressInt.builder()
                                    .address("test@courtesy.com")
                                    .build())
                            .build())
                    .build();

            ConfidentialTimelineElementDtoInt confidentialDto = ConfidentialTimelineElementDtoInt.builder()
                    .timelineElementId(timelineId)
                    .digitalAddress("confidential@courtesy.com")
                    .build();

            Mockito.when(timelineDao.getTimelineElement(Mockito.anyString(), Mockito.anyString(), Mockito.anyBoolean()))
                    .thenReturn(Mono.justOrEmpty(daoElement));

            Mockito.when(confidentialInformationService.getTimelineElementConfidentialInformation(Mockito.anyString(), Mockito.anyString()))
                    .thenReturn(Mono.justOrEmpty(confidentialDto));

            // WHEN & THEN
            StepVerifier.create(timeLineService.getTimelineElementDetails(iun, timelineId, SendCourtesyMessageDetailsInt.class))
                    .assertNext(details -> Assertions.assertEquals("confidential@courtesy.com", details.getDigitalAddress().getAddress()))
                    .verifyComplete();

            Mockito.verify(confidentialInformationService).getTimelineElementConfidentialInformation(iun, timelineId);
            Mockito.verifyNoMoreInteractions(confidentialInformationService);
        }

   @Test
    void getTimelineWithConfidentialInfo() {
        // GIVEN
        String iun = "iun_12345";
        String timelineId = null;
        boolean confidentialInfoRequired = true;
        boolean strongly = false;

        TimelineElementInternal timelineElement = TimelineElementInternal.builder()
                .elementId("elementId_12345")
                .details(SendAnalogDetailsInt.builder()
                        .physicalAddress(PhysicalAddressInt.builder()
                                .address("Old Address")
                                .build())
                        .build())
                .build();

        ConfidentialTimelineElementDtoInt confidentialDto = ConfidentialTimelineElementDtoInt.builder()
                .timelineElementId("elementId_12345")
                .physicalAddress(PhysicalAddressInt.builder()
                        .address("Confidential Address")
                        .build())
                .build();

        Mockito.when(timelineDao.getTimeline(iun)).thenReturn(Flux.just(timelineElement));
        Mockito.when(confidentialInformationService.getTimelineConfidentialInformation(iun))
                .thenReturn(Mono.just(Map.of("elementId_12345", confidentialDto)));

        // WHEN
        Mono<Set<TimelineElementInternal>> resultMono = timeLineService.getTimeline(iun, timelineId, confidentialInfoRequired, strongly)
                .collect(Collectors.toSet());

        // THEN
        StepVerifier.create(resultMono)
                .assertNext(result -> {
                    Assertions.assertEquals(1, result.size());
                    TimelineElementInternal enrichedElement = result.iterator().next();
                    PhysicalAddressInt enrichedAddress = ((SendAnalogDetailsInt) enrichedElement.getDetails()).getPhysicalAddress();
                    Assertions.assertEquals("Confidential Address", enrichedAddress.getAddress());
                })
                .verifyComplete();

        Mockito.verify(confidentialInformationService).getTimelineConfidentialInformation(iun);
        Mockito.verifyNoMoreInteractions(confidentialInformationService);
    }

    @Test
    void getTimelineElementDetails_SendCourtesyMessageDetailsInt_NullDigitalAddress() {
        // GIVEN
        String iun = "iun_12345";
        String timelineId = "idTimeline";

        TimelineElementInternal daoElement = TimelineElementInternal.builder()
                .elementId(timelineId)
                .iun(iun)
                .details(SendCourtesyMessageDetailsInt.builder()
                        .digitalAddress(null) // Digital address set to null
                        .build())
                .build();

        ConfidentialTimelineElementDtoInt confidentialDto = ConfidentialTimelineElementDtoInt.builder()
                .timelineElementId(timelineId)
                .digitalAddress("confidential@courtesy.com")
                .build();

        Mockito.when(timelineDao.getTimelineElement(Mockito.anyString(), Mockito.anyString(), Mockito.anyBoolean()))
                .thenReturn(Mono.justOrEmpty(daoElement));

        Mockito.when(confidentialInformationService.getTimelineElementConfidentialInformation(Mockito.anyString(), Mockito.anyString()))
                .thenReturn(Mono.justOrEmpty(confidentialDto));

        // WHEN
        Mono<SendCourtesyMessageDetailsInt> detailsMono = timeLineService.getTimelineElementDetails(iun, timelineId, SendCourtesyMessageDetailsInt.class);

        // THEN
        StepVerifier.create(detailsMono)
                .assertNext(details -> Assertions.assertEquals("confidential@courtesy.com", details.getDigitalAddress().getAddress()))
                .verifyComplete();

        Mockito.verify(confidentialInformationService).getTimelineElementConfidentialInformation(iun, timelineId);
        Mockito.verifyNoMoreInteractions(confidentialInformationService);
    }

    @Test
        void getTimelineElementDetails_PhysicalAddressRelatedTimelineElement() {
            // GIVEN
            String iun = "iun_12345";
            String timelineId = "idTimeline";

            TimelineElementInternal daoElement = TimelineElementInternal.builder()
                    .elementId(timelineId)
                    .iun(iun)
                    .details(SendAnalogDetailsInt.builder()
                            .physicalAddress(PhysicalAddressInt.builder()
                                    .municipality("Test Municipality")
                                    .province("Test Province")
                                    .build())
                            .build())
                    .build();

            ConfidentialTimelineElementDtoInt confidentialDto = ConfidentialTimelineElementDtoInt.builder()
                    .timelineElementId(timelineId)
                    .physicalAddress(PhysicalAddressInt.builder()
                            .municipality("Confidential Municipality")
                            .province("Confidential Province")
                            .build())
                    .build();

            Mockito.when(timelineDao.getTimelineElement(Mockito.anyString(), Mockito.anyString(), Mockito.anyBoolean()))
                    .thenReturn(Mono.just(daoElement));

            Mockito.when(confidentialInformationService.getTimelineElementConfidentialInformation(Mockito.anyString(), Mockito.anyString()))
                    .thenReturn(Mono.just(confidentialDto));

            // WHEN
            Mono<SendAnalogDetailsInt> resultMono = timeLineService.getTimelineElementDetails(
                    iun, timelineId, SendAnalogDetailsInt.class);

            // THEN
            StepVerifier.create(resultMono)
                    .assertNext(details -> {
                        Assertions.assertEquals("Confidential Municipality", details.getPhysicalAddress().getMunicipality());
                        Assertions.assertEquals("Confidential Province", details.getPhysicalAddress().getProvince());
                    })
                    .verifyComplete();

            Mockito.verify(confidentialInformationService).getTimelineElementConfidentialInformation(iun, timelineId);
            Mockito.verifyNoMoreInteractions(confidentialInformationService);
        }

    @Test
                        void getTimelineElementDetails_NewAddressRelatedTimelineElement() {
                            // GIVEN
                            String iun = "iun_12345";
                            String timelineId = "idTimeline";

                            TimelineElementInternal daoElement = TimelineElementInternal.builder()
                                    .elementId(timelineId)
                                    .iun(iun)
                                    .details(SendAnalogFeedbackDetailsInt.builder()
                                            .newAddress(PhysicalAddressInt.builder()
                                                    .municipality("Old Municipality")
                                                    .province("Old Province")
                                                    .build())
                                            .build())
                                    .build();

                            ConfidentialTimelineElementDtoInt confidentialDto = ConfidentialTimelineElementDtoInt.builder()
                                    .timelineElementId(timelineId)
                                    .newPhysicalAddress(PhysicalAddressInt.builder()
                                            .municipality("New Municipality")
                                            .province("New Province")
                                            .build())
                                    .build();

                            Mockito.when(timelineDao.getTimelineElement(Mockito.anyString(), Mockito.anyString(), Mockito.anyBoolean()))
                                    .thenReturn(Mono.just(daoElement));
                            Mockito.when(confidentialInformationService.getTimelineElementConfidentialInformation(Mockito.anyString(), Mockito.anyString()))
                                    .thenReturn(Mono.just(confidentialDto));

                            // WHEN
                            Mono<NewAddressRelatedTimelineElement> detailsMono = timeLineService.getTimelineElementDetails(
                                    iun, timelineId, NewAddressRelatedTimelineElement.class);

                            // THEN
                            StepVerifier.create(detailsMono)
                                    .assertNext(details -> {
                                        Assertions.assertEquals("New Municipality", details.getNewAddress().getMunicipality());
                                        Assertions.assertEquals("New Province", details.getNewAddress().getProvince());
                                    })
                                    .verifyComplete();

                            Mockito.verify(confidentialInformationService).getTimelineElementConfidentialInformation(iun, timelineId);
                            Mockito.verifyNoMoreInteractions(confidentialInformationService);
                        }

    @Test
    void getTimelineElementDetailsEmpty() {
        // GIVEN
        String iun = "iun";
        String timelineId = "idTimeline";

        // Mock per restituire un Optional vuoto
        Mockito.when(timelineDao.getTimelineElement(Mockito.anyString(), Mockito.anyString(), Mockito.anyBoolean()))
                .thenReturn(Mono.empty());

        // WHEN & THEN
        StepVerifier.create(timeLineService.getTimelineElementDetails(iun, timelineId, SendDigitalDetailsInt.class))
                .verifyComplete();
    }

    @Test
        void getTimelineElementWithoutConfidentialInformation(){
            //GIVEN
            String iun = "iun";
            String timelineId = "idTimeline";

            TimelineElementInternal daoElement = getScheduleAnalogWorkflowTimelineElement(iun, timelineId);
            Mockito.when(timelineDao.getTimelineElement(Mockito.anyString(), Mockito.anyString(), Mockito.anyBoolean()))
                    .thenReturn(Mono.just(daoElement));

            Mockito.when(confidentialInformationService.getTimelineElementConfidentialInformation(Mockito.anyString(), Mockito.anyString()))
                    .thenReturn(Mono.empty());

            //WHEN
            Mono<TimelineElementInternal> retrievedElementMono = timeLineService.getTimelineElement(iun, timelineId, false);

            //THEN
            StepVerifier.create(retrievedElementMono)
                    .assertNext(retrievedElement -> {
                        Assertions.assertEquals(retrievedElement.getElementId(), daoElement.getElementId());
                        Assertions.assertEquals(retrievedElement.getDetails(), daoElement.getDetails());
                    })
                    .verifyComplete();
        }

    @Test
    void getTimeline() {
        // GIVEN
        String iun = "iun";

        String timelineId1 = "idTimeline1";
        TimelineElementInternal scheduleAnalogNoConfInf = getScheduleAnalogWorkflowTimelineElement(iun, timelineId1);
        String timelineId2 = "idTimeline2";
        TimelineElementInternal sendDigitalConfInf = getSendDigitalTimelineElement(iun, timelineId2);
        String timelineId3 = "idTimeline3";
        TimelineElementInternal sendPaperFeedbackConfInf = getSendPaperFeedbackTimelineElement(iun, timelineId3, Instant.now());

        List<TimelineElementInternal> timelineElementList = new ArrayList<>();
        timelineElementList.add(scheduleAnalogNoConfInf);
        timelineElementList.add(sendDigitalConfInf);
        timelineElementList.add(sendPaperFeedbackConfInf);

        HashSet<TimelineElementInternal> hashSet = new HashSet<>(timelineElementList);
        Mockito.when(timelineDao.getTimeline(Mockito.anyString()))
                .thenReturn(Flux.fromIterable(hashSet));

        Map<String, ConfidentialTimelineElementDtoInt> mapConfInf = new HashMap<>();
        ConfidentialTimelineElementDtoInt confInfDigital = ConfidentialTimelineElementDtoInt.builder()
                .timelineElementId(timelineId2)
                .digitalAddress("prova@prova.com")
                .build();
        ConfidentialTimelineElementDtoInt confInfPhysical = ConfidentialTimelineElementDtoInt.builder()
                .timelineElementId(timelineId3)
                .physicalAddress(
                        PhysicalAddressInt.builder()
                                .at("at")
                                .municipality("muni")
                                .province("NA")
                                .addressDetails("details")
                                .build()
                )
                .build();
        mapConfInf.put(confInfDigital.getTimelineElementId(), confInfDigital);
        mapConfInf.put(confInfPhysical.getTimelineElementId(), confInfPhysical);

        Mockito.when(confidentialInformationService.getTimelineConfidentialInformation(Mockito.anyString()))
                .thenReturn(Mono.just(mapConfInf));

        // WHEN
        Mono<Set<TimelineElementInternal>> retrievedElementsMono = timeLineService.getTimeline(iun, null, true, false)
                .collect(Collectors.toSet());

        // THEN
        StepVerifier.create(retrievedElementsMono)
                .assertNext(retrievedElements -> {
                    Assertions.assertFalse(retrievedElements.isEmpty());

                    List<TimelineElementInternal> listElement = new ArrayList<>(retrievedElements);

                    TimelineElementInternal retrievedScheduleAnalog = getSpecificElementFromList(listElement, scheduleAnalogNoConfInf.getElementId());
                    Assertions.assertEquals(retrievedScheduleAnalog, scheduleAnalogNoConfInf);

                    TimelineElementInternal retrievedSendDigital = getSpecificElementFromList(listElement, sendDigitalConfInf.getElementId());
                    Assertions.assertNotNull(retrievedSendDigital);

                    SendDigitalDetailsInt details = (SendDigitalDetailsInt) retrievedSendDigital.getDetails();
                    Assertions.assertEquals(details, sendDigitalConfInf.getDetails());
                    Assertions.assertEquals(details.getDigitalAddress().getAddress(), confInfDigital.getDigitalAddress());

                    TimelineElementInternal retrievedSendPaperFeedback = getSpecificElementFromList(listElement, sendPaperFeedbackConfInf.getElementId());
                    Assertions.assertNotNull(retrievedSendPaperFeedback);

                    SendAnalogFeedbackDetailsInt details1 = (SendAnalogFeedbackDetailsInt) retrievedSendPaperFeedback.getDetails();
                    Assertions.assertEquals(details1, sendPaperFeedbackConfInf.getDetails());
                    Assertions.assertEquals(details1.getPhysicalAddress(), confInfPhysical.getPhysicalAddress());
                })
                .verifyComplete();
    }

    @Test
    void getTimelineFilteredByElementIdTest() {
        // GIVEN
        String iun = "iun_12345";
        String timelineId = "timelineId_12345";
        Set<TimelineElementInternal> expectedTimelineElements = Set.of(
                TimelineElementInternal.builder().elementId(timelineId).iun(iun).build()
        );

        Mockito.when(timelineDao.getTimelineFilteredByElementId(iun, timelineId))
                .thenReturn(Flux.fromIterable(expectedTimelineElements));

        // WHEN
        Mono<Set<TimelineElementInternal>> resultMono = timeLineService.getTimeline(iun, timelineId, false, false)
                .collect(Collectors.toSet());

        // THEN
        StepVerifier.create(resultMono)
                .assertNext(result -> Assertions.assertEquals(expectedTimelineElements, result))
                .verifyComplete();

        Mockito.verify(timelineDao).getTimelineFilteredByElementId(iun, timelineId);
        Mockito.verifyNoMoreInteractions(timelineDao);
    }

    @Test
    void getTimelineStronglyTest() {
        // GIVEN
        String iun = "iun_12345";
        Set<TimelineElementInternal> expectedTimelineElements = Set.of(
                TimelineElementInternal.builder().elementId("timelineId_1").iun(iun).build(),
                TimelineElementInternal.builder().elementId("timelineId_2").iun(iun).build()
        );

        Mockito.when(timelineDao.getTimelineStrongly(iun))
                .thenReturn(Flux.fromIterable(expectedTimelineElements));

        // WHEN
        Mono<Set<TimelineElementInternal>> resultMono = timeLineService.getTimeline(iun, null, false, true)
                .collect(Collectors.toSet());

        // THEN
        StepVerifier.create(resultMono)
                .assertNext(result -> Assertions.assertEquals(expectedTimelineElements, result))
                .verifyComplete();

        Mockito.verify(timelineDao).getTimelineStrongly(iun);
        Mockito.verifyNoMoreInteractions(timelineDao);
    }

    @Test
        void getSchedulingAnalogDateOKTest() {
            // GIVEN
            final String iun = "iun1";
            final int recIndex = 1;
            final Instant expectedDate = Instant.now();

            ProbableDateAnalogWorkflowDetailsInt details = ProbableDateAnalogWorkflowDetailsInt.builder()
                    .recIndex(recIndex)
                    .schedulingAnalogDate(expectedDate)
                    .build();

            TimelineElementInternal timelineElementExpected = TimelineElementInternal.builder()
                    .iun(iun)
                    .details(details)
                    .category(TimelineElementCategoryInt.PROBABLE_SCHEDULING_ANALOG_DATE) // Imposta la categoria
                    .build();

            Mockito.when(timelineDao.getTimeline(iun))
                    .thenReturn(Flux.just(timelineElementExpected));

            // WHEN
            Mono<ProbableSchedulingAnalogDateDto> resultMono = timeLineService.getSchedulingAnalogDate(iun, recIndex);

            // THEN
            StepVerifier.create(resultMono)
                    .assertNext(schedulingAnalogDateActual -> {
                        assertThat(schedulingAnalogDateActual.getSchedulingAnalogDate())
                                .isEqualTo(expectedDate);
                        assertThat(schedulingAnalogDateActual.getRecIndex())
                                .isEqualTo(recIndex);
                        assertThat(schedulingAnalogDateActual.getIun())
                                .isEqualTo(iun);
                    })
                    .verifyComplete();
        }

    @Test
    void getSchedulingAnalogDateNotFoundTest() {
        // GIVEN
        final String iun = "iun1";
        final int recIndex = 0;

        Mockito.when(timelineDao.getTimeline(iun))
                .thenReturn(Flux.empty());

        // WHEN & THEN
        Executable executable = () -> timeLineService.getSchedulingAnalogDate(iun, recIndex).block();
        Assertions.assertThrows(PnNotFoundException.class, executable);
    }

    @Test
    void retrieveAndIncrementCounterForTimelineEventTest() {
        final String timelineid = "iun1";
        TimelineCounterEntity timelineCounterEntity = new TimelineCounterEntity();
        timelineCounterEntity.setTimelineElementId(timelineid);
        timelineCounterEntity.setCounter(5L);

        Mockito.when(timelineCounterDao.getCounter(timelineid))
                .thenReturn(timelineCounterEntity);


        Long r = timeLineService.retrieveAndIncrementCounterForTimelineEvent(timelineid);
        Assertions.assertNotNull(r);
        Assertions.assertEquals(5L, r);
    }

    private TimelineElementInternal getSpecificElementFromList(List<TimelineElementInternal> listElement, String timelineId){
        for (TimelineElementInternal element : listElement){
            if(element.getElementId().equals(timelineId)){
                return element;
            }
        }
        return null;
    }

    private TimelineElementInternal getSendDigitalTimelineElement(String iun, String timelineId) {
        SendDigitalDetailsInt details = SendDigitalDetailsInt.builder()
                .digitalAddressSource(DigitalAddressSourceInt.SPECIAL)
                .digitalAddress(
                        LegalDigitalAddressInt.builder()
                                .type(LegalDigitalAddressInt.LEGAL_DIGITAL_ADDRESS_TYPE.PEC)
                                .build()
                )
                .recIndex(0)
                .build();
        return TimelineElementInternal.builder()
                .elementId(timelineId)
                .iun(iun)
                .details( details )
                .build();
    }

    private Set<TimelineElementInternal> getSendPaperDetailsList(String iun,  String elementId){
        List<TimelineElementInternal> timelineElementList = new ArrayList<>();
        TimelineElementInternal timelineElementInternal = getSendPaperDetailsTimelineElement(iun, elementId);
        timelineElementList.add(timelineElementInternal);
        return new HashSet<>(timelineElementList);
    }

    private TimelineElementInternal getSendPaperDetailsTimelineElement(String iun, String elementId) {
         SendAnalogDetailsInt details =  SendAnalogDetailsInt.builder()
                .physicalAddress(
                        PhysicalAddressInt.builder()
                                .province("province")
                                .municipality("munic")
                                .at("at")
                                .build()
                )
                .relatedRequestId("abc")
                 .analogCost(100)
                .recIndex(0)
                .sentAttemptMade(0)
                .build();
        return TimelineElementInternal.builder()
                .timestamp(Instant.now())
                .elementId(elementId)
                .iun(iun)
                .details( details )
                .category(TimelineElementCategoryInt.SEND_ANALOG_DOMICILE )
                .build();
    }

    private TimelineElementInternal getAarGenerationTimelineElement(String iun, String elementId) {
        AarGenerationDetailsInt details =  AarGenerationDetailsInt.builder()
                .recIndex(0)
                .generatedAarUrl("url")
                .numberOfPages(1)
                .build();
        return TimelineElementInternal.builder()
                .elementId(elementId)
                .category(TimelineElementCategoryInt.AAR_GENERATION)
                .iun(iun)
                .details( details )
                .timestamp(Instant.now())
                .build();
    }

    private TimelineElementInternal getAnalogSuccessTimelineCriticalElement(String iun, String elementId) {
        AarGenerationDetailsInt details =  AarGenerationDetailsInt.builder()
                .recIndex(0)
                .generatedAarUrl("url")
                .numberOfPages(1)
                .build();
        return TimelineElementInternal.builder()
                .elementId(elementId)
                .category(TimelineElementCategoryInt.ANALOG_SUCCESS_WORKFLOW)
                .iun(iun)
                .details( details )
                .build();
    }

    private TimelineElementInternal getSendPaperProgressTimelineElement(String iun, String elementId, Instant timestamp) {
        SendAnalogProgressDetailsInt details =  SendAnalogProgressDetailsInt.builder()
                .recIndex(0)
                .deliveryDetailCode("CON080")
                .notificationDate(timestamp)
                .build();
        return TimelineElementInternal.builder()
                .elementId(elementId)
                .iun(iun)
                .category(TimelineElementCategoryInt.SEND_ANALOG_PROGRESS)
                .timestamp(timestamp)
                .details( details )
                .build();
    }

    private TimelineElementInternal getSendPaperFeedbackTimelineElement(String iun, String elementId, Instant timestamp) {
         SendAnalogFeedbackDetailsInt details =  SendAnalogFeedbackDetailsInt.builder()
                 .notificationDate(timestamp)
                .newAddress(
                        PhysicalAddressInt.builder()
                                .province("province")
                                .municipality("munic")
                                .at("at")
                                .build()
                )
                .recIndex(0)
                .sentAttemptMade(0)
                .build();
        return TimelineElementInternal.builder()
                .elementId(elementId)
                .iun(iun)
                .category(TimelineElementCategoryInt.SEND_ANALOG_FEEDBACK)
                .timestamp(timestamp)
                .details( details )
                .build();
    }

    private TimelineElementInternal getValidatedF24TimelineElement(String iun, String elementId) {
        ValidatedF24DetailInt detail = ValidatedF24DetailInt.builder().build();

        return TimelineElementInternal.builder()
                .elementId(elementId)
                .iun(iun)
                .category(TimelineElementCategoryInt.VALIDATE_F24_REQUEST)
                .timestamp(Instant.now())
                .details(detail)
                .build();
    }

    private TimelineElementInternal getScheduleAnalogWorkflowTimelineElement(String iun, String timelineId) {
        ScheduleAnalogWorkflowDetailsInt details = ScheduleAnalogWorkflowDetailsInt.builder()
                .recIndex(0)
                .build();
        return TimelineElementInternal.builder()
                .elementId(timelineId)
                .iun(iun)
                .details( details )
                .build();
    }

    private NotificationInt getNotification(String iun) {
        return NotificationInt.builder()
                .iun(iun)
                .paProtocolNumber("protocol_01")
                .recipientsCount(1)
                .build();
    }

    private NotificationInt getNotificationWithMultipleRecipients(String iun) {
        return NotificationInt.builder()
                .iun(iun)
                .paProtocolNumber("protocol_01")
                .recipientsCount(5)
                .build();
    }

    @Test
    void getTimelineElementForSpecificRecipientTest() {
        // GIVEN
        String iun = "iun_12345";
        int recIndex = 0;
        TimelineElementCategoryInt category = TimelineElementCategoryInt.SEND_ANALOG_DOMICILE;

        TimelineElementInternal expectedElement = TimelineElementInternal.builder()
               .elementId("elementId_12345")
               .iun(iun)
               .category(category)
               .details(SendAnalogDetailsInt.builder()
                       .recIndex(recIndex)
                       .build())
               .build();

        Mockito.when(timelineDao.getTimeline(iun))
               .thenReturn(Flux.fromIterable(Set.of(expectedElement)));

        Mono<TimelineElementInternal> resultMono = timeLineService.getTimelineElementForSpecificRecipient(iun, recIndex, category);

        StepVerifier.create(resultMono)
                .assertNext(result -> {
                    Assertions.assertEquals(expectedElement, result);
                    Assertions.assertEquals(recIndex, ((RecipientRelatedTimelineElementDetails) result.getDetails()).getRecIndex());
                })
                .verifyComplete();
    }

    @Test
    void getTimelineElementDetailForSpecificRecipientWithConfidentialInfo() {
        // GIVEN
        String iun = "iun_12345";
        int recIndex = 0;
        TimelineElementCategoryInt category = TimelineElementCategoryInt.SEND_ANALOG_DOMICILE;

        TimelineElementInternal timelineElement = TimelineElementInternal.builder()
                .elementId("elementId_12345")
                .iun(iun)
                .category(category)
                .details(SendAnalogDetailsInt.builder()
                        .recIndex(recIndex)
                        .build())
                .build();

        ConfidentialTimelineElementDtoInt confidentialDto = ConfidentialTimelineElementDtoInt.builder()
                .timelineElementId("elementId_12345")
                .physicalAddress(PhysicalAddressInt.builder()
                        .municipality("Test Municipality")
                        .province("Test Province")
                        .build())
                .build();

        Mockito.when(timelineDao.getTimeline(iun))
                .thenReturn(Flux.fromIterable(Set.of(timelineElement)));

        Mockito.when(confidentialInformationService.getTimelineElementConfidentialInformation(iun, "elementId_12345"))
                .thenReturn(Mono.just(confidentialDto));

        // WHEN
        Mono<SendAnalogDetailsInt> result = timeLineService.getTimelineElementDetailForSpecificRecipient(
                iun, recIndex, true, category, SendAnalogDetailsInt.class);

        // THEN
        StepVerifier.create(result)
                .assertNext(details -> {
                    Assertions.assertEquals(recIndex, details.getRecIndex());
                    Assertions.assertEquals("Test Municipality", details.getPhysicalAddress().getMunicipality());
                    Assertions.assertEquals("Test Province", details.getPhysicalAddress().getProvince());
                })
                .verifyComplete();

        Mockito.verify(confidentialInformationService).getTimelineElementConfidentialInformation(iun, "elementId_12345");
        Mockito.verifyNoMoreInteractions(confidentialInformationService);
    }

    @Test
    void isNotDiagnosticTimelineElementTest() {
        // GIVEN
        TimelineElementInternal elementWithNullCategory = TimelineElementInternal.builder()
                .category(null)
                .build();

        TimelineElementInternal elementWithValidCategory = TimelineElementInternal.builder()
                .category(TimelineElementCategoryInt.SEND_ANALOG_DOMICILE)
                .build();

        // WHEN
        boolean resultForNullCategory = timeLineService.isNotDiagnosticTimelineElement(elementWithNullCategory);
        boolean resultForValidCategory = timeLineService.isNotDiagnosticTimelineElement(elementWithValidCategory);

        // THEN
        Assertions.assertTrue(resultForNullCategory, "Element with null category should return true");
        Assertions.assertTrue(resultForValidCategory, "Element with valid category should return true");
    }

}