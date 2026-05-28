package it.pagopa.pn.timelineservice.service.impl;

import it.pagopa.pn.timelineservice.dto.address.CourtesyDigitalAddressInt;
import it.pagopa.pn.timelineservice.dto.address.DigitalAddressSourceInt;
import it.pagopa.pn.timelineservice.dto.address.LegalDigitalAddressInt;
import it.pagopa.pn.timelineservice.dto.address.PhysicalAddressInt;
import it.pagopa.pn.timelineservice.dto.ext.datavault.ConfidentialTimelineElementDtoInt;
import it.pagopa.pn.timelineservice.dto.ext.notification.NotificationRefusedErrorInt;
import it.pagopa.pn.timelineservice.dto.notification.status.NotificationStatusHistoryElementInt;
import it.pagopa.pn.timelineservice.dto.notification.status.NotificationStatusHistoryInvalidatedElementInt;
import it.pagopa.pn.timelineservice.dto.notification.status.NotificationStatusInt;
import it.pagopa.pn.timelineservice.dto.timeline.CommunicationType;
import it.pagopa.pn.timelineservice.dto.timeline.TimelineElementInternal;
import it.pagopa.pn.timelineservice.dto.timeline.details.*;
import it.pagopa.pn.timelineservice.exceptions.PnNotFoundException;
import it.pagopa.pn.timelineservice.generated.openapi.server.v1.dto.*;
import it.pagopa.pn.timelineservice.middleware.dao.TimelineCounterEntityDao;
import it.pagopa.pn.timelineservice.middleware.dao.TimelineDao;
import it.pagopa.pn.timelineservice.middleware.dao.dynamo.entity.TimelineCounterEntity;
import it.pagopa.pn.timelineservice.operations.CommunicationTypeClassifier;
import it.pagopa.pn.timelineservice.operations.TimelineOperations;
import it.pagopa.pn.timelineservice.operations.TimelineOperationsResolver;
import it.pagopa.pn.timelineservice.operations.common.TimelineTimestampMapper;
import it.pagopa.pn.timelineservice.service.ConfidentialInformationService;
import it.pagopa.pn.timelineservice.service.StatusHistoryService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

import static org.mockito.ArgumentMatchers.anyString;

class TimelineServiceImplTest {
    private TimelineDao timelineDao;
    private TimelineCounterEntityDao timelineCounterDao;
    private StatusHistoryService statusHistoryService;
    private ConfidentialInformationService confidentialInformationService;
    private CommunicationTypeClassifier communicationTypeClassifier;
    private TimelineOperationsResolver timelineOperationsResolver;
    private TimelineServiceImpl timeLineService;

    @BeforeEach
    void setup() {
        timelineDao = Mockito.mock( TimelineDao.class );
        timelineCounterDao = Mockito.mock( TimelineCounterEntityDao.class );
        statusHistoryService = Mockito.mock( StatusHistoryService.class );
        confidentialInformationService = Mockito.mock( ConfidentialInformationService.class );
        communicationTypeClassifier = Mockito.mock( CommunicationTypeClassifier.class );
        timelineOperationsResolver = Mockito.mock( TimelineOperationsResolver.class );
        timeLineService = new TimelineServiceImpl(
                timelineDao,
                timelineCounterDao,
                statusHistoryService,
                confidentialInformationService,
                communicationTypeClassifier,
                timelineOperationsResolver
        );
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

        Mockito.when(communicationTypeClassifier.resolveFromTimelineElements(Mockito.any()))
                .thenReturn(CommunicationType.LEGAL);

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
                statusHistoryService.getStatusHistory(Mockito.anySet(), Mockito.anyInt(), Mockito.any(Instant.class), Mockito.any(CommunicationType.class))
        ).thenReturn(notificationStatusHistoryElements);

        //Mock timelinetimestamp mapper to return the same timeline element passed as argument
        //since the mapping logic is not relevant for this test and is already tested in unit tests for TimelineTimestampMapper
        TimelineOperations timelineOperations = Mockito.mock(TimelineOperations.class);
        TimelineTimestampMapper timelineTimestampMapper = Mockito.mock(TimelineTimestampMapper.class);
        Mockito.when(
                timelineOperationsResolver.resolve(Mockito.any(CommunicationType.class))
        ).thenReturn(timelineOperations);
        Mockito.when(
                timelineOperations.timelineTimestampMapper()
        ).thenReturn(timelineTimestampMapper);
        Mockito.when(
                timelineTimestampMapper.mapTimelineTimestamps(Mockito.any())
        ).thenAnswer(invocation -> ((TimelineTimestampMapper.TimestampMapperPayload) invocation.getArgument(0)).timelineElementInternal());


        // WHEN & THEN
        StepVerifier.create(timeLineService.getTimelineAndStatusHistory(iun, numberOfRecipients1, notificationCreatedAt))
                .assertNext(notificationHistoryResponse -> {
                    // Verifica che il numero di elementi restituiti sia 2
                    Assertions.assertEquals(2, notificationHistoryResponse.getNotificationStatusHistory().size());

                    NotificationStatusHistoryElementInt firstElement = notificationHistoryResponse.getNotificationStatusHistory().getFirst();
                    Assertions.assertEquals(acceptedElementElement.getStatus(), NotificationStatusInt.valueOf(firstElement.getStatus().getValue()));
                    Assertions.assertEquals(inValidationElement.getActiveFrom(), firstElement.getActiveFrom());

                    NotificationStatusHistoryElementInt secondElement = notificationHistoryResponse.getNotificationStatusHistory().getLast();
                    Assertions.assertEquals(deliveringElement.getStatus(), NotificationStatusInt.valueOf(secondElement.getStatus().getValue()));
                    Assertions.assertEquals(deliveringElement.getActiveFrom(), secondElement.getActiveFrom());

                    // Verifica timeline
                    List<TimelineElementInternal> timelineElementList = setTimelineElement.toStream().toList();
                    TimelineElementInternal elementInt = timelineElementList.getFirst();

                    Assertions.assertEquals(timelineElementList.size(), notificationHistoryResponse.getTimeline().size());

                    var firstElementReturned = notificationHistoryResponse.getTimeline().getFirst();

                    Assertions.assertEquals(notificationHistoryResponse.getNotificationStatus().name(), NotificationStatus.valueOf(currentStatus.getValue()).name());
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
        Mono<TimelineElementDetailsInt> detailsMono = timeLineService.getTimelineElementDetails(iun, timelineId);

        //THEN
        StepVerifier.create(detailsMono)
                .assertNext(details -> {
                    var sendDigitalDetailsInt = (SendDigitalDetailsInt) details;
                    Assertions.assertEquals(daoElement.getDetails(), details);
                    Assertions.assertEquals(confidentialTimelineElementDtoInt.getDigitalAddress(), sendDigitalDetailsInt.getDigitalAddress().getAddress());
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
        Optional<TimelineElementDetailsInt> detailsOpt = timeLineService.getTimelineElementDetails(iun, timelineId).blockOptional();

        var sendDigitalDetailsInt = (SendDigitalDetailsInt) detailsOpt.get();
        //THEN
        Assertions.assertEquals(daoElement.getDetails(), sendDigitalDetailsInt);
        Assertions.assertNull(sendDigitalDetailsInt.getDigitalAddress().getAddress(), "Digital address should be null");
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
        StepVerifier.create(timeLineService.getTimelineElementDetails(iun, timelineId))
                .assertNext(details -> {
                    var sendCourtesyDetails = (SendCourtesyMessageDetailsInt) details;
                    Assertions.assertEquals("confidential@courtesy.com", sendCourtesyDetails.getDigitalAddress().getAddress());
                })
                .verifyComplete();

        Mockito.verify(confidentialInformationService).getTimelineElementConfidentialInformation(iun, timelineId);
        Mockito.verifyNoMoreInteractions(confidentialInformationService);
    }

    @Test
    void getTimelineWithConfidentialInfo() {
        // GIVEN
        String iun = "iun_12345";
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
        Mono<Set<TimelineElementInternal>> resultMono = timeLineService.getTimeline(iun, null, confidentialInfoRequired, strongly)
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
    void getReworkTimelineWithConfidentialInfo() {
        // GIVEN
        String iun = "iun_12345";
        boolean confidentialInfoRequired = true;
        boolean strongly = false;

        TimelineElementInternal timelineElement = TimelineElementInternal.builder()
                .elementId("elementId_12345")
                .category(TimelineElementCategoryInt.NOTIFICATION_TIMELINE_REWORKED)
                .details(NotificationTimelineReworkedDetailsInt.builder()
                        .invalidatedTimelineAndStatusHistory(
                                List.of(NotificationStatusHistoryInvalidatedElementInt.builder()
                                                .relatedTimelineElements(List.of(TimelineElementInternal.builder()
                                                        .elementId("elementId_12346")
                                                        .details(SendAnalogDetailsInt.builder()
                                                                .physicalAddress(PhysicalAddressInt.builder()
                                                                        .address("Old Address")
                                                                        .build())
                                                                .build())
                                                        .build()))
                                        .build())
                        )
                        .build())
                .build();

        ConfidentialTimelineElementDtoInt confidentialDto = ConfidentialTimelineElementDtoInt.builder()
                .timelineElementId("elementId_12346")
                .physicalAddress(PhysicalAddressInt.builder()
                        .address("Confidential Address")
                        .build())
                .build();

        Mockito.when(timelineDao.getTimeline(iun)).thenReturn(Flux.just(timelineElement));
        Mockito.when(confidentialInformationService.getTimelineConfidentialInformation(iun))
                .thenReturn(Mono.just(Map.of("elementId_12346", confidentialDto)));

        // WHEN
        Mono<Set<TimelineElementInternal>> resultMono = timeLineService.getTimeline(iun, null, confidentialInfoRequired, strongly)
                .collect(Collectors.toSet());

        // THEN
        StepVerifier.create(resultMono)
                .assertNext(result -> {
                    Assertions.assertEquals(1, result.size());
                    TimelineElementInternal enrichedElement = result.iterator().next();
                    PhysicalAddressInt enrichedAddress = ((SendAnalogDetailsInt) ((NotificationTimelineReworkedDetailsInt) enrichedElement.getDetails()).getInvalidatedTimelineAndStatusHistory().getFirst().getRelatedTimelineElements().getFirst().getDetails()).getPhysicalAddress();
                    Assertions.assertEquals("Confidential Address", enrichedAddress.getAddress());
                })
                .verifyComplete();

        Mockito.verify(confidentialInformationService).getTimelineConfidentialInformation(iun);
        Mockito.verifyNoMoreInteractions(confidentialInformationService);
    }

    @Test
    void getTimelineReworkElementDetails_SendCourtesyMessageDetailsInt_NullDigitalAddress() {
        // GIVEN
        String iun = "iun_12345";
        String timelineId = "idTimeline";

        TimelineElementInternal daoElement = TimelineElementInternal.builder()
                .elementId("elementId_12345")
                .category(TimelineElementCategoryInt.NOTIFICATION_TIMELINE_REWORKED)
                .details(NotificationTimelineReworkedDetailsInt.builder()
                        .invalidatedTimelineAndStatusHistory(
                                List.of(NotificationStatusHistoryInvalidatedElementInt.builder()
                                        .relatedTimelineElements(List.of(TimelineElementInternal.builder()
                                                .elementId("elementId_12346")
                                                .details(SendCourtesyMessageDetailsInt.builder()
                                                        .digitalAddress(null) // Digital address set to null
                                                        .build())
                                                .build()))
                                        .build())
                        )
                        .build())
                .build();


        ConfidentialTimelineElementDtoInt confidentialDto = ConfidentialTimelineElementDtoInt.builder()
                .timelineElementId(timelineId)
                .digitalAddress("confidential@courtesy.com")
                .build();

        Mockito.when(timelineDao.getTimelineElement(Mockito.anyString(), Mockito.anyString(), Mockito.anyBoolean()))
                .thenReturn(Mono.justOrEmpty(daoElement));

        Mockito.when(confidentialInformationService.getTimelineConfidentialInformation(iun))
                .thenReturn(Mono.just(Map.of("elementId_12346", confidentialDto)));

        // WHEN
        Mono<TimelineElementDetailsInt> detailsMono = timeLineService.getTimelineElementDetails(iun, timelineId);

        // THEN
        StepVerifier.create(detailsMono)
                .assertNext(details -> {
                    var sendCourtesyDetails = ((SendCourtesyMessageDetailsInt) ((NotificationTimelineReworkedDetailsInt) details).getInvalidatedTimelineAndStatusHistory().getFirst().getRelatedTimelineElements().getFirst().getDetails());
                    Assertions.assertEquals("confidential@courtesy.com", sendCourtesyDetails.getDigitalAddress().getAddress());
                })
                .verifyComplete();

        Mockito.verify(confidentialInformationService).getTimelineConfidentialInformation(iun);
        Mockito.verifyNoMoreInteractions(confidentialInformationService);
    }

    @Test
    void getReworkTimelineElementDetailForSpecificRecipientWithConfidentialInfo() {
        // GIVEN
        String iun = "iun_12345";
        int recIndex = 0;

        TimelineElementInternal timelineElement = TimelineElementInternal.builder()
                .elementId("elementId_12345")
                .category(TimelineElementCategoryInt.NOTIFICATION_TIMELINE_REWORKED)
                .details(NotificationTimelineReworkedDetailsInt.builder()
                        .invalidatedTimelineAndStatusHistory(
                                List.of(NotificationStatusHistoryInvalidatedElementInt.builder()
                                        .relatedTimelineElements(List.of(TimelineElementInternal.builder()
                                                .elementId("elementId_12346")
                                                .details(SendAnalogDetailsInt.builder()
                                                        .recIndex(recIndex)
                                                        .build())
                                                .build()))
                                        .build())
                        )
                        .build())
                .build();

        ConfidentialTimelineElementDtoInt confidentialDto = ConfidentialTimelineElementDtoInt.builder()
                .timelineElementId("elementId_12346")
                .physicalAddress(PhysicalAddressInt.builder()
                        .municipality("Test Municipality")
                        .province("Test Province")
                        .build())
                .build();

        Mockito.when(timelineDao.getTimeline(iun))
                .thenReturn(Flux.fromIterable(Set.of(timelineElement)));

        Mockito.when(confidentialInformationService.getTimelineConfidentialInformation(iun))
                .thenReturn(Mono.just(Map.of("elementId_12346", confidentialDto)));

        // WHEN
        Mono<TimelineElementDetailsInt> result = timeLineService.getTimelineElementDetailForSpecificRecipient(
                iun, recIndex, true, TimelineElementCategoryInt.NOTIFICATION_TIMELINE_REWORKED);

        // THEN
        StepVerifier.create(result)
                .assertNext(details -> {
                    var sendAnalogDetailsInt = ((SendAnalogDetailsInt) ((NotificationTimelineReworkedDetailsInt) details).getInvalidatedTimelineAndStatusHistory().getFirst().getRelatedTimelineElements().getFirst().getDetails());
                    Assertions.assertEquals(recIndex, sendAnalogDetailsInt.getRecIndex());
                    Assertions.assertEquals("Test Municipality", sendAnalogDetailsInt.getPhysicalAddress().getMunicipality());
                    Assertions.assertEquals("Test Province", sendAnalogDetailsInt.getPhysicalAddress().getProvince());
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
        Mono<TimelineElementDetailsInt> detailsMono = timeLineService.getTimelineElementDetails(iun, timelineId);

        // THEN
        StepVerifier.create(detailsMono)
                .assertNext(details -> {
                    var sendCourtesyDetails = (SendCourtesyMessageDetailsInt) details;
                    Assertions.assertEquals("confidential@courtesy.com", sendCourtesyDetails.getDigitalAddress().getAddress());
                })
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
            Mono<TimelineElementDetailsInt> resultMono = timeLineService.getTimelineElementDetails(
                    iun, timelineId);

            // THEN
            StepVerifier.create(resultMono)
                    .assertNext(details -> {
                        var sendAnalogDetails = (SendAnalogDetailsInt) details;
                        Assertions.assertEquals("Confidential Municipality", sendAnalogDetails.getPhysicalAddress().getMunicipality());
                        Assertions.assertEquals("Confidential Province", sendAnalogDetails.getPhysicalAddress().getProvince());
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
                            Mono<TimelineElementDetailsInt> detailsMono = timeLineService.getTimelineElementDetails(
                                    iun, timelineId);

                            // THEN
                            StepVerifier.create(detailsMono)
                                    .assertNext(details -> {
                                        var sendAnalogFeedbackDetails = (NewAddressRelatedTimelineElement) details;
                                        Assertions.assertEquals("New Municipality", sendAnalogFeedbackDetails.getNewAddress().getMunicipality());
                                        Assertions.assertEquals("New Province", sendAnalogFeedbackDetails.getNewAddress().getProvince());
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
        StepVerifier.create(timeLineService.getTimelineElementDetails(iun, timelineId))
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
                    .thenReturn(Mono.just(new ConfidentialTimelineElementDtoInt()));

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
    void getTimelineElementWithoutConfidentialInformationNull(){
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
        TimelineElementInternal sendPaperFeedbackConfInf = getSendPaperFeedbackTimelineElement(iun, timelineId3, Instant.now(), false);

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

        Mockito.when(timelineDao.getTimelineFilteredByElementId(iun, timelineId, false))
                .thenReturn(Flux.fromIterable(expectedTimelineElements));

        // WHEN
        Mono<Set<TimelineElementInternal>> resultMono = timeLineService.getTimeline(iun, timelineId, false, false)
                .collect(Collectors.toSet());

        // THEN
        StepVerifier.create(resultMono)
                .assertNext(result -> Assertions.assertEquals(expectedTimelineElements, result))
                .verifyComplete();

        Mockito.verify(timelineDao).getTimelineFilteredByElementId(iun, timelineId, false);
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
    void retrieveAndIncrementCounterForTimelineEventTest() {
        final String timelineid = "iun1";
        TimelineCounterEntity timelineCounterEntity = new TimelineCounterEntity();
        timelineCounterEntity.setTimelineElementId(timelineid);
        timelineCounterEntity.setCounter(5L);

        Mockito.when(timelineCounterDao.getCounter(timelineid))
                .thenReturn(Mono.just(timelineCounterEntity));


        Long r = timeLineService.retrieveAndIncrementCounterForTimelineEvent(timelineid).block();
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

    private TimelineElementInternal getSendPaperFeedbackTimelineElement(String iun, String elementId, Instant timestamp, boolean withReworkId) {
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
                .reworkId(withReworkId ? "REWORK_0" : null)
                .details( details )
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
        Mono<TimelineElementDetailsInt> result = timeLineService.getTimelineElementDetailForSpecificRecipient(
                iun, recIndex, true, category);

        // THEN
        StepVerifier.create(result)
                .assertNext(details -> {
                    var sendAnalogDetailsInt = (SendAnalogDetailsInt) details;
                    Assertions.assertEquals(recIndex, sendAnalogDetailsInt.getRecIndex());
                    Assertions.assertEquals("Test Municipality", sendAnalogDetailsInt.getPhysicalAddress().getMunicipality());
                    Assertions.assertEquals("Test Province", sendAnalogDetailsInt.getPhysicalAddress().getProvince());
                })
                .verifyComplete();

        Mockito.verify(confidentialInformationService).getTimelineElementConfidentialInformation(iun, "elementId_12345");
        Mockito.verifyNoMoreInteractions(confidentialInformationService);
    }

    @Test
    void getDeliveryInformationReturnsMappedResponse() {
        String iun = "testIun";
        Integer recIndex = 0;
        String elementId = "elementId123";

        DeliveryInformationResponse expectedResponse = new DeliveryInformationResponse();
        expectedResponse.setDeliveryMode(ExtendedDeliveryMode.UNKNOWN);
        expectedResponse.setSchedulingAnalogDate(null);
        expectedResponse.setRefinementOrViewedDate(null);
        expectedResponse.setIsNotificationCancelled(false);
        expectedResponse.isNotificationAccepted(false);

        Set<TimelineElementInternal> timelineElements = getSendPaperDetailsList(iun, elementId);

        Mockito.when(timeLineService.getTimeline(iun, null, false, false))
                .thenReturn(Flux.fromIterable(timelineElements));

        Mono<DeliveryInformationResponse> resultMono = timeLineService.getDeliveryInformation(iun, recIndex);

        StepVerifier.create(resultMono)
                .assertNext(result -> Assertions.assertEquals(expectedResponse, result))
                .verifyComplete();
    }

    @Test
    void getDeliveryInformationThrowsNotFoundWhenTimelineIsEmpty() {
        String iun = "testIun";
        Integer recIndex = 0;

        Mockito.when(timeLineService.getTimeline(iun, null, false, false))
                .thenReturn(Flux.empty());

        Mono<DeliveryInformationResponse> result = timeLineService.getDeliveryInformation(iun, recIndex);

        StepVerifier.create(result)
                .expectErrorMatches(throwable -> throwable instanceof PnNotFoundException &&
                        throwable.getMessage().contains("IUN not found"))
                .verify();
    }

    @Test
    void getRequestRefused_returnsExpectedResponse() {
        String iun = "testIun";
        String errorCode = "ERR_CODE";
        String detail = "Some reason";
        int recIndex = 0;

        int notificationCost = 100;
        int numberOfRecipients = 1;
        NotificationRefusedErrorInt errorInt = NotificationRefusedErrorInt.builder()
                .errorCode(errorCode)
                .detail(detail)
                .recIndex(recIndex)
                .build();
        TimelineElementInternal timelineElement = TimelineElementInternal.builder()
                .elementId("elementId123")
                .iun(iun)
                .category(TimelineElementCategoryInt.REQUEST_REFUSED)
                .details(RequestRefusedDetailsInt.builder()
                        .refusalReasons(List.of(errorInt))
                        .notificationCost(notificationCost)
                        .numberOfRecipients(numberOfRecipients)
                        .build())
                .build();

        Mockito.when(timelineDao.getTimelineFilteredByElementId(anyString(), anyString(), Mockito.anyBoolean()))
                .thenReturn(Flux.just(timelineElement));

        Mono<RequestRefusedResponse> result = timeLineService.getRequestRefused(iun);

        StepVerifier.create(result)
                .assertNext(resp -> {
                    Assertions.assertEquals(1, resp.getRefusalReasons().size());
                    NotificationRefusedError reason = resp.getRefusalReasons().getFirst();
                    Assertions.assertEquals(errorCode, reason.getErrorCode());
                    Assertions.assertEquals(detail, reason.getDetail());
                    Assertions.assertEquals(recIndex, reason.getRecIndex());
                    Assertions.assertEquals(notificationCost, resp.getNotificationCost());
                    Assertions.assertEquals(numberOfRecipients, resp.getNumberOfRecipients());
                })
                .verifyComplete();

        Mockito.verify(timelineDao).getTimelineFilteredByElementId(iun, "REQUEST_REFUSED", false);
    }

    @Test
    void getRequestRefused_throwsNotFound() {
        String iun = "testIun";
        Mockito.when(timelineDao.getTimelineFilteredByElementId(anyString(), anyString(), Mockito.anyBoolean()))
                .thenReturn(Flux.empty());

        Mono<RequestRefusedResponse> result = timeLineService.getRequestRefused(iun);

        StepVerifier.create(result)
                .expectError(PnNotFoundException.class)
                .verify();
    }
    
    @Test
    void getCancellationRequest_returnsExpectedResponse() {
        String iun = "testIun";
        TimelineElementInternal elementInternal = new TimelineElementInternal();
        elementInternal.setCategory(TimelineElementCategoryInt.NOTIFICATION_CANCELLATION_REQUEST);
        Instant timestamp = Instant.now();
        elementInternal.setTimestamp(timestamp);

        Mockito.when(timelineDao.getTimelineFilteredByElementId(anyString(), anyString(), Mockito.anyBoolean()))
                .thenReturn(Flux.just(elementInternal));

        Mono<CancellationRequestResponse> result = timeLineService.getCancellationRequest(iun);

        StepVerifier.create(result)
                .expectNextMatches(resp -> timestamp.equals(resp.getTimestamp()))
                .verifyComplete();

        Mockito.verify(timelineDao).getTimelineFilteredByElementId(iun, "NOTIFICATION_CANCELLATION_REQUEST", false);
    }

    @Test
    void getCancellationRequest_returnsNotFoundResponse() {
        String iun = "testIun";

        Mockito.when(timelineDao.getTimelineFilteredByElementId(anyString(), anyString(), Mockito.anyBoolean()))
                .thenReturn(Flux.empty());

        Mono<CancellationRequestResponse> result = timeLineService.getCancellationRequest(iun);

        StepVerifier.create(result)
                .expectError(PnNotFoundException.class)
                .verify();

        Mockito.verify(timelineDao).getTimelineFilteredByElementId(iun, "NOTIFICATION_CANCELLATION_REQUEST", false);
    }
  
    @Test 
    void getAarForRecipientReturnsAarResponse() {
        String iun = "testIun";
        int recIndex = 0;
        String url = "http://aar-url";
        Integer numberOfPages = 5;

        AarGenerationDetailsInt details = AarGenerationDetailsInt.builder()
                .generatedAarUrl(url)
                .numberOfPages(numberOfPages)
                .build();

        TimelineElementInternal timelineElement = TimelineElementInternal.builder()
                .category(TimelineElementCategoryInt.AAR_GENERATION)
                .details(details)
                .build();

        Mockito.when(timelineDao.getTimeline(iun))
                .thenReturn(Flux.just(timelineElement));

        Mono<AarResponse> result = timeLineService.getAarForRecipient(iun, recIndex);

        StepVerifier.create(result)
                .assertNext(aarResponse -> {
                    Assertions.assertEquals(url, aarResponse.getUrl());
                    Assertions.assertEquals(numberOfPages, aarResponse.getNumberOfPages());
                })
                .verifyComplete();
    }

    @Test
    void getAarForRecipientReturnsNotFoundWhenElementMissing() {
        String iun = "testIun";
        int recIndex = 0;

        Mockito.when(timelineDao.getTimeline(iun))
                .thenReturn(Flux.empty());

        Mono<AarResponse> result = timeLineService.getAarForRecipient(iun, recIndex);

        StepVerifier.create(result)
                .expectErrorMatches(throwable -> throwable instanceof PnNotFoundException &&
                        throwable.getMessage().contains("AAR not found"))
                .verify();
    }

}
