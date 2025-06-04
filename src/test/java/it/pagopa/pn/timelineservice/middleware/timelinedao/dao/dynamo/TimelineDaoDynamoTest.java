package it.pagopa.pn.timelineservice.middleware.timelinedao.dao.dynamo;

import it.pagopa.pn.commons.exceptions.PnIdConflictException;
import it.pagopa.pn.timelineservice.config.PnTimelineServiceConfigs;
import it.pagopa.pn.timelineservice.dto.address.PhysicalAddressInt;
import it.pagopa.pn.timelineservice.dto.timeline.StatusInfoInternal;
import it.pagopa.pn.timelineservice.dto.timeline.TimelineElementInternal;
import it.pagopa.pn.timelineservice.dto.timeline.details.*;
import it.pagopa.pn.timelineservice.middleware.dao.dynamo.TimelineDaoDynamo;
import it.pagopa.pn.timelineservice.middleware.dao.dynamo.entity.StatusInfoEntity;
import it.pagopa.pn.timelineservice.middleware.dao.dynamo.entity.TimelineElementCategoryEntity;
import it.pagopa.pn.timelineservice.middleware.dao.dynamo.entity.TimelineElementDetailsEntity;
import it.pagopa.pn.timelineservice.middleware.dao.dynamo.entity.TimelineElementEntity;
import it.pagopa.pn.timelineservice.middleware.dao.dynamo.mapper.DtoToEntityTimelineMapper;
import it.pagopa.pn.timelineservice.middleware.dao.dynamo.mapper.EntityToDtoTimelineMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import software.amazon.awssdk.core.async.SdkPublisher;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbAsyncTable;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient;
import software.amazon.awssdk.enhanced.dynamodb.model.*;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

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

    @Test
    void getTimelineElementTest() {
        String iun = "202109-eb10750e-e876-4a5a-8762-c4348d679d35";

        String id1 = "sender_ack";
        TimelineElementEntity row1 = TimelineElementEntity.builder()
                .iun(iun)
                .timelineElementId(id1)
                .category(TimelineElementCategoryEntity.REQUEST_ACCEPTED)
                .details(TimelineElementDetailsEntity.builder().recIndex(0).build())
                .timestamp(Instant.now())
                .businessTimestamp(Instant.now().minus(1, ChronoUnit.HOURS))
                .statusInfo(StatusInfoEntity.builder().build())
                .build();
        String id2 = "SendDigitalDetails";
        TimelineElementEntity row2 = TimelineElementEntity.builder()
                .iun(iun)
                .timelineElementId(id2)
                .category(TimelineElementCategoryEntity.SEND_DIGITAL_DOMICILE)
                .details(TimelineElementDetailsEntity.builder().recIndex(0).build())
                .timestamp(Instant.now())
                .businessTimestamp(Instant.now().minus(1, ChronoUnit.HOURS))
                .statusInfo(StatusInfoEntity.builder().build())
                .build();

        when(table.getItem(any(GetItemEnhancedRequest.class)))
                .thenReturn(CompletableFuture.completedFuture(row1))
                .thenReturn(CompletableFuture.completedFuture(row2));

        TimelineElementInternal retrievedRow1 = dao.getTimelineElement(iun, id1, false).block();
        Assertions.assertNotNull(retrievedRow1);
        Assertions.assertEquals(row1.getIun(), retrievedRow1.getIun());
        Assertions.assertEquals(row1.getTimelineElementId(), retrievedRow1.getElementId());
        Assertions.assertEquals(row1.getCategory().name(), retrievedRow1.getCategory().name());
        Assertions.assertEquals(row1.getStatusInfo().isStatusChanged(), retrievedRow1.getStatusInfo().isStatusChanged());
        Assertions.assertEquals(row1.getNotificationSentAt(), retrievedRow1.getNotificationSentAt());
        Assertions.assertEquals(row1.getPaId(), retrievedRow1.getPaId());
        Assertions.assertEquals(row1.getTimestamp(), retrievedRow1.getTimestamp());
        Assertions.assertEquals(row1.getBusinessTimestamp(), retrievedRow1.getEventTimestamp());
        Assertions.assertInstanceOf(NotificationRequestAcceptedDetailsInt.class, retrievedRow1.getDetails());

        TimelineElementInternal retrievedRow2 = dao.getTimelineElement(iun, id2, false).block();
        Assertions.assertNotNull(retrievedRow2);
        Assertions.assertEquals(row2.getIun(), retrievedRow2.getIun());
        Assertions.assertEquals(row2.getTimelineElementId(), retrievedRow2.getElementId());
        Assertions.assertEquals(row2.getCategory().name(), retrievedRow2.getCategory().name());
        Assertions.assertEquals(row2.getStatusInfo().isStatusChanged(), retrievedRow2.getStatusInfo().isStatusChanged());
        Assertions.assertEquals(row2.getNotificationSentAt(), retrievedRow2.getNotificationSentAt());
        Assertions.assertEquals(row2.getPaId(), retrievedRow2.getPaId());
        Assertions.assertEquals(row2.getTimestamp(), retrievedRow2.getTimestamp());
        Assertions.assertEquals(row2.getBusinessTimestamp(), retrievedRow2.getEventTimestamp());
        Assertions.assertInstanceOf(SendDigitalDetailsInt.class, retrievedRow2.getDetails());

    }

    @Test
    void getTimelineElementNotFoundTest() {
        when(table.getItem(any(GetItemEnhancedRequest.class)))
                .thenReturn(CompletableFuture.completedFuture(null));

        TimelineElementInternal retrievedRow1 = dao.getTimelineElement("iun", "id1", false).block();
        Assertions.assertNull(retrievedRow1);
    }

    @Test
    void getTimelineElementStronglyTest() {
        String iun = "202109-eb10750e-e876-4a5a-8762-c4348d679d35";

        String id1 = "sender_ack";
        TimelineElementEntity row1 = TimelineElementEntity.builder()
                .iun(iun)
                .timelineElementId(id1)
                .category(TimelineElementCategoryEntity.REQUEST_ACCEPTED)
                .details(TimelineElementDetailsEntity.builder().recIndex(0).build())
                .timestamp(Instant.now())
                .businessTimestamp(Instant.now().minus(1, ChronoUnit.HOURS))
                .statusInfo(StatusInfoEntity.builder().build())
                .build();

        when(table.getItem(any(GetItemEnhancedRequest.class)))
                .thenReturn(CompletableFuture.completedFuture(row1));

        TimelineElementInternal retrievedRow1 = dao.getTimelineElement(iun, id1, true).block();
        Assertions.assertNotNull(retrievedRow1);
        Assertions.assertEquals(row1.getIun(), retrievedRow1.getIun());
        Assertions.assertEquals(row1.getTimelineElementId(), retrievedRow1.getElementId());
        Assertions.assertEquals(row1.getCategory().name(), retrievedRow1.getCategory().name());
        Assertions.assertEquals(row1.getStatusInfo().isStatusChanged(), retrievedRow1.getStatusInfo().isStatusChanged());
        Assertions.assertEquals(row1.getNotificationSentAt(), retrievedRow1.getNotificationSentAt());
        Assertions.assertEquals(row1.getPaId(), retrievedRow1.getPaId());
        Assertions.assertEquals(row1.getTimestamp(), retrievedRow1.getTimestamp());
        Assertions.assertEquals(row1.getBusinessTimestamp(), retrievedRow1.getEventTimestamp());
        Assertions.assertInstanceOf(NotificationRequestAcceptedDetailsInt.class, retrievedRow1.getDetails());
    }

    @Test
    void getTimelineElementStronglyNotFoundTest() {
        when(table.getItem(any(GetItemEnhancedRequest.class)))
                .thenReturn(CompletableFuture.completedFuture(null));

        TimelineElementInternal retrievedRow1 = dao.getTimelineElement("iun", "id1", true).block();
        Assertions.assertNull(retrievedRow1);
    }

    @Test
    void getTimelineFilteredByElementIdTest() {
        String iun = "202109-eb10750e-e876-4a5a-8762-c4348d679d35";

        String id1 = "element_1";
        TimelineElementEntity row1 = TimelineElementEntity.builder()
                .iun(iun)
                .timelineElementId(id1)
                .category(TimelineElementCategoryEntity.REQUEST_ACCEPTED)
                .details(TimelineElementDetailsEntity.builder().recIndex(0).build())
                .timestamp(Instant.now())
                .businessTimestamp(Instant.now().minus(1, ChronoUnit.HOURS))
                .statusInfo(StatusInfoEntity.builder().build())
                .build();
        String id2 = "element_2";
        TimelineElementEntity row2 = TimelineElementEntity.builder()
                .iun(iun)
                .timelineElementId(id2)
                .category(TimelineElementCategoryEntity.SEND_DIGITAL_DOMICILE)
                .details(TimelineElementDetailsEntity.builder().recIndex(0).build())
                .timestamp(Instant.now())
                .businessTimestamp(Instant.now().minus(1, ChronoUnit.HOURS))
                .statusInfo(StatusInfoEntity.builder().build())
                .build();

        mockQueryConditional(table, List.of(row1, row2));

        List<TimelineElementInternal> result = dao.getTimelineFilteredByElementId(iun, "element_").collectList().block();
        Assertions.assertNotNull(result);

        Assertions.assertEquals(row1.getIun(), result.getFirst().getIun());
        Assertions.assertEquals(row1.getTimelineElementId(), result.getFirst().getElementId());
        Assertions.assertEquals(row1.getCategory().name(), result.getFirst().getCategory().name());
        Assertions.assertEquals(row1.getStatusInfo().isStatusChanged(), result.getFirst().getStatusInfo().isStatusChanged());
        Assertions.assertEquals(row1.getNotificationSentAt(), result.getFirst().getNotificationSentAt());
        Assertions.assertEquals(row1.getPaId(), result.getFirst().getPaId());
        Assertions.assertEquals(row1.getTimestamp(), result.getFirst().getTimestamp());
        Assertions.assertEquals(row1.getBusinessTimestamp(), result.getFirst().getEventTimestamp());
        Assertions.assertInstanceOf(NotificationRequestAcceptedDetailsInt.class, result.getFirst().getDetails());

        Assertions.assertEquals(row2.getIun(), result.getLast().getIun());
        Assertions.assertEquals(row2.getTimelineElementId(), result.getLast().getElementId());
        Assertions.assertEquals(row2.getCategory().name(), result.getLast().getCategory().name());
        Assertions.assertEquals(row2.getStatusInfo().isStatusChanged(), result.getLast().getStatusInfo().isStatusChanged());
        Assertions.assertEquals(row2.getNotificationSentAt(), result.getLast().getNotificationSentAt());
        Assertions.assertEquals(row2.getPaId(), result.getLast().getPaId());
        Assertions.assertEquals(row2.getTimestamp(), result.getLast().getTimestamp());
        Assertions.assertEquals(row2.getBusinessTimestamp(), result.getLast().getEventTimestamp());
        Assertions.assertInstanceOf(SendDigitalDetailsInt.class, result.getLast().getDetails());
    }

    @Test
    void getTimelineFilteredByElementIdNotFound() {

        mockQueryConditional(table, List.of());

        List<TimelineElementInternal> result = dao.getTimelineFilteredByElementId("iun", "element_")
                .collectList()
                .block();

        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    void getTimelineTest() {
        String iun = "202109-eb10750e-e876-4a5a-8762-c4348d679d35";

        String id1 = "element_1";
        TimelineElementEntity row1 = TimelineElementEntity.builder()
                .iun(iun)
                .timelineElementId(id1)
                .category(TimelineElementCategoryEntity.REQUEST_ACCEPTED)
                .details(TimelineElementDetailsEntity.builder().recIndex(0).build())
                .timestamp(Instant.now())
                .businessTimestamp(Instant.now().minus(1, ChronoUnit.HOURS))
                .statusInfo(StatusInfoEntity.builder().build())
                .build();
        String id2 = "element_2";
        TimelineElementEntity row2 = TimelineElementEntity.builder()
                .iun(iun)
                .timelineElementId(id2)
                .category(TimelineElementCategoryEntity.SEND_DIGITAL_DOMICILE)
                .details(TimelineElementDetailsEntity.builder().recIndex(0).build())
                .timestamp(Instant.now())
                .businessTimestamp(Instant.now().minus(1, ChronoUnit.HOURS))
                .statusInfo(StatusInfoEntity.builder().build())
                .build();

        mockQueryEnahncedRequest(table, List.of(row1, row2));

        List<TimelineElementInternal> result = dao.getTimeline(iun).collectList().block();
        Assertions.assertNotNull(result);

        Assertions.assertEquals(row1.getIun(), result.getFirst().getIun());
        Assertions.assertEquals(row1.getTimelineElementId(), result.getFirst().getElementId());
        Assertions.assertEquals(row1.getCategory().name(), result.getFirst().getCategory().name());
        Assertions.assertEquals(row1.getStatusInfo().isStatusChanged(), result.getFirst().getStatusInfo().isStatusChanged());
        Assertions.assertEquals(row1.getNotificationSentAt(), result.getFirst().getNotificationSentAt());
        Assertions.assertEquals(row1.getPaId(), result.getFirst().getPaId());
        Assertions.assertEquals(row1.getTimestamp(), result.getFirst().getTimestamp());
        Assertions.assertEquals(row1.getBusinessTimestamp(), result.getFirst().getEventTimestamp());
        Assertions.assertInstanceOf(NotificationRequestAcceptedDetailsInt.class, result.getFirst().getDetails());

        Assertions.assertEquals(row2.getIun(), result.getLast().getIun());
        Assertions.assertEquals(row2.getTimelineElementId(), result.getLast().getElementId());
        Assertions.assertEquals(row2.getCategory().name(), result.getLast().getCategory().name());
        Assertions.assertEquals(row2.getStatusInfo().isStatusChanged(), result.getLast().getStatusInfo().isStatusChanged());
        Assertions.assertEquals(row2.getNotificationSentAt(), result.getLast().getNotificationSentAt());
        Assertions.assertEquals(row2.getPaId(), result.getLast().getPaId());
        Assertions.assertEquals(row2.getTimestamp(), result.getLast().getTimestamp());
        Assertions.assertEquals(row2.getBusinessTimestamp(), result.getLast().getEventTimestamp());
        Assertions.assertInstanceOf(SendDigitalDetailsInt.class, result.getLast().getDetails());
    }

    @Test
    void getTimelineNotFoundTest() {
        mockQueryEnahncedRequest(table, List.of());
        List<TimelineElementInternal> result = dao.getTimeline("iun").collectList().block();
        Assertions.assertNotNull(result);
    }

    @Test
    void getTimelineStronglyTest() {
        String iun = "202109-eb10750e-e876-4a5a-8762-c4348d679d35";

        String id1 = "element_1";
        TimelineElementEntity row1 = TimelineElementEntity.builder()
                .iun(iun)
                .timelineElementId(id1)
                .category(TimelineElementCategoryEntity.REQUEST_ACCEPTED)
                .details(TimelineElementDetailsEntity.builder().recIndex(0).build())
                .timestamp(Instant.now())
                .businessTimestamp(Instant.now().minus(1, ChronoUnit.HOURS))
                .statusInfo(StatusInfoEntity.builder().build())
                .build();
        String id2 = "element_2";
        TimelineElementEntity row2 = TimelineElementEntity.builder()
                .iun(iun)
                .timelineElementId(id2)
                .category(TimelineElementCategoryEntity.SEND_DIGITAL_DOMICILE)
                .details(TimelineElementDetailsEntity.builder().recIndex(0).build())
                .timestamp(Instant.now())
                .businessTimestamp(Instant.now().minus(1, ChronoUnit.HOURS))
                .statusInfo(StatusInfoEntity.builder().build())
                .build();

        mockQueryEnahncedRequest(table, List.of(row1, row2));

        List<TimelineElementInternal> result = dao.getTimelineStrongly(iun).collectList().block();
        Assertions.assertNotNull(result);

        Assertions.assertEquals(row1.getIun(), result.getFirst().getIun());
        Assertions.assertEquals(row1.getTimelineElementId(), result.getFirst().getElementId());
        Assertions.assertEquals(row1.getCategory().name(), result.getFirst().getCategory().name());
        Assertions.assertEquals(row1.getStatusInfo().isStatusChanged(), result.getFirst().getStatusInfo().isStatusChanged());
        Assertions.assertEquals(row1.getNotificationSentAt(), result.getFirst().getNotificationSentAt());
        Assertions.assertEquals(row1.getPaId(), result.getFirst().getPaId());
        Assertions.assertEquals(row1.getTimestamp(), result.getFirst().getTimestamp());
        Assertions.assertEquals(row1.getBusinessTimestamp(), result.getFirst().getEventTimestamp());
        Assertions.assertInstanceOf(NotificationRequestAcceptedDetailsInt.class, result.getFirst().getDetails());

        Assertions.assertEquals(row2.getIun(), result.getLast().getIun());
        Assertions.assertEquals(row2.getTimelineElementId(), result.getLast().getElementId());
        Assertions.assertEquals(row2.getCategory().name(), result.getLast().getCategory().name());
        Assertions.assertEquals(row2.getStatusInfo().isStatusChanged(), result.getLast().getStatusInfo().isStatusChanged());
        Assertions.assertEquals(row2.getNotificationSentAt(), result.getLast().getNotificationSentAt());
        Assertions.assertEquals(row2.getPaId(), result.getLast().getPaId());
        Assertions.assertEquals(row2.getTimestamp(), result.getLast().getTimestamp());
        Assertions.assertEquals(row2.getBusinessTimestamp(), result.getLast().getEventTimestamp());
        Assertions.assertInstanceOf(SendDigitalDetailsInt.class, result.getLast().getDetails());
    }

    @Test
    void getTimelineStronglyNotFoundTest() {
        mockQueryEnahncedRequest(table, List.of());
        List<TimelineElementInternal> result = dao.getTimelineStrongly("iun").collectList().block();
        Assertions.assertNotNull(result);
    }

    @Test
    void addTimelineElementIfAbsentTest() {
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

        mockPutItem(table);

        // WHEN
        dao.addTimelineElementIfAbsent(row1).block();
        dao.addTimelineElementIfAbsent(row2).block();
    }

    @Test
    void addTimelineElementIfAbsentErrorTest() {
        TimelineElementInternal row1 = TimelineElementInternal.builder()
                .iun("iun")
                .elementId("id1")
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
        when(table.putItem(any(PutItemEnhancedRequest.class)))
                .thenThrow(PnIdConflictException.class);
        Assertions.assertThrows(PnIdConflictException.class, () -> dao.addTimelineElementIfAbsent(row1));
    }

    public static <T> void mockPutItem(DynamoDbAsyncTable<T> dynamoDbAsyncTable) {
        when(dynamoDbAsyncTable.putItem(any(PutItemEnhancedRequest.class)))
                .thenReturn(CompletableFuture.completedFuture(null));
    }

    public static <T> void mockQueryConditional(DynamoDbAsyncTable<T> dynamoDbAsyncTable, List<T> entities) {
        SdkPublisher<Page<T>> sdkPublisher = mock(SdkPublisher.class);
        doAnswer(invocation -> {
            Page<T> page = Page.create(entities, null);

            Subscriber<? super Page<T>> subscriber = invocation.getArgument(0);
            subscriber.onSubscribe(new Subscription() {
                @Override
                public void request(long n) {
                    if (n != 0) {
                        subscriber.onNext(page);
                        subscriber.onComplete();
                    }
                }

                @Override
                public void cancel() { }
            });
            return null;
        }).when(sdkPublisher).subscribe((Subscriber<? super Page<T>>) any());

        PagePublisher<T> pagePublisher = PagePublisher.create(sdkPublisher);
        when(dynamoDbAsyncTable.query((QueryConditional) any())).thenReturn(pagePublisher);
    }

    public static <T> void mockQueryEnahncedRequest(DynamoDbAsyncTable<T> dynamoDbAsyncTable, List<T> entities) {
        SdkPublisher<Page<T>> sdkPublisher = mock(SdkPublisher.class);
        doAnswer(invocation -> {
            Page<T> page = Page.create(entities, null);

            Subscriber<? super Page<T>> subscriber = invocation.getArgument(0);
            subscriber.onSubscribe(new Subscription() {
                @Override
                public void request(long n) {
                    if (n != 0) {
                        subscriber.onNext(page);
                        subscriber.onComplete();
                    }
                }

                @Override
                public void cancel() { }
            });
            return null;
        }).when(sdkPublisher).subscribe((Subscriber<? super Page<T>>) any());

        PagePublisher<T> pagePublisher = PagePublisher.create(sdkPublisher);
        when(dynamoDbAsyncTable.query((QueryEnhancedRequest) any())).thenReturn(pagePublisher);
    }

}
