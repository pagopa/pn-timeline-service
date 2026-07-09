package it.pagopa.pn.timelineservice.service.impl;

import it.pagopa.pn.commons.exceptions.PnIdConflictException;
import it.pagopa.pn.commons.exceptions.PnInternalException;
import it.pagopa.pn.commons.log.PnAuditLogBuilder;
import it.pagopa.pn.commons.log.PnAuditLogEventType;
import it.pagopa.pn.timelineservice.config.PnTimelineServiceConfigs;
import it.pagopa.pn.timelineservice.dto.address.PhysicalAddressInt;
import it.pagopa.pn.timelineservice.dto.notification.NotificationInfoInt;
import it.pagopa.pn.timelineservice.dto.notification.status.NotificationStatusInt;
import it.pagopa.pn.timelineservice.dto.timeline.CommunicationType;
import it.pagopa.pn.timelineservice.dto.timeline.StatusInfoInternal;
import it.pagopa.pn.timelineservice.dto.timeline.TimelineElementInternal;
import it.pagopa.pn.timelineservice.dto.timeline.details.legal.AarGenerationDetailsInt;
import it.pagopa.pn.timelineservice.dto.timeline.details.legal.SendAnalogDetailsInt;
import it.pagopa.pn.timelineservice.dto.timeline.details.legal.SendAnalogFeedbackDetailsInt;
import it.pagopa.pn.timelineservice.dto.timeline.details.TimelineElementCategoryInt;
import it.pagopa.pn.timelineservice.exceptions.PnLockReserved;
import it.pagopa.pn.timelineservice.generated.openapi.server.v1.dto.TimelineElement;
import it.pagopa.pn.timelineservice.middleware.dao.TimelineDao;
import it.pagopa.pn.timelineservice.service.*;
import it.pagopa.pn.timelineservice.operations.legal.LegalTimelineElementPersistenceStrategy;
import it.pagopa.pn.timelineservice.operations.TimelineOperations;
import it.pagopa.pn.timelineservice.operations.TimelineOperationsResolver;
import it.pagopa.pn.timelineservice.utils.CommunicationTypeChecker;
import net.javacrumbs.shedlock.core.LockProvider;
import net.javacrumbs.shedlock.core.SimpleLock;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AddTimelineElementServiceImplTest {
    private TimelineService timelineService;
    private TimelineDao timelineDao;
    private LegalTimelineElementPersistenceStrategy legalTimelineElementPersistenceStrategy;

    private StatusService statusService;
    private ConfidentialInformationService confidentialInformationService;
    private SimpleLock simpleLock;
    private LockProvider lockProvider;

    private AddTimelineElementServiceImpl addTimelineElementService;

    @BeforeEach
    void setup() {
        timelineService = Mockito.mock( TimelineServiceImpl.class );
        timelineDao = Mockito.mock( TimelineDao.class );
        statusService = Mockito.mock( StatusService.class );
        confidentialInformationService = Mockito.mock( ConfidentialInformationService.class );
        simpleLock = Mockito.mock(SimpleLock.class);
        lockProvider = Mockito.mock(LockProvider.class);
        PnTimelineServiceConfigs pnTimelineServiceConfigs = Mockito.mock(PnTimelineServiceConfigs.class);
        when(pnTimelineServiceConfigs.getTimelineLockDuration()).thenReturn(Duration.ofSeconds(5));
        when(pnTimelineServiceConfigs.getInvalidableCategories()).thenReturn(List.of("PREPARE_ANALOG_DOMICILE","PREPARE_ANALOG_DOMICILE_FAILURE","SEND_ANALOG_DOMICILE","SEND_ANALOG_PROGRESS","SEND_ANALOG_FEEDBACK","ANALOG_SUCCESS_WORKFLOW","ANALOG_FAILURE_WORKFLOW","SCHEDULE_REFINEMENT","REFINEMENT","COMPLETELY_UNREACHABLE_CREATION_REQUEST","COMPLETELY_UNREACHABLE","ANALOG_WORKFLOW_RECIPIENT_DECEASED"));


        // In questi junit testiamo l'orchestrazione, dunque mockiamo il resolver per restituire sempre la strategia legale, in modo da testare la logica di persistenza più complessa.
        legalTimelineElementPersistenceStrategy = Mockito.mock(LegalTimelineElementPersistenceStrategy.class);
        TimelineOperations legalTimelineOperations = Mockito.mock(TimelineOperations.class);
        when(legalTimelineOperations.persistenceStrategy()).thenReturn(legalTimelineElementPersistenceStrategy);
        TimelineOperationsResolver strategyResolver = Mockito.mock(TimelineOperationsResolver.class);
        when(strategyResolver.resolve(Mockito.any())).thenReturn(legalTimelineOperations);
        // Mock della strategia legale, che restituisce sempre l'elemento con lo stesso timestamp (senza applicare la logica di business timestamp) e che non richiede il percorso critico, in modo da testare la logica di persistenza standard.
        when(legalTimelineElementPersistenceStrategy.applyBusinessTimestamp(Mockito.any(), Mockito.any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(legalTimelineElementPersistenceStrategy.requiresCriticalPath(Mockito.any(), Mockito.any())).thenReturn(false);
        when(legalTimelineElementPersistenceStrategy.buildAuditLogEvent(Mockito.any(), Mockito.any())).thenReturn(new PnAuditLogBuilder().before(PnAuditLogEventType.AUD_NT_TIMELINE, "test").build());
        when(legalTimelineElementPersistenceStrategy.enrichWithRework(Mockito.any(), Mockito.any())).thenAnswer(invocation -> invocation.getArgument(0));
        CommunicationTypeChecker communicationTypeChecker = Mockito.mock(CommunicationTypeChecker.class);
        Mockito.doNothing().when(communicationTypeChecker).checkAgainstIun(Mockito.any(), Mockito.any());
        addTimelineElementService = new AddTimelineElementServiceImpl(timelineService, timelineDao, confidentialInformationService, statusService, lockProvider, pnTimelineServiceConfigs, strategyResolver, communicationTypeChecker);
    }

    @Test
    void addTimelineElement() {
        // GIVEN
        String iun = "iun_12345";
        String elementId = "SEND_ANALOG_FEEDBACK.IUN_+"+iun+".RECINDEX_0.ATTEMPT_0";

        NotificationInfoInt notification = NotificationInfoInt.builder().iun(iun).build();
        StatusService.NotificationStatusUpdate notificationStatuses = new StatusService.NotificationStatusUpdate(NotificationStatusInt.ACCEPTED, NotificationStatusInt.ACCEPTED);
        Mockito.when(statusService.getStatus(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(notificationStatuses);
        Mockito.when(confidentialInformationService.saveTimelineConfidentialInformation(Mockito.any())).thenReturn(Mono.empty());
        Mockito.when(timelineDao.addTimelineElementIfAbsent(Mockito.any())).thenReturn(Mono.empty());
        Set<TimelineElementInternal> setTimelineElement = getSendPaperDetailsList(iun, elementId);
        Mockito.when(timelineService.getTimeline(iun,null, true, false))
                .thenReturn(Flux.fromIterable(setTimelineElement));

        TimelineElementInternal newElement = TimelineElementInternal.builder()
                .category(TimelineElementCategoryInt.SEND_ANALOG_FEEDBACK)
                .elementId(elementId)
                .iun(iun)
                .timestamp(Instant.now())
                .communicationType(CommunicationType.LEGAL)
                .build();

        // WHEN
        Mono<Void> result = addTimelineElementService.addTimelineElement(newElement, notification).then();

        // THEN
        StepVerifier.create(result)
                .verifyComplete();

        ArgumentCaptor<TimelineElementInternal> captor = ArgumentCaptor.forClass(TimelineElementInternal.class);
        verify(timelineDao).addTimelineElementIfAbsent(captor.capture());
        TimelineElementInternal dtoToPersist = captor.getValue();
        Assertions.assertEquals(dtoToPersist.getTimestamp(), newElement.getTimestamp());
    }

    @Test
    void addTimelineElementSavesReworkRequestType() {
        String iun = "iun_12345";
        String elementId = "SEND_ANALOG_FEEDBACK.IUN_" + iun + ".RECINDEX_0.ATTEMPT_0";
        TimelineElement.ReworkRequestTypeEnum reworkRequestType = TimelineElement.ReworkRequestTypeEnum.REWORK;

        NotificationInfoInt notification = NotificationInfoInt.builder().iun(iun).build();
        StatusService.NotificationStatusUpdate notificationStatuses =
                new StatusService.NotificationStatusUpdate(NotificationStatusInt.ACCEPTED, NotificationStatusInt.ACCEPTED);
        Mockito.when(statusService.getStatus(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(notificationStatuses);
        Mockito.when(confidentialInformationService.saveTimelineConfidentialInformation(Mockito.any())).thenReturn(Mono.empty());
        Mockito.when(timelineDao.addTimelineElementIfAbsent(Mockito.any())).thenReturn(Mono.empty());
        Mockito.when(timelineService.getTimeline(iun, null, true, false))
                .thenReturn(Flux.fromIterable(getSendPaperDetailsList(iun, elementId)));

        TimelineElementInternal newElement = TimelineElementInternal.builder()
                .category(TimelineElementCategoryInt.SEND_ANALOG_FEEDBACK)
                .elementId(elementId)
                .iun(iun)
                .timestamp(Instant.now())
                .reworkRequestType(reworkRequestType)
                .communicationType(CommunicationType.LEGAL)
                .build();

        StepVerifier.create(addTimelineElementService.addTimelineElement(newElement, notification))
                .expectNext(elementId)
                .verifyComplete();

        ArgumentCaptor<TimelineElementInternal> captor = ArgumentCaptor.forClass(TimelineElementInternal.class);
        verify(timelineDao).addTimelineElementIfAbsent(captor.capture());
        Assertions.assertEquals(reworkRequestType, captor.getValue().getReworkRequestType());
    }

    @Test
    void addCriticalTimelineElement() {
        // GIVEN
        String iun = "iun_12345";
        String elementId = "ANALOG_SUCCESS_WORKFLOW.IUN_"+iun+".RECINDEX_0";
        String elementId2 = "elementId2";

        NotificationInfoInt notification = getNotificationWithMultipleRecipients(iun);
        StatusService.NotificationStatusUpdate notificationStatuses = new StatusService.NotificationStatusUpdate(NotificationStatusInt.ACCEPTED, NotificationStatusInt.ACCEPTED);
        when(legalTimelineElementPersistenceStrategy.requiresCriticalPath(Mockito.any(), Mockito.any())).thenReturn(true);
        Mockito.when(statusService.getStatus(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(notificationStatuses);
        Set<TimelineElementInternal> setTimelineElement = getSendPaperDetailsList(iun, elementId2);
        Mockito.when(timelineService.getTimeline(iun,null, true, false)).thenReturn(Flux.fromIterable(setTimelineElement));
        Mockito.when(confidentialInformationService.saveTimelineConfidentialInformation(Mockito.any())).thenReturn(Mono.empty());
        Mockito.when(timelineDao.addTimelineElementIfAbsent(Mockito.any())).thenReturn(Mono.empty());
        Mockito.when(lockProvider.lock(Mockito.any())).thenReturn(Optional.of(simpleLock));

        TimelineElementInternal newElement = getAnalogSuccessTimelineCriticalElement(iun, elementId);

        // WHEN
        Mono<String> result = addTimelineElementService.addTimelineElement(newElement, notification);

        // THEN
        StepVerifier.create(result)
                .expectNext("ANALOG_SUCCESS_WORKFLOW.IUN_iun_12345.RECINDEX_0")
                .verifyComplete();

        TimelineElementInternal timelineElement = setTimelineElement.iterator().next();
        Instant timestampLastElementInTimeline = timelineElement.getTimestamp();
        StatusInfoInternal expectedStatusInfo = StatusInfoInternal.builder()
                .actual(NotificationStatusInt.ACCEPTED.getValue())
                .statusChangeTimestamp(timestampLastElementInTimeline).build();

        StatusInfoInternal actualStatusInfo = addTimelineElementService.buildStatusInfo(notificationStatuses, null);
        TimelineElementInternal dtoWithStatusInfo = newElement.toBuilder().statusInfo(actualStatusInfo).build();

        ArgumentCaptor<TimelineElementInternal> confInfoCaptor = ArgumentCaptor.forClass(TimelineElementInternal.class);
        verify(confidentialInformationService).saveTimelineConfidentialInformation(confInfoCaptor.capture());
        TimelineElementInternal confInfoToPersist = confInfoCaptor.getValue();
        Assertions.assertFalse(confInfoToPersist.getElementId().contains("REWORK"));

        Assertions.assertEquals(expectedStatusInfo.getActual(), actualStatusInfo.getActual());
        Assertions.assertEquals(expectedStatusInfo.isStatusChanged(), actualStatusInfo.isStatusChanged());
        Assertions.assertNull(actualStatusInfo.getStatusChangeTimestamp());
        Mockito.verify(timelineDao).addTimelineElementIfAbsent(dtoWithStatusInfo);
        Mockito.verify(statusService).getStatus(newElement, setTimelineElement, notification, newElement.getCommunicationType());
    }

    @Test
    void addTimelineElementWithNullStatusInfo() {
        // GIVEN
        String iun = "iun_12345";
        String elementId = "elementId_12345";
        NotificationInfoInt notification = getNotification(iun);
        TimelineElementInternal newElement = getAarGenerationTimelineElement(iun, elementId);

        // Simula una timeline vuota
        Mockito.when(timelineService.getTimeline(iun,null, true, false))
                .thenReturn(Flux.empty());
        // Simula un errore nella generazione dello status
        Mockito.when(statusService.getStatus(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any()))
                .thenThrow(new PnInternalException("Error", "test"));

        // WHEN & THEN
        StepVerifier.create(addTimelineElementService.addTimelineElement(newElement, notification))
                .expectError(PnInternalException.class)
                .verify();

        Mockito.verify(statusService).getStatus(newElement, new HashSet<>(), notification, newElement.getCommunicationType());
    }

    @Test
    void addCriticalTimelineElementLockNotAcquired() {
        String iun = "iun_12345";
        String elementId = "elementId_12345";

        NotificationInfoInt notification = getNotificationWithMultipleRecipients(iun);
        StatusService.NotificationStatusUpdate notificationStatuses = new StatusService.NotificationStatusUpdate(NotificationStatusInt.ACCEPTED, NotificationStatusInt.ACCEPTED);

        when(legalTimelineElementPersistenceStrategy.requiresCriticalPath(Mockito.any(), Mockito.any())).thenReturn(true);
        Mockito.when(statusService.getStatus(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any()))
                .thenReturn(notificationStatuses);
        String elementId2 = "elementId2";
        Set<TimelineElementInternal> setTimelineElement = getSendPaperDetailsList(iun, elementId2);
        Mockito.when(timelineService.getTimeline(iun,null, true, false))
                .thenReturn(Flux.fromIterable(setTimelineElement));
        Mockito.when(lockProvider.lock(Mockito.any())).thenReturn(Optional.empty());

        TimelineElementInternal newElement = getAnalogSuccessTimelineCriticalElement(iun, elementId);

        StepVerifier.create(addTimelineElementService.addTimelineElement(newElement, notification))
                .expectError(PnLockReserved.class)
                .verify();
    }

    @Test
    void addTimelineElementIdConflict(){
        // GIVEN
        String iun = "iun_12345";
        String elementId = "SEND_ANALOG_FEEDBACK.IUN_"+iun+".RECINDEX_0.ATTEMPT_0";

        NotificationInfoInt notification = NotificationInfoInt.builder().iun(iun).build();
        StatusService.NotificationStatusUpdate notificationStatuses = new StatusService.NotificationStatusUpdate(NotificationStatusInt.ACCEPTED, NotificationStatusInt.ACCEPTED);
        Mockito.when(statusService.getStatus(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(notificationStatuses);
        Mockito.when(confidentialInformationService.saveTimelineConfidentialInformation(Mockito.any())).thenReturn(Mono.empty());
        Mockito.when(timelineDao.addTimelineElementIfAbsent(Mockito.any())).thenReturn(Mono.error(new PnIdConflictException(new HashMap<>())));
        Set<TimelineElementInternal> setTimelineElement = getSendPaperDetailsList(iun, elementId);
        Mockito.when(timelineService.getTimeline(iun,null, true, false))
                .thenReturn(Flux.fromIterable(setTimelineElement));

        TimelineElementInternal newElement = TimelineElementInternal.builder()
                .category(TimelineElementCategoryInt.SEND_ANALOG_FEEDBACK)
                .elementId(elementId)
                .iun(iun)
                .timestamp(Instant.now())
                .communicationType(CommunicationType.LEGAL)
                .build();

        // WHEN
        Mono<Void> result = addTimelineElementService.addTimelineElement(newElement, notification).then();

        // THEN
        StepVerifier.create(result)
                .verifyError(PnIdConflictException.class);

        ArgumentCaptor<TimelineElementInternal> captor = ArgumentCaptor.forClass(TimelineElementInternal.class);
        verify(timelineDao).addTimelineElementIfAbsent(captor.capture());
        TimelineElementInternal dtoToPersist = captor.getValue();
        Assertions.assertEquals(dtoToPersist.getTimestamp(), newElement.getTimestamp());
    }

    @Test
    void addCriticalTimelineElementIdConflic() {
        // GIVEN
        String iun = "iun_12345";
        String elementId = "ANALOG_SUCCESS_WORKFLOW.IUN_"+iun+".RECINDEX_0";
        NotificationInfoInt notification = getNotificationWithMultipleRecipients(iun);
        TimelineElementInternal newElement = getAnalogSuccessTimelineCriticalElement(iun, elementId);

        when(legalTimelineElementPersistenceStrategy.requiresCriticalPath(Mockito.any(), Mockito.any())).thenReturn(true);
        Mockito.when(lockProvider.lock(Mockito.any())).thenReturn(Optional.of(simpleLock));
        Mockito.when(statusService.getStatus(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any()))
                .thenReturn(new StatusService.NotificationStatusUpdate(NotificationStatusInt.ACCEPTED, NotificationStatusInt.ACCEPTED));
        Mockito.when(timelineService.getTimeline(iun,null, true, false))
                .thenReturn(Flux.empty());
        Mockito.when(confidentialInformationService.saveTimelineConfidentialInformation(Mockito.any()))
                .thenReturn(Mono.empty());
        Mockito.when(timelineDao.addTimelineElementIfAbsent(Mockito.any())).thenReturn(Mono.error(new PnIdConflictException(new HashMap<>())));

        StepVerifier.create(addTimelineElementService.addTimelineElement(newElement, notification))
                .expectError(PnIdConflictException.class)
                .verify();

        Mockito.verify(simpleLock).unlock();
    }


    @Test
    void addCriticalTimelineElementException() {
        // GIVEN
        String iun = "iun_12345";
        String elementId = "elementId_12345";
        NotificationInfoInt notification = getNotificationWithMultipleRecipients(iun);
        TimelineElementInternal newElement = getAnalogSuccessTimelineCriticalElement(iun, elementId);

        when(legalTimelineElementPersistenceStrategy.requiresCriticalPath(Mockito.any(), Mockito.any())).thenReturn(true);
        Mockito.when(lockProvider.lock(Mockito.any())).thenReturn(Optional.of(simpleLock));
        Mockito.when(statusService.getStatus(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any()))
                .thenReturn(new StatusService.NotificationStatusUpdate(NotificationStatusInt.ACCEPTED, NotificationStatusInt.ACCEPTED));
        Mockito.when(timelineService.getTimeline(iun,null, true, false))
                .thenReturn(Flux.empty());
        Mockito.when(confidentialInformationService.saveTimelineConfidentialInformation(Mockito.any()))
                .thenReturn(Mono.empty());
        Mockito.doThrow(new PnInternalException("error", "test"))
                .when(timelineDao).addTimelineElementIfAbsent(Mockito.any(TimelineElementInternal.class));

        StepVerifier.create(addTimelineElementService.addTimelineElement(newElement, notification))
                .expectError(PnInternalException.class)
                .verify();

        Mockito.verify(simpleLock).unlock();
    }

    @Test
    void addTimelineElementError() {
        // GIVEN
        String iun = "iun";
        String elementId = "elementId";

        NotificationInfoInt notification = getNotification(iun);

        String elementId2 = "elementId";
        Set<TimelineElementInternal> setTimelineElement = getSendPaperDetailsList(iun, elementId2);
        Mockito.when(timelineService.getTimeline(iun,null, true, false))
                .thenReturn(Flux.fromIterable(setTimelineElement));

        TimelineElementInternal newElement = getSendPaperFeedbackTimelineElement(iun, elementId, Instant.now());

        Mockito.doThrow(new PnInternalException("error", "test")).when(statusService)
                .getStatus(Mockito.any(TimelineElementInternal.class), Mockito.anySet(), Mockito.any(NotificationInfoInt.class), Mockito.any(CommunicationType.class));

        // WHEN
        StepVerifier.create(addTimelineElementService.addTimelineElement(newElement, notification))
                .expectError(PnInternalException.class)
                .verify();
    }

    @Test
    void addTimelineElementWithUnchangedStatus() {
        // GIVEN
        String iun = "iun";
        String elementId = "AAR_GENERATION.IUN_"+iun+".RECINDEX_0";

        String expectedNewStatus = NotificationStatusInt.IN_VALIDATION.getValue();
        boolean expectedStatusChanged = false;

        NotificationInfoInt notification = getNotification(iun);
        StatusService.NotificationStatusUpdate notificationStatuses = new StatusService.NotificationStatusUpdate(NotificationStatusInt.IN_VALIDATION, NotificationStatusInt.IN_VALIDATION);
        Mockito.when(statusService.getStatus(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(notificationStatuses);
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
        Mockito.when(timelineService.getTimeline(iun,null, true, false))
                .thenReturn(timelineElementsWithStatusInfo);
        Instant timestampLastElementInTimeline = Objects.requireNonNull(timelineElementsWithStatusInfo.blockFirst()).getStatusInfo().getStatusChangeTimestamp();

        TimelineElementInternal newElement = getAarGenerationTimelineElement(iun, elementId);

        // WHEN & THEN
        StepVerifier.create(addTimelineElementService.addTimelineElement(newElement, notification))
                .expectNext(elementId)
                .verifyComplete();

        StepVerifier.create(Mono.just(addTimelineElementService.buildStatusInfo(notificationStatuses, timestampLastElementInTimeline)))
                .assertNext(actualStatusInfo -> {
                    Assertions.assertEquals(expectedNewStatus, actualStatusInfo.getActual());
                    Assertions.assertEquals(expectedStatusChanged, actualStatusInfo.isStatusChanged());
                    Assertions.assertEquals(timestampLastElementInTimeline, actualStatusInfo.getStatusChangeTimestamp());
                })
                .verifyComplete();
    }

    @Test
    void addTimelineElementWithChangedStatus() {
        // GIVEN
        String iun = "iun";
        String elementId = "AAR_GENERATION.IUN_"+iun+".RECINDEX_0";

        String expectedNewStatus = NotificationStatusInt.ACCEPTED.getValue();
        boolean expectedStatusChanged = true;

        NotificationInfoInt notification = getNotification(iun);
        StatusService.NotificationStatusUpdate notificationStatuses = new StatusService.NotificationStatusUpdate(NotificationStatusInt.IN_VALIDATION, NotificationStatusInt.ACCEPTED);
        Mockito.when(statusService.getStatus(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(notificationStatuses);
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
        Mockito.when(timelineService.getTimeline(iun, null, true, false))
                .thenReturn(timelineElementsWithStatusInfo);

        TimelineElementInternal newElement = getAarGenerationTimelineElement(iun, elementId);

        // WHEN & THEN
        StepVerifier.create(addTimelineElementService.addTimelineElement(newElement, notification))
                .expectNext(elementId)
                .verifyComplete();

        ArgumentCaptor<TimelineElementInternal> captor = ArgumentCaptor.forClass(TimelineElementInternal.class);
        verify(timelineDao).addTimelineElementIfAbsent(captor.capture());
        TimelineElementInternal persisted = captor.getValue();

        Assertions.assertEquals(expectedNewStatus, persisted.getStatusInfo().getActual());
        Assertions.assertEquals(expectedStatusChanged, persisted.getStatusInfo().isStatusChanged());
        Assertions.assertNotNull(persisted.getStatusInfo().getStatusChangeTimestamp());
    }

    private NotificationInfoInt getNotification(String iun) {
        return NotificationInfoInt.builder()
                .iun(iun)
                .paProtocolNumber("protocol_01")
                .numberOfRecipients(1)
                .build();
    }

    private NotificationInfoInt getNotificationWithMultipleRecipients(String iun) {
        return NotificationInfoInt.builder()
                .iun(iun)
                .paProtocolNumber("protocol_01")
                .numberOfRecipients(5)
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
                .communicationType(CommunicationType.LEGAL)
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
                .communicationType(CommunicationType.LEGAL)
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
                .category(TimelineElementCategoryInt.SEND_ANALOG_DOMICILE)
                .communicationType(CommunicationType.LEGAL)
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
}