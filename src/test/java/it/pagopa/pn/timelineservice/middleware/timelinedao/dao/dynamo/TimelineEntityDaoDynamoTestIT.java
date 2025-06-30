package it.pagopa.pn.timelineservice.middleware.timelinedao.dao.dynamo;

import it.pagopa.pn.commons.exceptions.PnIdConflictException;
import it.pagopa.pn.timelineservice.config.BaseTest;
import it.pagopa.pn.timelineservice.dto.address.DigitalAddressSourceInt;
import it.pagopa.pn.timelineservice.dto.address.LegalDigitalAddressInt;
import it.pagopa.pn.timelineservice.dto.legalfacts.LegalFactCategoryInt;
import it.pagopa.pn.timelineservice.dto.legalfacts.LegalFactsIdInt;
import it.pagopa.pn.timelineservice.dto.timeline.TimelineElementInternal;
import it.pagopa.pn.timelineservice.dto.timeline.details.*;
import it.pagopa.pn.timelineservice.middleware.dao.TimelineDao;
import it.pagopa.pn.timelineservice.middleware.dao.dynamo.entity.NotificationRefusedErrorEntity;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;


class TimelineEntityDaoDynamoTestIT extends BaseTest.WithLocalStack {
    @Autowired
    private TimelineDao timelineEntityDao;


    @Test
    void put() {
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
                .build();

        try{
            //WHEN
            timelineEntityDao.addTimelineElementIfAbsent(elementToInsert).block();

            TimelineElementInternal elementFromDbOpt =  timelineEntityDao.getTimelineElement(elementToInsert.getIun(), elementToInsert.getElementId(), false).block();
            Assertions.assertEquals(elementToInsert.toString(), elementFromDbOpt.toString());

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

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
        Assertions.assertEquals(elementToInsert.toString(), elementFromDbOpt.toString());
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
        Assertions.assertEquals(firstElementToInsert.toString(), firstElementFromDbOpt.toString());

        TimelineElementInternal secondElementFromDbOpt =  timelineEntityDao.getTimelineElement(secondElementToInsert.getIun(),secondElementToInsert.getElementId(), false).block();
        Assertions.assertEquals(secondElementToInsert.toString(), secondElementFromDbOpt.toString());
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
        Assertions.assertEquals(firstElementToInsert.toString(), firstElementFromDbOpt.toString());

        //Check second element
        //WHEN
        TimelineElementInternal secondElementFromDbOpt =  timelineEntityDao.getTimelineElement(secondElementToInsert.getIun(), secondElementToInsert.getElementId(), false).block();
        Assertions.assertEquals(secondElementToInsert.toString(), secondElementFromDbOpt.toString());
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
        Assertions.assertTrue(elementSet.stream().map(TimelineElementInternal::toString)
                .anyMatch(s -> s.equals(firstElementToInsert.toString())));
        Assertions.assertTrue(elementSet.stream().map(TimelineElementInternal::toString)
                .anyMatch(s -> s.equals(secondElementToInsert.toString())));
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
        Assertions.assertFalse(elementSet.isEmpty());
        Assertions.assertTrue(elementSet.stream().map(TimelineElementInternal::toString)
                .anyMatch(s -> s.equals(firstElementToInsert.toString())));
        Assertions.assertTrue(elementSet.stream().map(TimelineElementInternal::toString)
                .anyMatch(s -> s.equals(secondElementToInsert.toString())));
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
        Assertions.assertEquals(timelineElmentStrongly.toString(), secondElementToInsert.toString());
    }

    @Test
    void findByIunNoElements() {
        String iun = "pa1-1";
        List<TimelineElementInternal> elementSet =  timelineEntityDao.getTimeline(iun).collectList().block();
        Assertions.assertTrue(elementSet.isEmpty());
    }


    @Test
    void searchByIunAndElementId() {


        String iun = "pa1-1";
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
        List<TimelineElementInternal> elementSet =  timelineEntityDao.getTimelineFilteredByElementId(iun, elementId).collectList().block();

        //THEN
        Assertions.assertNotNull(elementSet);
        Assertions.assertFalse(elementSet.isEmpty());
        Assertions.assertTrue(elementSet.stream().map(TimelineElementInternal::toString)
                .anyMatch(s -> s.equals(firstElementToInsert.toString())));
        Assertions.assertTrue(elementSet.stream().map(TimelineElementInternal::toString)
                .anyMatch(s -> s.equals(secondElementToInsert.toString())));
        Assertions.assertEquals(2, elementSet.size());
    }

    @Test
    void checkSendDigitalProgress() {
        List<NotificationRefusedErrorEntity> errors = new ArrayList<>();
        NotificationRefusedErrorEntity notificationRefusedError = NotificationRefusedErrorEntity.builder()
                .errorCode("FILE_NOTFOUND")
                .detail("details")
                .build();
        errors.add(notificationRefusedError);
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
        Assertions.assertEquals(elementToInsert.toString(), elementFromDbOpt.toString());
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

        checkElement(elementToInsert);
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

        checkElement(elementToInsert);
    }

    private void checkElement(TimelineElementInternal elementToInsert) {
            //WHEN
            timelineEntityDao.addTimelineElementIfAbsent(elementToInsert).block();
            TimelineElementInternal elementFromDbOpt =  timelineEntityDao.getTimelineElement(elementToInsert.getIun(), elementToInsert.getElementId(), false).block();
            Assertions.assertEquals(elementToInsert.toString(), elementFromDbOpt.toString());
    }

}
