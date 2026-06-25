package it.pagopa.pn.timelineservice.middleware.timelinedao.dao.dynamo;

import it.pagopa.pn.commons.exceptions.PnIdConflictException;
import it.pagopa.pn.timelineservice.config.BaseTest;
import it.pagopa.pn.timelineservice.dto.address.DigitalAddressSourceInt;
import it.pagopa.pn.timelineservice.dto.address.LegalDigitalAddressInt;
import it.pagopa.pn.timelineservice.dto.legalfacts.LegalFactCategoryInt;
import it.pagopa.pn.timelineservice.dto.legalfacts.LegalFactsIdInt;
import it.pagopa.pn.timelineservice.dto.notification.status.NotificationStatusHistoryInvalidatedElementInt;
import it.pagopa.pn.timelineservice.dto.timeline.CommunicationType;
import it.pagopa.pn.timelineservice.dto.timeline.TimelineElementInternal;
import it.pagopa.pn.timelineservice.dto.timeline.details.TimelineElementCategoryInt;
import it.pagopa.pn.timelineservice.dto.timeline.details.common.NotificationRequestAcceptedDetailsInt;
import it.pagopa.pn.timelineservice.dto.timeline.details.legal.*;
import it.pagopa.pn.timelineservice.middleware.dao.TimelineDao;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.*;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;


class TimelineEntityDaoDynamoTestIT extends BaseTest.WithLocalStack {
    @Autowired
    private TimelineDao timelineEntityDao;

    // Per le logiche di mapping quando si esegue una lettura dal DB (es: getTimelineElement), l'oggetto internal avrà sempre un communicationType valorizzato
    // e in questi test noi usiamo sempre le letture per verificare l'avvenuta persistenza. Ma in realtà sul record fisico del DB non ci sarà mai un communicationType = LEGAL.
    private static Stream<Arguments> provideCommunicationTypeArgs() {
        return Stream.of(
                Arguments.of(null, CommunicationType.LEGAL),
                Arguments.of(CommunicationType.INFORMAL, CommunicationType.INFORMAL),
                Arguments.of(CommunicationType.LEGAL, CommunicationType.LEGAL)
        );
    }

    @ParameterizedTest
    @MethodSource("provideCommunicationTypeArgs")
    void put(CommunicationType elementToInsertCommunicationType, CommunicationType expectedCommunicationType) {
        //GIVEN
        TimelineElementInternal elementToInsert = TimelineElementInternal.builder()
                .iun("pa1-1")
                .elementId(UUID.randomUUID().toString())
                .paId("paid001")
                .timestamp(Instant.now())
                .category(TimelineElementCategoryInt.SEND_SIMPLE_REGISTERED_LETTER)
                .details(
                        SimpleRegisteredLetterDetailsInt.builder()
                                .recIndex(0)
                                .numberOfPages(1)
                                .build()
                )
                .legalFactsIds(
                        Collections.singletonList(
                                LegalFactsIdInt.builder()
                                        .key("key")
                                        .category(LegalFactCategoryInt.DIGITAL_DELIVERY)
                                        .build()
                        )
                )
                .communicationType(elementToInsertCommunicationType)
                .build();

        try{
            //WHEN
            timelineEntityDao.addTimelineElementIfAbsent(elementToInsert).block();

            TimelineElementInternal elementFromDbOpt =  timelineEntityDao.getTimelineElement(elementToInsert.getIun(), elementToInsert.getElementId(), false).block();
            Assertions.assertNotNull(elementFromDbOpt);
            checkPersistenceOfTimelineElement(elementToInsert, elementFromDbOpt, expectedCommunicationType);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    private void checkPersistenceOfTimelineElement(TimelineElementInternal elementToInsert, TimelineElementInternal elementFromDb, CommunicationType expectedCommunicationType) {
        Assertions.assertEquals(elementToInsert.getIun(), elementFromDb.getIun());
        Assertions.assertEquals(elementToInsert.getElementId(), elementFromDb.getElementId());
        Assertions.assertEquals(elementToInsert.getPaId(), elementFromDb.getPaId());
        Assertions.assertEquals(elementToInsert.getTimestamp(), elementFromDb.getTimestamp());
        Assertions.assertEquals(elementToInsert.getCategory(), elementFromDb.getCategory());
        Assertions.assertEquals(elementToInsert.getDetails().toString(), elementFromDb.getDetails().toString());
        if(elementToInsert.getLegalFactsIds() != null) {
            Assertions.assertEquals(elementToInsert.getLegalFactsIds().toString(), elementFromDb.getLegalFactsIds().toString());
        }
        Assertions.assertEquals(expectedCommunicationType, elementFromDb.getCommunicationType());
    }

    @Test
    void putIfAbsentKo() {

        //GIVEN
        TimelineElementInternal elementToInsert = TimelineElementInternal.builder()
                .iun("pa1-1")
                .elementId(UUID.randomUUID().toString())
                .category(TimelineElementCategoryInt.PUBLIC_REGISTRY_CALL)
                .details(PublicRegistryCallDetailsInt.builder()
                        .recIndex(0)
                        .build())
                .legalFactsIds(
                        Collections.singletonList(
                                LegalFactsIdInt.builder()
                                        .key("key")
                                        .category(LegalFactCategoryInt.DIGITAL_DELIVERY)
                                        .build()
                        )
                )
                .build();

        assertDoesNotThrow(() -> timelineEntityDao.addTimelineElementIfAbsent(elementToInsert).block());
        Mono<Void> putIfAbsentMono = timelineEntityDao.addTimelineElementIfAbsent(elementToInsert);
        assertThrows(PnIdConflictException.class, putIfAbsentMono::block);


        TimelineElementInternal elementFromDbOpt =  timelineEntityDao.getTimelineElement(elementToInsert.getIun(), elementToInsert.getElementId(), false).block();
        Assertions.assertNotNull(elementFromDbOpt);
        checkPersistenceOfTimelineElement(elementToInsert, elementFromDbOpt, CommunicationType.LEGAL);
    }

    @Test
    void putIfAbsentOk() {

        //GIVEN
        TimelineElementInternal firstElementToInsert = TimelineElementInternal.builder()
                .iun("pa1-1")
                .elementId(UUID.randomUUID().toString())
                .category(TimelineElementCategoryInt.NOTIFICATION_VIEWED)
                .details(NotificationViewedDetailsInt.builder()
                        .recIndex(0)
                        .build())
                .legalFactsIds(
                        Collections.singletonList(
                                LegalFactsIdInt.builder()
                                        .key("key")
                                        .category(LegalFactCategoryInt.DIGITAL_DELIVERY)
                                        .build()
                        )
                )
                .build();

        TimelineElementInternal secondElementToInsert = TimelineElementInternal.builder()
                .iun("pa1-1")
                .elementId(UUID.randomUUID().toString())
                .category(TimelineElementCategoryInt.SEND_ANALOG_DOMICILE)
                .details(SendAnalogDetailsInt.builder()
                        .recIndex(0)
                        .build())
                .legalFactsIds(
                        Collections.singletonList(
                                LegalFactsIdInt.builder()
                                        .key("key")
                                        .category(LegalFactCategoryInt.DIGITAL_DELIVERY)
                                        .build()
                        )
                )
                .build();

        //WHEN
        assertDoesNotThrow(() -> timelineEntityDao.addTimelineElementIfAbsent(firstElementToInsert).block());
        assertDoesNotThrow(() -> timelineEntityDao.addTimelineElementIfAbsent(secondElementToInsert).block());

        //THEN
        TimelineElementInternal firstElementFromDbOpt =  timelineEntityDao.getTimelineElement(firstElementToInsert.getIun(), firstElementToInsert.getElementId(), false).block();
        Assertions.assertNotNull(firstElementFromDbOpt);
        checkPersistenceOfTimelineElement(firstElementToInsert, firstElementFromDbOpt, CommunicationType.LEGAL);

        TimelineElementInternal secondElementFromDbOpt =  timelineEntityDao.getTimelineElement(secondElementToInsert.getIun(),secondElementToInsert.getElementId(), false).block();
        Assertions.assertNotNull(secondElementFromDbOpt);
        checkPersistenceOfTimelineElement(secondElementToInsert, secondElementFromDbOpt, CommunicationType.LEGAL);
    }

    @Test
    void get() {

        //GIVEN
        TimelineElementInternal firstElementToInsert = TimelineElementInternal.builder()
                .iun("pa1-1")
                .elementId(UUID.randomUUID().toString())
                .category(TimelineElementCategoryInt.REQUEST_ACCEPTED)
                .details(NotificationRequestAcceptedDetailsInt.builder()
                        .build())
                .legalFactsIds(
                        Collections.singletonList(
                                LegalFactsIdInt.builder()
                                        .key("key")
                                        .category(LegalFactCategoryInt.DIGITAL_DELIVERY)
                                        .build()
                        )
                )
                .build();

        TimelineElementInternal secondElementToInsert = TimelineElementInternal.builder()
                .iun("pa1-2")
                .elementId(UUID.randomUUID().toString())
                .category(TimelineElementCategoryInt.SEND_ANALOG_DOMICILE)
                .details(SendAnalogDetailsInt.builder()
                        .recIndex(0)
                        .build())
                .legalFactsIds(
                        Collections.singletonList(
                                LegalFactsIdInt.builder()
                                        .key("key")
                                        .category(LegalFactCategoryInt.DIGITAL_DELIVERY)
                                        .build()
                        )
                )
                .build();

        timelineEntityDao.addTimelineElementIfAbsent(firstElementToInsert).block();
        timelineEntityDao.addTimelineElementIfAbsent(secondElementToInsert).block();

        //Check first element
        //WHEN
        TimelineElementInternal firstElementFromDbOpt =  timelineEntityDao.getTimelineElement(firstElementToInsert.getIun(), firstElementToInsert.getElementId(), false).block();
        Assertions.assertNotNull(firstElementFromDbOpt);
        checkPersistenceOfTimelineElement(firstElementToInsert, firstElementFromDbOpt, CommunicationType.LEGAL);

        //Check second element
        //WHEN
        TimelineElementInternal secondElementFromDbOpt =  timelineEntityDao.getTimelineElement(secondElementToInsert.getIun(), secondElementToInsert.getElementId(), false).block();
        Assertions.assertNotNull(secondElementFromDbOpt);
        checkPersistenceOfTimelineElement(secondElementToInsert, secondElementFromDbOpt, CommunicationType.LEGAL);
    }

    @Test
    void getNoElement() {

        //Check first element
        //WHEN
        TimelineElementInternal firstElementFromDbOpt =  timelineEntityDao.getTimelineElement("iun", "timelineId", false).block();

        //THEN
        Assertions.assertTrue(Objects.isNull(firstElementFromDbOpt));
    }

    @Test
    void findByIun() {
        String iun = "pa1-1";

        //GIVEN
        TimelineElementInternal firstElementToInsert = TimelineElementInternal.builder()
                .iun(iun)
                .elementId(UUID.randomUUID().toString())
                .category(TimelineElementCategoryInt.REFINEMENT)
                .details(RefinementDetailsInt.builder()
                        .recIndex(0)
                        .build())
                .legalFactsIds(
                        Collections.singletonList(
                                LegalFactsIdInt.builder()
                                        .key("key")
                                        .category(LegalFactCategoryInt.DIGITAL_DELIVERY)
                                        .build()
                        )
                )
                .build();

        TimelineElementInternal secondElementToInsert = TimelineElementInternal.builder()
                .iun(iun)
                .elementId(UUID.randomUUID().toString())
                .category(TimelineElementCategoryInt.SEND_ANALOG_DOMICILE)
                .details(SendAnalogDetailsInt.builder()
                        .recIndex(0)
                        .build())
                .legalFactsIds(
                        Collections.singletonList(
                                LegalFactsIdInt.builder()
                                        .key("key")
                                        .category(LegalFactCategoryInt.DIGITAL_DELIVERY)
                                        .build()
                        )
                )
                .build();

        timelineEntityDao.addTimelineElementIfAbsent(firstElementToInsert).block();

        timelineEntityDao.addTimelineElementIfAbsent(secondElementToInsert).block();

        //WHEN
        List<TimelineElementInternal> elementSet =  timelineEntityDao.getTimeline(iun).collectList().block();

        //THEN
        Assertions.assertNotNull(elementSet);
        Assertions.assertFalse(elementSet.isEmpty());
        Optional<TimelineElementInternal> firstElementDbOpt = elementSet.stream()
                .filter(el -> el.getElementId().equals(firstElementToInsert.getElementId()))
                .findFirst();
        Assertions.assertTrue(firstElementDbOpt.isPresent());
        checkPersistenceOfTimelineElement(firstElementToInsert, firstElementDbOpt.get(), CommunicationType.LEGAL);

        Optional<TimelineElementInternal> secondElementDbOpt = elementSet.stream()
                .filter(el -> el.getElementId().equals(secondElementToInsert.getElementId()))
                .findFirst();
        Assertions.assertTrue(secondElementDbOpt.isPresent());
        checkPersistenceOfTimelineElement(secondElementToInsert, secondElementDbOpt.get(), CommunicationType.LEGAL);
    }

    @Test
    void findByIunWithRework() {
        String iun = "pa1-1BIS";
        String invalidatedTimelineElementId1 = UUID.randomUUID().toString();
        String invalidatedTimelineElementId2 = UUID.randomUUID().toString();


        //GIVEN
        TimelineElementInternal firstElementToInsert = TimelineElementInternal.builder()
                .iun(iun)
                .elementId(invalidatedTimelineElementId1)
                .category(TimelineElementCategoryInt.REFINEMENT)
                .details(RefinementDetailsInt.builder()
                        .recIndex(0)
                        .build())
                .legalFactsIds(
                        Collections.singletonList(
                                LegalFactsIdInt.builder()
                                        .key("key")
                                        .category(LegalFactCategoryInt.DIGITAL_DELIVERY)
                                        .build()
                        )
                )
                .build();

        TimelineElementInternal secondElementToInsert = TimelineElementInternal.builder()
                .iun(iun)
                .elementId(UUID.randomUUID().toString())
                .category(TimelineElementCategoryInt.SEND_ANALOG_DOMICILE)
                .details(SendAnalogDetailsInt.builder()
                        .recIndex(0)
                        .build())
                .legalFactsIds(
                        Collections.singletonList(
                                LegalFactsIdInt.builder()
                                        .key("key")
                                        .category(LegalFactCategoryInt.DIGITAL_DELIVERY)
                                        .build()
                        )
                )
                .build();

        TimelineElementInternal thirdElementToInsert = TimelineElementInternal.builder()
                .iun(iun)
                .elementId(invalidatedTimelineElementId2)
                .category(TimelineElementCategoryInt.SEND_ANALOG_FEEDBACK)
                .details(SendAnalogFeedbackDetailsInt.builder()
                        .recIndex(0)
                        .build())
                .build();

        NotificationStatusHistoryInvalidatedElementInt notificationStatusHistoryElement = new NotificationStatusHistoryInvalidatedElementInt();
        notificationStatusHistoryElement.setRelatedTimelineElementIds(List.of(invalidatedTimelineElementId1, invalidatedTimelineElementId2));
        TimelineElementInternal fourthlementToInsert = TimelineElementInternal.builder()
                .iun(iun)
                .elementId(UUID.randomUUID().toString())
                .category(TimelineElementCategoryInt.NOTIFICATION_TIMELINE_REWORKED)
                .details(NotificationTimelineReworkedDetailsInt.builder()
                        .invalidatedTimelineAndStatusHistory(List.of(notificationStatusHistoryElement))
                        .build())
                .build();

        TimelineElementInternal fifthElementToInsert = TimelineElementInternal.builder()
                .iun(iun)
                .elementId(UUID.randomUUID().toString())
                .category(TimelineElementCategoryInt.SEND_ANALOG_FEEDBACK)
                .details(SendAnalogFeedbackDetailsInt.builder()
                        .recIndex(0)
                        .build())
                .build();

        timelineEntityDao.addTimelineElementIfAbsent(firstElementToInsert).block();

        timelineEntityDao.addTimelineElementIfAbsent(secondElementToInsert).block();
        timelineEntityDao.addTimelineElementIfAbsent(thirdElementToInsert).block();
        timelineEntityDao.addTimelineElementIfAbsent(fourthlementToInsert).block();
        timelineEntityDao.addTimelineElementIfAbsent(fifthElementToInsert).block();

        //WHEN
        List<TimelineElementInternal> elementSet =  timelineEntityDao.getTimeline(iun).collectList().block();

        //THEN
        Assertions.assertNotNull(elementSet);
        Assertions.assertFalse(elementSet.isEmpty());
        Assertions.assertTrue(elementSet.stream().map(TimelineElementInternal::getElementId)
                .anyMatch(s -> s.equals(secondElementToInsert.getElementId())));
        Assertions.assertTrue(elementSet.stream().map(TimelineElementInternal::getElementId)
                .anyMatch(s -> s.equals(fourthlementToInsert.getElementId())));
        Assertions.assertTrue(elementSet.stream().map(TimelineElementInternal::getElementId)
                .anyMatch(s -> s.equals(fifthElementToInsert.getElementId())));
    }

    @Test
    void getTimelineElementWithRework() {
        String iun = "pa1-IUN2";
        String invalidatedTimelineElementId1 = "PREPARE_ANALOG_DOMICILE.IUN_"+iun+".RECINDEX_0.ATTEMPT_1.REWORK_0";

        //GIVEN
        TimelineElementInternal firstElementToInsert = TimelineElementInternal.builder()
                .iun(iun)
                .elementId(invalidatedTimelineElementId1)
                .category(TimelineElementCategoryInt.PREPARE_ANALOG_DOMICILE)
                .details(RefinementDetailsInt.builder()
                        .recIndex(0)
                        .build())
                .legalFactsIds(
                        Collections.singletonList(
                                LegalFactsIdInt.builder()
                                        .key("key")
                                        .category(LegalFactCategoryInt.DIGITAL_DELIVERY)
                                        .build()
                        )
                )
                .build();

        NotificationStatusHistoryInvalidatedElementInt notificationStatusHistoryElementInt = new NotificationStatusHistoryInvalidatedElementInt();
        notificationStatusHistoryElementInt.setRelatedTimelineElementIds(List.of("SEND_ANALOG_PROGRESS.IUN_"+iun+".RECINDEX_0.ATTEMPT_1"));

        TimelineElementInternal secondReworkElementToInsert = TimelineElementInternal.builder()
                .iun(iun)
                .elementId("NOTIFICATION_TIMELINE_REWORKED.IUN_"+iun+".RECINDEX_0.ATTEMPT_0.REWORK_0")
                .category(TimelineElementCategoryInt.NOTIFICATION_TIMELINE_REWORKED)
                .details(NotificationTimelineReworkedDetailsInt.builder()
                        .recIndex(0)
                        .sentAttemptMade(0)
                        .invalidatedTimelineAndStatusHistory(List.of(notificationStatusHistoryElementInt))
                        .build())
                .legalFactsIds(
                        Collections.singletonList(
                                LegalFactsIdInt.builder()
                                        .key("key")
                                        .category(LegalFactCategoryInt.DIGITAL_DELIVERY)
                                        .build()
                        )
                )
                .build();

        timelineEntityDao.addTimelineElementIfAbsent(firstElementToInsert).block();
        timelineEntityDao.addTimelineElementIfAbsent(secondReworkElementToInsert).block();

        //WHEN
        TimelineElementInternal elementSet =  timelineEntityDao.getTimelineElement(iun, firstElementToInsert.getElementId(), false).block();

        //THEN
        Assertions.assertNotNull(elementSet);
        Assertions.assertEquals("PREPARE_ANALOG_DOMICILE.IUN_"+iun+".RECINDEX_0.ATTEMPT_1.REWORK_0", elementSet.getElementId());
    }

    @Test
    void getTimelineElementWithReworkWithInvalidatedElement() {
        String iun = "pa1-1";
        String invalidatedTimelineElementId1 = "REFINEMENT.IUN_"+iun+".RECINDEX_0";

        //GIVEN
        TimelineElementInternal firstElementToInsert = TimelineElementInternal.builder()
                .iun(iun)
                .elementId(invalidatedTimelineElementId1)
                .category(TimelineElementCategoryInt.REFINEMENT)
                .details(RefinementDetailsInt.builder()
                        .recIndex(0)
                        .build())
                .legalFactsIds(
                        Collections.singletonList(
                                LegalFactsIdInt.builder()
                                        .key("key")
                                        .category(LegalFactCategoryInt.DIGITAL_DELIVERY)
                                        .build()
                        )
                )
                .build();

        NotificationStatusHistoryInvalidatedElementInt notificationStatusHistoryElementInt = new NotificationStatusHistoryInvalidatedElementInt();
        notificationStatusHistoryElementInt.setRelatedTimelineElementIds(List.of("REFINEMENT.IUN_"+iun+".RECINDEX_0"));

        TimelineElementInternal secondReworkElementToInsert = TimelineElementInternal.builder()
                .iun(iun)
                .elementId("NOTIFICATION_TIMELINE_REWORKED.IUN_"+iun+".RECINDEX_0.ATTEMPT_0.REWORK_0")
                .category(TimelineElementCategoryInt.NOTIFICATION_TIMELINE_REWORKED)
                .details(NotificationTimelineReworkedDetailsInt.builder()
                        .recIndex(0)
                        .sentAttemptMade(0)
                        .invalidatedTimelineAndStatusHistory(List.of(notificationStatusHistoryElementInt))
                        .build())
                .legalFactsIds(
                        Collections.singletonList(
                                LegalFactsIdInt.builder()
                                        .key("key")
                                        .category(LegalFactCategoryInt.DIGITAL_DELIVERY)
                                        .build()
                        )
                )
                .build();

        timelineEntityDao.addTimelineElementIfAbsent(firstElementToInsert).block();
        timelineEntityDao.addTimelineElementIfAbsent(secondReworkElementToInsert).block();

        //WHEN
        TimelineElementInternal elementSet =  timelineEntityDao.getTimelineElement(iun, firstElementToInsert.getElementId(), false).block();

        //THEN
        Assertions.assertNull(elementSet);
    }

    @Test
    void getTimelineElementWithReworkWithInvalidatedPrepare() {
        String iun = "pa1-IUN1";
        String invalidatedTimelineElementId1 = "REFINEMENT.IUN_"+iun+".RECINDEX_0";
        String invalidatedTimelineElementId2 = "REFINEMENT.IUN_"+iun+".RECINDEX_0.REWORK_0";

        //GIVEN
        TimelineElementInternal firstElementToInsert = TimelineElementInternal.builder()
                .iun(iun)
                .elementId(invalidatedTimelineElementId1)
                .category(TimelineElementCategoryInt.REFINEMENT)
                .details(RefinementDetailsInt.builder()
                        .recIndex(0)
                        .build())
                .legalFactsIds(
                        Collections.singletonList(
                                LegalFactsIdInt.builder()
                                        .key("key")
                                        .category(LegalFactCategoryInt.DIGITAL_DELIVERY)
                                        .build()
                        )
                )
                .build();

        TimelineElementInternal thirdElementToInsert = TimelineElementInternal.builder()
                .iun(iun)
                .elementId(invalidatedTimelineElementId2)
                .category(TimelineElementCategoryInt.REFINEMENT)
                .details(RefinementDetailsInt.builder()
                        .recIndex(0)
                        .build())
                .legalFactsIds(
                        Collections.singletonList(
                                LegalFactsIdInt.builder()
                                        .key("key")
                                        .category(LegalFactCategoryInt.DIGITAL_DELIVERY)
                                        .build()
                        )
                )
                .build();

        NotificationStatusHistoryInvalidatedElementInt notificationStatusHistoryElementInt = new NotificationStatusHistoryInvalidatedElementInt();
        notificationStatusHistoryElementInt.setRelatedTimelineElementIds(List.of("REFINEMENT.IUN_"+iun+".RECINDEX_0"));

        TimelineElementInternal secondReworkElementToInsert = TimelineElementInternal.builder()
                .iun(iun)
                .elementId("NOTIFICATION_TIMELINE_REWORKED.IUN_"+iun+".RECINDEX_0.ATTEMPT_0.REWORK_0")
                .category(TimelineElementCategoryInt.NOTIFICATION_TIMELINE_REWORKED)
                .details(NotificationTimelineReworkedDetailsInt.builder()
                        .recIndex(0)
                        .sentAttemptMade(0)
                        .invalidatedTimelineAndStatusHistory(List.of(notificationStatusHistoryElementInt))
                        .build())
                .legalFactsIds(
                        Collections.singletonList(
                                LegalFactsIdInt.builder()
                                        .key("key")
                                        .category(LegalFactCategoryInt.DIGITAL_DELIVERY)
                                        .build()
                        )
                )
                .build();

        timelineEntityDao.addTimelineElementIfAbsent(firstElementToInsert).block();
        timelineEntityDao.addTimelineElementIfAbsent(secondReworkElementToInsert).block();
        timelineEntityDao.addTimelineElementIfAbsent(thirdElementToInsert).block();

        //WHEN
        TimelineElementInternal elementSet =  timelineEntityDao.getTimelineElement(iun, firstElementToInsert.getElementId(), false).block();

        //THEN
        Assertions.assertNotNull(elementSet);
        Assertions.assertEquals("REFINEMENT.IUN_"+iun+".RECINDEX_0.REWORK_0", elementSet.getElementId());
    }


    @Test
    void findByIunStrongly() {
        String iun = "pa1-1";

        //GIVEN
        TimelineElementInternal firstElementToInsert = TimelineElementInternal.builder()
                .iun(iun)
                .elementId(UUID.randomUUID().toString())
                .category(TimelineElementCategoryInt.REFINEMENT)
                .details(RefinementDetailsInt.builder()
                        .recIndex(0)
                        .build())
                .legalFactsIds(
                        Collections.singletonList(
                                LegalFactsIdInt.builder()
                                        .key("key")
                                        .category(LegalFactCategoryInt.DIGITAL_DELIVERY)
                                        .build()
                        )
                )
                .build();

        TimelineElementInternal secondElementToInsert = TimelineElementInternal.builder()
                .iun(iun)
                .elementId(UUID.randomUUID().toString())
                .category(TimelineElementCategoryInt.SEND_ANALOG_DOMICILE)
                .details(SendAnalogDetailsInt.builder()
                        .recIndex(0)
                        .build())
                .legalFactsIds(
                        Collections.singletonList(
                                LegalFactsIdInt.builder()
                                        .key("key")
                                        .category(LegalFactCategoryInt.DIGITAL_DELIVERY)
                                        .build()
                        )
                )
                .build();

        timelineEntityDao.addTimelineElementIfAbsent(firstElementToInsert).block();
        timelineEntityDao.addTimelineElementIfAbsent(secondElementToInsert).block();

        //WHEN
        List<TimelineElementInternal> elementSet =  timelineEntityDao.getTimelineStrongly(iun).collectList().block();

        //THEN
        Assertions.assertNotNull(elementSet);
        Assertions.assertFalse(elementSet.isEmpty());
        Optional<TimelineElementInternal> firstElementDbOpt = elementSet.stream()
                .filter(el -> el.getElementId().equals(firstElementToInsert.getElementId()))
                .findFirst();
        Assertions.assertTrue(firstElementDbOpt.isPresent());
        checkPersistenceOfTimelineElement(firstElementToInsert, firstElementDbOpt.get(), CommunicationType.LEGAL);

        Optional<TimelineElementInternal> secondElementDbOpt = elementSet.stream()
                .filter(el -> el.getElementId().equals(secondElementToInsert.getElementId()))
                .findFirst();
        Assertions.assertTrue(secondElementDbOpt.isPresent());
        checkPersistenceOfTimelineElement(secondElementToInsert, secondElementDbOpt.get(), CommunicationType.LEGAL);
    }

    @Test
    void getTimelineElmentStrongly() {
        String iun = "pa1-1";
        String elementIdToSearch = UUID.randomUUID().toString();
        //GIVEN
        TimelineElementInternal firstElementToInsert = TimelineElementInternal.builder()
                .iun(iun)
                .elementId(UUID.randomUUID().toString())
                .category(TimelineElementCategoryInt.AAR_CREATION_REQUEST)
                .details(AarCreationRequestDetailsInt.builder()
                        .recIndex(0)
                        .build())
                .legalFactsIds(
                        Collections.singletonList(
                                LegalFactsIdInt.builder()
                                        .key("key")
                                        .category(LegalFactCategoryInt.DIGITAL_DELIVERY)
                                        .build()
                        )
                )
                .build();

        TimelineElementInternal secondElementToInsert = TimelineElementInternal.builder()
                .iun(iun)
                .elementId(elementIdToSearch)
                .category(TimelineElementCategoryInt.SEND_ANALOG_DOMICILE)
                .details(SendAnalogDetailsInt.builder()
                        .recIndex(0)
                        .build())
                .legalFactsIds(
                        Collections.singletonList(
                                LegalFactsIdInt.builder()
                                        .key("key")
                                        .category(LegalFactCategoryInt.DIGITAL_DELIVERY)
                                        .build()
                        )
                )
                .build();

        timelineEntityDao.addTimelineElementIfAbsent(firstElementToInsert).block();
        timelineEntityDao.addTimelineElementIfAbsent(secondElementToInsert).block();

        //WHEN
        TimelineElementInternal timelineElmentStrongly = timelineEntityDao.getTimelineElement(iun, elementIdToSearch, true).block();

        Assertions.assertNotNull(timelineElmentStrongly);
        checkPersistenceOfTimelineElement(secondElementToInsert, timelineElmentStrongly, CommunicationType.LEGAL);
    }

    @Test
    void findByIunNoElements() {
        String iun = "pa1-1";
        List<TimelineElementInternal> elementSet =  timelineEntityDao.getTimeline(iun).collectList().block();
        Assertions.assertNotNull(elementSet);
        Assertions.assertTrue(elementSet.isEmpty());
    }


    @Test
    void searchByIunAndElementId() {


        String iun = "pa1-1tris";
        String elementId = UUID.randomUUID().toString();

        //GIVEN
        TimelineElementInternal firstElementToInsert = TimelineElementInternal.builder()
                .iun(iun)
                .elementId(elementId + "1")
                .category(TimelineElementCategoryInt.REFINEMENT)
                .details(RefinementDetailsInt.builder()
                        .recIndex(0)
                        .build())
                .legalFactsIds(
                        Collections.singletonList(
                                LegalFactsIdInt.builder()
                                        .key("key")
                                        .category(LegalFactCategoryInt.DIGITAL_DELIVERY)
                                        .build()
                        )
                )
                .build();

        TimelineElementInternal secondElementToInsert = TimelineElementInternal.builder()
                .iun(iun)
                .elementId(elementId + "2")
                .category(TimelineElementCategoryInt.SEND_ANALOG_DOMICILE)
                .details(SendAnalogDetailsInt.builder()
                        .recIndex(0)
                        .build())
                .legalFactsIds(
                        Collections.singletonList(
                                LegalFactsIdInt.builder()
                                        .key("key")
                                        .category(LegalFactCategoryInt.DIGITAL_DELIVERY)
                                        .build()
                        )
                )
                .build();

        TimelineElementInternal nomatchElementToInsert = TimelineElementInternal.builder()
                .iun(iun)
                .elementId(UUID.randomUUID() + "1")
                .category(TimelineElementCategoryInt.SEND_ANALOG_DOMICILE)
                .details(SendAnalogDetailsInt.builder()
                        .recIndex(0)
                        .build())
                .legalFactsIds(
                        Collections.singletonList(
                                LegalFactsIdInt.builder()
                                        .key("key")
                                        .category(LegalFactCategoryInt.DIGITAL_DELIVERY)
                                        .build()
                        )
                )
                .build();

        timelineEntityDao.addTimelineElementIfAbsent(firstElementToInsert).block();
        timelineEntityDao.addTimelineElementIfAbsent(secondElementToInsert).block();
        timelineEntityDao.addTimelineElementIfAbsent(nomatchElementToInsert).block();

        //WHEN
        List<TimelineElementInternal> elementSet =  timelineEntityDao.getTimelineFilteredByElementId(iun, elementId, true).collectList().block();

        //THEN
        Assertions.assertNotNull(elementSet);
        Assertions.assertFalse(elementSet.isEmpty());
        Optional<TimelineElementInternal> firstElementDbOpt = elementSet.stream()
                .filter(el -> el.getElementId().equals(firstElementToInsert.getElementId()))
                .findFirst();
        Assertions.assertTrue(firstElementDbOpt.isPresent());
        checkPersistenceOfTimelineElement(firstElementToInsert, firstElementDbOpt.get(), CommunicationType.LEGAL);

        Optional<TimelineElementInternal> secondElementDbOpt = elementSet.stream()
                .filter(el -> el.getElementId().equals(secondElementToInsert.getElementId()))
                .findFirst();
        Assertions.assertTrue(secondElementDbOpt.isPresent());
        checkPersistenceOfTimelineElement(secondElementToInsert, secondElementDbOpt.get(), CommunicationType.LEGAL);
        Assertions.assertEquals(2, elementSet.size());
    }

    @Test
    void checkSendDigitalProgress() {
        //GIVEN
        TimelineElementInternal elementToInsert = TimelineElementInternal.builder()
                .iun("pa1-1")
                .elementId(UUID.randomUUID().toString())
                .paId("paid001")
                .legalFactsIds(
                        Collections.singletonList(
                                LegalFactsIdInt.builder()
                                        .category(LegalFactCategoryInt.PEC_RECEIPT)
                                        .key("test")
                                        .build()
                        )
                )
                .category(TimelineElementCategoryInt.SEND_DIGITAL_PROGRESS)
                .details(
                        SendDigitalProgressDetailsInt.builder()
                                .recIndex(0)
                                .digitalAddress(
                                        LegalDigitalAddressInt.builder()
                                                .type(LegalDigitalAddressInt.LEGAL_DIGITAL_ADDRESS_TYPE.PEC)
                                                .address("test@address.it")
                                                .build()
                                )
                                .digitalAddressSource(DigitalAddressSourceInt.PLATFORM)
                                .retryNumber(0)
                                .build()
                )
                .build();
        //WHEN
        timelineEntityDao.addTimelineElementIfAbsent(elementToInsert).block();

        TimelineElementInternal elementFromDbOpt =  timelineEntityDao.getTimelineElement(elementToInsert.getIun(), elementToInsert.getElementId(), false).block();
        Assertions.assertNotNull(elementFromDbOpt);
        checkPersistenceOfTimelineElement(elementToInsert, elementFromDbOpt, CommunicationType.LEGAL);
    }

    @Test
    void checkNotificationView() {
        //GIVEN
        TimelineElementInternal elementToInsert = TimelineElementInternal.builder()
                .iun("pa1-1")
                .elementId(UUID.randomUUID().toString())
                .paId("paid001")
                .timestamp(Instant.now())
                .category(TimelineElementCategoryInt.NOTIFICATION_VIEWED)
                .details(
                        NotificationViewedDetailsInt.builder()
                                .recIndex(0)
                                .notificationCost(100)
                                .build()
                )
                .legalFactsIds(
                        Collections.singletonList(
                                LegalFactsIdInt.builder()
                                        .key("key")
                                        .category(LegalFactCategoryInt.RECIPIENT_ACCESS)
                                        .build()
                        )
                )
                .build();

        timelineEntityDao.addTimelineElementIfAbsent(elementToInsert).block();
        TimelineElementInternal elementFromDbOpt =  timelineEntityDao.getTimelineElement(elementToInsert.getIun(), elementToInsert.getElementId(), false).block();
        Assertions.assertNotNull(elementFromDbOpt);
        checkPersistenceOfTimelineElement(elementToInsert, elementFromDbOpt, CommunicationType.LEGAL);
    }

    @Test
    void checkRefinement() {
        //GIVEN
        TimelineElementInternal elementToInsert = TimelineElementInternal.builder()
                .iun("pa1-1")
                .elementId(UUID.randomUUID().toString())
                .paId("paid001")
                .timestamp(Instant.now())
                .category(TimelineElementCategoryInt.REFINEMENT)
                .details(
                        RefinementDetailsInt.builder()
                                .recIndex(0)
                                .notificationCost(100)
                                .build()
                )
                .build();

        timelineEntityDao.addTimelineElementIfAbsent(elementToInsert).block();
        TimelineElementInternal elementFromDbOpt =  timelineEntityDao.getTimelineElement(elementToInsert.getIun(), elementToInsert.getElementId(), false).block();
        Assertions.assertNotNull(elementFromDbOpt);
        checkPersistenceOfTimelineElement(elementToInsert, elementFromDbOpt, CommunicationType.LEGAL);
    }

    @Test
    void getTimelineElmentStronglyWithReworkCategory() {
        String iun = "pa1-IUN3";
        String elementIdToSearch = "SEND_ANALOG_FEEDBACK.IUN_"+iun+".RECINDEX_0.ATTEMPT_0";
        //GIVEN
        TimelineElementInternal firstElementToInsert = TimelineElementInternal.builder()
                .iun(iun)
                .elementId("SEND_ANALOG_FEEDBACK.IUN_"+iun+".RECINDEX_0.ATTEMPT_0.REWORK_1")
                .category(TimelineElementCategoryInt.SEND_ANALOG_FEEDBACK)
                .details(AarCreationRequestDetailsInt.builder()
                        .recIndex(0)
                        .build())
                .legalFactsIds(
                        Collections.singletonList(
                                LegalFactsIdInt.builder()
                                        .key("key")
                                        .category(LegalFactCategoryInt.DIGITAL_DELIVERY)
                                        .build()
                        )
                )
                .build();

        TimelineElementInternal reworkElementToInsert = TimelineElementInternal.builder()
                .iun(iun)
                .elementId("NOTIFICATION_TIMELINE_REWORKED.IUN_"+iun+".RECINDEX_0.ATTEMPT_0.REWORK_0")
                .category(TimelineElementCategoryInt.NOTIFICATION_TIMELINE_REWORKED)
                .details(NotificationTimelineReworkedDetailsInt.builder()
                        .recIndex(0)
                        .sentAttemptMade(0)
                        .invalidatedTimelineAndStatusHistory(List.of())
                        .build())
                .legalFactsIds(
                        Collections.singletonList(
                                LegalFactsIdInt.builder()
                                        .key("key")
                                        .category(LegalFactCategoryInt.DIGITAL_DELIVERY)
                                        .build()
                        )
                )
                .build();

        NotificationStatusHistoryInvalidatedElementInt notificationStatusHistoryElementInt = new NotificationStatusHistoryInvalidatedElementInt();
        notificationStatusHistoryElementInt.setRelatedTimelineElementIds(List.of("SEND_ANALOG_FEEDBACK.IUN_"+iun+".ATTEMPT_0"));


        TimelineElementInternal secondReworkElementToInsert = TimelineElementInternal.builder()
                .iun(iun)
                .elementId("NOTIFICATION_TIMELINE_REWORKED.IUN_"+iun+".RECINDEX_0.ATTEMPT_0.REWORK_1")
                .category(TimelineElementCategoryInt.NOTIFICATION_TIMELINE_REWORKED)
                .details(NotificationTimelineReworkedDetailsInt.builder()
                        .recIndex(0)
                        .sentAttemptMade(0)
                        .invalidatedTimelineAndStatusHistory(List.of(notificationStatusHistoryElementInt))
                        .build())
                .legalFactsIds(
                        Collections.singletonList(
                                LegalFactsIdInt.builder()
                                        .key("key")
                                        .category(LegalFactCategoryInt.DIGITAL_DELIVERY)
                                        .build()
                        )
                )
                .build();

        timelineEntityDao.addTimelineElementIfAbsent(firstElementToInsert).block();
        timelineEntityDao.addTimelineElementIfAbsent(reworkElementToInsert).block();
        timelineEntityDao.addTimelineElementIfAbsent(secondReworkElementToInsert).block();

        //WHEN
        TimelineElementInternal timelineElmentStrongly = timelineEntityDao.getTimelineElement(iun, elementIdToSearch, true).block();

        Assertions.assertNotNull(timelineElmentStrongly);
    }

}
