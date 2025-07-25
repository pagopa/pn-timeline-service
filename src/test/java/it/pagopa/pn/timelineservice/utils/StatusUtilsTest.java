package it.pagopa.pn.timelineservice.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.pagopa.pn.timelineservice.config.PnTimelineServiceConfigs;
import it.pagopa.pn.timelineservice.dto.address.LegalDigitalAddressInt;
import it.pagopa.pn.timelineservice.dto.notification.status.NotificationStatusHistoryElementInt;
import it.pagopa.pn.timelineservice.dto.notification.status.NotificationStatusInt;
import it.pagopa.pn.timelineservice.dto.timeline.TimelineElementInternal;
import it.pagopa.pn.timelineservice.dto.timeline.details.*;
import it.pagopa.pn.timelineservice.service.TimelineService;
import it.pagopa.pn.timelineservice.service.mapper.SmartMapper;
import it.pagopa.pn.timelineservice.service.mapper.TimelineMapperFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.test.annotation.DirtiesContext;

import java.time.Duration;
import java.time.Instant;
import java.util.*;

import static org.mockito.Mockito.mock;

@DirtiesContext
class StatusUtilsTest {
    @Mock
    private TimelineService timelineService;

    private StatusUtils statusUtils;

    private static final String SERCQ_ADDRESS = "x-pagopa-pn-sercq:send-self:notification-already-delivered";
    private static final String PEC_ADDRESS = "test@pec.it";


    @BeforeEach
    void setup() {
        PnTimelineServiceConfigs pnDeliveryPushConfigs = mock(PnTimelineServiceConfigs.class);
        FeatureEnabledUtils featureEnabledUtils = mock(FeatureEnabledUtils.class);
        ObjectMapper objectMapper = new ObjectMapper();
        this.statusUtils = new StatusUtils(new SmartMapper(new TimelineMapperFactory(pnDeliveryPushConfigs), objectMapper, featureEnabledUtils));
    }

    @Test
    void getTimelineHistoryTest() {

        SendDigitalDetailsInt sendDigtalDetailsIntPec = getSendDigitalDetails(LegalDigitalAddressInt.LEGAL_DIGITAL_ADDRESS_TYPE.PEC);

        NotificationViewedDetailsInt detailsInt = NotificationViewedDetailsInt.builder()
                .recIndex(0)
                .eventTimestamp(Instant.parse("2021-09-16T17:00:00.00Z"))
                .build();

        // GIVEN a timeline
        TimelineElementInternal timelineElement1 = TimelineElementInternal.builder()
                .elementId("el1")
                .timestamp(Instant.parse("2021-09-16T15:24:00.00Z"))
                .category(TimelineElementCategoryInt.REQUEST_ACCEPTED)
                .build();
        TimelineElementInternal timelineElement3 = TimelineElementInternal.builder()
                .elementId("el3")
                .timestamp((Instant.parse("2021-09-16T15:26:00.00Z")))
                .category(TimelineElementCategoryInt.SEND_DIGITAL_DOMICILE)
                .details(sendDigtalDetailsIntPec)
                .build();
        TimelineElementInternal timelineElement4 = TimelineElementInternal.builder()
                .elementId("el4")
                .timestamp((Instant.parse("2021-09-16T15:27:00.00Z")))
                .category(TimelineElementCategoryInt.SEND_DIGITAL_FEEDBACK)
                .build();
        TimelineElementInternal timelineElement5 = TimelineElementInternal.builder()
                .elementId("el5")
                .timestamp((Instant.parse("2021-09-16T15:28:00.00Z")))
                .category(TimelineElementCategoryInt.DIGITAL_DELIVERY_CREATION_REQUEST)
                .build();
        TimelineElementInternal timelineElement6 = TimelineElementInternal.builder()
                .elementId("el6")
                .timestamp((Instant.parse("2021-09-16T17:00:00.00Z")))
                .category(TimelineElementCategoryInt.NOTIFICATION_VIEWED)
                .details(detailsInt)
                .build();


        Set<TimelineElementInternal> timelineElementList = Set.of(timelineElement1, timelineElement3,
                timelineElement4, timelineElement5, timelineElement6);


        // WHEN ask for status history
        Instant notificationCreatedAt = Instant.parse("2021-09-16T15:20:00.00Z");

        List<NotificationStatusHistoryElementInt> actualStatusHistory = statusUtils.getStatusHistory(
                timelineElementList, 1,
                notificationCreatedAt
        );

        printStatus(actualStatusHistory);

        // THEN status histories have same length
        Assertions.assertEquals(5, actualStatusHistory.size(), "Check length");

        //  ... 1st initial status
        Assertions.assertEquals(NotificationStatusHistoryElementInt.builder()
                        .status(NotificationStatusInt.IN_VALIDATION)
                        .activeFrom(notificationCreatedAt)
                        .relatedTimelineElements(List.of())
                        .build(),
                actualStatusHistory.get(0),
                "1st status wrong"
        );

        //  ... 2nd initial status
        Assertions.assertEquals(NotificationStatusHistoryElementInt.builder()
                        .status(NotificationStatusInt.ACCEPTED)
                        .activeFrom(timelineElement1.getTimestamp())
                        .relatedTimelineElements(List.of("el1"))
                        .build(),
                actualStatusHistory.get(1),
                "2nd status wrong"
        );

        //  ... 3rd initial status
        Assertions.assertEquals(NotificationStatusHistoryElementInt.builder()
                        .status(NotificationStatusInt.DELIVERING)
                        .activeFrom(timelineElement3.getTimestamp())
                        .relatedTimelineElements(Arrays.asList("el3", "el4"))
                        .build(),
                actualStatusHistory.get(2),
                "3rd status wrong"
        );

        //  ... 4th initial status
        Assertions.assertEquals(NotificationStatusHistoryElementInt.builder()
                        .status(NotificationStatusInt.DELIVERED)
                        .activeFrom(timelineElement5.getTimestamp())
                        .relatedTimelineElements(List.of("el5"))
                        .build(),
                actualStatusHistory.get(3),
                "4th status wrong"
        );

        //  ... 5th initial status
        Assertions.assertEquals(NotificationStatusHistoryElementInt.builder()
                        .status(NotificationStatusInt.VIEWED)
                        .activeFrom(timelineElement6.getTimestamp())
                        .relatedTimelineElements(List.of("el6"))
                        .build(),
                actualStatusHistory.get(4),
                "2nd status wrong"
        );

    }

    @Test
    void checkStatusNotDeliveredWithDigitalFailure() {

        SendDigitalDetailsInt sendDigitalDetailsIntPec = getSendDigitalDetails(LegalDigitalAddressInt.LEGAL_DIGITAL_ADDRESS_TYPE.PEC);


        // GIVEN a timeline
        TimelineElementInternal requestAccepted = TimelineElementInternal.builder()
                .elementId("el1")
                .timestamp(Instant.parse("2021-09-16T15:24:00.00Z"))
                .category(TimelineElementCategoryInt.REQUEST_ACCEPTED)
                .build();
        TimelineElementInternal sendDigitalDomicile = TimelineElementInternal.builder()
                .elementId("el3")
                .timestamp((Instant.parse("2021-09-16T15:26:00.00Z")))
                .category(TimelineElementCategoryInt.SEND_DIGITAL_DOMICILE)
                .details(sendDigitalDetailsIntPec)
                .build();
        TimelineElementInternal sendDigitalFeedback = TimelineElementInternal.builder()
                .elementId("el4")
                .timestamp((Instant.parse("2021-09-16T15:27:00.00Z")))
                .category(TimelineElementCategoryInt.SEND_DIGITAL_FEEDBACK)
                .build();
        TimelineElementInternal digitalDeliveryCreationRequest = TimelineElementInternal.builder()
                .elementId("el5")
                .timestamp((Instant.parse("2021-09-16T15:28:00.00Z")))
                .category(TimelineElementCategoryInt.DIGITAL_DELIVERY_CREATION_REQUEST)
                .build();


        Set<TimelineElementInternal> timelineElementList = Set.of(requestAccepted, sendDigitalDomicile,
                sendDigitalFeedback, digitalDeliveryCreationRequest);


        // WHEN ask for status history
        Instant notificationCreatedAt = requestAccepted.getTimestamp().minus(Duration.ofHours(1));

        List<NotificationStatusHistoryElementInt> actualStatusHistory = statusUtils.getStatusHistory(
                timelineElementList,
                1,
                notificationCreatedAt
        );

        printStatus(actualStatusHistory);

        // THEN status histories have same length
        Assertions.assertEquals(4, actualStatusHistory.size(), "Check length");

        //  ... 1st initial status
        Assertions.assertEquals(NotificationStatusHistoryElementInt.builder()
                        .status(NotificationStatusInt.IN_VALIDATION)
                        .activeFrom(notificationCreatedAt)
                        .relatedTimelineElements(List.of())
                        .build(),
                actualStatusHistory.get(0),
                "1st status wrong"
        );

        //  ... 2nd initial status
        Assertions.assertEquals(NotificationStatusHistoryElementInt.builder()
                        .status(NotificationStatusInt.ACCEPTED)
                        .activeFrom(requestAccepted.getTimestamp())
                        .relatedTimelineElements(List.of(requestAccepted.getElementId()))
                        .build(),
                actualStatusHistory.get(1),
                "2nd status wrong"
        );

        //  ... 3rd initial status
        Assertions.assertEquals(NotificationStatusHistoryElementInt.builder()
                        .status(NotificationStatusInt.DELIVERING)
                        .activeFrom(sendDigitalDomicile.getTimestamp())
                        .relatedTimelineElements(Arrays.asList(
                                sendDigitalDomicile.getElementId(),
                                sendDigitalFeedback.getElementId()
                        ))
                        .build(),
                actualStatusHistory.get(2),
                "3rd status wrong"
        );

        //  ... 4rd initial status
        Assertions.assertEquals(NotificationStatusHistoryElementInt.builder()
                        .status(NotificationStatusInt.DELIVERED)
                        .activeFrom(digitalDeliveryCreationRequest.getTimestamp())
                        .relatedTimelineElements(List.of(
                                digitalDeliveryCreationRequest.getElementId())
                        )
                        .build(),
                actualStatusHistory.get(3),
                "4rd status wrong"
        );
    }

    @Test
    void checkStatusDeliveredWithRegisteredLetter() {

        SendDigitalDetailsInt sendDigitalDetailsIntPec = getSendDigitalDetails(LegalDigitalAddressInt.LEGAL_DIGITAL_ADDRESS_TYPE.PEC);

        // GIVEN a timeline
        TimelineElementInternal requestAccepted = TimelineElementInternal.builder()
                .elementId("el1")
                .timestamp(Instant.parse("2021-09-16T15:24:00.00Z"))
                .category(TimelineElementCategoryInt.REQUEST_ACCEPTED)
                .build();
        TimelineElementInternal sendDigitalDomicile = TimelineElementInternal.builder()
                .elementId("el3")
                .timestamp((Instant.parse("2021-09-16T15:26:00.00Z")))
                .category(TimelineElementCategoryInt.SEND_DIGITAL_DOMICILE)
                .details(sendDigitalDetailsIntPec)
                .build();
        TimelineElementInternal sendDigitalFeedback = TimelineElementInternal.builder()
                .elementId("el4")
                .timestamp((Instant.parse("2021-09-16T15:27:00.00Z")))
                .category(TimelineElementCategoryInt.SEND_DIGITAL_FEEDBACK)
                .build();
        TimelineElementInternal digitalDeliveryCreationRequest = TimelineElementInternal.builder()
                .elementId("el5")
                .timestamp((Instant.parse("2021-09-16T15:28:00.00Z")))
                .category(TimelineElementCategoryInt.DIGITAL_DELIVERY_CREATION_REQUEST)
                .build();
        TimelineElementInternal digitalFailureWorkflow = TimelineElementInternal.builder()
                .elementId("el6")
                .timestamp((Instant.parse("2021-09-16T15:28:30.00Z")))
                .category(TimelineElementCategoryInt.DIGITAL_FAILURE_WORKFLOW)
                .build();
        TimelineElementInternal sendSimpleRegisteredLetter = TimelineElementInternal.builder()
                .elementId("el7")
                .timestamp((Instant.parse("2021-09-16T15:29:00.00Z")))
                .category(TimelineElementCategoryInt.PREPARE_SIMPLE_REGISTERED_LETTER)
                .build();

        Set<TimelineElementInternal> timelineElementList = Set.of(requestAccepted, sendDigitalDomicile,
                sendDigitalFeedback, digitalDeliveryCreationRequest, digitalFailureWorkflow, sendSimpleRegisteredLetter);


        // WHEN ask for status history
        Instant notificationCreatedAt = requestAccepted.getTimestamp().minus(Duration.ofHours(1));

        List<NotificationStatusHistoryElementInt> actualStatusHistory = statusUtils.getStatusHistory(
                timelineElementList,
                1,
                notificationCreatedAt
        );

        printStatus(actualStatusHistory);

        // THEN status histories have same length
        Assertions.assertEquals(4, actualStatusHistory.size(), "Check length");

        //  ... 1st initial status
        Assertions.assertEquals(NotificationStatusHistoryElementInt.builder()
                        .status(NotificationStatusInt.IN_VALIDATION)
                        .activeFrom(notificationCreatedAt)
                        .relatedTimelineElements(List.of())
                        .build(),
                actualStatusHistory.get(0),
                "1st status wrong"
        );

        //  ... 2nd initial status
        Assertions.assertEquals(NotificationStatusHistoryElementInt.builder()
                        .status(NotificationStatusInt.ACCEPTED)
                        .activeFrom(requestAccepted.getTimestamp())
                        .relatedTimelineElements(List.of(requestAccepted.getElementId()))
                        .build(),
                actualStatusHistory.get(1),
                "2nd status wrong"
        );

        //  ... 3rd initial status
        Assertions.assertEquals(NotificationStatusHistoryElementInt.builder()
                        .status(NotificationStatusInt.DELIVERING)
                        .activeFrom(sendDigitalDomicile.getTimestamp())
                        .relatedTimelineElements(Arrays.asList(
                                        sendDigitalDomicile.getElementId(),
                                        sendDigitalFeedback.getElementId()
                                )
                        )
                        .build(),
                actualStatusHistory.get(2),
                "3rd status wrong"
        );

        //  ... 4rd initial status
        Assertions.assertEquals(NotificationStatusHistoryElementInt.builder()
                        .status(NotificationStatusInt.DELIVERED)
                        .activeFrom(digitalDeliveryCreationRequest.getTimestamp())
                        .relatedTimelineElements(List.of(
                                digitalDeliveryCreationRequest.getElementId(),
                                digitalFailureWorkflow.getElementId(),
                                sendSimpleRegisteredLetter.getElementId()
                        ))
                        .build(),
                actualStatusHistory.get(3),
                "4rd status wrong"
        );
    }

    // IN_VALIDATION - ACCEPTED - DELIVERING
    // un destinatario è in fase di SEND_DIGITAL_DOMICILE
    @Test
    void getTimelineHistoryMultiRecipientWithOneSendDigitalDomicileTest() {
        final int NUMBER_OF_RECIPIENTS = 3;
        // GIVEN a timeline

        SendDigitalDetailsInt sendDigitalDetailsIntPec = getSendDigitalDetails(LegalDigitalAddressInt.LEGAL_DIGITAL_ADDRESS_TYPE.PEC);

        TimelineElementInternal requestAcceptedTimelineElement = TimelineElementInternal.builder()
                .elementId("requestAcceptedTimelineElement")
                .timestamp(Instant.parse("2021-09-16T15:24:00.00Z"))
                .category(TimelineElementCategoryInt.REQUEST_ACCEPTED)
                .build();
        TimelineElementInternal sendPecFirstRecipientTimelineElement = TimelineElementInternal.builder()
                .elementId("sendPecFirstRecipientTimelineElement")
                .timestamp((Instant.parse("2021-09-16T15:26:00.00Z")))
                .category(TimelineElementCategoryInt.SEND_DIGITAL_DOMICILE)
                .details(sendDigitalDetailsIntPec)
                .build();

        Set<TimelineElementInternal> timelineElementList = Set.of(requestAcceptedTimelineElement, sendPecFirstRecipientTimelineElement);

        // WHEN ask for status history
        Instant notificationCreatedAt = Instant.parse("2021-09-16T15:20:00.00Z");

        List<NotificationStatusHistoryElementInt> actualStatusHistory = statusUtils.getStatusHistory(
                timelineElementList,
                NUMBER_OF_RECIPIENTS,
                notificationCreatedAt
        );

        printStatus(actualStatusHistory);

        // THEN status histories have 5 elements
        Assertions.assertEquals(3, actualStatusHistory.size(), "Check length");

        //  ... 1st initial status
        Assertions.assertEquals(NotificationStatusHistoryElementInt.builder()
                        .status(NotificationStatusInt.IN_VALIDATION)
                        .activeFrom(notificationCreatedAt)
                        .relatedTimelineElements(List.of())
                        .build(),
                actualStatusHistory.get(0),
                "1st status wrong"
        );

        //  ... 2nd initial status
        Assertions.assertEquals(NotificationStatusHistoryElementInt.builder()
                        .status(NotificationStatusInt.ACCEPTED)
                        .activeFrom(requestAcceptedTimelineElement.getTimestamp())
                        .relatedTimelineElements(List.of("requestAcceptedTimelineElement"))
                        .build(),
                actualStatusHistory.get(1),
                "2nd status wrong"
        );

        //  ... 3rd initial status
        Assertions.assertEquals(NotificationStatusHistoryElementInt.builder()
                        .status(NotificationStatusInt.DELIVERING)
                        .activeFrom(sendPecFirstRecipientTimelineElement.getTimestamp())
                        .relatedTimelineElements(List.of("sendPecFirstRecipientTimelineElement"))
                        .build(),
                actualStatusHistory.get(2),
                "3rd status wrong"
        );
    }

    // IN_VALIDATION - ACCEPTED - DELIVERING - VIEWED
    // tutti e 3 destinatari sono raggiungibili via domicilio digitale ma un destinatario visualizza la notifica sul portale di PN
    // Stato finale: VIEWED
    @Test
    void getTimelineHistoryMultiRecipientWithOneViewViaPNTest() {
        final int NUMBER_OF_RECIPIENTS = 3;

        // GIVEN a timeline
        TimelineElementInternal requestAcceptedTimelineElement = TimelineElementInternal.builder()
                .elementId("requestAcceptedTimelineElement")
                .timestamp(Instant.parse("2021-09-16T15:24:00.00Z"))
                .category(TimelineElementCategoryInt.REQUEST_ACCEPTED)
                .build();

        SendDigitalDetailsInt sendDigitalDetailsIntPec = getSendDigitalDetails(LegalDigitalAddressInt.LEGAL_DIGITAL_ADDRESS_TYPE.PEC);
        SendDigitalDetailsInt sendDigitalDetailsIntSercq = getSendDigitalDetails(LegalDigitalAddressInt.LEGAL_DIGITAL_ADDRESS_TYPE.SERCQ);

        TimelineElementInternal sendPecFirstRecipientTimelineElement = TimelineElementInternal.builder()
                .elementId("sendPecFirstRecipientTimelineElement")
                .timestamp((Instant.parse("2021-09-16T15:26:00.00Z")))
                .category(TimelineElementCategoryInt.SEND_DIGITAL_DOMICILE)
                .details(sendDigitalDetailsIntPec)
                .build();
        TimelineElementInternal sendPecSecondRecipientTimelineElement = TimelineElementInternal.builder()
                .elementId("sendPecSecondRecipientTimelineElement")
                .timestamp((Instant.parse("2021-09-16T15:26:30.00Z")))
                .category(TimelineElementCategoryInt.AAR_GENERATION)
                .details(sendDigitalDetailsIntSercq)
                .build();
        TimelineElementInternal sendPecThirdRecipientTimelineElement = TimelineElementInternal.builder()
                .elementId("sendPecThirdRecipientTimelineElement")
                .timestamp((Instant.parse("2021-09-16T15:26:40.00Z")))
                .category(TimelineElementCategoryInt.SEND_DIGITAL_DOMICILE)
                .details(sendDigitalDetailsIntPec)
                .build();
        TimelineElementInternal feedbackOKFirstRecipientTimelineElement = TimelineElementInternal.builder()
                .elementId("feedbackOKFirstRecipientTimelineElement")
                .timestamp((Instant.parse("2021-09-16T15:27:00.00Z")))
                .category(TimelineElementCategoryInt.SEND_DIGITAL_FEEDBACK)
                .build();
        TimelineElementInternal feedbackOKSecondRecipientTimelineElement = TimelineElementInternal.builder()
                .elementId("feedbackOKSecondRecipientTimelineElement")
                .timestamp((Instant.parse("2021-09-16T15:27:10.00Z")))
                .category(TimelineElementCategoryInt.SEND_DIGITAL_FEEDBACK)
                .build();
        TimelineElementInternal feedbackOKThirdRecipientTimelineElement = TimelineElementInternal.builder()
                .elementId("feedbackOKThirdRecipientTimelineElement")
                .timestamp((Instant.parse("2021-09-16T15:27:30.00Z")))
                .category(TimelineElementCategoryInt.SEND_DIGITAL_FEEDBACK)
                .build();

        //uno dei 3 destinatari visualizza la notifica sul portale di PN
        TimelineElementInternal viewedFromPNTimelineElement = TimelineElementInternal.builder()
                .elementId("viewedFromPNTimelineElement")
                .timestamp((Instant.parse("2021-09-16T15:30:00.00Z")))
                .category(TimelineElementCategoryInt.NOTIFICATION_VIEWED)
                .details(NotificationViewedDetailsInt.builder()
                        .recIndex(0)
                        .eventTimestamp(Instant.parse("2021-09-16T15:30:00.00Z"))
                        .build())
                .build();
        //tutti e 3 destinatari ricevono con successo la notifica via PEC
        TimelineElementInternal pecReceivedFirstRecipientTimelineElement = TimelineElementInternal.builder()
                .elementId("pecReceivedFirstRecipientTimelineElement")
                .timestamp((Instant.parse("2021-09-16T15:32:00.00Z")))
                .category(TimelineElementCategoryInt.DIGITAL_SUCCESS_WORKFLOW)
                .build();
        TimelineElementInternal pecReceivedSecondRecipientTimelineElement = TimelineElementInternal.builder()
                .elementId("pecReceivedSecondRecipientTimelineElement")
                .timestamp((Instant.parse("2021-09-16T15:33:00.00Z")))
                .category(TimelineElementCategoryInt.DIGITAL_SUCCESS_WORKFLOW)
                .build();
        TimelineElementInternal pecReceivedThirdRecipientTimelineElement = TimelineElementInternal.builder()
                .elementId("pecReceivedThirdRecipientTimelineElement")
                .timestamp((Instant.parse("2021-09-16T15:34:00.00Z")))
                .category(TimelineElementCategoryInt.DIGITAL_SUCCESS_WORKFLOW)
                .build();
        //uno dei 3 destinatari visualizza la notifica dalla PEC
        TimelineElementInternal viewedFromPecTimelineElement = TimelineElementInternal.builder()
                .elementId("viewedFromPecTimelineElement")
                .timestamp((Instant.parse("2021-09-16T16:00:00.00Z")))
                .category(TimelineElementCategoryInt.NOTIFICATION_VIEWED)
                .details(NotificationViewedDetailsInt.builder().recIndex(0).eventTimestamp(Instant.parse("2021-09-16T16:00:00.00Z")).build())
                .build();

        Set<TimelineElementInternal> timelineElementList = Set.of(requestAcceptedTimelineElement, sendPecFirstRecipientTimelineElement, sendPecSecondRecipientTimelineElement, sendPecThirdRecipientTimelineElement,
                feedbackOKFirstRecipientTimelineElement, feedbackOKSecondRecipientTimelineElement, feedbackOKThirdRecipientTimelineElement, viewedFromPNTimelineElement, pecReceivedFirstRecipientTimelineElement, pecReceivedSecondRecipientTimelineElement,
                pecReceivedThirdRecipientTimelineElement, viewedFromPecTimelineElement);


        // WHEN ask for status history
        Instant notificationCreatedAt = Instant.parse("2021-09-16T15:20:00.00Z");

        List<NotificationStatusHistoryElementInt> actualStatusHistory = statusUtils.getStatusHistory(
                timelineElementList,
                NUMBER_OF_RECIPIENTS,
                notificationCreatedAt
        );

        printStatus(actualStatusHistory);

        // THEN status histories have 4 elements
        Assertions.assertEquals(4, actualStatusHistory.size(), "Check length");

        //  ... 1st initial status
        Assertions.assertEquals(NotificationStatusHistoryElementInt.builder()
                        .status(NotificationStatusInt.IN_VALIDATION)
                        .activeFrom(notificationCreatedAt)
                        .relatedTimelineElements(List.of())
                        .build(),
                actualStatusHistory.get(0),
                "1st status wrong"
        );

        //  ... 2nd initial status
        Assertions.assertEquals(NotificationStatusHistoryElementInt.builder()
                        .status(NotificationStatusInt.ACCEPTED)
                        .activeFrom(requestAcceptedTimelineElement.getTimestamp())
                        .relatedTimelineElements(List.of("requestAcceptedTimelineElement"))
                        .build(),
                actualStatusHistory.get(1),
                "2nd status wrong"
        );

        //  ... 3rd initial status
        Assertions.assertEquals(NotificationStatusHistoryElementInt.builder()
                        .status(NotificationStatusInt.DELIVERING)
                        .activeFrom(sendPecFirstRecipientTimelineElement.getTimestamp())
                        .relatedTimelineElements(List.of("sendPecFirstRecipientTimelineElement",
                                "sendPecSecondRecipientTimelineElement", "sendPecThirdRecipientTimelineElement",
                                "feedbackOKFirstRecipientTimelineElement", "feedbackOKSecondRecipientTimelineElement",
                                "feedbackOKThirdRecipientTimelineElement"))
                        .build(),
                actualStatusHistory.get(2),
                "3rd status wrong"
        );

        //  ... 4th initial status
        Assertions.assertEquals(NotificationStatusHistoryElementInt.builder()
                        .status(NotificationStatusInt.VIEWED)
                        .activeFrom(viewedFromPNTimelineElement.getTimestamp())
                        .relatedTimelineElements(List.of("viewedFromPNTimelineElement", "pecReceivedFirstRecipientTimelineElement", "pecReceivedSecondRecipientTimelineElement", "pecReceivedThirdRecipientTimelineElement", "viewedFromPecTimelineElement"))
                        .build(),
                actualStatusHistory.get(3),
                "4th status wrong"
        );

    }

    // IN_VALIDATION - ACCEPTED - DELIVERING - VIEWED - PAID
    // tutti e 3 destinatari sono raggiungibili via domicilio digitale ma un destinatario visualizza la notifica sul portale di PN
    // prima che la visualizzi su PEC (ma dopo che ExtChannels dà feedback positivo per tutti e 3).
    // Successivamente, tutti e 3 ricevono la PEC e gli altri 2 la visualizzano. Stato finale: VIEWED
    @Test
    void getTimelineHistoryMultiRecipientWithOnePayedBeforeAllWorkflowsCompletedTest() {
        final int NUMBER_OF_RECIPIENTS = 3;

        SendDigitalDetailsInt sendDigitalDetailsIntPec = getSendDigitalDetails(LegalDigitalAddressInt.LEGAL_DIGITAL_ADDRESS_TYPE.PEC);
        SendDigitalDetailsInt sendDigitalDetailsIntSercq = getSendDigitalDetails(LegalDigitalAddressInt.LEGAL_DIGITAL_ADDRESS_TYPE.SERCQ);


        // GIVEN a timeline
        TimelineElementInternal requestAcceptedTimelineElement = TimelineElementInternal.builder()
                .elementId("requestAcceptedTimelineElement")
                .timestamp(Instant.parse("2021-09-16T15:24:00.00Z"))
                .category(TimelineElementCategoryInt.REQUEST_ACCEPTED)
                .build();
        TimelineElementInternal sendPecFirstRecipientTimelineElement = TimelineElementInternal.builder()
                .elementId("sendPecFirstRecipientTimelineElement")
                .timestamp((Instant.parse("2021-09-16T15:26:00.00Z")))
                .category(TimelineElementCategoryInt.SEND_DIGITAL_DOMICILE)
                .details(sendDigitalDetailsIntPec)
                .build();
        TimelineElementInternal sendPecSecondRecipientTimelineElement = TimelineElementInternal.builder()
                .elementId("sendPecSecondRecipientTimelineElement")
                .timestamp((Instant.parse("2021-09-16T15:26:30.00Z")))
                .category(TimelineElementCategoryInt.AAR_GENERATION)
                .details(sendDigitalDetailsIntSercq)
                .build();
        TimelineElementInternal sendPecThirdRecipientTimelineElement = TimelineElementInternal.builder()
                .elementId("sendPecThirdRecipientTimelineElement")
                .timestamp((Instant.parse("2021-09-16T15:26:40.00Z")))
                .category(TimelineElementCategoryInt.SEND_DIGITAL_DOMICILE)
                .details(sendDigitalDetailsIntPec)
                .build();
        TimelineElementInternal feedbackOKFirstRecipientTimelineElement = TimelineElementInternal.builder()
                .elementId("feedbackOKFirstRecipientTimelineElement")
                .timestamp((Instant.parse("2021-09-16T15:27:00.00Z")))
                .category(TimelineElementCategoryInt.SEND_DIGITAL_FEEDBACK)
                .build();
        TimelineElementInternal feedbackOKSecondRecipientTimelineElement = TimelineElementInternal.builder()
                .elementId("feedbackOKSecondRecipientTimelineElement")
                .timestamp((Instant.parse("2021-09-16T15:27:10.00Z")))
                .category(TimelineElementCategoryInt.SEND_DIGITAL_FEEDBACK)
                .build();
        TimelineElementInternal feedbackOKThirdRecipientTimelineElement = TimelineElementInternal.builder()
                .elementId("feedbackOKThirdRecipientTimelineElement")
                .timestamp((Instant.parse("2021-09-16T15:27:30.00Z")))
                .category(TimelineElementCategoryInt.SEND_DIGITAL_FEEDBACK)
                .build();

        //uno dei 3 destinatari visualizza la notifica sul portale di PN
        TimelineElementInternal viewedFromPNTimelineElement = TimelineElementInternal.builder()
                .elementId("viewedFromPNTimelineElement")
                .timestamp((Instant.parse("2021-09-16T15:30:00.00Z")))
                .category(TimelineElementCategoryInt.NOTIFICATION_VIEWED)
                .details(NotificationViewedDetailsInt.builder().recIndex(0).eventTimestamp(Instant.parse("2021-09-16T15:30:00.00Z")).build())
                .build();

        //tutti e 3 destinatari ricevono con successo la notifica via PEC
        TimelineElementInternal pecReceivedFirstRecipientTimelineElement = TimelineElementInternal.builder()
                .elementId("pecReceivedFirstRecipientTimelineElement")
                .timestamp((Instant.parse("2021-09-16T18:32:00.00Z")))
                .category(TimelineElementCategoryInt.DIGITAL_SUCCESS_WORKFLOW)
                .build();
        TimelineElementInternal pecReceivedSecondRecipientTimelineElement = TimelineElementInternal.builder()
                .elementId("pecReceivedSecondRecipientTimelineElement")
                .timestamp((Instant.parse("2021-09-16T18:33:00.00Z")))
                .category(TimelineElementCategoryInt.DIGITAL_SUCCESS_WORKFLOW)
                .build();
        TimelineElementInternal pecReceivedThirdRecipientTimelineElement = TimelineElementInternal.builder()
                .elementId("pecReceivedThirdRecipientTimelineElement")
                .timestamp((Instant.parse("2021-09-16T18:34:00.00Z")))
                .category(TimelineElementCategoryInt.DIGITAL_SUCCESS_WORKFLOW)
                .build();
        TimelineElementInternal viewedSecondRecipientTimelineElement = TimelineElementInternal.builder()
                .elementId("viewedSecondRecipientTimelineElement")
                .timestamp((Instant.parse("2021-09-16T18:35:00.00Z")))
                .category(TimelineElementCategoryInt.NOTIFICATION_VIEWED)
                .details(NotificationViewedDetailsInt.builder().recIndex(1).eventTimestamp(Instant.parse("2021-09-16T18:35:00.00Z")).build())
                .build();
        TimelineElementInternal viewedThirdRecipientTimelineElement = TimelineElementInternal.builder()
                .elementId("viewedThirdRecipientTimelineElement")
                .timestamp((Instant.parse("2021-09-16T18:36:00.00Z")))
                .category(TimelineElementCategoryInt.NOTIFICATION_VIEWED)
                .details(NotificationViewedDetailsInt.builder().recIndex(2).eventTimestamp(Instant.parse("2021-09-16T18:36:00.00Z")).build())
                .build();

        Set<TimelineElementInternal> timelineElementList = Set.of(requestAcceptedTimelineElement, sendPecFirstRecipientTimelineElement,
                sendPecSecondRecipientTimelineElement, sendPecThirdRecipientTimelineElement, feedbackOKFirstRecipientTimelineElement,
                feedbackOKSecondRecipientTimelineElement, feedbackOKThirdRecipientTimelineElement, viewedFromPNTimelineElement,
                pecReceivedFirstRecipientTimelineElement, pecReceivedSecondRecipientTimelineElement,
                pecReceivedThirdRecipientTimelineElement, viewedSecondRecipientTimelineElement, viewedThirdRecipientTimelineElement);


        // WHEN ask for status history
        Instant notificationCreatedAt = Instant.parse("2021-09-16T15:20:00.00Z");

        List<NotificationStatusHistoryElementInt> actualStatusHistory = statusUtils.getStatusHistory(
                timelineElementList,
                NUMBER_OF_RECIPIENTS,
                notificationCreatedAt
        );

        printStatus(actualStatusHistory);

        // THEN status histories have 5 elements
        Assertions.assertEquals(4, actualStatusHistory.size(), "Check length");

        //  ... 1st initial status
        Assertions.assertEquals(NotificationStatusHistoryElementInt.builder()
                        .status(NotificationStatusInt.IN_VALIDATION)
                        .activeFrom(notificationCreatedAt)
                        .relatedTimelineElements(List.of())
                        .build(),
                actualStatusHistory.get(0),
                "1st status wrong"
        );

        //  ... 2nd initial status
        Assertions.assertEquals(NotificationStatusHistoryElementInt.builder()
                        .status(NotificationStatusInt.ACCEPTED)
                        .activeFrom(requestAcceptedTimelineElement.getTimestamp())
                        .relatedTimelineElements(List.of("requestAcceptedTimelineElement"))
                        .build(),
                actualStatusHistory.get(1),
                "2nd status wrong"
        );

        //  ... 3rd initial status
        Assertions.assertEquals(NotificationStatusHistoryElementInt.builder()
                        .status(NotificationStatusInt.DELIVERING)
                        .activeFrom(sendPecFirstRecipientTimelineElement.getTimestamp())
                        .relatedTimelineElements(List.of("sendPecFirstRecipientTimelineElement",
                                "sendPecSecondRecipientTimelineElement", "sendPecThirdRecipientTimelineElement",
                                "feedbackOKFirstRecipientTimelineElement", "feedbackOKSecondRecipientTimelineElement",
                                "feedbackOKThirdRecipientTimelineElement"))
                        .build(),
                actualStatusHistory.get(2),
                "3rd status wrong"
        );

        //  ... 4th initial status
        Assertions.assertEquals(NotificationStatusHistoryElementInt.builder()
                        .status(NotificationStatusInt.VIEWED)
                        .activeFrom(viewedFromPNTimelineElement.getTimestamp())
                        .relatedTimelineElements(List.of("viewedFromPNTimelineElement", "pecReceivedFirstRecipientTimelineElement",
                                "pecReceivedSecondRecipientTimelineElement", "pecReceivedThirdRecipientTimelineElement",
                                "viewedSecondRecipientTimelineElement", "viewedThirdRecipientTimelineElement"))
                        .build(),
                actualStatusHistory.get(3),
                "4th status wrong"
        );

    }


    // IN_VALIDATION - ACCEPTED - DELIVERING - VIEWED
    // NOTA: lo stato non passa da DELIVERING a DELIVERED quando viene completato il workflow per il primo destinatario
    // poiché gli altri 2 destinatari non hanno completato il workflow.

    // tutti e 3 destinatari sono raggiungibili via domicilio digitale, un destinatario visualizza la notifica dalla PEC
    // prima che PN riceva un feedback positivo da External Channels per gli altri 2 destinatari. Stato finale: VIEWED
    @Test
    void getTimelineHistoryMultiRecipientWithOneViewViaPecWithOneCompleteWorkflowTest() {
        final int NUMBER_OF_RECIPIENTS = 3;

        SendDigitalDetailsInt sendDigitalDetailsIntPec = getSendDigitalDetails(LegalDigitalAddressInt.LEGAL_DIGITAL_ADDRESS_TYPE.PEC);
        SendDigitalDetailsInt sendDigitalDetailsIntSercq = getSendDigitalDetails(LegalDigitalAddressInt.LEGAL_DIGITAL_ADDRESS_TYPE.SERCQ);

        NotificationViewedDetailsInt detailsInt = NotificationViewedDetailsInt.builder()
                .recIndex(0)
                .eventTimestamp(Instant.parse("2021-09-16T15:35:00.00Z"))
                .build();

        // GIVEN a timeline
        TimelineElementInternal requestAcceptedTimelineElement = TimelineElementInternal.builder()
                .elementId("requestAcceptedTimelineElement")
                .timestamp(Instant.parse("2021-09-16T15:24:00.00Z"))
                .category(TimelineElementCategoryInt.REQUEST_ACCEPTED)
                .build();
        TimelineElementInternal sendPecFirstRecipientTimelineElement = TimelineElementInternal.builder()
                .elementId("sendPecFirstRecipientTimelineElement")
                .timestamp((Instant.parse("2021-09-16T15:26:00.00Z")))
                .category(TimelineElementCategoryInt.SEND_DIGITAL_DOMICILE)
                .details(sendDigitalDetailsIntPec)
                .build();
        TimelineElementInternal sendPecSecondRecipientTimelineElement = TimelineElementInternal.builder()
                .elementId("sendPecSecondRecipientTimelineElement")
                .timestamp((Instant.parse("2021-09-16T15:26:30.00Z")))
                .category(TimelineElementCategoryInt.AAR_GENERATION)
                .details(sendDigitalDetailsIntSercq)
                .build();
        TimelineElementInternal sendPecThirdRecipientTimelineElement = TimelineElementInternal.builder()
                .elementId("sendPecThirdRecipientTimelineElement")
                .timestamp((Instant.parse("2021-09-16T15:26:40.00Z")))
                .category(TimelineElementCategoryInt.SEND_DIGITAL_DOMICILE)
                .details(sendDigitalDetailsIntPec)
                .build();
        //PN riceve feedback positivo da External Channels per uno dei destinatari
        TimelineElementInternal feedbackOKFirstRecipientTimelineElement = TimelineElementInternal.builder()
                .elementId("feedbackOKFirstRecipientTimelineElement")
                .timestamp((Instant.parse("2021-09-16T15:30:00.00Z")))
                .category(TimelineElementCategoryInt.SEND_DIGITAL_FEEDBACK)
                .build();
        TimelineElementInternal pecReceivedFirstRecipientTimelineElement = TimelineElementInternal.builder()
                .elementId("pecReceivedFirstRecipientTimelineElement")
                .timestamp((Instant.parse("2021-09-16T15:32:00.00Z")))
                .category(TimelineElementCategoryInt.DIGITAL_SUCCESS_WORKFLOW)
                .build();
        //Questo stesso destinatario visualizza la notifica via PEC
        TimelineElementInternal viewedFromPNTimelineElement = TimelineElementInternal.builder()
                .elementId("viewedFromPNTimelineElement")
                .timestamp((Instant.parse("2021-09-16T15:35:00.00Z")))
                .category(TimelineElementCategoryInt.NOTIFICATION_VIEWED)
                .details(detailsInt)
                .build();
        //PN riceve feedback positivo da External Channels per gli altri 2 destinatari
        TimelineElementInternal feedbackOKSecondRecipientTimelineElement = TimelineElementInternal.builder()
                .elementId("feedbackOKSecondRecipientTimelineElement")
                .timestamp((Instant.parse("2021-09-16T15:40:00.00Z")))
                .category(TimelineElementCategoryInt.SEND_DIGITAL_FEEDBACK)
                .build();
        TimelineElementInternal feedbackOKThirdRecipientTimelineElement = TimelineElementInternal.builder()
                .elementId("feedbackOKThirdRecipientTimelineElement")
                .timestamp((Instant.parse("2021-09-16T15:40:10.00Z")))
                .category(TimelineElementCategoryInt.SEND_DIGITAL_FEEDBACK)
                .build();
        TimelineElementInternal pecReceivedSecondRecipientTimelineElement = TimelineElementInternal.builder()
                .elementId("pecReceivedSecondRecipientTimelineElement")
                .timestamp((Instant.parse("2021-09-16T15:41:00.00Z")))
                .category(TimelineElementCategoryInt.DIGITAL_SUCCESS_WORKFLOW)
                .build();
        TimelineElementInternal pecReceivedThirdRecipientTimelineElement = TimelineElementInternal.builder()
                .elementId("pecReceivedThirdRecipientTimelineElement")
                .timestamp((Instant.parse("2021-09-16T15:41:10.00Z")))
                .category(TimelineElementCategoryInt.DIGITAL_SUCCESS_WORKFLOW)
                .build();

        Set<TimelineElementInternal> timelineElementList = Set.of(requestAcceptedTimelineElement,
                sendPecFirstRecipientTimelineElement, sendPecSecondRecipientTimelineElement, sendPecThirdRecipientTimelineElement,
                feedbackOKFirstRecipientTimelineElement, pecReceivedFirstRecipientTimelineElement, viewedFromPNTimelineElement,
                feedbackOKSecondRecipientTimelineElement, feedbackOKThirdRecipientTimelineElement, pecReceivedSecondRecipientTimelineElement,
                pecReceivedThirdRecipientTimelineElement);


        // WHEN ask for status history
        Instant notificationCreatedAt = Instant.parse("2021-09-16T15:20:00.00Z");

        List<NotificationStatusHistoryElementInt> actualStatusHistory = statusUtils.getStatusHistory(
                timelineElementList,
                NUMBER_OF_RECIPIENTS,
                notificationCreatedAt
        );

        printStatus(actualStatusHistory);

        // THEN status histories have 4 elements
        Assertions.assertEquals(4, actualStatusHistory.size(), "Check length");

        //  ... 1st initial status
        Assertions.assertEquals(NotificationStatusHistoryElementInt.builder()
                        .status(NotificationStatusInt.IN_VALIDATION)
                        .activeFrom(notificationCreatedAt)
                        .relatedTimelineElements(List.of())
                        .build(),
                actualStatusHistory.get(0),
                "1st status wrong"
        );

        //  ... 2nd initial status
        Assertions.assertEquals(NotificationStatusHistoryElementInt.builder()
                        .status(NotificationStatusInt.ACCEPTED)
                        .activeFrom(requestAcceptedTimelineElement.getTimestamp())
                        .relatedTimelineElements(List.of("requestAcceptedTimelineElement"))
                        .build(),
                actualStatusHistory.get(1),
                "2nd status wrong"
        );

        //  ... 3rd initial status
        Assertions.assertEquals(NotificationStatusHistoryElementInt.builder()
                        .status(NotificationStatusInt.DELIVERING)
                        .activeFrom(sendPecFirstRecipientTimelineElement.getTimestamp())
                        .relatedTimelineElements(List.of("sendPecFirstRecipientTimelineElement",
                                "sendPecSecondRecipientTimelineElement", "sendPecThirdRecipientTimelineElement",
                                "feedbackOKFirstRecipientTimelineElement", "pecReceivedFirstRecipientTimelineElement"))
                        .build(),
                actualStatusHistory.get(2),
                "3rd status wrong"
        );

        //  ... 4th initial status
        Assertions.assertEquals(NotificationStatusHistoryElementInt.builder()
                        .status(NotificationStatusInt.VIEWED)
                        .activeFrom(viewedFromPNTimelineElement.getTimestamp())
                        .relatedTimelineElements(List.of("viewedFromPNTimelineElement", "feedbackOKSecondRecipientTimelineElement",
                                "feedbackOKThirdRecipientTimelineElement", "pecReceivedSecondRecipientTimelineElement",
                                "pecReceivedThirdRecipientTimelineElement"))
                        .build(),
                actualStatusHistory.get(3),
                "4th status wrong"
        );
    }

    // IN_VALIDATION - ACCEPTED - DELIVERING - DELIVERED - VIEWED
    // tutti e 3 destinatari sono raggiungibili via domicilio digitale, un destinatario visualizza la notifica dalla PEC
    // dopo che il workflow è completo per tutti e 3. Stato finale: VIEWED
    @Test
    void getTimelineHistoryMultiRecipientWithOneViewViaPecWithAllCompleteWorkflowTest() {
        final int NUMBER_OF_RECIPIENTS = 3;

        SendDigitalDetailsInt sendDigitalDetailsIntPec = getSendDigitalDetails(LegalDigitalAddressInt.LEGAL_DIGITAL_ADDRESS_TYPE.PEC);
        SendDigitalDetailsInt sendDigitalDetailsIntSercq = getSendDigitalDetails(LegalDigitalAddressInt.LEGAL_DIGITAL_ADDRESS_TYPE.SERCQ);

        NotificationViewedDetailsInt detailsInt = NotificationViewedDetailsInt.builder()
                .recIndex(0)
                .eventTimestamp(Instant.parse("2021-09-16T15:50:00.00Z"))
                .build();

        // GIVEN a timeline
        TimelineElementInternal requestAcceptedTimelineElement = TimelineElementInternal.builder()
                .elementId("requestAcceptedTimelineElement")
                .timestamp(Instant.parse("2021-09-16T15:24:00.00Z"))
                .category(TimelineElementCategoryInt.REQUEST_ACCEPTED)
                .build();

        TimelineElementInternal sendPecFirstRecipientTimelineElement = TimelineElementInternal.builder()
                .elementId("sendPecFirstRecipientTimelineElement")
                .timestamp((Instant.parse("2021-09-16T15:26:00.00Z")))
                .category(TimelineElementCategoryInt.SEND_DIGITAL_DOMICILE)
                .details(sendDigitalDetailsIntPec)
                .build();
        TimelineElementInternal sendPecSecondRecipientTimelineElement = TimelineElementInternal.builder()
                .elementId("sendPecSecondRecipientTimelineElement")
                .timestamp((Instant.parse("2021-09-16T15:26:30.00Z")))
                .category(TimelineElementCategoryInt.AAR_GENERATION)
                .details(sendDigitalDetailsIntSercq)
                .build();
        TimelineElementInternal sendPecThirdRecipientTimelineElement = TimelineElementInternal.builder()
                .elementId("sendPecThirdRecipientTimelineElement")
                .timestamp((Instant.parse("2021-09-16T15:26:40.00Z")))
                .category(TimelineElementCategoryInt.SEND_DIGITAL_DOMICILE)
                .details(sendDigitalDetailsIntPec)
                .build();
        //PN riceve feedback positivo da External Channels per tutti e 3 destinatari
        TimelineElementInternal feedbackOKFirstRecipientTimelineElement = TimelineElementInternal.builder()
                .elementId("feedbackOKFirstRecipientTimelineElement")
                .timestamp((Instant.parse("2021-09-16T15:30:00.00Z")))
                .category(TimelineElementCategoryInt.SEND_DIGITAL_FEEDBACK)
                .build();
        TimelineElementInternal pecReceivedFirstRecipientTimelineElement = TimelineElementInternal.builder()
                .elementId("pecReceivedFirstRecipientTimelineElement")
                .timestamp((Instant.parse("2021-09-16T15:32:00.00Z")))
                .category(TimelineElementCategoryInt.DIGITAL_DELIVERY_CREATION_REQUEST)
                .build();
        //PN riceve feedback positivo da External Channels per tutti e 3 destinatari e il workflow si completa per tutti e 3
        TimelineElementInternal feedbackOKSecondRecipientTimelineElement = TimelineElementInternal.builder()
                .elementId("feedbackOKSecondRecipientTimelineElement")
                .timestamp((Instant.parse("2021-09-16T15:33:00.00Z")))
                .category(TimelineElementCategoryInt.SEND_DIGITAL_FEEDBACK)
                .build();
        TimelineElementInternal pecReceivedSecondRecipientTimelineElement = TimelineElementInternal.builder()
                .elementId("pecReceivedSecondRecipientTimelineElement")
                .timestamp((Instant.parse("2021-09-16T15:34:00.00Z")))
                .category(TimelineElementCategoryInt.DIGITAL_DELIVERY_CREATION_REQUEST)
                .build();
        TimelineElementInternal feedbackOKThirdRecipientTimelineElement = TimelineElementInternal.builder()
                .elementId("feedbackOKThirdRecipientTimelineElement")
                .timestamp((Instant.parse("2021-09-16T15:35:00.00Z")))
                .category(TimelineElementCategoryInt.SEND_DIGITAL_FEEDBACK)
                .build();
        TimelineElementInternal pecReceivedThirdRecipientTimelineElement = TimelineElementInternal.builder()
                .elementId("pecReceivedThirdRecipientTimelineElement")
                .timestamp((Instant.parse("2021-09-16T15:36:00.00Z")))
                .category(TimelineElementCategoryInt.DIGITAL_DELIVERY_CREATION_REQUEST)
                .build();
        //Un destinatario visualizza la notifica
        TimelineElementInternal viewedFromPNTimelineElement = TimelineElementInternal.builder()
                .elementId("viewedFromPNTimelineElement")
                .timestamp((Instant.parse("2021-09-16T15:50:00.00Z")))
                .category(TimelineElementCategoryInt.NOTIFICATION_VIEWED)
                .details(detailsInt)
                .build();

        Set<TimelineElementInternal> timelineElementList = Set.of(requestAcceptedTimelineElement,
                sendPecFirstRecipientTimelineElement, sendPecSecondRecipientTimelineElement, sendPecThirdRecipientTimelineElement,
                feedbackOKFirstRecipientTimelineElement, pecReceivedFirstRecipientTimelineElement, feedbackOKSecondRecipientTimelineElement,
                pecReceivedSecondRecipientTimelineElement, feedbackOKThirdRecipientTimelineElement, pecReceivedThirdRecipientTimelineElement,
                viewedFromPNTimelineElement);


        // WHEN ask for status history
        Instant notificationCreatedAt = Instant.parse("2021-09-16T15:20:00.00Z");

        List<NotificationStatusHistoryElementInt> actualStatusHistory = statusUtils.getStatusHistory(
                timelineElementList,
                NUMBER_OF_RECIPIENTS,
                notificationCreatedAt
        );

        printStatus(actualStatusHistory);

        // THEN status histories have 5 elements
        Assertions.assertEquals(5, actualStatusHistory.size(), "Check length");

//        //  ... 1st initial status
        Assertions.assertEquals(NotificationStatusHistoryElementInt.builder()
                        .status(NotificationStatusInt.IN_VALIDATION)
                        .activeFrom(notificationCreatedAt)
                        .relatedTimelineElements(List.of())
                        .build(),
                actualStatusHistory.get(0),
                "1st status wrong"
        );

        //  ... 2nd initial status
        Assertions.assertEquals(NotificationStatusHistoryElementInt.builder()
                        .status(NotificationStatusInt.ACCEPTED)
                        .activeFrom(requestAcceptedTimelineElement.getTimestamp())
                        .relatedTimelineElements(List.of("requestAcceptedTimelineElement"))
                        .build(),
                actualStatusHistory.get(1),
                "2nd status wrong"
        );

        //  ... 3rd initial status
        Assertions.assertEquals(NotificationStatusHistoryElementInt.builder()
                        .status(NotificationStatusInt.DELIVERING)
                        .activeFrom(sendPecFirstRecipientTimelineElement.getTimestamp())
                        .relatedTimelineElements(List.of("sendPecFirstRecipientTimelineElement",
                                "sendPecSecondRecipientTimelineElement", "sendPecThirdRecipientTimelineElement",
                                "feedbackOKFirstRecipientTimelineElement", "pecReceivedFirstRecipientTimelineElement",
                                "feedbackOKSecondRecipientTimelineElement", "pecReceivedSecondRecipientTimelineElement",
                                "feedbackOKThirdRecipientTimelineElement"))
                        .build(),
                actualStatusHistory.get(2),
                "3rd status wrong"
        );

        //  ... 4th initial status
        Assertions.assertEquals(NotificationStatusHistoryElementInt.builder()
                        .status(NotificationStatusInt.DELIVERED)
                        .activeFrom(pecReceivedThirdRecipientTimelineElement.getTimestamp())
                        .relatedTimelineElements(List.of("pecReceivedThirdRecipientTimelineElement"))
                        .build(),
                actualStatusHistory.get(3),
                "4th status wrong"
        );

        //  ... 5th initial status
        Assertions.assertEquals(NotificationStatusHistoryElementInt.builder()
                        .status(NotificationStatusInt.VIEWED)
                        .activeFrom(viewedFromPNTimelineElement.getTimestamp())
                        .relatedTimelineElements(List.of("viewedFromPNTimelineElement"))
                        .build(),
                actualStatusHistory.get(4),
                "5th status wrong"
        );
    }

    // IN_VALIDATION - ACCEPTED - DELIVERING - VIEWED
    // tutti e 3 destinatari sono raggiungibili via domicilio digitale, un destinatario visualizza la notifica dalla PEC
    // dopo che External Channels ha dato feedback positivo per tutti e 3, ma prima che gli altri 2 destinatari ricevano
    // la PEC (e quindi il workflow non è completo per questi 2). Stato finale: VIEWED
    @Test
    void getTimelineHistoryMultiRecipientWithOneViewViaPecWithOneCompleteWorkflowWithAllFeedbacksTest() {
        final int NUMBER_OF_RECIPIENTS = 3;

        NotificationViewedDetailsInt detailsInt = NotificationViewedDetailsInt.builder()
                .recIndex(0)
                .eventTimestamp(Instant.parse("2021-09-16T15:35:00.00Z"))
                .build();

        SendDigitalDetailsInt sendDigitalDetailsIntPec = getSendDigitalDetails(LegalDigitalAddressInt.LEGAL_DIGITAL_ADDRESS_TYPE.PEC);
        SendDigitalDetailsInt sendDigitalDetailsIntSercq = getSendDigitalDetails(LegalDigitalAddressInt.LEGAL_DIGITAL_ADDRESS_TYPE.SERCQ);

        // GIVEN a timeline
        TimelineElementInternal requestAcceptedTimelineElement = TimelineElementInternal.builder()
                .elementId("requestAcceptedTimelineElement")
                .timestamp(Instant.parse("2021-09-16T15:24:00.00Z"))
                .category(TimelineElementCategoryInt.REQUEST_ACCEPTED)
                .build();
        TimelineElementInternal sendPecFirstRecipientTimelineElement = TimelineElementInternal.builder()
                .elementId("sendPecFirstRecipientTimelineElement")
                .timestamp((Instant.parse("2021-09-16T15:26:00.00Z")))
                .category(TimelineElementCategoryInt.SEND_DIGITAL_DOMICILE)
                .details(sendDigitalDetailsIntPec)
                .build();
        TimelineElementInternal sendPecSecondRecipientTimelineElement = TimelineElementInternal.builder()
                .elementId("sendPecSecondRecipientTimelineElement")
                .timestamp((Instant.parse("2021-09-16T15:26:30.00Z")))
                .category(TimelineElementCategoryInt.AAR_GENERATION)
                .details(sendDigitalDetailsIntSercq)
                .build();
        TimelineElementInternal sendPecThirdRecipientTimelineElement = TimelineElementInternal.builder()
                .elementId("sendPecThirdRecipientTimelineElement")
                .timestamp((Instant.parse("2021-09-16T15:26:40.00Z")))
                .category(TimelineElementCategoryInt.SEND_DIGITAL_DOMICILE)
                .details(sendDigitalDetailsIntPec)
                .build();
        //PN riceve feedback positivo da External Channels per tutti e 3 destinatari
        TimelineElementInternal feedbackOKFirstRecipientTimelineElement = TimelineElementInternal.builder()
                .elementId("feedbackOKFirstRecipientTimelineElement")
                .timestamp((Instant.parse("2021-09-16T15:30:00.00Z")))
                .category(TimelineElementCategoryInt.SEND_DIGITAL_FEEDBACK)
                .build();
        TimelineElementInternal pecReceivedFirstRecipientTimelineElement = TimelineElementInternal.builder()
                .elementId("pecReceivedFirstRecipientTimelineElement")
                .timestamp((Instant.parse("2021-09-16T15:32:00.00Z")))
                .category(TimelineElementCategoryInt.DIGITAL_SUCCESS_WORKFLOW)
                .build();
        //PN riceve feedback positivo da External Channels per tutti e 3 destinatari e il workflow si completa per tutti e 3
        TimelineElementInternal feedbackOKSecondRecipientTimelineElement = TimelineElementInternal.builder()
                .elementId("feedbackOKSecondRecipientTimelineElement")
                .timestamp((Instant.parse("2021-09-16T15:33:00.00Z")))
                .category(TimelineElementCategoryInt.SEND_DIGITAL_FEEDBACK)
                .build();
        TimelineElementInternal feedbackOKThirdRecipientTimelineElement = TimelineElementInternal.builder()
                .elementId("feedbackOKThirdRecipientTimelineElement")
                .timestamp((Instant.parse("2021-09-16T15:34:00.00Z")))
                .category(TimelineElementCategoryInt.SEND_DIGITAL_FEEDBACK)
                .build();
        //Un destinatario visualizza la notifica prima che il workflow sia completo per gli altri 2 destinatari
        TimelineElementInternal viewedFromPNTimelineElement = TimelineElementInternal.builder()
                .elementId("viewedFromPNTimelineElement")
                .timestamp((Instant.parse("2021-09-16T15:35:00.00Z")))
                .category(TimelineElementCategoryInt.NOTIFICATION_VIEWED)
                .details(detailsInt)
                .build();
        TimelineElementInternal pecReceivedSecondRecipientTimelineElement = TimelineElementInternal.builder()
                .elementId("pecReceivedSecondRecipientTimelineElement")
                .timestamp((Instant.parse("2021-09-16T15:36:00.00Z")))
                .category(TimelineElementCategoryInt.DIGITAL_SUCCESS_WORKFLOW)
                .build();

        TimelineElementInternal pecReceivedThirdRecipientTimelineElement = TimelineElementInternal.builder()
                .elementId("pecReceivedThirdRecipientTimelineElement")
                .timestamp((Instant.parse("2021-09-16T15:37:00.00Z")))
                .category(TimelineElementCategoryInt.DIGITAL_SUCCESS_WORKFLOW)
                .build();

        Set<TimelineElementInternal> timelineElementList = Set.of(requestAcceptedTimelineElement,
                sendPecFirstRecipientTimelineElement, sendPecSecondRecipientTimelineElement, sendPecThirdRecipientTimelineElement,
                feedbackOKFirstRecipientTimelineElement, pecReceivedFirstRecipientTimelineElement, feedbackOKSecondRecipientTimelineElement,
                feedbackOKThirdRecipientTimelineElement, viewedFromPNTimelineElement, pecReceivedSecondRecipientTimelineElement,
                pecReceivedThirdRecipientTimelineElement);


        // WHEN ask for status history
        Instant notificationCreatedAt = Instant.parse("2021-09-16T15:20:00.00Z");

        List<NotificationStatusHistoryElementInt> actualStatusHistory = statusUtils.getStatusHistory(
                timelineElementList,
                NUMBER_OF_RECIPIENTS,
                notificationCreatedAt
        );

        printStatus(actualStatusHistory);

        // THEN status histories have 4 elements
        Assertions.assertEquals(4, actualStatusHistory.size(), "Check length");

//        //  ... 1st initial status
        Assertions.assertEquals(NotificationStatusHistoryElementInt.builder()
                        .status(NotificationStatusInt.IN_VALIDATION)
                        .activeFrom(notificationCreatedAt)
                        .relatedTimelineElements(List.of())
                        .build(),
                actualStatusHistory.get(0),
                "1st status wrong"
        );

        //  ... 2nd initial status
        Assertions.assertEquals(NotificationStatusHistoryElementInt.builder()
                        .status(NotificationStatusInt.ACCEPTED)
                        .activeFrom(requestAcceptedTimelineElement.getTimestamp())
                        .relatedTimelineElements(List.of("requestAcceptedTimelineElement"))
                        .build(),
                actualStatusHistory.get(1),
                "2nd status wrong"
        );

        //  ... 3rd initial status
        Assertions.assertEquals(NotificationStatusHistoryElementInt.builder()
                        .status(NotificationStatusInt.DELIVERING)
                        .activeFrom(sendPecFirstRecipientTimelineElement.getTimestamp())
                        .relatedTimelineElements(List.of("sendPecFirstRecipientTimelineElement",
                                "sendPecSecondRecipientTimelineElement", "sendPecThirdRecipientTimelineElement",
                                "feedbackOKFirstRecipientTimelineElement", "pecReceivedFirstRecipientTimelineElement",
                                "feedbackOKSecondRecipientTimelineElement",
                                "feedbackOKThirdRecipientTimelineElement"))
                        .build(),
                actualStatusHistory.get(2),
                "3rd status wrong"
        );


        //  ... 4th initial status
        Assertions.assertEquals(NotificationStatusHistoryElementInt.builder()
                        .status(NotificationStatusInt.VIEWED)
                        .activeFrom(viewedFromPNTimelineElement.getTimestamp())
                        .relatedTimelineElements(List.of("viewedFromPNTimelineElement",
                                "pecReceivedSecondRecipientTimelineElement", "pecReceivedThirdRecipientTimelineElement"))
                        .build(),
                actualStatusHistory.get(3),
                "5th status wrong"
        );
    }

    // IN VALIDATION - ACCEPTED - DELIVERING
    // per 2 dei 3 destinatari vien generato l'AAR dopo il SEND_DIGITAL_DOMICILE. Stato finale: DELIVERING
    @Test
    void getTimelineHistoryMultiRecipientAARGenerationTest() {
        final int NUMBER_OF_RECIPIENTS = 3;

        SendDigitalDetailsInt sendDigitalDetailsIntPec = getSendDigitalDetails(LegalDigitalAddressInt.LEGAL_DIGITAL_ADDRESS_TYPE.PEC);
        SendDigitalDetailsInt sendDigitalDetailsIntSercq = getSendDigitalDetails(LegalDigitalAddressInt.LEGAL_DIGITAL_ADDRESS_TYPE.SERCQ);

        // GIVEN a timeline
        TimelineElementInternal requestAcceptedTimelineElement = TimelineElementInternal.builder()
                .elementId("requestAcceptedTimelineElement")
                .timestamp(Instant.parse("2021-09-16T15:24:00.00Z"))
                .category(TimelineElementCategoryInt.REQUEST_ACCEPTED)
                .build();
        TimelineElementInternal courtesyFirstTimelineElement = TimelineElementInternal.builder()
                .elementId("courtesyFirstTimelineElement")
                .timestamp(Instant.parse("2021-09-16T15:25:00.00Z"))
                .category(TimelineElementCategoryInt.SEND_COURTESY_MESSAGE)
                .build();
        TimelineElementInternal courtesySecondTimelineElement = TimelineElementInternal.builder()
                .elementId("courtesySecondTimelineElement")
                .timestamp(Instant.parse("2021-09-16T15:25:10.00Z"))
                .category(TimelineElementCategoryInt.SEND_COURTESY_MESSAGE)
                .build();
        TimelineElementInternal courtesyThirdTimelineElement = TimelineElementInternal.builder()
                .elementId("courtesyThirdTimelineElement")
                .timestamp(Instant.parse("2021-09-16T15:25:30.00Z"))
                .category(TimelineElementCategoryInt.SEND_COURTESY_MESSAGE)
                .build();
        TimelineElementInternal firstAARTimelineElement = TimelineElementInternal.builder()
                .elementId("firstAARTimelineElement")
                .timestamp(Instant.parse("2021-09-16T15:26:00.00Z"))
                .category(TimelineElementCategoryInt.AAR_GENERATION)
                .build();
        TimelineElementInternal sendPecFirstRecipientTimelineElement = TimelineElementInternal.builder()
                .elementId("sendPecFirstRecipientTimelineElement")
                .timestamp((Instant.parse("2021-09-16T15:26:10.00Z")))
                .category(TimelineElementCategoryInt.SEND_DIGITAL_DOMICILE)
                .details(sendDigitalDetailsIntPec)
                .build();
        TimelineElementInternal sendPecSecondRecipientTimelineElement = TimelineElementInternal.builder()
                .elementId("sendPecSecondRecipientTimelineElement")
                .timestamp((Instant.parse("2021-09-16T15:26:30.00Z")))
                .category(TimelineElementCategoryInt.AAR_GENERATION)
                .details(sendDigitalDetailsIntSercq)
                .build();
        TimelineElementInternal sendPecThirdRecipientTimelineElement = TimelineElementInternal.builder()
                .elementId("sendPecThirdRecipientTimelineElement")
                .timestamp((Instant.parse("2021-09-16T15:26:40.00Z")))
                .category(TimelineElementCategoryInt.SEND_DIGITAL_DOMICILE)
                .details(sendDigitalDetailsIntPec)
                .build();
        TimelineElementInternal secondAARTimelineElement = TimelineElementInternal.builder()
                .elementId("secondAARTimelineElement")
                .timestamp(Instant.parse("2021-09-16T15:30:00.00Z"))
                .category(TimelineElementCategoryInt.AAR_GENERATION)
                .build();
        TimelineElementInternal thirdAARTimelineElement = TimelineElementInternal.builder()
                .elementId("thirdAARTimelineElement")
                .timestamp(Instant.parse("2021-09-16T15:31:00.00Z"))
                .category(TimelineElementCategoryInt.AAR_GENERATION)
                .build();

        Set<TimelineElementInternal> timelineElementList = Set.of(requestAcceptedTimelineElement, courtesyFirstTimelineElement,
                courtesySecondTimelineElement, courtesyThirdTimelineElement, firstAARTimelineElement,
                sendPecFirstRecipientTimelineElement, sendPecSecondRecipientTimelineElement, sendPecThirdRecipientTimelineElement,
                secondAARTimelineElement, thirdAARTimelineElement);


        // WHEN ask for status history
        Instant notificationCreatedAt = Instant.parse("2021-09-16T15:20:00.00Z");

        List<NotificationStatusHistoryElementInt> actualStatusHistory = statusUtils.getStatusHistory(
                timelineElementList,
                NUMBER_OF_RECIPIENTS,
                notificationCreatedAt
        );

        printStatus(actualStatusHistory);

        // THEN status histories have 3 elements
        Assertions.assertEquals(3, actualStatusHistory.size(), "Check length");

        //  ... 1st initial status
        Assertions.assertEquals(NotificationStatusHistoryElementInt.builder()
                        .status(NotificationStatusInt.IN_VALIDATION)
                        .activeFrom(notificationCreatedAt)
                        .relatedTimelineElements(List.of())
                        .build(),
                actualStatusHistory.get(0),
                "1st status wrong"
        );

        //  ... 2nd initial status
        Assertions.assertEquals(NotificationStatusHistoryElementInt.builder()
                        .status(NotificationStatusInt.ACCEPTED)
                        .activeFrom(requestAcceptedTimelineElement.getTimestamp())
                        .relatedTimelineElements(List.of("requestAcceptedTimelineElement", "courtesyFirstTimelineElement",
                                "courtesySecondTimelineElement", "courtesyThirdTimelineElement", "firstAARTimelineElement"))
                        .build(),
                actualStatusHistory.get(1),
                "2nd status wrong"
        );

        //  ... 3rd initial status
        Assertions.assertEquals(NotificationStatusHistoryElementInt.builder()
                        .status(NotificationStatusInt.DELIVERING)
                        .activeFrom(sendPecFirstRecipientTimelineElement.getTimestamp())
                        .relatedTimelineElements(List.of("sendPecFirstRecipientTimelineElement",
                                "sendPecSecondRecipientTimelineElement", "sendPecThirdRecipientTimelineElement",
                                "secondAARTimelineElement", "thirdAARTimelineElement"))
                        .build(),
                actualStatusHistory.get(2),
                "3rd status wrong"
        );
    }


    @Test
    void getTimelineHistoryMoreRecipientTest() {

        SendDigitalDetailsInt sendDigitalDetailsIntPec = getSendDigitalDetails(LegalDigitalAddressInt.LEGAL_DIGITAL_ADDRESS_TYPE.PEC);
        SendDigitalDetailsInt sendDigitalDetailsIntSercq = getSendDigitalDetails(LegalDigitalAddressInt.LEGAL_DIGITAL_ADDRESS_TYPE.SERCQ);

        NotificationViewedDetailsInt detailsInt = NotificationViewedDetailsInt.builder()
                .recIndex(0)
                .eventTimestamp(Instant.parse("2021-09-16T17:00:00.00Z"))
                .build();

        // GIVEN a timeline
        TimelineElementInternal timelineElement1 = TimelineElementInternal.builder()
                .elementId("el1")
                .timestamp((Instant.parse("2021-09-16T15:24:00.00Z")))
                .category(TimelineElementCategoryInt.REQUEST_ACCEPTED)
                .build();
        TimelineElementInternal timelineElement3b = TimelineElementInternal.builder()
                .elementId("el2")
                .timestamp((Instant.parse("2021-09-16T15:25:00.00Z")))
                .category(TimelineElementCategoryInt.AAR_GENERATION)
                .details(sendDigitalDetailsIntSercq)
                .build();
        TimelineElementInternal timelineElement3 = TimelineElementInternal.builder()
                .elementId("el3")
                .timestamp((Instant.parse("2021-09-16T15:26:00.00Z")))
                .category(TimelineElementCategoryInt.SEND_DIGITAL_DOMICILE)
                .details(sendDigitalDetailsIntPec)
                .build();
        TimelineElementInternal timelineElement4 = TimelineElementInternal.builder()
                .elementId("el4")
                .timestamp((Instant.parse("2021-09-16T15:27:00.00Z")))
                .category(TimelineElementCategoryInt.SEND_DIGITAL_FEEDBACK)
                .build();
        TimelineElementInternal timelineElement5 = TimelineElementInternal.builder()
                .elementId("el5")
                .timestamp((Instant.parse("2021-09-16T15:28:00.00Z")))
                .category(TimelineElementCategoryInt.GET_ADDRESS)
                .build();
        TimelineElementInternal timelineElement4b = TimelineElementInternal.builder()
                .elementId("el6")
                .timestamp((Instant.parse("2021-09-16T15:30:00.00Z")))
                .category(TimelineElementCategoryInt.SEND_DIGITAL_FEEDBACK)
                .build();
        TimelineElementInternal timelineElement5b = TimelineElementInternal.builder()
                .elementId("el8")
                .timestamp((Instant.parse("2021-09-16T15:31:00.00Z")))
                .category(TimelineElementCategoryInt.DIGITAL_DELIVERY_CREATION_REQUEST)
                .build();
        TimelineElementInternal timelineElement6 = TimelineElementInternal.builder()
                .elementId("el9")
                .timestamp((Instant.parse("2021-09-16T17:00:00.00Z")))
                .category(TimelineElementCategoryInt.NOTIFICATION_VIEWED)
                .details(detailsInt)
                .build();

        Set<TimelineElementInternal> timelineElementList = Set.of(timelineElement1,
                timelineElement3, timelineElement4, timelineElement5, timelineElement3b, timelineElement4b,
                timelineElement5b, timelineElement6);


        // creare List<NotificationStatusHistoryElementInt>
        Instant notificationCreatedAt = Instant.parse("2021-09-16T15:20:00.00Z");

        NotificationStatusHistoryElementInt historyElement = NotificationStatusHistoryElementInt.builder()
                .status(NotificationStatusInt.IN_VALIDATION)
                .activeFrom(notificationCreatedAt)
                .relatedTimelineElements(List.of())
                .build();

        NotificationStatusHistoryElementInt historyElement1 = NotificationStatusHistoryElementInt.builder()
                .status(NotificationStatusInt.ACCEPTED)
                .activeFrom((Instant.parse("2021-09-16T15:24:00.00Z")))
                .relatedTimelineElements(List.of("el1", "el2"))
                .build();

        NotificationStatusHistoryElementInt historyElement2 = NotificationStatusHistoryElementInt.builder()
                .status(NotificationStatusInt.DELIVERING)
                .activeFrom((Instant.parse("2021-09-16T15:26:00.00Z")))
                .relatedTimelineElements(Arrays.asList("el3", "el4", "el5", "el6"))
                .build();
        NotificationStatusHistoryElementInt historyElement4b = NotificationStatusHistoryElementInt.builder()
                .status(NotificationStatusInt.DELIVERED)
                .activeFrom((Instant.parse("2021-09-16T15:31:00.00Z")))
                .relatedTimelineElements(List.of("el8"))
                .build();
        NotificationStatusHistoryElementInt historyElement5 = NotificationStatusHistoryElementInt.builder()
                .status(NotificationStatusInt.VIEWED)
                .activeFrom((Instant.parse("2021-09-16T17:00:00.00Z")))
                .relatedTimelineElements(List.of("el9"))
                .build();

        List<NotificationStatusHistoryElementInt> historyElementList = Arrays.asList(historyElement, historyElement1,
                historyElement2, historyElement4b, historyElement5);

        // chiamare metodo di test
        List<NotificationStatusHistoryElementInt> resHistoryElementList = statusUtils.getStatusHistory(
                timelineElementList, 1,
                notificationCreatedAt
        );
        // verificare che è risultato atteso
        Assertions.assertEquals(historyElementList, resHistoryElementList);
    }

    @Test
    void getTimelineHistoryErrorTest() {
        // creare TimelineElement
        TimelineElementInternal timelineElement1 = TimelineElementInternal.builder()
                .elementId("el1")
                .timestamp((Instant.parse("2021-09-16T15:24:00.00Z")))
                .category(TimelineElementCategoryInt.REQUEST_ACCEPTED)
                .build();
        TimelineElementInternal timelineElement2 = TimelineElementInternal.builder()
                .elementId("el2")
                .timestamp((Instant.parse("2021-09-16T15:25:00.00Z")))
                .category(TimelineElementCategoryInt.NOTIFICATION_VIEWED)
                .details(NotificationViewedDetailsInt.builder().recIndex(0).eventTimestamp(Instant.parse("2021-09-16T15:25:00.00Z")).build())
                .build();

        Set<TimelineElementInternal> timelineElementList = Set.of(timelineElement1,
                timelineElement2);

        Instant notificationCreatedAt = Instant.parse("2021-09-16T15:23:00.00Z");

        NotificationStatusHistoryElementInt historyElement1 = NotificationStatusHistoryElementInt.builder()
                .status(NotificationStatusInt.IN_VALIDATION)
                .relatedTimelineElements(List.of())
                .activeFrom(notificationCreatedAt)
                .build();

        NotificationStatusHistoryElementInt historyElement2 = NotificationStatusHistoryElementInt.builder()
                .status(NotificationStatusInt.ACCEPTED)
                .activeFrom((Instant.parse("2021-09-16T15:24:00.00Z")))
                .relatedTimelineElements(List.of("el1"))
                .build();

        NotificationStatusHistoryElementInt historyElement3 = NotificationStatusHistoryElementInt.builder()
                .status(NotificationStatusInt.VIEWED)
                .activeFrom((Instant.parse("2021-09-16T15:25:00.00Z")))
                .relatedTimelineElements(List.of("el2"))
                .build();


        List<NotificationStatusHistoryElementInt> historyElementList = Arrays.asList(
                historyElement1, historyElement2, historyElement3);

        // chiamare metodo di test
        List<NotificationStatusHistoryElementInt> resHistoryElementList = statusUtils.getStatusHistory(
                timelineElementList, 2,
                notificationCreatedAt
        );
        // verificare che è risultato atteso
        Assertions.assertEquals(historyElementList, resHistoryElementList);
    }

    @Test
    void emptyTimelineInitialStateTest() {
        //
        Assertions.assertEquals(NotificationStatusInt.IN_VALIDATION, statusUtils.getCurrentStatus(Collections.emptyList()));
    }

    @Test
    void getCurrentStatusTest() {
        List<NotificationStatusHistoryElementInt> statusHistory = new ArrayList<>();
        NotificationStatusHistoryElementInt statusHistoryDelivering = NotificationStatusHistoryElementInt.builder()
                .activeFrom(Instant.now())
                .status(NotificationStatusInt.DELIVERING)
                .build();
        NotificationStatusHistoryElementInt statusHistoryAccepted = NotificationStatusHistoryElementInt.builder()
                .activeFrom(Instant.now())
                .status(NotificationStatusInt.ACCEPTED)
                .build();
        statusHistory.add(statusHistoryDelivering);
        statusHistory.add(statusHistoryAccepted);

        Assertions.assertEquals(NotificationStatusInt.ACCEPTED, statusUtils.getCurrentStatus(statusHistory));
    }


    private void printStatus(List<NotificationStatusHistoryElementInt> notificationHistoryElements) {
        String methodName = Thread.currentThread().getStackTrace()[2].getMethodName();

        System.out.print(methodName + " - ");
        notificationHistoryElements.stream()
                .map(NotificationStatusHistoryElementInt::getStatus)
                .forEach(notificationStatusInt -> System.out.print(notificationStatusInt + " "));
        System.out.println();
    }

    private SendDigitalDetailsInt getSendDigitalDetails(LegalDigitalAddressInt.LEGAL_DIGITAL_ADDRESS_TYPE addressType) {

        String address = addressType == LegalDigitalAddressInt.LEGAL_DIGITAL_ADDRESS_TYPE.PEC ? PEC_ADDRESS : SERCQ_ADDRESS;
        LegalDigitalAddressInt legalDigitalAddressIntPec = LegalDigitalAddressInt.builder()
                .type(addressType).address(address).build();
        SendDigitalDetailsInt sendDigitalDetailsIntPec = new SendDigitalDetailsInt();
        sendDigitalDetailsIntPec.setDigitalAddress(legalDigitalAddressIntPec);
        return sendDigitalDetailsIntPec;
    }

    /*
        IN_VALIDATION - ACCEPTED - DELIVERING - RETURNED_TO_SENDER
        Per Il destinatario arriva un evento di deceduto e successivamente visualizza la notifica.
        Stato finale: RETURNED_TO_SENDER
    */
    @Test
    void getTimelineHistorySingleRecipientWithOneDeceasedWorkflowAndViewTest() {
        final int NUMBER_OF_RECIPIENTS = 1;

        SendDigitalDetailsInt sendDigitalDetailsIntPec = getSendDigitalDetails(LegalDigitalAddressInt.LEGAL_DIGITAL_ADDRESS_TYPE.PEC);

        // GIVEN a timeline
        TimelineElementInternal requestAcceptedTimelineElement = TimelineElementInternal.builder()
                .elementId("requestAcceptedTimelineElement")
                .timestamp(Instant.parse("2021-09-10T15:24:00.00Z"))
                .category(TimelineElementCategoryInt.REQUEST_ACCEPTED)
                .build();
        TimelineElementInternal sendAnalogFirstRecipientTimelineElement = TimelineElementInternal.builder()
                .elementId("sendAnalogFirstRecipientTimelineElement")
                .timestamp((Instant.parse("2021-09-12T15:26:00.00Z")))
                .category(TimelineElementCategoryInt.SEND_ANALOG_DOMICILE)
                .details(sendDigitalDetailsIntPec)
                .build();
        TimelineElementInternal prepareAnalogDomicileSecondRecipientTimelineElement = TimelineElementInternal.builder()
                .elementId("prepareAnalogDomicileSecondRecipientTimelineElement")
                .timestamp((Instant.parse("2021-09-15T15:40:00.00Z")))
                .category(TimelineElementCategoryInt.PREPARE_ANALOG_DOMICILE)
                .build();
        Instant feedbackFirstRecipientBusinessDate = Instant.parse("2021-09-17T10:30:00.00Z");
        TimelineElementInternal feedbackFirstRecipientTimelineElement = TimelineElementInternal.builder()
                .elementId("feedbackFirstRecipientTimelineElement")
                .timestamp((Instant.parse("2021-09-16T18:00:00.00Z")))
                .category(TimelineElementCategoryInt.SEND_ANALOG_FEEDBACK)
                .details(SendAnalogFeedbackDetailsInt.builder().recIndex(0).notificationDate(feedbackFirstRecipientBusinessDate).build())
                .build();
        TimelineElementInternal deceasedWorkflowFirstRecipientTimelineElement = TimelineElementInternal.builder()
                .elementId("deceasedWorkflowFirstRecipientTimelineElement")
                .timestamp((Instant.parse("2021-09-16T18:01:00.00Z")))
                .category(TimelineElementCategoryInt.ANALOG_WORKFLOW_RECIPIENT_DECEASED)
                .details(AnalogWorfklowRecipientDeceasedDetailsInt.builder().recIndex(0).notificationDate(feedbackFirstRecipientBusinessDate).build())
                .build();

        TimelineElementInternal firstRecViewedTimelineElement = TimelineElementInternal.builder()
                .elementId("firstRecViewedTimelineElement")
                .timestamp((Instant.parse("2021-09-18T12:00:00.00Z")))
                .category(TimelineElementCategoryInt.NOTIFICATION_VIEWED)
                .details(NotificationViewedDetailsInt.builder()
                        .recIndex(0)
                        .eventTimestamp(Instant.parse("2021-09-18T12:00:00.00Z"))
                        .build())
                .build();

        Set<TimelineElementInternal> timelineElementList = Set.of(requestAcceptedTimelineElement, sendAnalogFirstRecipientTimelineElement,
                feedbackFirstRecipientTimelineElement, deceasedWorkflowFirstRecipientTimelineElement,
                prepareAnalogDomicileSecondRecipientTimelineElement, firstRecViewedTimelineElement );


        // WHEN ask for status history
        Instant notificationCreatedAt = Instant.parse("2021-09-16T15:20:00.00Z");

        List<NotificationStatusHistoryElementInt> actualStatusHistory = statusUtils.getStatusHistory(
                timelineElementList,
                NUMBER_OF_RECIPIENTS,
                notificationCreatedAt
        );

        printStatus(actualStatusHistory);

        // THEN status histories have 4 elements
        Assertions.assertEquals(4, actualStatusHistory.size(), "Check length");

        //  ... 1st initial status
        Assertions.assertEquals(NotificationStatusHistoryElementInt.builder()
                        .status(NotificationStatusInt.IN_VALIDATION)
                        .activeFrom(notificationCreatedAt)
                        .relatedTimelineElements(List.of())
                        .build(),
                actualStatusHistory.get(0),
                "1st status wrong"
        );

        //  ... 2nd initial status
        Assertions.assertEquals(NotificationStatusHistoryElementInt.builder()
                        .status(NotificationStatusInt.ACCEPTED)
                        .activeFrom(requestAcceptedTimelineElement.getTimestamp())
                        .relatedTimelineElements(List.of("requestAcceptedTimelineElement"))
                        .build(),
                actualStatusHistory.get(1),
                "2nd status wrong"
        );

        //  ... 3rd initial status
        Assertions.assertEquals(NotificationStatusHistoryElementInt.builder()
                        .status(NotificationStatusInt.DELIVERING)
                        .activeFrom(sendAnalogFirstRecipientTimelineElement.getTimestamp())
                        .relatedTimelineElements(List.of("sendAnalogFirstRecipientTimelineElement", "prepareAnalogDomicileSecondRecipientTimelineElement",
                                "feedbackFirstRecipientTimelineElement"))
                        .build(),
                actualStatusHistory.get(2),
                "3rd status wrong"
        );

        //  ... 4th initial status
        Assertions.assertEquals(NotificationStatusHistoryElementInt.builder()
                        .status(NotificationStatusInt.RETURNED_TO_SENDER)
                        .activeFrom(feedbackFirstRecipientBusinessDate)
                        .relatedTimelineElements(List.of("deceasedWorkflowFirstRecipientTimelineElement", "firstRecViewedTimelineElement"))
                        .build(),
                actualStatusHistory.get(3),
                "4th status wrong"
        );
    }

    /*
        IN_VALIDATION - ACCEPTED - DELIVERING - VIEWED - RETURNED_TO_SENDER
        Il destinatario visualizza la notifica e successivamente arriva un evento di deceduto.
        Stato finale: RETURNED_TO_SENDER
    */
    @Test
    void getTimelineHistorySingleRecipientWithViewAndDeceasedWorkflowTest() {
        final int NUMBER_OF_RECIPIENTS = 1;

        SendDigitalDetailsInt sendDigitalDetailsIntPec = getSendDigitalDetails(LegalDigitalAddressInt.LEGAL_DIGITAL_ADDRESS_TYPE.PEC);

        // GIVEN a timeline
        TimelineElementInternal requestAcceptedTimelineElement = TimelineElementInternal.builder()
                .elementId("requestAcceptedTimelineElement")
                .timestamp(Instant.parse("2021-09-10T15:24:00.00Z"))
                .category(TimelineElementCategoryInt.REQUEST_ACCEPTED)
                .build();
        TimelineElementInternal sendAnalogFirstRecipientTimelineElement = TimelineElementInternal.builder()
                .elementId("sendAnalogFirstRecipientTimelineElement")
                .timestamp((Instant.parse("2021-09-12T15:26:00.00Z")))
                .category(TimelineElementCategoryInt.SEND_ANALOG_DOMICILE)
                .details(sendDigitalDetailsIntPec)
                .build();
        TimelineElementInternal prepareAnalogDomicileSecondRecipientTimelineElement = TimelineElementInternal.builder()
                .elementId("prepareAnalogDomicileSecondRecipientTimelineElement")
                .timestamp((Instant.parse("2021-09-15T15:40:00.00Z")))
                .category(TimelineElementCategoryInt.PREPARE_ANALOG_DOMICILE)
                .build();
        TimelineElementInternal firstRecViewedTimelineElement = TimelineElementInternal.builder()
                .elementId("firstRecViewedTimelineElement")
                .timestamp((Instant.parse("2021-09-15T17:00:00.00Z")))
                .category(TimelineElementCategoryInt.NOTIFICATION_VIEWED)
                .details(NotificationViewedDetailsInt.builder()
                        .recIndex(0)
                        .eventTimestamp(Instant.parse("2021-09-15T17:00:00.00Z"))
                        .build())
                .build();
        Instant feedbackFirstRecipientBusinessDate = Instant.parse("2021-09-17T10:30:00.00Z");
        TimelineElementInternal feedbackFirstRecipientTimelineElement = TimelineElementInternal.builder()
                .elementId("feedbackFirstRecipientTimelineElement")
                .timestamp((Instant.parse("2021-09-16T18:00:00.00Z")))
                .category(TimelineElementCategoryInt.SEND_ANALOG_FEEDBACK)
                .details(SendAnalogFeedbackDetailsInt.builder().recIndex(0).notificationDate(feedbackFirstRecipientBusinessDate).build())
                .build();
        TimelineElementInternal deceasedWorkflowFirstRecipientTimelineElement = TimelineElementInternal.builder()
                .elementId("deceasedWorkflowFirstRecipientTimelineElement")
                .timestamp((Instant.parse("2021-09-16T18:01:00.00Z")))
                .category(TimelineElementCategoryInt.ANALOG_WORKFLOW_RECIPIENT_DECEASED)
                .details(AnalogWorfklowRecipientDeceasedDetailsInt.builder().recIndex(0).notificationDate(feedbackFirstRecipientBusinessDate).build())
                .build();



        Set<TimelineElementInternal> timelineElementList = Set.of(requestAcceptedTimelineElement, sendAnalogFirstRecipientTimelineElement,
                feedbackFirstRecipientTimelineElement, deceasedWorkflowFirstRecipientTimelineElement,
                prepareAnalogDomicileSecondRecipientTimelineElement, firstRecViewedTimelineElement );


        // WHEN ask for status history
        Instant notificationCreatedAt = Instant.parse("2021-09-16T15:20:00.00Z");

        List<NotificationStatusHistoryElementInt> actualStatusHistory = statusUtils.getStatusHistory(
                timelineElementList,
                NUMBER_OF_RECIPIENTS,
                notificationCreatedAt
        );

        printStatus(actualStatusHistory);

        // THEN status histories have 4 elements
        Assertions.assertEquals(5, actualStatusHistory.size(), "Check length");

        //  ... 1st initial status
        Assertions.assertEquals(NotificationStatusHistoryElementInt.builder()
                        .status(NotificationStatusInt.IN_VALIDATION)
                        .activeFrom(notificationCreatedAt)
                        .relatedTimelineElements(List.of())
                        .build(),
                actualStatusHistory.get(0),
                "1st status wrong"
        );

        //  ... 2nd initial status
        Assertions.assertEquals(NotificationStatusHistoryElementInt.builder()
                        .status(NotificationStatusInt.ACCEPTED)
                        .activeFrom(requestAcceptedTimelineElement.getTimestamp())
                        .relatedTimelineElements(List.of("requestAcceptedTimelineElement"))
                        .build(),
                actualStatusHistory.get(1),
                "2nd status wrong"
        );

        //  ... 3rd initial status
        Assertions.assertEquals(NotificationStatusHistoryElementInt.builder()
                        .status(NotificationStatusInt.DELIVERING)
                        .activeFrom(sendAnalogFirstRecipientTimelineElement.getTimestamp())
                        .relatedTimelineElements(List.of("sendAnalogFirstRecipientTimelineElement", "prepareAnalogDomicileSecondRecipientTimelineElement"))
                        .build(),
                actualStatusHistory.get(2),
                "3rd status wrong"
        );

        //  ... 4th initial status
        Assertions.assertEquals(NotificationStatusHistoryElementInt.builder()
                        .status(NotificationStatusInt.VIEWED)
                        .activeFrom(firstRecViewedTimelineElement.getTimestamp())
                        .relatedTimelineElements(List.of("firstRecViewedTimelineElement", "feedbackFirstRecipientTimelineElement"))
                        .build(),
                actualStatusHistory.get(3),
                "4th status wrong"
        );

        //  ... 5th initial status
        Assertions.assertEquals(NotificationStatusHistoryElementInt.builder()
                        .status(NotificationStatusInt.RETURNED_TO_SENDER)
                        .activeFrom(feedbackFirstRecipientBusinessDate)
                        .relatedTimelineElements(List.of("deceasedWorkflowFirstRecipientTimelineElement"))
                        .build(),
                actualStatusHistory.get(4),
                "4th status wrong"
        );
    }

    /*
        IN_VALIDATION - ACCEPTED - DELIVERING - RETURNED_TO_SENDER - CANCELLED
        Per il destinatario arriva un evento di deceduto e successivamente viene richiesta la cancellazione della notifica.
        Stato finale: CANCELLED
    */
    @Test
    void getTimelineHistorySingleRecipientWithOneDeceasedWorkflowBeforeCancellation() {
        final int NUMBER_OF_RECIPIENTS = 1;

        SendDigitalDetailsInt sendDigitalDetailsIntPec = getSendDigitalDetails(LegalDigitalAddressInt.LEGAL_DIGITAL_ADDRESS_TYPE.PEC);

        // GIVEN a timeline
        TimelineElementInternal requestAcceptedTimelineElement = TimelineElementInternal.builder()
                .elementId("requestAcceptedTimelineElement")
                .timestamp(Instant.parse("2021-09-10T15:24:00.00Z"))
                .category(TimelineElementCategoryInt.REQUEST_ACCEPTED)
                .build();
        TimelineElementInternal sendAnalogFirstRecipientTimelineElement = TimelineElementInternal.builder()
                .elementId("sendAnalogFirstRecipientTimelineElement")
                .timestamp((Instant.parse("2021-09-12T15:26:00.00Z")))
                .category(TimelineElementCategoryInt.SEND_ANALOG_DOMICILE)
                .details(sendDigitalDetailsIntPec)
                .build();
        TimelineElementInternal prepareAnalogDomicileFirstRecipientTimelineElement = TimelineElementInternal.builder()
                .elementId("prepareAnalogDomicileFirstRecipientTimelineElement")
                .timestamp((Instant.parse("2021-09-15T15:40:00.00Z")))
                .category(TimelineElementCategoryInt.PREPARE_ANALOG_DOMICILE)
                .build();
        Instant feedbackFirstRecipientBusinessDate = Instant.parse("2021-09-17T10:30:00.00Z");
        TimelineElementInternal feedbackFirstRecipientTimelineElement = TimelineElementInternal.builder()
                .elementId("feedbackFirstRecipientTimelineElement")
                .timestamp((Instant.parse("2021-09-16T18:00:00.00Z")))
                .category(TimelineElementCategoryInt.SEND_ANALOG_FEEDBACK)
                .details(SendAnalogFeedbackDetailsInt.builder().recIndex(0).notificationDate(feedbackFirstRecipientBusinessDate).build())
                .build();
        TimelineElementInternal deceasedWorkflowFirstRecipientTimelineElement = TimelineElementInternal.builder()
                .elementId("deceasedWorkflowFirstRecipientTimelineElement")
                .timestamp((Instant.parse("2021-09-16T18:01:00.00Z")))
                .category(TimelineElementCategoryInt.ANALOG_WORKFLOW_RECIPIENT_DECEASED)
                .details(AnalogWorfklowRecipientDeceasedDetailsInt.builder().recIndex(0).notificationDate(feedbackFirstRecipientBusinessDate).build())
                .build();
        TimelineElementInternal cancelRequestTimelineElement = TimelineElementInternal.builder()
                .elementId("cancelRequestTimelineElement")
                .timestamp((Instant.parse("2021-09-17T18:00:00.00Z")))
                .category(TimelineElementCategoryInt.NOTIFICATION_CANCELLATION_REQUEST)
                .build();
        TimelineElementInternal cancelledTimelineElement = TimelineElementInternal.builder()
                .elementId("cancelledTimelineElement")
                .timestamp((Instant.parse("2021-09-17T18:01:00.00Z")))
                .category(TimelineElementCategoryInt.NOTIFICATION_CANCELLED)
                .build();

        Set<TimelineElementInternal> timelineElementList = Set.of(requestAcceptedTimelineElement, sendAnalogFirstRecipientTimelineElement,
                feedbackFirstRecipientTimelineElement, deceasedWorkflowFirstRecipientTimelineElement,
                prepareAnalogDomicileFirstRecipientTimelineElement, cancelRequestTimelineElement, cancelledTimelineElement);


        // WHEN ask for status history
        Instant notificationCreatedAt = Instant.parse("2021-09-16T15:20:00.00Z");

        List<NotificationStatusHistoryElementInt> actualStatusHistory = statusUtils.getStatusHistory(
                timelineElementList,
                NUMBER_OF_RECIPIENTS,
                notificationCreatedAt
        );

        printStatus(actualStatusHistory);

        // THEN status histories have 5 elements
        Assertions.assertEquals(5, actualStatusHistory.size(), "Check length");

        //  ... 1st initial status
        Assertions.assertEquals(NotificationStatusHistoryElementInt.builder()
                        .status(NotificationStatusInt.IN_VALIDATION)
                        .activeFrom(notificationCreatedAt)
                        .relatedTimelineElements(List.of())
                        .build(),
                actualStatusHistory.get(0),
                "1st status wrong"
        );

        //  ... 2nd initial status
        Assertions.assertEquals(NotificationStatusHistoryElementInt.builder()
                        .status(NotificationStatusInt.ACCEPTED)
                        .activeFrom(requestAcceptedTimelineElement.getTimestamp())
                        .relatedTimelineElements(List.of("requestAcceptedTimelineElement"))
                        .build(),
                actualStatusHistory.get(1),
                "2nd status wrong"
        );

        //  ... 3rd initial status
        Assertions.assertEquals(NotificationStatusHistoryElementInt.builder()
                        .status(NotificationStatusInt.DELIVERING)
                        .activeFrom(sendAnalogFirstRecipientTimelineElement.getTimestamp())
                        .relatedTimelineElements(List.of("sendAnalogFirstRecipientTimelineElement", "prepareAnalogDomicileFirstRecipientTimelineElement",
                                "feedbackFirstRecipientTimelineElement"))
                        .build(),
                actualStatusHistory.get(2),
                "3rd status wrong"
        );

        //  ... 4th initial status
        Assertions.assertEquals(NotificationStatusHistoryElementInt.builder()
                        .status(NotificationStatusInt.RETURNED_TO_SENDER)
                        .activeFrom(feedbackFirstRecipientBusinessDate)
                        .relatedTimelineElements(List.of("deceasedWorkflowFirstRecipientTimelineElement", "cancelRequestTimelineElement"))
                        .build(),
                actualStatusHistory.get(3),
                "4th status wrong"
        );

        //  ... 5th initial status
        Assertions.assertEquals(NotificationStatusHistoryElementInt.builder()
                        .status(NotificationStatusInt.CANCELLED)
                        .activeFrom(cancelledTimelineElement.getTimestamp())
                        .relatedTimelineElements(List.of("cancelledTimelineElement"))
                        .build(),
                actualStatusHistory.get(4),
                "5th status wrong"
        );
    }

    /*
        IN_VALIDATION - ACCEPTED - DELIVERING - CANCELLED
        Viene richiesta la cancellazione della notifica e successivamente per il destinatario arriva un evento di deceduto.
        Stato finale: CANCELLED
    */
    @Test
    void getTimelineHistorySingleRecipientWithOneDeceasedWorkflowAfterCancellation() {
        final int NUMBER_OF_RECIPIENTS = 1;

        SendDigitalDetailsInt sendDigitalDetailsIntPec = getSendDigitalDetails(LegalDigitalAddressInt.LEGAL_DIGITAL_ADDRESS_TYPE.PEC);

        // GIVEN a timeline
        TimelineElementInternal requestAcceptedTimelineElement = TimelineElementInternal.builder()
                .elementId("requestAcceptedTimelineElement")
                .timestamp(Instant.parse("2021-09-10T15:24:00.00Z"))
                .category(TimelineElementCategoryInt.REQUEST_ACCEPTED)
                .build();
        TimelineElementInternal sendAnalogFirstRecipientTimelineElement = TimelineElementInternal.builder()
                .elementId("sendAnalogFirstRecipientTimelineElement")
                .timestamp((Instant.parse("2021-09-12T15:26:00.00Z")))
                .category(TimelineElementCategoryInt.SEND_ANALOG_DOMICILE)
                .details(sendDigitalDetailsIntPec)
                .build();
        TimelineElementInternal prepareAnalogDomicileFirstRecipientTimelineElement = TimelineElementInternal.builder()
                .elementId("prepareAnalogDomicileFirstRecipientTimelineElement")
                .timestamp((Instant.parse("2021-09-15T15:40:00.00Z")))
                .category(TimelineElementCategoryInt.PREPARE_ANALOG_DOMICILE)
                .build();
        TimelineElementInternal cancelRequestTimelineElement = TimelineElementInternal.builder()
                .elementId("cancelRequestTimelineElement")
                .timestamp((Instant.parse("2021-09-15T18:00:00.00Z")))
                .category(TimelineElementCategoryInt.NOTIFICATION_CANCELLATION_REQUEST)
                .build();
        TimelineElementInternal cancelledTimelineElement = TimelineElementInternal.builder()
                .elementId("cancelledTimelineElement")
                .timestamp((Instant.parse("2021-09-15T18:01:00.00Z")))
                .category(TimelineElementCategoryInt.NOTIFICATION_CANCELLED)
                .build();
        Instant feedbackFirstRecipientBusinessDate = Instant.parse("2021-09-17T10:30:00.00Z");
        TimelineElementInternal feedbackFirstRecipientTimelineElement = TimelineElementInternal.builder()
                .elementId("feedbackFirstRecipientTimelineElement")
                .timestamp((Instant.parse("2021-09-16T18:00:00.00Z")))
                .category(TimelineElementCategoryInt.SEND_ANALOG_FEEDBACK)
                .details(SendAnalogFeedbackDetailsInt.builder().recIndex(0).notificationDate(feedbackFirstRecipientBusinessDate).build())
                .build();
        TimelineElementInternal deceasedWorkflowFirstRecipientTimelineElement = TimelineElementInternal.builder()
                .elementId("deceasedWorkflowFirstRecipientTimelineElement")
                .timestamp((Instant.parse("2021-09-16T18:01:00.00Z")))
                .category(TimelineElementCategoryInt.ANALOG_WORKFLOW_RECIPIENT_DECEASED)
                .details(AnalogWorfklowRecipientDeceasedDetailsInt.builder().recIndex(0).notificationDate(feedbackFirstRecipientBusinessDate).build())
                .build();

        Set<TimelineElementInternal> timelineElementList = Set.of(requestAcceptedTimelineElement, sendAnalogFirstRecipientTimelineElement,
                feedbackFirstRecipientTimelineElement, deceasedWorkflowFirstRecipientTimelineElement,
                prepareAnalogDomicileFirstRecipientTimelineElement, cancelRequestTimelineElement, cancelledTimelineElement);


        // WHEN ask for status history
        Instant notificationCreatedAt = Instant.parse("2021-09-16T15:20:00.00Z");

        List<NotificationStatusHistoryElementInt> actualStatusHistory = statusUtils.getStatusHistory(
                timelineElementList,
                NUMBER_OF_RECIPIENTS,
                notificationCreatedAt
        );

        printStatus(actualStatusHistory);

        // THEN status histories have 4 elements
        Assertions.assertEquals(4, actualStatusHistory.size(), "Check length");

        //  ... 1st initial status
        Assertions.assertEquals(NotificationStatusHistoryElementInt.builder()
                        .status(NotificationStatusInt.IN_VALIDATION)
                        .activeFrom(notificationCreatedAt)
                        .relatedTimelineElements(List.of())
                        .build(),
                actualStatusHistory.get(0),
                "1st status wrong"
        );

        //  ... 2nd initial status
        Assertions.assertEquals(NotificationStatusHistoryElementInt.builder()
                        .status(NotificationStatusInt.ACCEPTED)
                        .activeFrom(requestAcceptedTimelineElement.getTimestamp())
                        .relatedTimelineElements(List.of("requestAcceptedTimelineElement"))
                        .build(),
                actualStatusHistory.get(1),
                "2nd status wrong"
        );

        //  ... 3rd initial status
        Assertions.assertEquals(NotificationStatusHistoryElementInt.builder()
                        .status(NotificationStatusInt.DELIVERING)
                        .activeFrom(sendAnalogFirstRecipientTimelineElement.getTimestamp())
                        .relatedTimelineElements(List.of("sendAnalogFirstRecipientTimelineElement", "prepareAnalogDomicileFirstRecipientTimelineElement",
                                "cancelRequestTimelineElement"))
                        .build(),
                actualStatusHistory.get(2),
                "3rd status wrong"
        );

        //  ... 4th initial status
        Assertions.assertEquals(NotificationStatusHistoryElementInt.builder()
                        .status(NotificationStatusInt.CANCELLED)
                        .activeFrom(cancelledTimelineElement.getTimestamp())
                        .relatedTimelineElements(List.of("cancelledTimelineElement", "feedbackFirstRecipientTimelineElement","deceasedWorkflowFirstRecipientTimelineElement"))
                        .build(),
                actualStatusHistory.get(3),
                "4th status wrong"
        );
    }
}