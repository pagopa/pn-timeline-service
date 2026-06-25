package it.pagopa.pn.timelineservice.middleware.timelinedao.dao.mapper;

import it.pagopa.pn.timelineservice.dto.legalfacts.LegalFactCategoryInt;
import it.pagopa.pn.timelineservice.dto.legalfacts.LegalFactsIdInt;
import it.pagopa.pn.timelineservice.dto.informalnotification.AnalogDeliveryDetailsInt;
import it.pagopa.pn.timelineservice.dto.informalnotification.AnalogDeliveryTypeInt;
import it.pagopa.pn.timelineservice.dto.informalnotification.DigitalChannelsInt;
import it.pagopa.pn.timelineservice.dto.informalnotification.DigitalDeliveryDetailsInt;
import it.pagopa.pn.timelineservice.dto.timeline.CommunicationType;
import it.pagopa.pn.timelineservice.dto.timeline.TimelineElementInternal;
import it.pagopa.pn.timelineservice.dto.timeline.details.TimelineElementCategoryInt;
import it.pagopa.pn.timelineservice.dto.timeline.details.common.*;
import it.pagopa.pn.timelineservice.dto.timeline.details.informal.CoverpageCreationRequestDetailsInt;
import it.pagopa.pn.timelineservice.dto.timeline.details.informal.ReachedDetailsInt;
import it.pagopa.pn.timelineservice.dto.timeline.details.informal.SendAnalogMessageProgressDetailsInt;
import it.pagopa.pn.timelineservice.dto.timeline.details.informal.SendDigitalMessageFeedbackDetailsInt;
import it.pagopa.pn.timelineservice.dto.timeline.details.informal.SendDigitalMessageProgressDetailsInt;
import it.pagopa.pn.timelineservice.dto.timeline.details.informal.WorkflowDoneDetailsInt;
import it.pagopa.pn.timelineservice.dto.timeline.details.informal.WorkflowEndedReachedDetailsInt;
import it.pagopa.pn.timelineservice.dto.timeline.details.legal.*;
import it.pagopa.pn.timelineservice.legalfacts.AarTemplateType;
import it.pagopa.pn.timelineservice.middleware.dao.dynamo.entity.TimelineElementDetailsEntity;
import it.pagopa.pn.timelineservice.middleware.dao.dynamo.entity.TimelineElementEntity;
import it.pagopa.pn.timelineservice.middleware.dao.dynamo.mapper.DtoToEntityTimelineMapper;
import it.pagopa.pn.timelineservice.service.mapper.SmartMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

class DtoToEntityTimelineMapperTest {

    private final DtoToEntityTimelineMapper mapper = new DtoToEntityTimelineMapper();

    /* Attenzione è un vincolo importante che se l'oggetto interno ha communicationType LEGAL non sia persistito nulla sul DB */
    private static Stream<Arguments> provideCommunicationTypeArgs() {
        return Stream.of(
                Arguments.of(CommunicationType.LEGAL, null),
                Arguments.of(CommunicationType.INFORMAL, CommunicationType.INFORMAL),
                Arguments.of(null, null)
        );
    }

    @ParameterizedTest
    @MethodSource("provideCommunicationTypeArgs")
    void dtoToEntity(CommunicationType domainCommunicationType, CommunicationType expectedCommunicationType) {
        TimelineElementInternal timelineElementInternal = buildTimelineElementInternal(domainCommunicationType);
        TimelineElementEntity actual = mapper.dtoToEntity(timelineElementInternal);

        assertThat(actual).isNotNull();
        assertThat(actual.getIun()).isEqualTo(timelineElementInternal.getIun());
        assertThat(actual.getTimelineElementId()).isEqualTo(timelineElementInternal.getElementId());
        assertThat(actual.getPaId()).isEqualTo(timelineElementInternal.getPaId());
        assertThat(actual.getNotificationSentAt()).isEqualTo(timelineElementInternal.getNotificationSentAt());
        assertThat(actual.getCategory().name()).isEqualTo(timelineElementInternal.getCategory().name());
        assertThat(actual.getTimestamp()).isEqualTo(timelineElementInternal.getTimestamp());

        // verifica details
        NotificationViewedDetailsInt details = (NotificationViewedDetailsInt) timelineElementInternal.getDetails();
        assertThat(actual.getDetails()).isNotNull();
        assertThat(actual.getDetails().getRecIndex()).isEqualTo(details.getRecIndex());
        assertThat(actual.getDetails().getNotificationCost()).isEqualTo(details.getNotificationCost());

        // verifica legalFacts
        assertThat(actual.getLegalFactIds()).isNotNull().hasSize(timelineElementInternal.getLegalFactsIds().size());
        assertThat(actual.getLegalFactIds().getFirst().getKey()).isEqualTo(timelineElementInternal.getLegalFactsIds().getFirst().getKey());
        assertThat(actual.getLegalFactIds().getFirst().getCategory().name()).isEqualTo(timelineElementInternal.getLegalFactsIds().getFirst().getCategory().name());
        assertThat(actual.getReworkId()).isEqualTo(timelineElementInternal.getReworkId());

        // verifica communicationType
        assertEquals(expectedCommunicationType, actual.getCommunicationType());
    }

    @Test
    void dtoToEntityPaid() {
        TimelineElementInternal timelineElementInternal = TimelineElementInternal.builder()
                .elementId("NOTIFICATION_PAID.IUN_MPKG-MHLY-GXHE-202301-P-1.CODE_PPA30229167420586447277777777777")
                .category(TimelineElementCategoryInt.PAYMENT)
                .notificationSentAt(Instant.now())
                .paId("aa6e8c72-7944-4dcd-8668-f596447fec6d")
                .timestamp(Instant.now())
                .details(NotificationPaidDetailsInt.builder()
                        .creditorTaxId("creditorTaxId")
                        .noticeCode("noticeId")
                        .recipientType("PF")
                        .amount(1200)
                        .recIndex(0)
                        .paymentSourceChannel("PA")
                        .build())
                .build();

        TimelineElementEntity actual = mapper.dtoToEntity(timelineElementInternal);

        assertThat(actual).isNotNull();
        assertThat(actual.getIun()).isEqualTo(timelineElementInternal.getIun());
        assertThat(actual.getTimelineElementId()).isEqualTo(timelineElementInternal.getElementId());
        assertThat(actual.getPaId()).isEqualTo(timelineElementInternal.getPaId());
        assertThat(actual.getNotificationSentAt()).isEqualTo(timelineElementInternal.getNotificationSentAt());
        assertThat(actual.getCategory().name()).isEqualTo(timelineElementInternal.getCategory().name());
        assertThat(actual.getTimestamp()).isEqualTo(timelineElementInternal.getTimestamp());

        // verifica details
        NotificationPaidDetailsInt details = (NotificationPaidDetailsInt) timelineElementInternal.getDetails();
        assertThat(actual.getDetails()).isNotNull();
        assertThat(actual.getDetails().getRecIndex()).isEqualTo(details.getRecIndex());
        assertThat(actual.getDetails().getCreditorTaxId()).isEqualTo(details.getCreditorTaxId());
        assertThat(actual.getDetails().getNoticeCode()).isEqualTo(details.getNoticeCode());
        assertThat(actual.getDetails().getRecipientType()).isEqualTo(details.getRecipientType());
        assertThat(actual.getDetails().getPaymentSourceChannel()).isEqualTo(details.getPaymentSourceChannel());
        assertThat(actual.getDetails().getAmount()).isEqualTo(details.getAmount());

    }


    @Test
    void dtoToEntityPrepareAnalogDomicile_ServiceLevel_AR_REGISTERED_LETTER() {
        TimelineElementInternal timelineElementInternal = TimelineElementInternal.builder()
                .elementId("PREPARE_ANALOG_DOMICILE.IUN_ATVR-VRDL-GPQG-202304-J-1.RECINDEX_0.SENTATTEMPTMADE_0")
                .category(TimelineElementCategoryInt.PREPARE_ANALOG_DOMICILE)
                .notificationSentAt(Instant.now())
                .paId("paTestMv")
                .timestamp(Instant.now())
                .details(BaseAnalogDetailsInt.builder()
                        .recIndex(0)
                        .physicalAddress(null)
                        .serviceLevel(ServiceLevelInt.AR_REGISTERED_LETTER)
                        .sentAttemptMade(0)
                        .build())
                .build();

        TimelineElementEntity actual = mapper.dtoToEntity(timelineElementInternal);

        assertThat(actual).isNotNull();
        assertThat(actual.getIun()).isEqualTo(timelineElementInternal.getIun());
        assertThat(actual.getTimelineElementId()).isEqualTo(timelineElementInternal.getElementId());
        assertThat(actual.getPaId()).isEqualTo(timelineElementInternal.getPaId());
        assertThat(actual.getNotificationSentAt()).isEqualTo(timelineElementInternal.getNotificationSentAt());
        assertThat(actual.getCategory().name()).isEqualTo(timelineElementInternal.getCategory().name());
        assertThat(actual.getTimestamp()).isEqualTo(timelineElementInternal.getTimestamp());

        // verifica details
        BaseAnalogDetailsInt details = (BaseAnalogDetailsInt) timelineElementInternal.getDetails();
        assertThat(actual.getDetails()).isNotNull();
        assertThat(actual.getDetails().getRecIndex()).isEqualTo(details.getRecIndex());
        assertThat(actual.getDetails().getServiceLevel().name()).isEqualTo(details.getServiceLevel().name());
        assertThat(actual.getDetails().getSentAttemptMade()).isEqualTo(details.getSentAttemptMade());
    }

    @Test
    void dtoToEntityPrepareAnalogDomicile_ServiceLevel_REGISTERED_LETTER_890() {
        TimelineElementInternal timelineElementInternal = TimelineElementInternal.builder()
                .elementId("PREPARE_ANALOG_DOMICILE.IUN_ATVR-VRDL-GPQG-202304-J-1.RECINDEX_0.SENTATTEMPTMADE_0")
                .category(TimelineElementCategoryInt.PREPARE_ANALOG_DOMICILE)
                .notificationSentAt(Instant.now())
                .paId("paTestMv")
                .timestamp(Instant.now())
                .details(BaseAnalogDetailsInt.builder()
                        .recIndex(0)
                        .physicalAddress(null)
                        .serviceLevel(ServiceLevelInt.REGISTERED_LETTER_890)
                        .sentAttemptMade(0)
                        .build())
                .build();

        TimelineElementEntity actual = mapper.dtoToEntity(timelineElementInternal);

        assertThat(actual).isNotNull();
        assertThat(actual.getIun()).isEqualTo(timelineElementInternal.getIun());
        assertThat(actual.getTimelineElementId()).isEqualTo(timelineElementInternal.getElementId());
        assertThat(actual.getPaId()).isEqualTo(timelineElementInternal.getPaId());
        assertThat(actual.getNotificationSentAt()).isEqualTo(timelineElementInternal.getNotificationSentAt());
        assertThat(actual.getCategory().name()).isEqualTo(timelineElementInternal.getCategory().name());
        assertThat(actual.getTimestamp()).isEqualTo(timelineElementInternal.getTimestamp());

        // verifica details
        BaseAnalogDetailsInt details = (BaseAnalogDetailsInt) timelineElementInternal.getDetails();
        assertThat(actual.getDetails()).isNotNull();
        assertThat(actual.getDetails().getRecIndex()).isEqualTo(details.getRecIndex());
        assertThat(actual.getDetails().getServiceLevel().name()).isEqualTo(details.getServiceLevel().name());
        assertThat(actual.getDetails().getSentAttemptMade()).isEqualTo(details.getSentAttemptMade());

    }

    @Test
    void dtoToEntityAarCreationRequest() {
        TimelineElementInternal timelineElementInternal = TimelineElementInternal.builder()
                .elementId("AAR_CREATION_REQUEST.IUN_AAAA-WLRL-YUKX-202405-Z-1.RECINDEX_0")
                .category(TimelineElementCategoryInt.AAR_CREATION_REQUEST)
                .notificationSentAt(Instant.now())
                .paId("aa6e8c72-7944-4dcd-8668-f596447fec6d")
                .timestamp(Instant.now())
                .details(AarCreationRequestDetailsInt.builder()
                        .aarKey("safestorage://PN_AAR-mock.pdf")
                        .numberOfPages(2)
                        .recIndex(0)
                        .aarTemplateType(AarTemplateType.AAR_NOTIFICATION_RADD_ALT)
                        .build())
                .build();

        TimelineElementEntity actual = mapper.dtoToEntity(timelineElementInternal);

        assertThat(actual).isNotNull();
        assertThat(actual.getIun()).isEqualTo(timelineElementInternal.getIun());
        assertThat(actual.getTimelineElementId()).isEqualTo(timelineElementInternal.getElementId());
        assertThat(actual.getPaId()).isEqualTo(timelineElementInternal.getPaId());
        assertThat(actual.getNotificationSentAt()).isEqualTo(timelineElementInternal.getNotificationSentAt());
        assertThat(actual.getCategory().name()).isEqualTo(timelineElementInternal.getCategory().name());
        assertThat(actual.getTimestamp()).isEqualTo(timelineElementInternal.getTimestamp());

        // verifica details
        AarCreationRequestDetailsInt details = (AarCreationRequestDetailsInt) timelineElementInternal.getDetails();
        assertThat(actual.getDetails()).isNotNull();
        assertThat(actual.getDetails().getAarKey()).isEqualTo(details.getAarKey());
        assertThat(actual.getDetails().getNumberOfPages()).isEqualTo(details.getNumberOfPages());
        assertThat(actual.getDetails().getRecIndex()).isEqualTo(details.getRecIndex());
        assertThat(actual.getDetails().getAarTemplateType().name()).isEqualTo(details.getAarTemplateType().name());
    }

    @Test
    void dtoToEntityInformalDigitalDeliveryDetail() {
        Instant eventTimestamp = Instant.parse("2024-01-02T10:15:30Z");
        TimelineElementInternal timelineElementInternal = TimelineElementInternal.builder()
                .category(TimelineElementCategoryInt.SEND_DIGITAL_MESSAGE_PROGRESS)
                .details(SendDigitalMessageProgressDetailsInt.builder()
                        .recIndex(0)
                        .requestId("requestId")
                        .channel(DigitalChannelsInt.PEC)
                        .deliveryDetail(DigitalDeliveryDetailsInt.builder()
                                .code("DELIVERY_CODE")
                                .failureCause("failureCause")
                                .eventTimestamp(eventTimestamp)
                                .build())
                        .build())
                .build();

        TimelineElementEntity actual = mapper.dtoToEntity(timelineElementInternal);

        assertThat(actual.getDetails()).isNotNull();
        assertThat(actual.getDetails().getRequestId()).isEqualTo("requestId");
        assertThat(actual.getDetails().getChannel()).isEqualTo("PEC");
        assertThat(actual.getDetails().getDeliveryDetail()).isNotNull();
        assertThat(actual.getDetails().getDeliveryDetail().getCode()).isEqualTo("DELIVERY_CODE");
        assertThat(actual.getDetails().getDeliveryDetail().getFailureCause()).isEqualTo("failureCause");
        assertThat(actual.getDetails().getDeliveryDetail().getEventTimestamp()).isEqualTo(eventTimestamp);
    }

    @Test
    void dtoToEntityInformalAnalogDeliveryDetail() {
        Instant eventTimestamp = Instant.parse("2024-01-02T10:15:30Z");
        TimelineElementInternal timelineElementInternal = TimelineElementInternal.builder()
                .category(TimelineElementCategoryInt.SEND_ANALOG_MESSAGE_PROGRESS)
                .details(SendAnalogMessageProgressDetailsInt.builder()
                        .recIndex(0)
                        .deliveryType(AnalogDeliveryTypeInt.RS)
                        .deliveryDetail(AnalogDeliveryDetailsInt.builder()
                                .code("DELIVERY_CODE")
                                .failureCause("failureCause")
                                .eventTimestamp(eventTimestamp)
                                .build())
                        .build())
                .build();

        TimelineElementEntity actual = mapper.dtoToEntity(timelineElementInternal);

        assertThat(actual.getDetails()).isNotNull();
        assertThat(actual.getDetails().getDeliveryType()).isEqualTo(it.pagopa.pn.timelineservice.middleware.dao.dynamo.entity.AnalogDeliveryTypeEntity.RS);
        assertThat(actual.getDetails().getDeliveryDetail()).isNotNull();
        assertThat(actual.getDetails().getDeliveryDetail().getCode()).isEqualTo("DELIVERY_CODE");
        assertThat(actual.getDetails().getDeliveryDetail().getFailureCause()).isEqualTo("failureCause");
        assertThat(actual.getDetails().getDeliveryDetail().getEventTimestamp()).isEqualTo(eventTimestamp);
    }

    @Test
    void dtoToEntityInformalStringAndListFields() {
        TimelineElementEntity coverpageEntity = mapper.dtoToEntity(TimelineElementInternal.builder()
                .category(TimelineElementCategoryInt.COVERPAGE_CREATION_REQUEST)
                .details(CoverpageCreationRequestDetailsInt.builder()
                        .recIndex(0)
                        .fileKey("fileKey")
                        .build())
                .build());

        TimelineElementEntity workflowDoneEntity = mapper.dtoToEntity(TimelineElementInternal.builder()
                .category(TimelineElementCategoryInt.WORKFLOW_DONE)
                .details(WorkflowDoneDetailsInt.builder()
                        .recIndex(0)
                        .sourceElementId("sourceElementId")
                        .build())
                .build());

        TimelineElementEntity reachedEntity = mapper.dtoToEntity(TimelineElementInternal.builder()
                .category(TimelineElementCategoryInt.REACHED)
                .details(ReachedDetailsInt.builder()
                        .recIndex(0)
                        .channel("PEC")
                        .sourceElementId("reachedSourceElementId")
                        .build())
                .build());

        TimelineElementEntity workflowEndedReachedEntity = mapper.dtoToEntity(TimelineElementInternal.builder()
                .category(TimelineElementCategoryInt.WORKFLOW_ENDED_REACHED)
                .details(WorkflowEndedReachedDetailsInt.builder()
                        .recIndex(0)
                        .channels(List.of("PEC", "SMS"))
                        .build())
                .build());

        TimelineElementEntity requestIdEntity = mapper.dtoToEntity(TimelineElementInternal.builder()
                .category(TimelineElementCategoryInt.SEND_DIGITAL_MESSAGE_FEEDBACK)
                .details(SendDigitalMessageFeedbackDetailsInt.builder()
                        .recIndex(0)
                        .requestId("feedbackRequestId")
                        .channel(DigitalChannelsInt.APPIO)
                        .build())
                .build());

        assertThat(coverpageEntity.getDetails().getFileKey()).isEqualTo("fileKey");
        assertThat(workflowDoneEntity.getDetails().getSourceElementId()).isEqualTo("sourceElementId");
        assertThat(reachedEntity.getDetails().getChannel()).isEqualTo("PEC");
        assertThat(reachedEntity.getDetails().getSourceElementId()).isEqualTo("reachedSourceElementId");
        assertThat(workflowEndedReachedEntity.getDetails().getChannels()).containsExactly("PEC", "SMS");
        assertThat(requestIdEntity.getDetails().getRequestId()).isEqualTo("feedbackRequestId");
        assertThat(requestIdEntity.getDetails().getChannel()).isEqualTo("APPIO");
    }

    private TimelineElementInternal buildTimelineElementInternal(CommunicationType communicationType) {
        Instant instant = Instant.parse("2021-09-16T15:23:00.00Z");
        TimelineElementDetailsInt elementDetailsInt = parseDetailsFromEntity(TimelineElementDetailsEntity.builder()
                .recIndex(0)
                .notificationCost(100)
                .build(), TimelineElementCategoryInt.NOTIFICATION_VIEWED);

        LegalFactsIdInt legalFactsIdInt = buildLegalFactsIdInt();
        List<LegalFactsIdInt> legalFactsIdInts = new ArrayList<>();
        legalFactsIdInts.add(legalFactsIdInt);

        return TimelineElementInternal.builder()
                .iun("001")
                .elementId("002")
                .paId("003")
                .category(TimelineElementCategoryInt.REQUEST_ACCEPTED)
                .timestamp(instant)
                .details(elementDetailsInt)
                .legalFactsIds(legalFactsIdInts)
                .notificationSentAt(Instant.now())
                .reworkId("reworkId")
                .communicationType(communicationType)
                .build();
    }

    private TimelineElementDetailsInt parseDetailsFromEntity(TimelineElementDetailsEntity entity, TimelineElementCategoryInt category) {
        return SmartMapper.mapToClass(entity, category.getDetailsJavaClass());
    }

    private LegalFactsIdInt buildLegalFactsIdInt() {
        return LegalFactsIdInt.builder()
                .key("001")
                .category(LegalFactCategoryInt.ANALOG_DELIVERY)
                .build();
    }
}