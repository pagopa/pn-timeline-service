package it.pagopa.pn.timelineservice.middleware.timelinedao.dao.dynamo;

import it.pagopa.pn.commons.exceptions.PnIdConflictException;
import it.pagopa.pn.timelineservice.dto.address.PhysicalAddressInt;
import it.pagopa.pn.timelineservice.dto.timeline.StatusInfoInternal;
import it.pagopa.pn.timelineservice.dto.timeline.TimelineElementInternal;
import it.pagopa.pn.timelineservice.dto.timeline.details.*;
import it.pagopa.pn.timelineservice.middleware.dao.TimelineDao;
import it.pagopa.pn.timelineservice.middleware.dao.TimelineEntityDao;
import it.pagopa.pn.timelineservice.middleware.dao.dynamo.TimelineDaoDynamo;
import it.pagopa.pn.timelineservice.middleware.dao.dynamo.entity.TimelineElementEntity;
import it.pagopa.pn.timelineservice.middleware.dao.dynamo.mapper.DtoToEntityTimelineMapper;
import it.pagopa.pn.timelineservice.middleware.dao.dynamo.mapper.EntityToDtoTimelineMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import software.amazon.awssdk.enhanced.dynamodb.Key;

import java.time.Instant;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

class TimelineDaoDynamoTest {

    private TimelineDao dao;

    @BeforeEach
    void setup() {

        DtoToEntityTimelineMapper dto2Entity = new DtoToEntityTimelineMapper();
        EntityToDtoTimelineMapper entity2dto = new EntityToDtoTimelineMapper();
        TimelineEntityDao entityDao = new TestMyTimelineEntityDao();

        dao = new TimelineDaoDynamo(entityDao, dto2Entity, entity2dto);
    }

    @ExtendWith(MockitoExtension.class)
    @Test
    void successfullyInsertAndRetrieve() {
        // GIVEN
        String iun = "202109-eb10750e-e876-4a5a-8762-c4348d679d35";

        String id1 = "sender_ack";
        TimelineElementInternal row1 = TimelineElementInternal.builder()
                .iun(iun)
                .elementId(id1)
                .category(TimelineElementCategoryInt.REQUEST_ACCEPTED)
                .details(NotificationRequestAcceptedDetailsInt.builder().build())
                .timestamp(Instant.now())
                .statusInfo(StatusInfoInternal.builder().build())
                .build();
        String id2 = "SendDigitalDetails";
        TimelineElementInternal row2 = TimelineElementInternal.builder()
                .iun(iun)
                .elementId(id2)
                .category(TimelineElementCategoryInt.SEND_DIGITAL_DOMICILE)
                .details(SendDigitalDetailsInt.builder().build())
                .timestamp(Instant.now())
                .statusInfo(StatusInfoInternal.builder().build())
                .build();

        // WHEN
        dao.addTimelineElementIfAbsent(row1);
        dao.addTimelineElementIfAbsent(row2);

        // THEN
        // check first row
        Mono<TimelineElementInternal> retrievedRow1 = dao.getTimelineElement(iun, id1, false);
        retrievedRow1
                .doOnNext(row -> Assertions.assertEquals(row1, row))
                .block();

        // check second row
        Mono<TimelineElementInternal> retrievedRow2 = dao.getTimelineElement(iun, id2, false);
        retrievedRow2
                .doOnNext(row -> Assertions.assertEquals(row2, row))
                .block();

        // check full retrieve
        Flux<TimelineElementInternal> result = dao.getTimeline(iun);
        result.collectList()
                .doOnNext(rows -> Assertions.assertEquals(Set.of(row1, row2), Set.copyOf(rows)))
                .block();
    }

    @ExtendWith(MockitoExtension.class)
    @Test
    void successfullyInsertAndRetrieveWithTimestamps() {
        // GIVEN
        String iun = "202109-eb10750e-e876-4a5a-8762-c4348d679d35";

        String id1 = "sender_ack";
        TimelineElementInternal row1 = TimelineElementInternal.builder()
                .iun(iun)
                .elementId(id1)
                .category(TimelineElementCategoryInt.REQUEST_ACCEPTED)
                .details(NotificationRequestAcceptedDetailsInt.builder().build())
                .timestamp(Instant.now())
                .eventTimestamp(Instant.now())
                .statusInfo(StatusInfoInternal.builder().build())
                .build();

        // WHEN
        dao.addTimelineElementIfAbsent(row1);

        // THEN
        // check first row
        Mono<TimelineElementInternal> retrievedRow1 = dao.getTimelineElement(iun, id1, false);
        retrievedRow1
                .doOnNext(row -> Assertions.assertEquals(row1, row))
                .block();
    }

    @ExtendWith(MockitoExtension.class)
    @Test
    void successfullyInsertAndRetrieveStrongly() {
        // GIVEN
        String iun = "202109-eb10750e-e876-4a5a-8762-c4348d679d35";

        String id1 = "sender_ack";
        TimelineElementInternal row1 = TimelineElementInternal.builder()
                .iun(iun)
                .elementId(id1)
                .category(TimelineElementCategoryInt.REQUEST_ACCEPTED)
                .details(NotificationRequestAcceptedDetailsInt.builder().build())
                .timestamp(Instant.now())
                .statusInfo(StatusInfoInternal.builder().build())
                .build();
        String id2 = "SendDigitalDetails";
        TimelineElementInternal row2 = TimelineElementInternal.builder()
                .iun(iun)
                .elementId(id2)
                .category(TimelineElementCategoryInt.SEND_DIGITAL_DOMICILE)
                .details(SendDigitalDetailsInt.builder().build())
                .timestamp(Instant.now())
                .statusInfo(StatusInfoInternal.builder().build())
                .build();

        // WHEN
        dao.addTimelineElementIfAbsent(row1);
        dao.addTimelineElementIfAbsent(row2);

        // THEN
        // check first row
        Mono<TimelineElementInternal> retrievedRow1 = dao.getTimelineElement(iun, id1, false);
        retrievedRow1
                .doOnNext(row -> Assertions.assertEquals(row1, row))
                .block();

        // check second row
        Mono<TimelineElementInternal> retrievedRow2 = dao.getTimelineElement(iun, id2, false);
        retrievedRow2
                .doOnNext(row -> Assertions.assertEquals(row2, row))
                .block();

        // check full retrieve
        Flux<TimelineElementInternal> result = dao.getTimelineStrongly(iun);
        result.collectList()
                .doOnNext(rows -> Assertions.assertEquals(Set.of(row1, row2), Set.copyOf(rows)))
                .block();
    }

    @ExtendWith(MockitoExtension.class)
    @Test
    void successfullyInsertAndRetrieveWithPhysicalAddress() {
        // GIVEN
        String iun = "202109-eb10750e-e876-4a5a-8762-c4348d679d35";

        String id1 = "sender_ack";
        TimelineElementInternal row1 = TimelineElementInternal.builder()
                .iun(iun)
                .elementId(id1)
                .category(TimelineElementCategoryInt.SEND_SIMPLE_REGISTERED_LETTER)
                .details(SimpleRegisteredLetterDetailsInt.builder()
                        .physicalAddress(PhysicalAddressInt.builder()
                                .foreignState("IT")
                                .zip("12345")
                                .address("via esempio 123")
                                .municipalityDetails("municipalityDetails")
                                .municipality("roma")
                                .province("RM")
                                .at("at")
                                .build())
                        .build())
                .timestamp(Instant.now())
                .statusInfo(StatusInfoInternal.builder().build())
                .build();
        String id2 = "SendDigitalDetails";
        TimelineElementInternal row2 = TimelineElementInternal.builder()
                .iun(iun)
                .elementId(id2)
                .category(TimelineElementCategoryInt.SEND_ANALOG_DOMICILE)
                .details(SendAnalogDetailsInt.builder()
                        .physicalAddress(PhysicalAddressInt.builder()
                                .foreignState("IT")
                                .zip("12345")
                                .address("via esempio 123")
                                .municipalityDetails("municipalityDetails")
                                .municipality("roma")
                                .province("RM")
                                .at("at")
                                .build())
                        .build())
                .timestamp(Instant.now())
                .statusInfo(StatusInfoInternal.builder().build())
                .build();

        // WHEN
        dao.addTimelineElementIfAbsent(row1);
        dao.addTimelineElementIfAbsent(row2);

        // THEN
        // check first row
        StepVerifier.create(dao.getTimelineElement(iun, id1, false))
                .assertNext(row -> {
                    Assertions.assertEquals(((SimpleRegisteredLetterDetailsInt) row1.getDetails()).getPhysicalAddress().getForeignState(),
                            ((SimpleRegisteredLetterDetailsInt) row.getDetails()).getPhysicalAddress().getForeignState());
                    Assertions.assertEquals(((SimpleRegisteredLetterDetailsInt) row1.getDetails()).getPhysicalAddress().getZip(),
                            ((SimpleRegisteredLetterDetailsInt) row.getDetails()).getPhysicalAddress().getZip());
                    Assertions.assertNull(((SimpleRegisteredLetterDetailsInt) row.getDetails()).getPhysicalAddress().getAddress());
                    Assertions.assertNull(((SimpleRegisteredLetterDetailsInt) row.getDetails()).getPhysicalAddress().getMunicipality());
                    Assertions.assertNull(((SimpleRegisteredLetterDetailsInt) row.getDetails()).getPhysicalAddress().getMunicipalityDetails());
                    Assertions.assertNull(((SimpleRegisteredLetterDetailsInt) row.getDetails()).getPhysicalAddress().getProvince());
                    Assertions.assertNull(((SimpleRegisteredLetterDetailsInt) row.getDetails()).getPhysicalAddress().getAt());
                })
                .verifyComplete();

        // check second row
        StepVerifier.create(dao.getTimelineElement(iun, id2, false))
                .assertNext(row -> {
                    Assertions.assertEquals(((SendAnalogDetailsInt) row2.getDetails()).getPhysicalAddress().getForeignState(),
                            ((SendAnalogDetailsInt) row.getDetails()).getPhysicalAddress().getForeignState());
                    Assertions.assertEquals(((SendAnalogDetailsInt) row2.getDetails()).getPhysicalAddress().getZip(),
                            ((SendAnalogDetailsInt) row.getDetails()).getPhysicalAddress().getZip());
                    Assertions.assertNull(((SendAnalogDetailsInt) row.getDetails()).getPhysicalAddress().getAddress());
                    Assertions.assertNull(((SendAnalogDetailsInt) row.getDetails()).getPhysicalAddress().getMunicipality());
                    Assertions.assertNull(((SendAnalogDetailsInt) row.getDetails()).getPhysicalAddress().getMunicipalityDetails());
                    Assertions.assertNull(((SendAnalogDetailsInt) row.getDetails()).getPhysicalAddress().getProvince());
                    Assertions.assertNull(((SendAnalogDetailsInt) row.getDetails()).getPhysicalAddress().getAt());
                })
                .verifyComplete();
    }

    @ExtendWith(MockitoExtension.class)
    @Test
    void successfullyInsertAndRetrieveSearch() {
        // GIVEN
        String iun = "202109-eb10750e-e876-4a5a-8762-c4348d679d35";
        String idPrefix = "SendDigitalDetails_";

        String id1 = "sender_ack";
        TimelineElementInternal row1 = TimelineElementInternal.builder()
                .iun(iun)
                .elementId(id1)
                .category(TimelineElementCategoryInt.REQUEST_ACCEPTED)
                .details(NotificationRequestAcceptedDetailsInt.builder().build())
                .timestamp(Instant.now())
                .statusInfo(StatusInfoInternal.builder().build())
                .notificationSentAt(Instant.now())
                .build();
        String id2 = idPrefix + "1";
        TimelineElementInternal row2 = TimelineElementInternal.builder()
                .iun(iun)
                .elementId(id2)
                .category(TimelineElementCategoryInt.SEND_DIGITAL_DOMICILE)
                .details(SendDigitalDetailsInt.builder().build())
                .timestamp(Instant.now())
                .statusInfo(StatusInfoInternal.builder().build())
                .notificationSentAt(Instant.now())
                .build();
        String id3 = idPrefix + "2";
        TimelineElementInternal row3 = TimelineElementInternal.builder()
                .iun(iun)
                .elementId(id3)
                .category(TimelineElementCategoryInt.SEND_DIGITAL_DOMICILE)
                .details(SendDigitalDetailsInt.builder().build())
                .timestamp(Instant.now())
                .statusInfo(StatusInfoInternal.builder().build())
                .notificationSentAt(Instant.now())
                .build();

        // WHEN
        dao.addTimelineElementIfAbsent(row1);
        dao.addTimelineElementIfAbsent(row2);
        dao.addTimelineElementIfAbsent(row3);

        // THEN
        StepVerifier.create(dao.getTimelineFilteredByElementId(iun, idPrefix))
                .expectNextMatches(row -> row.equals(row2) || row.equals(row3))
                .expectNextMatches(row -> row.equals(row2) || row.equals(row3))
                .verifyComplete();
    }

    @ExtendWith(MockitoExtension.class)
    @Test
    void successfullyDelete() {
        // GIVEN
        String iun = "iun1";

        StatusInfoInternal statusInfo = Mockito.mock(StatusInfoInternal.class);

        String id1 = "sender_ack";
        TimelineElementInternal row1 = TimelineElementInternal.builder()
                .iun(iun)
                .elementId(id1)
                .category(TimelineElementCategoryInt.REQUEST_ACCEPTED)
                .details(NotificationRequestAcceptedDetailsInt.builder().build())
                .timestamp(Instant.now())
                .statusInfo(statusInfo)
                .notificationSentAt(Instant.now())
                .build();
        String id2 = "SendDigitalDetails";
        TimelineElementInternal row2 = TimelineElementInternal.builder()
                .iun(iun)
                .elementId(id2)
                .category(TimelineElementCategoryInt.SEND_DIGITAL_DOMICILE)
                .details(SendDigitalDetailsInt.builder().build())
                .timestamp(Instant.now())
                .statusInfo(statusInfo)
                .notificationSentAt(Instant.now())
                .build();

        // WHEN
        dao.addTimelineElementIfAbsent(row1);
        dao.addTimelineElementIfAbsent(row2);

        // THEN
        StepVerifier.create(dao.deleteTimeline(iun))
                .verifyComplete();

        StepVerifier.create(dao.getTimeline(iun))
                .expectNextCount(0)
                .verifyComplete();
    }

    private static class TestMyTimelineEntityDao implements TimelineEntityDao {

        private final Map<Key, TimelineElementEntity> store = new ConcurrentHashMap<>();

        @Override
        public Mono<Void> putIfAbsent(TimelineElementEntity timelineElementEntity) throws PnIdConflictException {
            Key key = Key.builder()
                    .partitionValue(timelineElementEntity.getIun())
                    .sortValue(timelineElementEntity.getTimelineElementId())
                    .build();

            if (this.store.put(key, timelineElementEntity) != null) {
                throw new PnIdConflictException(Collections.singletonMap("errorKey", key.toString()));
            }
            return Mono.empty();
        }

        public void delete(Key key) {
            store.remove(key);
        }


        @Override
        public Flux<TimelineElementEntity> findByIun(String iun) {
            return Flux.fromStream(this.store.values().stream()
                    .filter(el -> iun.equals(el.getIun())));
        }

        @Override
        public Flux<TimelineElementEntity> findByIunStrongly(String iun) {
            return Flux.fromStream(this.store.values().stream()
                    .filter(el -> iun.equals(el.getIun())));
        }

        @Override
        public Mono<TimelineElementEntity> getTimelineElementStrongly(String iun, String timelineId) {
            return Flux.fromStream(this.store.values().stream()
                    .filter(el -> iun.equals(el.getIun()) && el.getTimelineElementId().startsWith(timelineId)))
                    .next();
        }

        @Override
        public Mono<TimelineElementEntity> getTimelineElement(String iun, String timelineId) {
            return Flux.fromStream(this.store.values().stream()
                    .filter(el -> iun.equals(el.getIun()) && el.getTimelineElementId().startsWith(timelineId)))
                    .next();
        }

        @Override
        public Flux<TimelineElementEntity> searchByIunAndElementId(String iun, String elementId) {
            return Flux.fromStream(this.store.values().stream()
                    .filter(el -> iun.equals(el.getIun()) && el.getTimelineElementId().startsWith(elementId)));
        }

        @Override
        public Mono<Void> deleteByIun(String iun) {
            return findByIunStrongly(iun)
                    .flatMap(entity -> {
                        Key key = Key.builder()
                                .partitionValue(entity.getIun())
                                .sortValue(entity.getTimelineElementId())
                                .build();
                        delete(key);
                        return Mono.empty();
                    })
                    .then();
        }
    }

}