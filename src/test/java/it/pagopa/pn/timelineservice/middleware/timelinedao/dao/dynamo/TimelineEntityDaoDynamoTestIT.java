package it.pagopa.pn.timelineservice.middleware.timelinedao.dao.dynamo;

import it.pagopa.pn.commons.abstractions.impl.MiddlewareTypes;
import it.pagopa.pn.commons.exceptions.PnIdConflictException;
import it.pagopa.pn.timelineservice.LocalStackTestConfig;
import it.pagopa.pn.timelineservice.middleware.dao.TimelineCounterEntityDao;
import it.pagopa.pn.timelineservice.middleware.dao.TimelineDao;
import it.pagopa.pn.timelineservice.middleware.dao.TimelineEntityDao;
import it.pagopa.pn.timelineservice.middleware.dao.dynamo.entity.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import software.amazon.awssdk.enhanced.dynamodb.Key;

import java.time.Instant;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(SpringExtension.class)
@TestPropertySource(properties = {
        TimelineDao.IMPLEMENTATION_TYPE_PROPERTY_NAME + "=" + MiddlewareTypes.DYNAMO,
        TimelineCounterEntityDao.IMPLEMENTATION_TYPE_PROPERTY_NAME + "=" + MiddlewareTypes.DYNAMO,
})
@SpringBootTest
@Import(LocalStackTestConfig.class)
class TimelineEntityDaoDynamoTestIT {
    @Autowired
    private TimelineEntityDao timelineEntityDao;

    @Test
    void putIfAbsentKo() {

        //GIVEN
        TimelineElementEntity elementToInsert = TimelineElementEntity.builder()
                .iun("pa1-1")
                .timelineElementId("elementId1")
                .category(TimelineElementCategoryEntity.PUBLIC_REGISTRY_CALL)
                .details(TimelineElementDetailsEntity.builder()
                        .recIndex(0)
                        .build())
                .legalFactIds(
                        Collections.singletonList(
                                LegalFactsIdEntity.builder()
                                        .key("key")
                                        .category(LegalFactCategoryEntity.DIGITAL_DELIVERY)
                                        .build()
                        )
                )
                .build();

        TimelineElementEntity elementNotToBeInserted = TimelineElementEntity.builder()
                .iun("pa1-1")
                .timelineElementId("elementId1")
                .category(TimelineElementCategoryEntity.SEND_ANALOG_DOMICILE)
                .details(TimelineElementDetailsEntity.builder()
                        .recIndex(0)
                        .build())
                .legalFactIds(
                        Collections.singletonList(
                                LegalFactsIdEntity.builder()
                                        .key("key")
                                        .category(LegalFactCategoryEntity.DIGITAL_DELIVERY)
                                        .build()
                        )
                )
                .build();

        Key elementsKey = Key.builder()
                .partitionValue(elementToInsert.getIun())
                .sortValue(elementToInsert.getTimelineElementId())
                .build();

        removeElementFromDb(elementToInsert);
        removeElementFromDb(elementNotToBeInserted);


        assertDoesNotThrow(() -> timelineEntityDao.putIfAbsent(elementToInsert).block());

        //WHEN

        assertThrows(PnIdConflictException.class, () -> timelineEntityDao.putIfAbsent(elementNotToBeInserted));

        //THEN
        TimelineElementEntity elementFromDb =  timelineEntityDao.getTimelineElement(elementToInsert.getIun(), elementToInsert.getTimelineElementId()).block();

        Assertions.assertEquals(elementToInsert, elementFromDb);
        Assertions.assertNotEquals(elementNotToBeInserted, elementFromDb);
    }

    @Test
    void putIfAbsentOk() {

        //GIVEN
        TimelineElementEntity firstElementToInsert = TimelineElementEntity.builder()
                .iun("pa1-1")
                .timelineElementId("elementId1")
                .category(TimelineElementCategoryEntity.NOTIFICATION_VIEWED)
                .details(TimelineElementDetailsEntity.builder()
                        .recIndex(0)
                        .build())
                .legalFactIds(
                        Collections.singletonList(
                                LegalFactsIdEntity.builder()
                                        .key("key")
                                        .category(LegalFactCategoryEntity.DIGITAL_DELIVERY)
                                        .build()
                        )
                )
                .build();

        TimelineElementEntity secondElementToInsert = TimelineElementEntity.builder()
                .iun("pa1-1")
                .timelineElementId("elementId2")
                .category(TimelineElementCategoryEntity.SEND_ANALOG_DOMICILE)
                .details(TimelineElementDetailsEntity.builder()
                        .recIndex(0)
                        .build())
                .legalFactIds(
                        Collections.singletonList(
                                LegalFactsIdEntity.builder()
                                        .key("key")
                                        .category(LegalFactCategoryEntity.DIGITAL_DELIVERY)
                                        .build()
                        )
                )
                .build();

        removeElementFromDb(firstElementToInsert);
        removeElementFromDb(secondElementToInsert);

        //WHEN
        assertDoesNotThrow(() -> timelineEntityDao.putIfAbsent(firstElementToInsert).block());
        assertDoesNotThrow(() -> timelineEntityDao.putIfAbsent(secondElementToInsert).block());

        //THEN
        TimelineElementEntity firstElementFromDb =  timelineEntityDao.getTimelineElement(firstElementToInsert.getIun(), firstElementToInsert.getTimelineElementId()).block();
        Assertions.assertNotNull(firstElementFromDb);
        Assertions.assertEquals(firstElementToInsert, firstElementFromDb);

        TimelineElementEntity secondElementFromDb =  timelineEntityDao.getTimelineElement(secondElementToInsert.getIun(), secondElementToInsert.getTimelineElementId()).block();
        Assertions.assertNotNull(secondElementFromDb);
        Assertions.assertEquals(secondElementToInsert, secondElementFromDb);
    }

    @Test
    void get() {

        //GIVEN
        TimelineElementEntity firstElementToInsert = TimelineElementEntity.builder()
                .iun("pa1-1")
                .timelineElementId("elementId1")
                .category(TimelineElementCategoryEntity.REQUEST_ACCEPTED)
                .details(TimelineElementDetailsEntity.builder()
                        .recIndex(0)
                        .build())
                .legalFactIds(
                        Collections.singletonList(
                                LegalFactsIdEntity.builder()
                                        .key("key")
                                        .category(LegalFactCategoryEntity.DIGITAL_DELIVERY)
                                        .build()
                        )
                )
                .build();

        TimelineElementEntity secondElementToInsert = TimelineElementEntity.builder()
                .iun("pa1-2")
                .timelineElementId("elementId1")
                .category(TimelineElementCategoryEntity.SEND_ANALOG_DOMICILE)
                .details(TimelineElementDetailsEntity.builder()
                        .recIndex(0)
                        .build())
                .legalFactIds(
                        Collections.singletonList(
                                LegalFactsIdEntity.builder()
                                        .key("key")
                                        .category(LegalFactCategoryEntity.DIGITAL_DELIVERY)
                                        .build()
                        )
                )
                .build();

        removeElementFromDb(firstElementToInsert);
        timelineEntityDao.putIfAbsent(firstElementToInsert);

        removeElementFromDb(secondElementToInsert);
        timelineEntityDao.putIfAbsent(secondElementToInsert);

        //Check first element
        //WHEN
        TimelineElementEntity firstElementFromDb =  timelineEntityDao.getTimelineElement(firstElementToInsert.getIun(),firstElementToInsert.getTimelineElementId()).block();

        //THEN
        Assertions.assertNotNull(firstElementFromDb);
        Assertions.assertEquals(firstElementToInsert, firstElementFromDb);

        //Check second element
        //WHEN
        TimelineElementEntity secondElementFromDb =  timelineEntityDao.getTimelineElement(secondElementToInsert.getIun(), secondElementToInsert.getTimelineElementId()).block();

        //THEN
        Assertions.assertNotNull(secondElementFromDb);
        Assertions.assertEquals(secondElementToInsert, secondElementFromDb);
    }

    @Test
    void getNoElement() {

        //GIVEN
        TimelineElementEntity element = TimelineElementEntity.builder()
                .iun("pa1-1")
                .timelineElementId("elementId1")
                .category(TimelineElementCategoryEntity.SEND_DIGITAL_DOMICILE)
                .details(TimelineElementDetailsEntity.builder()
                        .recIndex(0)
                        .build())
                .legalFactIds(
                        Collections.singletonList(
                                LegalFactsIdEntity.builder()
                                        .key("key")
                                        .category(LegalFactCategoryEntity.DIGITAL_DELIVERY)
                                        .build()
                        )
                )
                .build();

        Key elementToInsertKey = Key.builder()
                .partitionValue(element.getIun())
                .sortValue(element.getTimelineElementId())
                .build();

        removeElementFromDb(element);

        //Check first element
        //WHEN
        TimelineElementEntity firstElementFromDb =  timelineEntityDao.getTimelineElement(element.getIun(),element.getTimelineElementId()).block();

        //THEN
        Assertions.assertNull(firstElementFromDb);
    }

    @Test
    void delete() {
        //GIVEN
        TimelineElementEntity elementToInsert = TimelineElementEntity.builder()
                .iun("pa1-delete")
                .timelineElementId("elementId1")
                .category(TimelineElementCategoryEntity.PUBLIC_REGISTRY_CALL)
                .details(TimelineElementDetailsEntity.builder()
                        .recIndex(0)
                        .build())
                .legalFactIds(
                        Collections.singletonList(
                                LegalFactsIdEntity.builder()
                                        .key("key")
                                        .category(LegalFactCategoryEntity.DIGITAL_DELIVERY)
                                        .build()
                        )
                )
                .build();

        removeElementFromDb(elementToInsert);
        timelineEntityDao.putIfAbsent(elementToInsert);

        timelineEntityDao.deleteByIun(elementToInsert.getIun());

        //THEN
        TimelineElementEntity elementFromDb =  timelineEntityDao.getTimelineElement(elementToInsert.getIun(), elementToInsert.getTimelineElementId()).block();

        Assertions.assertNull(elementFromDb);
    }

    @Test
    void findByIun() {
        String iun = "pa1-1";

        // GIVEN
        TimelineElementEntity firstElementToInsert = TimelineElementEntity.builder()
                .iun(iun)
                .timelineElementId("elementId1")
                .category(TimelineElementCategoryEntity.REFINEMENT)
                .details(TimelineElementDetailsEntity.builder()
                        .recIndex(0)
                        .build())
                .legalFactIds(
                        Collections.singletonList(
                                LegalFactsIdEntity.builder()
                                        .key("key")
                                        .category(LegalFactCategoryEntity.DIGITAL_DELIVERY)
                                        .build()
                        )
                )
                .build();

        TimelineElementEntity secondElementToInsert = TimelineElementEntity.builder()
                .iun(iun)
                .timelineElementId("elementId2")
                .category(TimelineElementCategoryEntity.SEND_ANALOG_DOMICILE)
                .details(TimelineElementDetailsEntity.builder()
                        .recIndex(0)
                        .build())
                .legalFactIds(
                        Collections.singletonList(
                                LegalFactsIdEntity.builder()
                                        .key("key")
                                        .category(LegalFactCategoryEntity.DIGITAL_DELIVERY)
                                        .build()
                        )
                )
                .build();

        removeElementFromDb(firstElementToInsert);
        timelineEntityDao.putIfAbsent(firstElementToInsert);
        removeElementFromDb(secondElementToInsert);
        timelineEntityDao.putIfAbsent(secondElementToInsert);

        // WHEN & THEN
        StepVerifier.create(timelineEntityDao.findByIun(iun))
                .expectNextMatches(element -> element.equals(firstElementToInsert))
                .expectNextMatches(element -> element.equals(secondElementToInsert))
                .verifyComplete();
    }

    @Test
    void findByIunStrongly() {
        String iun = "pa1-1";

        // GIVEN
        TimelineElementEntity firstElementToInsert = TimelineElementEntity.builder()
                .iun(iun)
                .timelineElementId("elementId1")
                .category(TimelineElementCategoryEntity.REFINEMENT)
                .details(TimelineElementDetailsEntity.builder()
                        .recIndex(0)
                        .build())
                .legalFactIds(
                        Collections.singletonList(
                                LegalFactsIdEntity.builder()
                                        .key("key")
                                        .category(LegalFactCategoryEntity.DIGITAL_DELIVERY)
                                        .build()
                        )
                )
                .build();

        TimelineElementEntity secondElementToInsert = TimelineElementEntity.builder()
                .iun(iun)
                .timelineElementId("elementId2")
                .category(TimelineElementCategoryEntity.SEND_ANALOG_DOMICILE)
                .details(TimelineElementDetailsEntity.builder()
                        .recIndex(0)
                        .build())
                .legalFactIds(
                        Collections.singletonList(
                                LegalFactsIdEntity.builder()
                                        .key("key")
                                        .category(LegalFactCategoryEntity.DIGITAL_DELIVERY)
                                        .build()
                        )
                )
                .build();

        removeElementFromDb(firstElementToInsert);
        timelineEntityDao.putIfAbsent(firstElementToInsert);
        removeElementFromDb(secondElementToInsert);
        timelineEntityDao.putIfAbsent(secondElementToInsert);

        // WHEN & THEN
        StepVerifier.create(timelineEntityDao.findByIunStrongly(iun))
                .expectNextMatches(element -> element.equals(firstElementToInsert))
                .expectNextMatches(element -> element.equals(secondElementToInsert))
                .verifyComplete();
    }

    @Test
    void getTimelineElmentStrongly() {
        String iun = "pa1-1";
        String timelineElementIdToSearch = "elementId2";
        //GIVEN
        TimelineElementEntity firstElementToInsert = TimelineElementEntity.builder()
                .iun(iun)
                .timelineElementId("elementId1")
                .category(TimelineElementCategoryEntity.REFINEMENT)
                .details(TimelineElementDetailsEntity.builder()
                        .recIndex(0)
                        .build())
                .legalFactIds(
                        Collections.singletonList(
                                LegalFactsIdEntity.builder()
                                        .key("key")
                                        .category(LegalFactCategoryEntity.DIGITAL_DELIVERY)
                                        .build()
                        )
                )
                .build();

        TimelineElementEntity secondElementToInsert = TimelineElementEntity.builder()
                .iun(iun)
                .timelineElementId(timelineElementIdToSearch)
                .category(TimelineElementCategoryEntity.SEND_ANALOG_DOMICILE)
                .details(TimelineElementDetailsEntity.builder()
                        .recIndex(0)
                        .build())
                .legalFactIds(
                        Collections.singletonList(
                                LegalFactsIdEntity.builder()
                                        .key("key")
                                        .category(LegalFactCategoryEntity.DIGITAL_DELIVERY)
                                        .build()
                        )
                )
                .build();

        removeElementFromDb(firstElementToInsert);
        timelineEntityDao.putIfAbsent(firstElementToInsert);
        removeElementFromDb(secondElementToInsert);
        timelineEntityDao.putIfAbsent(secondElementToInsert);

        //WHEN
        Mono<TimelineElementEntity> timelineElmentStrongly = timelineEntityDao.getTimelineElementStrongly(iun, timelineElementIdToSearch);

        StepVerifier.create(timelineElmentStrongly)
                        .expectNextMatches(element -> element.equals(secondElementToInsert))
                        .verifyComplete();
    }

    @Test
    void findByIunNoElements() {
        String iun = "pa1-1";
        timelineEntityDao.deleteByIun(iun);

        // WHEN & THEN
        StepVerifier.create(timelineEntityDao.findByIun(iun))
                .expectComplete()
                .verify();
    }


    @Test
    void searchByIunAndElementId() {
        String iun = "pa1-1";
        String elementId = "elementId";

        // GIVEN
        TimelineElementEntity firstElementToInsert = TimelineElementEntity.builder()
                .iun(iun)
                .timelineElementId(elementId + "1")
                .category(TimelineElementCategoryEntity.REFINEMENT)
                .details(TimelineElementDetailsEntity.builder()
                        .recIndex(0)
                        .build())
                .legalFactIds(
                        Collections.singletonList(
                                LegalFactsIdEntity.builder()
                                        .key("key")
                                        .category(LegalFactCategoryEntity.DIGITAL_DELIVERY)
                                        .build()
                        )
                )
                .build();

        TimelineElementEntity secondElementToInsert = TimelineElementEntity.builder()
                .iun(iun)
                .timelineElementId(elementId + "2")
                .category(TimelineElementCategoryEntity.SEND_ANALOG_DOMICILE)
                .details(TimelineElementDetailsEntity.builder()
                        .recIndex(0)
                        .build())
                .legalFactIds(
                        Collections.singletonList(
                                LegalFactsIdEntity.builder()
                                        .key("key")
                                        .category(LegalFactCategoryEntity.DIGITAL_DELIVERY)
                                        .build()
                        )
                )
                .build();

        TimelineElementEntity nomatchElementToInsert = TimelineElementEntity.builder()
                .iun(iun)
                .timelineElementId("otherelement" + "1")
                .category(TimelineElementCategoryEntity.SEND_ANALOG_DOMICILE)
                .details(TimelineElementDetailsEntity.builder()
                        .recIndex(0)
                        .build())
                .legalFactIds(
                        Collections.singletonList(
                                LegalFactsIdEntity.builder()
                                        .key("key")
                                        .category(LegalFactCategoryEntity.DIGITAL_DELIVERY)
                                        .build()
                        )
                )
                .build();

        removeElementFromDb(firstElementToInsert);
        timelineEntityDao.putIfAbsent(firstElementToInsert);
        removeElementFromDb(secondElementToInsert);
        timelineEntityDao.putIfAbsent(secondElementToInsert);
        removeElementFromDb(nomatchElementToInsert);
        timelineEntityDao.putIfAbsent(nomatchElementToInsert);

        // WHEN & THEN
        StepVerifier.create(timelineEntityDao.searchByIunAndElementId(iun, elementId))
                .expectNextMatches(element -> element.equals(firstElementToInsert))
                .expectNextMatches(element -> element.equals(secondElementToInsert))
                .verifyComplete();
    }

    @Test
    void deleteByIun() {
        String iun = "pa1-1";

        // GIVEN
        TimelineElementEntity firstElementToInsert = TimelineElementEntity.builder()
                .iun(iun)
                .timelineElementId("elementId1")
                .category(TimelineElementCategoryEntity.PUBLIC_REGISTRY_CALL)
                .details(TimelineElementDetailsEntity.builder()
                        .recIndex(0)
                        .build())
                .legalFactIds(
                        Collections.singletonList(
                                LegalFactsIdEntity.builder()
                                        .key("key")
                                        .category(LegalFactCategoryEntity.DIGITAL_DELIVERY)
                                        .build()
                        )
                )
                .build();

        TimelineElementEntity secondElementToInsert = TimelineElementEntity.builder()
                .iun(iun)
                .timelineElementId("elementId2")
                .category(TimelineElementCategoryEntity.SEND_ANALOG_DOMICILE)
                .details(TimelineElementDetailsEntity.builder()
                        .recIndex(0)
                        .build())
                .legalFactIds(
                        Collections.singletonList(
                                LegalFactsIdEntity.builder()
                                        .key("key")
                                        .category(LegalFactCategoryEntity.DIGITAL_DELIVERY)
                                        .build()
                        )
                )
                .build();

        removeElementFromDb(firstElementToInsert);
        timelineEntityDao.putIfAbsent(firstElementToInsert);
        removeElementFromDb(secondElementToInsert);
        timelineEntityDao.putIfAbsent(secondElementToInsert);

        // Check elements are present
        StepVerifier.create(timelineEntityDao.findByIun(iun).collectList())
                .assertNext(elementSet -> {
                    Assertions.assertFalse(elementSet.isEmpty());
                    Assertions.assertTrue(elementSet.contains(firstElementToInsert));
                    Assertions.assertTrue(elementSet.contains(secondElementToInsert));
                })
                .verifyComplete();

        // WHEN
        StepVerifier.create(timelineEntityDao.deleteByIun(iun))
                .verifyComplete();

        // THEN
        // Check elements are not present
        StepVerifier.create(timelineEntityDao.findByIun(iun).collectList())
                .assertNext(elementSetAfterDelete -> Assertions.assertTrue(elementSetAfterDelete.isEmpty()))
                .verifyComplete();
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
        TimelineElementEntity elementToInsert = TimelineElementEntity.builder()
                .iun("pa1-1")
                .timelineElementId("elementId1")
                .paId("paid001")
                .legalFactIds(
                        Collections.singletonList(
                                LegalFactsIdEntity.builder()
                                        .category(LegalFactCategoryEntity.PEC_RECEIPT)
                                        .key("test")
                                        .build()
                        )
                )
                .category(TimelineElementCategoryEntity.SEND_DIGITAL_PROGRESS)
                .details(
                        TimelineElementDetailsEntity.builder()
                                .recIndex(0)
                                .digitalAddress(
                                        DigitalAddressEntity.builder()
                                                .type(DigitalAddressEntity.TypeEnum.PEC)
                                                .address("test@address.it")
                                                .build()
                                )
                                .digitalAddressSource(DigitalAddressSourceEntity.PLATFORM)
                                .retryNumber(0)
                                .notificationDate(Instant.now())
                                .sendingReceipts(
                                        Collections.singletonList(
                                                SendingReceiptEntity.builder()
                                                        .id("id")
                                                        .system("system")
                                                        .build()
                                        )
                                )
                                .refusalReasons(errors)
                                .build()
                )
                .build();

        try{
            //WHEN
            timelineEntityDao.putIfAbsent(elementToInsert);

            TimelineElementEntity elementFromDb =  timelineEntityDao.getTimelineElement(elementToInsert.getIun(),elementToInsert.getTimelineElementId()).block();

            Assertions.assertNotNull(elementFromDb);
            Assertions.assertEquals(elementToInsert, elementFromDb);

        } finally {
            removeElementFromDb(elementToInsert);
        }
    }

    @Test
    void checkNotificationView() {
        //GIVEN
        TimelineElementEntity elementToInsert = TimelineElementEntity.builder()
                .iun("pa1-1")
                .timelineElementId("elementId1")
                .paId("paid001")
                .timestamp(Instant.now())
                .category(TimelineElementCategoryEntity.NOTIFICATION_VIEWED)
                .details(
                        TimelineElementDetailsEntity.builder()
                                .recIndex(0)
                                .notificationCost(100)
                                .build()
                )
                .legalFactIds(
                        Collections.singletonList(
                                LegalFactsIdEntity.builder()
                                        .key("key")
                                        .category(LegalFactCategoryEntity.RECIPIENT_ACCESS)
                                        .build()
                        )
                )
                .build();

        checkElement(elementToInsert);
    }

    @Test
    void checkRefinement() {
        //GIVEN
        TimelineElementEntity elementToInsert = TimelineElementEntity.builder()
                .iun("pa1-1")
                .timelineElementId("elementId1")
                .paId("paid001")
                .timestamp(Instant.now())
                .category(TimelineElementCategoryEntity.REFINEMENT)
                .details(
                        TimelineElementDetailsEntity.builder()
                                .recIndex(0)
                                .notificationCost(100)
                                .build()
                )
                .build();

        checkElement(elementToInsert);
    }

    private void checkElement(TimelineElementEntity elementToInsert) {
        try{
            //WHEN
            timelineEntityDao.putIfAbsent(elementToInsert);

            TimelineElementEntity elementFromDb =  timelineEntityDao.getTimelineElement(elementToInsert.getIun(),elementToInsert.getTimelineElementId()).block();

            Assertions.assertNotNull(elementFromDb);
            Assertions.assertEquals(elementToInsert, elementFromDb);

        }finally {
            removeElementFromDb(elementToInsert);
        }
    }

    private void removeElementFromDb(TimelineElementEntity element) {
        timelineEntityDao.deleteByIun(element.getIun());
    }

}