package it.pagopa.pn.timelineservice.middleware.timelinedao.dao.dynamo;

import it.pagopa.pn.commons.exceptions.PnIdConflictException;
import it.pagopa.pn.timelineservice.config.PnTimelineServiceConfigs;
import it.pagopa.pn.timelineservice.dto.address.PhysicalAddressInt;
import it.pagopa.pn.timelineservice.dto.timeline.StatusInfoInternal;
import it.pagopa.pn.timelineservice.dto.timeline.TimelineElementInternal;
import it.pagopa.pn.timelineservice.dto.timeline.details.TimelineElementCategoryInt;
import it.pagopa.pn.timelineservice.dto.timeline.details.common.NormalizedAddressDetailsInt;
import it.pagopa.pn.timelineservice.dto.timeline.details.common.NotificationRequestAcceptedDetailsInt;
import it.pagopa.pn.timelineservice.dto.timeline.details.legal.*;
import it.pagopa.pn.timelineservice.generated.openapi.server.v1.dto.TimelineElement;
import it.pagopa.pn.timelineservice.middleware.dao.dynamo.TimelineDaoDynamo;
import it.pagopa.pn.timelineservice.middleware.dao.dynamo.entity.*;
import it.pagopa.pn.timelineservice.middleware.dao.dynamo.mapper.DtoToEntityTimelineMapper;
import it.pagopa.pn.timelineservice.middleware.dao.dynamo.mapper.EntityToDtoTimelineMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import reactor.test.StepVerifier;
import software.amazon.awssdk.core.async.SdkPublisher;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbAsyncTable;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient;
import software.amazon.awssdk.enhanced.dynamodb.model.*;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TimelineDaoDynamoTest {

    @Mock
    private DynamoDbEnhancedAsyncClient dynamoDbEnhancedAsyncClient;
    @Mock
    private DynamoDbAsyncTable<Object> table;

    private TestableTimelineDaoDynamo dao;

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
        pnTimelineServiceConfigs.setInvalidableCategories(List.of("PREPARE_ANALOG_DOMICILE","PREPARE_ANALOG_DOMICILE_FAILURE","SEND_ANALOG_DOMICILE","SEND_ANALOG_PROGRESS","SEND_ANALOG_FEEDBACK","ANALOG_SUCCESS_WORKFLOW","ANALOG_FAILURE_WORKFLOW","SCHEDULE_REFINEMENT","REFINEMENT","COMPLETELY_UNREACHABLE_CREATION_REQUEST","COMPLETELY_UNREACHABLE","ANALOG_WORKFLOW_RECIPIENT_DECEASED"));
        dao = new TestableTimelineDaoDynamo(dynamoDbEnhancedAsyncClient, pnTimelineServiceConfigs, dtoToEntityTimelineMapper, entityToDtoTimelineMapper);
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
        mockQueryEnahncedRequest(table, List.of());

        List<TimelineElementInternal> result = dao.getTimelineFilteredByElementId(iun, "IUN_iun-di-prova", true).collectList().block();
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
        mockQueryEnahncedRequest(table, List.of());

        List<TimelineElementInternal> result = dao.getTimelineFilteredByElementId("iun", "element_", true)
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
    void removeAttachmentsFromInvalidatedElements_shouldRemoveLegalFactsIdsForNotificationViewed() throws Exception {
        TimelineElementEntity invalidatedTimelineElementEntity = TimelineElementEntity.builder()
                .iun("iun-test")
                .timelineElementId("NOTIFICATION_VIEWED.IUN_test.RECINDEX_0")
                .category(TimelineElementCategoryEntity.NOTIFICATION_VIEWED)
                .reworkRequestType(TimelineElement.ReworkRequestTypeEnum.INVALIDATE_ELEMENTS.name())
                .legalFactIds(List.of(
                        LegalFactsIdEntity.builder()
                                .key("legal-fact-key")
                                .category(LegalFactCategoryEntity.RECIPIENT_ACCESS)
                                .build()))
                .build();
        TimelineElementInternal invalidatedTimelineElement = entityToDtoTimelineMapper.entityToDto(invalidatedTimelineElementEntity, null);
        Map<String, TimelineElementInternal> invalidatedElementMap = new HashMap<>();
        invalidatedElementMap.put(invalidatedTimelineElement.getElementId(), invalidatedTimelineElement);

        dao.removeAttachmentsFromInvalidatedElementsForTest(invalidatedElementMap);

        Assertions.assertNull(invalidatedTimelineElement.getLegalFactsIds());
    }

    @Test
    void removeAttachmentsFromInvalidatedElements_shouldRemoveAttachmentsForSendAnalogProgress() throws Exception {
        TimelineElementEntity invalidatedTimelineElementEntity = TimelineElementEntity.builder()
                .iun("iun-test")
                .timelineElementId("SEND_ANALOG_PROGRESS.IUN_test.RECINDEX_0.ATTEMPT_0.IDX_1")
                .category(TimelineElementCategoryEntity.SEND_ANALOG_PROGRESS)
                .reworkRequestType(TimelineElement.ReworkRequestTypeEnum.INVALIDATE_ELEMENTS.name())
                .details(TimelineElementDetailsEntity.builder()
                        .recIndex(0)
                        .attachments(List.of(
                                AttachmentDetailsEntity.builder()
                                        .id("attachment-id")
                                        .documentType("AAR")
                                        .url("safestorage://attachment-id")
                                        .date(Instant.now())
                                        .build()))
                        .build())
                .build();
        TimelineElementInternal invalidatedTimelineElement = entityToDtoTimelineMapper.entityToDto(invalidatedTimelineElementEntity, null);
        Map<String, TimelineElementInternal> invalidatedElementMap = new HashMap<>();
        invalidatedElementMap.put(invalidatedTimelineElement.getElementId(), invalidatedTimelineElement);

        dao.removeAttachmentsFromInvalidatedElementsForTest(invalidatedElementMap);

        Assertions.assertNull(((SendAnalogProgressDetailsInt) invalidatedTimelineElement.getDetails()).getAttachments());
    }

    @Test
    void removeAttachmentsFromInvalidatedElements_shouldNotRemoveAttachmentsForSendAnalogProgressIfReworkRequestTypeIsNotInvalidateElements() throws Exception {
        TimelineElementEntity invalidatedTimelineElementEntity = TimelineElementEntity.builder()
                .iun("iun-test")
                .timelineElementId("SEND_ANALOG_PROGRESS.IUN_test.RECINDEX_0.ATTEMPT_0.IDX_2")
                .category(TimelineElementCategoryEntity.SEND_ANALOG_PROGRESS)
                .reworkRequestType(TimelineElement.ReworkRequestTypeEnum.REWORK.name())
                .details(TimelineElementDetailsEntity.builder()
                        .recIndex(0)
                        .attachments(List.of(
                                AttachmentDetailsEntity.builder()
                                        .id("attachment-id")
                                        .documentType("AAR")
                                        .url("safestorage://attachment-id")
                                        .date(Instant.now())
                                        .build()))
                        .build())
                .build();
        TimelineElementInternal invalidatedTimelineElement = entityToDtoTimelineMapper.entityToDto(invalidatedTimelineElementEntity, null);
        Map<String, TimelineElementInternal> invalidatedElementMap = new HashMap<>();
        invalidatedElementMap.put(invalidatedTimelineElement.getElementId(), invalidatedTimelineElement);

        dao.removeAttachmentsFromInvalidatedElementsForTest(invalidatedElementMap);

        Assertions.assertNotNull(((SendAnalogProgressDetailsInt) invalidatedTimelineElement.getDetails()).getAttachments());
    }

    @Test
    void removeAttachmentsFromInvalidatedElements_shouldNotChangeCompletelyUnreachable() throws Exception {
        TimelineElementEntity invalidatedTimelineElementEntity = TimelineElementEntity.builder()
                .iun("iun-test")
                .timelineElementId("COMPLETELY_UNREACHABLE.IUN_test.RECINDEX_0")
                .category(TimelineElementCategoryEntity.COMPLETELY_UNREACHABLE)
                .reworkRequestType(TimelineElement.ReworkRequestTypeEnum.REWORK.name())
                .legalFactIds(List.of(
                        LegalFactsIdEntity.builder()
                                .key("legal-fact-key")
                                .category(LegalFactCategoryEntity.RECIPIENT_ACCESS)
                                .build()))
                .build();
        TimelineElementInternal invalidatedTimelineElement = entityToDtoTimelineMapper.entityToDto(invalidatedTimelineElementEntity, null);
        Map<String, TimelineElementInternal> invalidatedElementMap = new HashMap<>();
        invalidatedElementMap.put(invalidatedTimelineElement.getElementId(), invalidatedTimelineElement);

        dao.removeAttachmentsFromInvalidatedElementsForTest(invalidatedElementMap);

        Assertions.assertNotNull(invalidatedTimelineElement.getLegalFactsIds());
    }

    @Test
    void removeAttachmentsFromInvalidatedElements_shouldChangeCompletelyUnreachable() throws Exception {
        TimelineElementEntity invalidatedTimelineElementEntity = TimelineElementEntity.builder()
                .iun("iun-test")
                .timelineElementId("COMPLETELY_UNREACHABLE.IUN_test.RECINDEX_0")
                .category(TimelineElementCategoryEntity.COMPLETELY_UNREACHABLE)
                .reworkRequestType(TimelineElement.ReworkRequestTypeEnum.INVALIDATE_ELEMENTS.name())
                .legalFactIds(List.of(
                        LegalFactsIdEntity.builder()
                                .key("legal-fact-key")
                                .category(LegalFactCategoryEntity.RECIPIENT_ACCESS)
                                .build()))
                .build();
        TimelineElementInternal invalidatedTimelineElement = entityToDtoTimelineMapper.entityToDto(invalidatedTimelineElementEntity, null);
        Map<String, TimelineElementInternal> invalidatedElementMap = new HashMap<>();
        invalidatedElementMap.put(invalidatedTimelineElement.getElementId(), invalidatedTimelineElement);

        dao.removeAttachmentsFromInvalidatedElementsForTest(invalidatedElementMap);

        Assertions.assertNull(invalidatedTimelineElement.getLegalFactsIds());
    }

    @Test
    void getTimeline_shouldStripAttachmentsFromNestedInvalidatedElementsInReworkDetails() throws Exception {
        String iun = "JQUD-NRZR-ZVTH-202503-Y-1";

        String invalidatedSendId = "SEND_ANALOG_PROGRESS.IUN_" + iun + ".RECINDEX_0.ATTEMPT_0.IDX_1";
        TimelineElementEntity invalidatedSend = TimelineElementEntity.builder()
                .iun(iun)
                .timelineElementId(invalidatedSendId)
                .category(TimelineElementCategoryEntity.SEND_ANALOG_PROGRESS)
                .reworkRequestType(TimelineElement.ReworkRequestTypeEnum.INVALIDATE_ELEMENTS.name())
                .details(TimelineElementDetailsEntity.builder()
                        .recIndex(0)
                        .attachments(List.of(
                                AttachmentDetailsEntity.builder()
                                        .id("attachment-id")
                                        .documentType("AAR")
                                        .url("safestorage://attachment-id")
                                        .date(Instant.now())
                                        .build()))
                        .build())
                .build();

        String invalidatedUnreachableId = "COMPLETELY_UNREACHABLE.IUN_" + iun + ".RECINDEX_0";
        TimelineElementEntity invalidatedUnreachable = TimelineElementEntity.builder()
                .iun(iun)
                .timelineElementId(invalidatedUnreachableId)
                .category(TimelineElementCategoryEntity.COMPLETELY_UNREACHABLE)
                .reworkRequestType(TimelineElement.ReworkRequestTypeEnum.INVALIDATE_ELEMENTS.name())
                .legalFactIds(List.of(
                        LegalFactsIdEntity.builder()
                                .key("legal-fact-key")
                                .category(LegalFactCategoryEntity.RECIPIENT_ACCESS)
                                .build()))
                .build();

        NotificationStatusHistoryElementEntity statusHistoryElement = NotificationStatusHistoryElementEntity.builder()
                .relatedTimelineElementIds(List.of(invalidatedSendId, invalidatedUnreachableId))
                .build();

        TimelineElementEntity rework = TimelineElementEntity.builder()
                .iun(iun)
                .timelineElementId("NOTIFICATION_TIMELINE_REWORKED.IUN_" + iun + ".RECINDEX_0.ATTEMPT_0.REWORK_0")
                .category(TimelineElementCategoryEntity.NOTIFICATION_TIMELINE_REWORKED)
                .reworkRequestType(TimelineElement.ReworkRequestTypeEnum.REWORK.name())
                .details(TimelineElementDetailsEntity.builder()
                        .recIndex(0)
                        .invalidatedTimelineAndStatusHistory(List.of(statusHistoryElement))
                        .build())
                .build();

        Map<String, TimelineElementInternal> invalidatedTimelineElements = new HashMap<>();
        invalidatedTimelineElements.put(invalidatedSendId, entityToDtoTimelineMapper.entityToDto(invalidatedSend, null));
        invalidatedTimelineElements.put(invalidatedUnreachableId, entityToDtoTimelineMapper.entityToDto(invalidatedUnreachable, null));
        dao.removeAttachmentsFromInvalidatedElementsForTest(invalidatedTimelineElements);

        TimelineElementInternal reworkElement = entityToDtoTimelineMapper.entityToDto(rework, invalidatedTimelineElements);
        Assertions.assertEquals(TimelineElementCategoryInt.NOTIFICATION_TIMELINE_REWORKED, reworkElement.getCategory());

        NotificationTimelineReworkedDetailsInt details = (NotificationTimelineReworkedDetailsInt) reworkElement.getDetails();
        List<TimelineElementInternal> relatedTimelineElements = details.getInvalidatedTimelineAndStatusHistory()
                .getFirst()
                .getRelatedTimelineElements();

        Assertions.assertEquals(2, relatedTimelineElements.size());
        TimelineElementInternal sanitizedSend = relatedTimelineElements.stream()
                .filter(elem -> invalidatedSendId.equals(elem.getElementId()))
                .findFirst()
                .orElseThrow();
        Assertions.assertNull(((SendAnalogProgressDetailsInt) sanitizedSend.getDetails()).getAttachments());

        TimelineElementInternal sanitizedUnreachable = relatedTimelineElements.stream()
                .filter(elem -> invalidatedUnreachableId.equals(elem.getElementId()))
                .findFirst()
                .orElseThrow();
        Assertions.assertNull(sanitizedUnreachable.getLegalFactsIds());
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
        StepVerifier.create(dao.addTimelineElementIfAbsent(row1)).expectComplete();
        StepVerifier.create(dao.addTimelineElementIfAbsent(row2)).expectComplete();
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

    @Test
    void getTimelineFilteredByElementIdWithReworkItemTest() {
        String iun = "JQUD-NRZR-ZVTH-202503-Y-1";

        String id1 = "SEND_ANALOG_PROGRESS.IUN_JQUD-NRZR-ZVTH-202503-Y-1.RECINDEX_0.ATTEMPT_0.IDX_1";
        TimelineElementEntity row1 = TimelineElementEntity.builder()
                .iun(iun)
                .timelineElementId(id1)
                .category(TimelineElementCategoryEntity.SEND_ANALOG_PROGRESS)
                .reworkRequestType(TimelineElement.ReworkRequestTypeEnum.INVALIDATE_ELEMENTS.name())
                .timestamp(Instant.now())
                .businessTimestamp(Instant.now().minus(1, ChronoUnit.HOURS))
                .statusInfo(StatusInfoEntity.builder().build())
                .build();

        String id3 = "SEND_ANALOG_PROGRESS.IUN_JQUD-NRZR-ZVTH-202503-Y-1.RECINDEX_0.ATTEMPT_0.IDX_2";
        TimelineElementEntity row3 = TimelineElementEntity.builder()
                .iun(iun)
                .timelineElementId(id3)
                .category(TimelineElementCategoryEntity.SEND_ANALOG_PROGRESS)
                .reworkRequestType(TimelineElement.ReworkRequestTypeEnum.INVALIDATE_ELEMENTS.name())
                .timestamp(Instant.now())
                .businessTimestamp(Instant.now().minus(1, ChronoUnit.HOURS))
                .statusInfo(StatusInfoEntity.builder().build())
                .build();

        String id2 = "SEND_ANALOG_PROGRESS.IUN_JQUD-NRZR-ZVTH-202503-Y-1.RECINDEX_0.ATTEMPT_0.IDX_3.REWORK_0";
        TimelineElementEntity row2 = TimelineElementEntity.builder()
                .iun(iun)
                .timelineElementId(id2)
                .category(TimelineElementCategoryEntity.SEND_ANALOG_PROGRESS)
                .reworkRequestType(TimelineElement.ReworkRequestTypeEnum.INVALIDATE_ELEMENTS.name())
                .timestamp(Instant.now())
                .businessTimestamp(Instant.now().minus(1, ChronoUnit.HOURS))
                .statusInfo(StatusInfoEntity.builder().build())
                .build();

        NotificationStatusHistoryElementEntity notificationStatusHistoryElementInt = new NotificationStatusHistoryElementEntity();
        notificationStatusHistoryElementInt.setRelatedTimelineElementIds(List.of("SEND_ANALOG_PROGRESS.IUN_"+iun+".RECINDEX_0.ATTEMPT_0.IDX_1",
                "SEND_ANALOG_PROGRESS.IUN_"+iun+".RECINDEX_0.ATTEMPT_0.IDX_2"));

        String id4 = "NOTIFICATION_TIMELINE_REWORKED.IUN_JQUD-NRZR-ZVTH-202503-Y-1.RECINDEX_0.ATTEMPT_0.REWORK_0";
        TimelineElementEntity rework = TimelineElementEntity.builder()
                .iun(iun)
                .timelineElementId(id4)
                .category(TimelineElementCategoryEntity.NOTIFICATION_TIMELINE_REWORKED)
                .reworkRequestType(TimelineElement.ReworkRequestTypeEnum.REWORK.name())
                .details(TimelineElementDetailsEntity.builder().recIndex(0).invalidatedTimelineAndStatusHistory(List.of(notificationStatusHistoryElementInt)).build())
                .timestamp(Instant.now())
                .businessTimestamp(Instant.now().minus(1, ChronoUnit.HOURS))
                .statusInfo(StatusInfoEntity.builder().build())
                .build();

        mockQueryConditional(table, List.of(row1, row3, row2));
        mockQueryEnahncedRequest(table, List.of(rework));

        List<TimelineElementInternal> result = dao.getTimelineFilteredByElementId(iun, "SEND_ANALOG_PROGRESS.IUN_JQUD-NRZR-ZVTH-202503-Y-1.RECINDEX_0.ATTEMPT_0", true).collectList().block();
        Assertions.assertNotNull(result);

        Assertions.assertEquals(row2.getIun(), result.getFirst().getIun());
        Assertions.assertEquals(row2.getTimelineElementId(), result.getFirst().getElementId());
        Assertions.assertEquals(row2.getCategory().name(), result.getFirst().getCategory().name());
        Assertions.assertEquals(row2.getStatusInfo().isStatusChanged(), result.getFirst().getStatusInfo().isStatusChanged());
        Assertions.assertEquals(row2.getNotificationSentAt(), result.getFirst().getNotificationSentAt());
        Assertions.assertEquals(row2.getPaId(), result.getFirst().getPaId());
        Assertions.assertEquals(row2.getTimestamp(), result.getFirst().getTimestamp());
        Assertions.assertEquals(row2.getBusinessTimestamp(), result.getFirst().getEventTimestamp());

        Assertions.assertTrue(result.stream().allMatch(elem -> elem.getElementId().contains(".REWORK_")));
    }

    @Test
    void getTimelineElementCategoryReworkable_NoReworkEntityTest() {
        String iun = "202109-eb10750e-e876-4a5a-8762-c4348d679d35";

        String id1 = "PREPARE_ANALOG_DOMICILE.IUN_prepare_analog_domicile";
        TimelineElementEntity row1 = TimelineElementEntity.builder()
                .iun(iun)
                .timelineElementId(id1)
                .category(TimelineElementCategoryEntity.PREPARE_ANALOG_DOMICILE)
                .details(TimelineElementDetailsEntity.builder().recIndex(0).build())
                .timestamp(Instant.now())
                .businessTimestamp(Instant.now().minus(1, ChronoUnit.HOURS))
                .statusInfo(StatusInfoEntity.builder().build())
                .build();

        when(table.getItem(any(GetItemEnhancedRequest.class)))
                .thenReturn(CompletableFuture.completedFuture(row1));

        mockQueryEnahncedRequest(table, List.of());

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
        Assertions.assertInstanceOf(BaseAnalogDetailsInt.class, retrievedRow1.getDetails());
    }

    @Test
    void getTimelineElementCategoryReworkable_ReworkEntityFoundTest() {
        String iun = "202109-eb10750e-e876-4a5a-8762-c4348d679d35";

        String id1 = "PREPARE_ANALOG_DOMICILE.IUN_prepare_analog_domicile";
        TimelineElementEntity row1 = TimelineElementEntity.builder()
                .iun(iun)
                .timelineElementId(id1)
                .category(TimelineElementCategoryEntity.PREPARE_ANALOG_DOMICILE)
                .details(TimelineElementDetailsEntity.builder().recIndex(0).build())
                .timestamp(Instant.now())
                .businessTimestamp(Instant.now().minus(1, ChronoUnit.HOURS))
                .statusInfo(StatusInfoEntity.builder().build())
                .build();

        String id3 = "SEND_DIGITAL_DOMICILE.IUN_iun-di-prova.RECINDEX_0.REWORK_0";
        TimelineElementEntity rework = TimelineElementEntity.builder()
                .iun(iun)
                .timelineElementId(id3)
                .category(TimelineElementCategoryEntity.SEND_DIGITAL_DOMICILE)
                .reworkRequestType(TimelineElement.ReworkRequestTypeEnum.REWORK.name())
                .details(TimelineElementDetailsEntity.builder().recIndex(0).invalidatedTimelineAndStatusHistory(List.of()).build())
                .timestamp(Instant.now())
                .businessTimestamp(Instant.now().minus(1, ChronoUnit.HOURS))
                .statusInfo(StatusInfoEntity.builder().build())
                .build();

        when(table.getItem(any(GetItemEnhancedRequest.class)))
                .thenReturn(CompletableFuture.completedFuture(row1));

        mockQueryEnahncedRequest(table, List.of(rework));

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
        Assertions.assertInstanceOf(BaseAnalogDetailsInt.class, retrievedRow1.getDetails());
    }

    @Test
    void addTimelineElementIfAbsent_visibilityCheckSensitiveAddressFields_SEND_SIMPLE_REGISTERED_LETTER() {
        TimelineElementInternal element = TimelineElementInternal.builder()
                .iun("iun")
                .elementId("id1")
                .category(TimelineElementCategoryInt.SEND_SIMPLE_REGISTERED_LETTER)
                .details(SimpleRegisteredLetterDetailsInt.builder()
                        .physicalAddress(PhysicalAddressInt.builder()
                                .foreignState("IT")
                                .zip("12345")
                                .address("via esempio 123")
                                .addressDetails("addressDetails")
                                .municipalityDetails("municipalityDetails")
                                .municipality("Roma")
                                .province("RM")
                                .at("at")
                                .build())
                        .build())
                .timestamp(Instant.now())
                .statusInfo(StatusInfoInternal.builder().build())
                .build();

        when(table.putItem(any(PutItemEnhancedRequest.class)))
                .thenReturn(CompletableFuture.completedFuture(null));

        ArgumentCaptor<PutItemEnhancedRequest> requestCaptor =
                ArgumentCaptor.forClass(PutItemEnhancedRequest.class);

        StepVerifier.create(dao.addTimelineElementIfAbsent(element))
                .verifyComplete();

        verify(table, times(1)).putItem(requestCaptor.capture());

        PutItemEnhancedRequest captured = requestCaptor.getValue();
        Assertions.assertNotNull(captured);

        TimelineElementEntity saved = (TimelineElementEntity) captured.item();
        Assertions.assertNotNull(saved);
        Assertions.assertNotNull(saved.getDetails());
        Assertions.assertNotNull(saved.getDetails().getPhysicalAddress());

        PhysicalAddressEntity physicalAddress = saved.getDetails().getPhysicalAddress();
        Assertions.assertNull(physicalAddress.getAt());
        Assertions.assertNull(physicalAddress.getMunicipalityDetails());
        Assertions.assertNull(physicalAddress.getAddressDetails());
        Assertions.assertNull(physicalAddress.getProvince());
        Assertions.assertNull(physicalAddress.getAddress());

        Assertions.assertEquals("Roma", physicalAddress.getMunicipality());
        Assertions.assertEquals("12345", physicalAddress.getZip());
        Assertions.assertEquals("IT", physicalAddress.getForeignState());
    }

    @Test
    void addTimelineElementIfAbsent_visibilityCheckSensitiveAddressField_SEND_ANALOG_DOMICILE() {
        when(table.putItem(any(PutItemEnhancedRequest.class)))
                .thenReturn(CompletableFuture.completedFuture(null));

        TimelineElementInternal element = TimelineElementInternal.builder()
                .iun("iun")
                .elementId("id1")
                .category(TimelineElementCategoryInt.SEND_ANALOG_DOMICILE)
                .details(BaseAnalogDetailsInt.builder()
                        .recIndex(0)
                        .physicalAddress(PhysicalAddressInt.builder()
                                .foreignState("IT").zip("12345").municipality("Roma")
                                .province("RM").address("via esempio 123")
                                .addressDetails("addressDetails")
                                .municipalityDetails("municipalityDetails")
                                .at("at")
                                .build())
                        .build())
                .timestamp(Instant.now())
                .statusInfo(StatusInfoInternal.builder().build())
                .build();

        ArgumentCaptor<PutItemEnhancedRequest> captor =
                ArgumentCaptor.forClass(PutItemEnhancedRequest.class);

        StepVerifier.create(dao.addTimelineElementIfAbsent(element))
                .verifyComplete();

        verify(table, times(1)).putItem(captor.capture());

        PhysicalAddressEntity saved = ((TimelineElementEntity) captor
                .getValue().item()).getDetails().getPhysicalAddress();

        Assertions.assertNotNull(saved);
        Assertions.assertNull(saved.getAt());
        Assertions.assertNull(saved.getMunicipalityDetails());
        Assertions.assertNull(saved.getAddressDetails());
        Assertions.assertNull(saved.getProvince());
        Assertions.assertNull(saved.getAddress());

        Assertions.assertEquals("Roma",  saved.getMunicipality());
        Assertions.assertEquals("12345", saved.getZip());
        Assertions.assertEquals("IT",    saved.getForeignState());
    }

    @Test
    void addTimelineElementIfAbsent_visibilityCheckSensitiveAddressField_NORMALIZED_ADDRESS() {
        when(table.putItem(any(PutItemEnhancedRequest.class)))
                .thenReturn(CompletableFuture.completedFuture(null));

        TimelineElementInternal element = TimelineElementInternal.builder()
                .iun("iun")
                .elementId("id2")
                .category(TimelineElementCategoryInt.NORMALIZED_ADDRESS)
                .details(NormalizedAddressDetailsInt.builder()
                        .recIndex(0)
                        .normalizedAddress(PhysicalAddressInt.builder()
                                .foreignState("FR").zip("75001").municipality("Paris")
                                .province("PA").address("Rue de Exemple 10")
                                .addressDetails("Batiment B")
                                .municipalityDetails("Paris details")
                                .at("Chez Dupont")
                                .build())
                        .build())
                .timestamp(Instant.now())
                .statusInfo(StatusInfoInternal.builder().build())
                .build();

        ArgumentCaptor<PutItemEnhancedRequest> captor =
                ArgumentCaptor.forClass(PutItemEnhancedRequest.class);

        StepVerifier.create(dao.addTimelineElementIfAbsent(element))
                .verifyComplete();

        verify(table, times(1)).putItem(captor.capture());

        PhysicalAddressEntity saved = ((TimelineElementEntity) captor
                .getValue().item()).getDetails().getNewAddress();

        Assertions.assertNotNull(saved);
        Assertions.assertNull(saved.getAt());
        Assertions.assertNull(saved.getMunicipalityDetails());
        Assertions.assertNull(saved.getAddressDetails());
        Assertions.assertNull(saved.getProvince());
        Assertions.assertNull(saved.getAddress());

        Assertions.assertEquals("Paris",  saved.getMunicipality());
        Assertions.assertEquals("75001",  saved.getZip());
        Assertions.assertEquals("FR",     saved.getForeignState());
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
                public void cancel() {
                    // No action needed for cancel in this mock
                }
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
                public void cancel() {
                    // No action needed for cancel in this mock
                }
            });
            return null;
        }).when(sdkPublisher).subscribe((Subscriber<? super Page<T>>) any());

        PagePublisher<T> pagePublisher = PagePublisher.create(sdkPublisher);
        when(dynamoDbAsyncTable.query((QueryEnhancedRequest) any())).thenReturn(pagePublisher);
    }

    private static class TestableTimelineDaoDynamo extends TimelineDaoDynamo {
        TestableTimelineDaoDynamo(DynamoDbEnhancedAsyncClient dynamoDbEnhancedClient, PnTimelineServiceConfigs cfg,
                                  DtoToEntityTimelineMapper dto2entity, EntityToDtoTimelineMapper entity2dto) {
            super(dynamoDbEnhancedClient, cfg, dto2entity, entity2dto);
        }

        void removeAttachmentsFromInvalidatedElementsForTest(Map<String, TimelineElementInternal> invalidatedElementMap) {
            super.removeAttachmentsFromInvalidatedElements(invalidatedElementMap);
        }
    }

}
