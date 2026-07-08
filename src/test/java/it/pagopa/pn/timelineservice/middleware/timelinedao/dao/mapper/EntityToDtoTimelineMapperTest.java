package it.pagopa.pn.timelineservice.middleware.timelinedao.dao.mapper;

import it.pagopa.pn.timelineservice.dto.informalnotification.DigitalChannelsInt;
import it.pagopa.pn.timelineservice.dto.timeline.CommunicationType;
import it.pagopa.pn.timelineservice.dto.timeline.TimelineElementInternal;
import it.pagopa.pn.timelineservice.dto.timeline.details.informal.*;
import it.pagopa.pn.timelineservice.dto.timeline.details.legal.AarCreationRequestDetailsInt;
import it.pagopa.pn.timelineservice.dto.timeline.details.legal.PublicRegistryCallDetailsInt;
import it.pagopa.pn.timelineservice.dto.timeline.details.common.RequestRefusedDetailsInt;
import it.pagopa.pn.timelineservice.dto.timeline.details.legal.SendAnalogDetailsInt;
import it.pagopa.pn.timelineservice.middleware.dao.dynamo.entity.*;
import it.pagopa.pn.timelineservice.middleware.dao.dynamo.mapper.EntityToDtoTimelineMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

class EntityToDtoTimelineMapperTest {
    private final EntityToDtoTimelineMapper mapper = new EntityToDtoTimelineMapper();
    
    @Test
    void entityToDtoSendAnalogDomicile() {

        TimelineElementEntity entity = TimelineElementEntity.builder()
                .paId("PaId")
                .iun("iun")
                .category(TimelineElementCategoryEntity.SEND_ANALOG_DOMICILE)
                .notificationSentAt(Instant.now())
                .details(
                        TimelineElementDetailsEntity.builder()
                                .recIndex(0)
                                .physicalAddress(
                                        PhysicalAddressEntity.builder()
                                                .address("addr")
                                                .at("at")
                                                .foreignState("IT")
                                                .build()
                                )
                                .serviceLevel(ServiceLevelEntity.REGISTERED_LETTER_890)
                                .sentAttemptMade(0)
                                .relatedRequestId("abc")
                                .productType("NR_AR")
                                .analogCost(100)
                                .build()  
                )
                .build();

        TimelineElementInternal actual = mapper.entityToDto(entity, Map.of());

        Assertions.assertEquals(entity.getIun(), actual.getIun());
        Assertions.assertEquals(entity.getTimelineElementId(), actual.getElementId());
        Assertions.assertEquals(entity.getNotificationSentAt(), actual.getNotificationSentAt());
        Assertions.assertEquals(entity.getTimestamp(), actual.getTimestamp());
        Assertions.assertEquals(entity.getPaId(), actual.getPaId());
        Assertions.assertEquals(entity.getCategory().name(), actual.getCategory().name());

        SendAnalogDetailsInt details = (SendAnalogDetailsInt) actual.getDetails();
        
        Assertions.assertEquals(entity.getDetails().getRecIndex(), details.getRecIndex());
        Assertions.assertEquals(entity.getDetails().getSentAttemptMade(), details.getSentAttemptMade());
        Assertions.assertEquals(entity.getDetails().getRelatedRequestId(), details.getRelatedRequestId());
        Assertions.assertEquals(entity.getDetails().getServiceLevel().getValue(), details.getServiceLevel().getValue());
        Assertions.assertEquals(entity.getDetails().getAnalogCost(), details.getAnalogCost());
        Assertions.assertEquals(entity.getDetails().getProductType(), details.getProductType());
        Assertions.assertEquals(entity.getDetails().getAnalogCost(), details.getAnalogCost());
        Assertions.assertEquals(entity.getDetails().getPhysicalAddress().getAddress(), details.getPhysicalAddress().getAddress());
        Assertions.assertEquals(entity.getDetails().getPhysicalAddress().getForeignState(), details.getPhysicalAddress().getForeignState());
    }

    @Test
    void entityToDtoRefusedError() {

        TimelineElementEntity entity = TimelineElementEntity.builder()
                .paId("PaId")
                .iun("iun")
                .category( TimelineElementCategoryEntity.REQUEST_REFUSED )
                .details( TimelineElementDetailsEntity.builder()
                        .refusalReasons( List.of( NotificationRefusedErrorEntity.builder()
                                .errorCode( "FILE_NOTFOUND" )
                                .detail( "Allegato non trovato. fileKey=81dde2a8-9719-4407-b7b3-63e7ea694869" )
                                .build()
                                )
                        )
                        .build())
                .build();

        TimelineElementInternal actual = mapper.entityToDto(entity,  Map.of());


        RequestRefusedDetailsInt requestRefusedDetailsInt = (RequestRefusedDetailsInt) actual.getDetails();

        Assertions.assertEquals( entity.getDetails().getRefusalReasons().getFirst().getErrorCode(), requestRefusedDetailsInt.getRefusalReasons().getFirst().getErrorCode() );
    }

    private static Stream<Arguments> provideCommunicationTypeArgs() {
        return Stream.of(
                Arguments.of(null, CommunicationType.LEGAL),
                Arguments.of(CommunicationType.INFORMAL, CommunicationType.INFORMAL),
                Arguments.of(CommunicationType.LEGAL, CommunicationType.LEGAL) // Caso impossibile vista la logica di business in fase di mapping tra Dto e Entity
        );
    }

    @ParameterizedTest
    @MethodSource("provideCommunicationTypeArgs")
    void entityToDto(CommunicationType entityCommunicationType, CommunicationType expectedCommunicationType) {

        TimelineElementEntity entity = TimelineElementEntity.builder()
                .paId("PaId")
                .iun("iun")
                .reworkId("reworkId")
                .campaignId("campaignId")
                .category(TimelineElementCategoryEntity.PUBLIC_REGISTRY_CALL)
                .details(
                        TimelineElementDetailsEntity.builder()
                                .recIndex(0)
                                .deliveryMode(
                                        DeliveryModeEntity.DIGITAL
                                )
                                .contactPhase(
                                        ContactPhaseEntity.SEND_ATTEMPT
                                )
                                .sentAttemptMade(0)
                                .sendDate(Instant.now())
                                .aarTemplateType(AarTemplateTypeEntity.AAR_NOTIFICATION)
                                .build()
                )
                .communicationType(entityCommunicationType)
                .build();
        
        TimelineElementInternal internal = mapper.entityToDto(entity, Map.of());

        PublicRegistryCallDetailsInt details = (PublicRegistryCallDetailsInt) internal.getDetails();
        
        Assertions.assertEquals(entity.getDetails().getRecIndex(), details.getRecIndex());
        Assertions.assertEquals(entity.getDetails().getSentAttemptMade(), details.getSentAttemptMade());
        Assertions.assertEquals(entity.getDetails().getDeliveryMode().getValue(), details.getDeliveryMode().getValue());
        Assertions.assertEquals("reworkId", internal.getReworkId());
        Assertions.assertEquals("campaignId", internal.getCampaignId());
        Assertions.assertEquals(expectedCommunicationType, internal.getCommunicationType());
    }

    @Test
    void entityToDtoWithoutDetails() {

        TimelineElementEntity entity = TimelineElementEntity.builder()
                .timelineElementId("PUBLIC_REGISTRY_CALL.IUN_AAAA-WLRL-YUKX-202405-Z-1.RECINDEX_0")
                .paId("PaId")
                .iun("iun")
                .category(TimelineElementCategoryEntity.PUBLIC_REGISTRY_CALL)
                .build();

        TimelineElementInternal internal = mapper.entityToDto(entity,  Map.of());


        Assertions.assertNull(internal.getDetails());
        Assertions.assertEquals(entity.getIun(), internal.getIun());
        Assertions.assertEquals(entity.getTimelineElementId(), internal.getElementId());
    }

    @Test
    void entityToDtoAarCreationRequest() {

        TimelineElementEntity entity = TimelineElementEntity.builder()
                .paId("PaId")
                .iun("iun")
                .timelineElementId("AAR_CREATION_REQUEST.IUN_AAAA-WLRL-YUKX-202405-Z-1.RECINDEX_0")
                .category( TimelineElementCategoryEntity.AAR_CREATION_REQUEST )
                .notificationSentAt(Instant.now())
                .timestamp(Instant.now())
                .details( TimelineElementDetailsEntity.builder()
                        .aarKey("safestorage://PN_AAR-mock.pdf")
                        .numberOfPages(2)
                        .recIndex(0)
                        .aarTemplateType(AarTemplateTypeEntity.AAR_NOTIFICATION_RADD_ALT)
                        .build())
                .build();

        TimelineElementInternal actual = mapper.entityToDto(entity, Map.of());


        AarCreationRequestDetailsInt details = (AarCreationRequestDetailsInt) actual.getDetails();

        Assertions.assertEquals(entity.getDetails().getAarKey(), details.getAarKey());
        Assertions.assertEquals(entity.getDetails().getNumberOfPages(), details.getNumberOfPages());
        Assertions.assertEquals(entity.getDetails().getRecIndex(), details.getRecIndex());
        Assertions.assertEquals(entity.getDetails().getAarTemplateType().name(), details.getAarTemplateType().name());
    }

    @Test
    void entityToDtoInformalDigitalDeliveryDetail() {
        Instant eventTimestamp = Instant.parse("2024-01-02T10:15:30Z");
        TimelineElementEntity entity = TimelineElementEntity.builder()
                .category(TimelineElementCategoryEntity.SEND_DIGITAL_MESSAGE_PROGRESS)
                .details(TimelineElementDetailsEntity.builder()
                        .recIndex(0)
                        .requestId("requestId")
                        .channel(String.valueOf(DigitalChannelsInt.PEC))
                        .deliveryDetail(DeliveryDetailsEntity.builder()
                                .code("DELIVERY_CODE")
                                .failureCause("failureCause")
                                .eventTimestamp(eventTimestamp)
                                .build())
                        .build())
                .build();

        TimelineElementInternal actual = mapper.entityToDto(entity, Map.of());
        SendDigitalMessageProgressDetailsInt details = (SendDigitalMessageProgressDetailsInt) actual.getDetails();

        Assertions.assertEquals("requestId", details.getRequestId());
        Assertions.assertEquals("PEC", details.getChannel().getValue());
        Assertions.assertEquals("DELIVERY_CODE", details.getDeliveryDetail().getCode());
        Assertions.assertEquals("failureCause", details.getDeliveryDetail().getFailureCause());
        Assertions.assertEquals(eventTimestamp, details.getDeliveryDetail().getEventTimestamp());
    }

    @Test
    void entityToDtoInformalAnalogDeliveryDetail() {
        Instant eventTimestamp = Instant.parse("2024-01-02T10:15:30Z");
        TimelineElementEntity entity = TimelineElementEntity.builder()
                .category(TimelineElementCategoryEntity.SEND_ANALOG_MESSAGE_PROGRESS)
                .details(TimelineElementDetailsEntity.builder()
                        .recIndex(0)
                        .deliveryType(AnalogDeliveryTypeEntity.RS)
                        .deliveryDetail(DeliveryDetailsEntity.builder()
                                .code("DELIVERY_CODE")
                                .failureCause("failureCause")
                                .eventTimestamp(eventTimestamp)
                                .build())
                        .build())
                .build();

        TimelineElementInternal actual = mapper.entityToDto(entity, Map.of());
        SendAnalogMessageProgressDetailsInt details = (SendAnalogMessageProgressDetailsInt) actual.getDetails();

        Assertions.assertEquals(AnalogDeliveryTypeEntity.RS.getValue(), details.getDeliveryType().getValue());
        Assertions.assertEquals("DELIVERY_CODE", details.getDeliveryDetail().getCode());
        Assertions.assertEquals("failureCause", details.getDeliveryDetail().getFailureCause());
        Assertions.assertEquals(eventTimestamp, details.getDeliveryDetail().getEventTimestamp());
    }

    @Test
    void entityToDtoInformalStringAndListFields() {
        CoverpageCreationRequestDetailsInt coverpageDetails = (CoverpageCreationRequestDetailsInt) mapper.entityToDto(TimelineElementEntity.builder()
                .category(TimelineElementCategoryEntity.COVERPAGE_CREATION_REQUEST)
                .details(TimelineElementDetailsEntity.builder()
                        .recIndex(0)
                        .fileKey("fileKey")
                        .build())
                .build(), Map.of()).getDetails();

        WorkflowDoneReachedDetailsInt workflowDoneDetails = (WorkflowDoneReachedDetailsInt) mapper.entityToDto(TimelineElementEntity.builder()
                .category(TimelineElementCategoryEntity.WORKFLOW_DONE_REACHED)
                .details(TimelineElementDetailsEntity.builder()
                        .recIndex(0)
                        .sourceElementId("sourceElementId")
                        .build())
                .build(), Map.of()).getDetails();

        DeliveredDetailsInt reachedDetails = (DeliveredDetailsInt) mapper.entityToDto(TimelineElementEntity.builder()
                .category(TimelineElementCategoryEntity.DELIVERED)
                .details(TimelineElementDetailsEntity.builder()
                        .recIndex(0)
                        .channel(String.valueOf(DigitalChannelsInt.PEC))
                        .sourceElementId("reachedSourceElementId")
                        .build())
                .build(), Map.of()).getDetails();

        WorkflowEndedReachedDetailsInt workflowEndedReachedDetails = (WorkflowEndedReachedDetailsInt) mapper.entityToDto(TimelineElementEntity.builder()
                .category(TimelineElementCategoryEntity.WORKFLOW_ENDED_REACHED)
                .details(TimelineElementDetailsEntity.builder()
                        .recIndex(0)
                        .sourceElementId("workflowEndedSourceElementId")
                        .build())
                .build(), Map.of()).getDetails();

        SendDigitalMessageFeedbackDetailsInt feedbackDetails = (SendDigitalMessageFeedbackDetailsInt) mapper.entityToDto(TimelineElementEntity.builder()
                .category(TimelineElementCategoryEntity.SEND_DIGITAL_MESSAGE_FEEDBACK)
                .details(TimelineElementDetailsEntity.builder()
                        .recIndex(0)
                        .requestId("feedbackRequestId")
                        .channel(String.valueOf(DigitalChannelsInt.IO))
                        .build())
                .build(), Map.of()).getDetails();

        Assertions.assertEquals("fileKey", coverpageDetails.getFileKey());
        Assertions.assertEquals("sourceElementId", workflowDoneDetails.getSourceElementId());
        Assertions.assertEquals("PEC", reachedDetails.getChannel());
        Assertions.assertEquals("reachedSourceElementId", reachedDetails.getSourceElementId());
        Assertions.assertEquals("workflowEndedSourceElementId",
                workflowEndedReachedDetails.getSourceElementId());
        Assertions.assertEquals("feedbackRequestId", feedbackDetails.getRequestId());
        Assertions.assertEquals("APPIO", feedbackDetails.getChannel().getValue());
    }
    
}