/*
package it.pagopa.pn.timelineservice.middleware.timelinedao.dao.dynamo;

import it.pagopa.pn.timelineservice.config.PnTimelineServiceConfigs;
import it.pagopa.pn.timelineservice.dto.address.PhysicalAddressInt;
import it.pagopa.pn.timelineservice.dto.timeline.StatusInfoInternal;
import it.pagopa.pn.timelineservice.dto.timeline.TimelineElementInternal;
import it.pagopa.pn.timelineservice.dto.timeline.details.*;
import it.pagopa.pn.timelineservice.middleware.dao.dynamo.TimelineDaoDynamo;
import it.pagopa.pn.timelineservice.middleware.dao.dynamo.mapper.DtoToEntityTimelineMapper;
import it.pagopa.pn.timelineservice.middleware.dao.dynamo.mapper.EntityToDtoTimelineMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.test.StepVerifier;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbAsyncTable;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient;
import software.amazon.awssdk.enhanced.dynamodb.model.PutItemEnhancedRequest;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TimelineDaoDynamoTest {

    @Mock
    private DynamoDbEnhancedAsyncClient dynamoDbEnhancedAsyncClient;
    @Mock
    private DynamoDbAsyncTable<Object> table;

    private TimelineDaoDynamo dao;

    @Spy
    private DtoToEntityTimelineMapper dtoToEntityTimelineMapper;

    @Spy
    private EntityToDtoTimelineMapper entityToDtoTimelineMapper;

    @BeforeEach
    void setup() {
        when(dynamoDbEnhancedAsyncClient.table(any(), any())).thenReturn(table);
        PnTimelineServiceConfigs pnTimelineServiceConfigs = new PnTimelineServiceConfigs();
        PnTimelineServiceConfigs.TimelineDao timelineDao = new PnTimelineServiceConfigs.TimelineDao();
        timelineDao.setTableName("timeline");
        pnTimelineServiceConfigs.setTimelineDao(timelineDao);
        dao = new TimelineDaoDynamo(dynamoDbEnhancedAsyncClient, pnTimelineServiceConfigs, dtoToEntityTimelineMapper, entityToDtoTimelineMapper);
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
                .details(NotificationRequestAcceptedDetailsInt.builder().categoryType("REQUEST_ACCEPTED").build())
                .timestamp(Instant.now())
                .eventTimestamp(Instant.now())
                .statusInfo(StatusInfoInternal.builder().build())
                .build();
        String id2 = "SendDigitalDetails";
        TimelineElementInternal row2 = TimelineElementInternal.builder()
                .iun(iun)
                .elementId(id2)
                .category(TimelineElementCategoryInt.SEND_DIGITAL_DOMICILE)
                .details(SendDigitalDetailsInt.builder().build())
                .timestamp(Instant.now())
                .eventTimestamp(Instant.now())
                .statusInfo(StatusInfoInternal.builder().build())
                .build();

        when(table.putItem(any(PutItemEnhancedRequest.class))).thenReturn(CompletableFuture.completedFuture(null));
        // WHEN
        dao.addTimelineElementIfAbsent(row1).block();
        dao.addTimelineElementIfAbsent(row2).block();


        // THEN
        // check first row
        TimelineElementInternal retrievedRow1 = dao.getTimelineElement(iun, id1, false).block();
        Assertions.assertEquals(row1.getTimestamp(), retrievedRow1.getTimestamp());
        Assertions.assertEquals(row1.getIun(), retrievedRow1.getIun());
        Assertions.assertEquals(row1.getElementId(), retrievedRow1.getElementId());
        Assertions.assertEquals(row1.getCategory(), retrievedRow1.getCategory());
        Assertions.assertEquals(row1.getStatusInfo(), retrievedRow1.getStatusInfo());
        Assertions.assertEquals(row1.getNotificationSentAt(), retrievedRow1.getNotificationSentAt());
        Assertions.assertEquals(row1.getPaId(), retrievedRow1.getPaId());
        Assertions.assertEquals(row1.getLegalFactsIds(), retrievedRow1.getLegalFactsIds());
        Assertions.assertEquals(row1.getEventTimestamp(), retrievedRow1.getEventTimestamp());
        Assertions.assertEquals(row1.getIngestionTimestamp(), retrievedRow1.getIngestionTimestamp());
        Assertions.assertEquals(row1.getCategory().getDetailsJavaClass(), retrievedRow1.getCategory().getDetailsJavaClass());
        Assertions.assertEquals(row1.getCategory().getPriority(), retrievedRow1.getCategory().getPriority());

        // check second row
        TimelineElementInternal retrievedRow2 = dao.getTimelineElement(iun, id2, false).block();
        Assertions.assertEquals(row2.getTimestamp(), retrievedRow2.getTimestamp());
        Assertions.assertEquals(row2.getIun(), retrievedRow2.getIun());
        Assertions.assertEquals(row2.getElementId(), retrievedRow2.getElementId());
        Assertions.assertEquals(row2.getCategory(), retrievedRow2.getCategory());
        Assertions.assertEquals(row2.getStatusInfo(), retrievedRow2.getStatusInfo());
        Assertions.assertEquals(row2.getNotificationSentAt(), retrievedRow2.getNotificationSentAt());
        Assertions.assertEquals(row2.getPaId(), retrievedRow2.getPaId());
        Assertions.assertEquals(row2.getLegalFactsIds(), retrievedRow2.getLegalFactsIds());
        Assertions.assertEquals(row2.getEventTimestamp(), retrievedRow2.getEventTimestamp());
        Assertions.assertEquals(row2.getIngestionTimestamp(), retrievedRow2.getIngestionTimestamp());
        Assertions.assertEquals(row2.getCategory().getDetailsJavaClass(), retrievedRow2.getCategory().getDetailsJavaClass());
        Assertions.assertEquals(row2.getCategory().getPriority(), retrievedRow2.getCategory().getPriority());

        // check full retrieve
        List<TimelineElementInternal> result = dao.getTimeline(iun).collectList().block();
        Assertions.assertEquals(row1.getTimestamp(), result.getFirst().getTimestamp());
        Assertions.assertEquals(row1.getIun(), result.getFirst().getIun());
        Assertions.assertEquals(row1.getElementId(), result.getFirst().getElementId());
        Assertions.assertEquals(row1.getCategory(), result.getFirst().getCategory());
        Assertions.assertEquals(row1.getStatusInfo(), result.getFirst().getStatusInfo());
        Assertions.assertEquals(row1.getNotificationSentAt(), result.getFirst().getNotificationSentAt());
        Assertions.assertEquals(row1.getPaId(), result.getFirst().getPaId());
        Assertions.assertEquals(row1.getLegalFactsIds(), result.getFirst().getLegalFactsIds());
        Assertions.assertEquals(row1.getEventTimestamp(), result.getFirst().getEventTimestamp());
        Assertions.assertEquals(row1.getIngestionTimestamp(), result.getFirst().getIngestionTimestamp());
        Assertions.assertEquals(row1.getCategory().getDetailsJavaClass(), result.getFirst().getCategory().getDetailsJavaClass());
        Assertions.assertEquals(row1.getCategory().getPriority(), result.getFirst().getCategory().getPriority());

        Assertions.assertEquals(row2.getIun(), result.getLast().getIun());
        Assertions.assertEquals(row2.getElementId(), result.getLast().getElementId());
        Assertions.assertEquals(row2.getCategory(), result.getLast().getCategory());
        Assertions.assertEquals(row2.getStatusInfo(), result.getLast().getStatusInfo());
        Assertions.assertEquals(row2.getNotificationSentAt(), result.getLast().getNotificationSentAt());
        Assertions.assertEquals(row2.getPaId(), result.getLast().getPaId());
        Assertions.assertEquals(row2.getLegalFactsIds(), result.getLast().getLegalFactsIds());
        Assertions.assertEquals(row2.getEventTimestamp(), result.getLast().getEventTimestamp());
        Assertions.assertEquals(row2.getIngestionTimestamp(), result.getLast().getIngestionTimestamp());
        Assertions.assertEquals(row2.getCategory().getDetailsJavaClass(), result.getLast().getCategory().getDetailsJavaClass());
        Assertions.assertEquals(row2.getCategory().getPriority(), result.getLast().getCategory().getPriority());

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
        TimelineElementInternal retrievedRow1 = dao.getTimelineElement(iun, id1, true).block();
        Assertions.assertEquals(row1.getTimestamp(), retrievedRow1.getTimestamp());
        Assertions.assertEquals(row1.getIun(), retrievedRow1.getIun());
        Assertions.assertEquals(row1.getElementId(), retrievedRow1.getElementId());
        Assertions.assertEquals(row1.getCategory(), retrievedRow1.getCategory());
        Assertions.assertEquals(row1.getStatusInfo(), retrievedRow1.getStatusInfo());
        Assertions.assertEquals(row1.getNotificationSentAt(), retrievedRow1.getNotificationSentAt());
        Assertions.assertEquals(row1.getPaId(), retrievedRow1.getPaId());
        Assertions.assertEquals(row1.getLegalFactsIds(), retrievedRow1.getLegalFactsIds());
        Assertions.assertEquals(row1.getEventTimestamp(), retrievedRow1.getEventTimestamp());
        Assertions.assertEquals(row1.getIngestionTimestamp(), retrievedRow1.getIngestionTimestamp());
        Assertions.assertEquals(row1.getCategory().getDetailsJavaClass(), retrievedRow1.getCategory().getDetailsJavaClass());
        Assertions.assertEquals(row1.getCategory().getPriority(), retrievedRow1.getCategory().getPriority());

        // check second row
        TimelineElementInternal retrievedRow2 = dao.getTimelineElement(iun, id2, true).block();
        Assertions.assertEquals(row2.getTimestamp(), retrievedRow2.getTimestamp());
        Assertions.assertEquals(row2.getIun(), retrievedRow2.getIun());
        Assertions.assertEquals(row2.getElementId(), retrievedRow2.getElementId());
        Assertions.assertEquals(row2.getCategory(), retrievedRow2.getCategory());
        Assertions.assertEquals(row2.getStatusInfo(), retrievedRow2.getStatusInfo());
        Assertions.assertEquals(row2.getNotificationSentAt(), retrievedRow2.getNotificationSentAt());
        Assertions.assertEquals(row2.getPaId(), retrievedRow2.getPaId());
        Assertions.assertEquals(row2.getLegalFactsIds(), retrievedRow2.getLegalFactsIds());
        Assertions.assertEquals(row2.getEventTimestamp(), retrievedRow2.getEventTimestamp());
        Assertions.assertEquals(row2.getIngestionTimestamp(), retrievedRow2.getIngestionTimestamp());
        Assertions.assertEquals(row2.getCategory().getDetailsJavaClass(), retrievedRow2.getCategory().getDetailsJavaClass());
        Assertions.assertEquals(row2.getCategory().getPriority(), retrievedRow2.getCategory().getPriority());

        // check full retrieve
        List<TimelineElementInternal> result = dao.getTimeline(iun).collectList().block();
        Assertions.assertEquals(row1.getTimestamp(), result.getFirst().getTimestamp());
        Assertions.assertEquals(row1.getIun(), result.getFirst().getIun());
        Assertions.assertEquals(row1.getElementId(), result.getFirst().getElementId());
        Assertions.assertEquals(row1.getCategory(), result.getFirst().getCategory());
        Assertions.assertEquals(row1.getStatusInfo(), result.getFirst().getStatusInfo());
        Assertions.assertEquals(row1.getNotificationSentAt(), result.getFirst().getNotificationSentAt());
        Assertions.assertEquals(row1.getPaId(), result.getFirst().getPaId());
        Assertions.assertEquals(row1.getLegalFactsIds(), result.getFirst().getLegalFactsIds());
        Assertions.assertEquals(row1.getEventTimestamp(), result.getFirst().getEventTimestamp());
        Assertions.assertEquals(row1.getIngestionTimestamp(), result.getFirst().getIngestionTimestamp());
        Assertions.assertEquals(row1.getCategory().getDetailsJavaClass(), result.getFirst().getCategory().getDetailsJavaClass());
        Assertions.assertEquals(row1.getCategory().getPriority(), result.getFirst().getCategory().getPriority());

        Assertions.assertEquals(row2.getIun(), result.getLast().getIun());
        Assertions.assertEquals(row2.getElementId(), result.getLast().getElementId());
        Assertions.assertEquals(row2.getCategory(), result.getLast().getCategory());
        Assertions.assertEquals(row2.getStatusInfo(), result.getLast().getStatusInfo());
        Assertions.assertEquals(row2.getNotificationSentAt(), result.getLast().getNotificationSentAt());
        Assertions.assertEquals(row2.getPaId(), result.getLast().getPaId());
        Assertions.assertEquals(row2.getLegalFactsIds(), result.getLast().getLegalFactsIds());
        Assertions.assertEquals(row2.getEventTimestamp(), result.getLast().getEventTimestamp());
        Assertions.assertEquals(row2.getIngestionTimestamp(), result.getLast().getIngestionTimestamp());
        Assertions.assertEquals(row2.getCategory().getDetailsJavaClass(), result.getLast().getCategory().getDetailsJavaClass());
        Assertions.assertEquals(row2.getCategory().getPriority(), result.getLast().getCategory().getPriority());
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

        // WHEN
        dao.addTimelineElementIfAbsent(row1);
        dao.addTimelineElementIfAbsent(row2);

        List<TimelineElementInternal> result = dao.getTimelineFilteredByElementId(iun, idPrefix).collectList().block();
        Assertions.assertEquals(row2.getTimestamp(), result.getFirst().getTimestamp());
        Assertions.assertEquals(row2.getIun(), result.getFirst().getIun());
        Assertions.assertEquals(row2.getElementId(), result.getFirst().getElementId());
        Assertions.assertEquals(row2.getCategory(), result.getFirst().getCategory());
        Assertions.assertEquals(row2.getStatusInfo(), result.getFirst().getStatusInfo());
        Assertions.assertEquals(row2.getNotificationSentAt(), result.getFirst().getNotificationSentAt());
        Assertions.assertEquals(row2.getPaId(), result.getFirst().getPaId());
        Assertions.assertEquals(row2.getLegalFactsIds(), result.getFirst().getLegalFactsIds());
        Assertions.assertEquals(row2.getEventTimestamp(), result.getFirst().getEventTimestamp());
        Assertions.assertEquals(row2.getIngestionTimestamp(), result.getFirst().getIngestionTimestamp());
        Assertions.assertEquals(row2.getCategory().getDetailsJavaClass(), result.getFirst().getCategory().getDetailsJavaClass());
        Assertions.assertEquals(row2.getCategory().getPriority(), result.getFirst().getCategory().getPriority());

    }

}*/
