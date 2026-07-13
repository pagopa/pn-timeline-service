package it.pagopa.pn.timelineservice.service.mapper;

import it.pagopa.pn.timelineservice.dto.legalfacts.LegalFactCategoryInt;
import it.pagopa.pn.timelineservice.dto.timeline.CommunicationType;
import it.pagopa.pn.timelineservice.dto.timeline.ReworkRequestTypeEnum;
import it.pagopa.pn.timelineservice.dto.timeline.TimelineElementInternal;
import it.pagopa.pn.timelineservice.dto.timeline.details.TimelineElementCategoryInt;
import it.pagopa.pn.timelineservice.generated.openapi.server.v1.dto.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.Instant;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class TimelineElementMapperTest {

    /* Attenzione è un vincolo importante che se l'oggetto generato NON ha communicationType, sia trattato come LEGAL nel modello interno */
    private static Stream<Arguments> provideCommunicationTypeArgs() {
        return Stream.of(
                Arguments.of(null, CommunicationType.LEGAL),
                Arguments.of(it.pagopa.pn.timelineservice.generated.openapi.server.v1.dto.CommunicationType.INFORMAL, CommunicationType.INFORMAL),
                Arguments.of(it.pagopa.pn.timelineservice.generated.openapi.server.v1.dto.CommunicationType.LEGAL, CommunicationType.LEGAL)
        );
    }

    @ParameterizedTest
    @MethodSource("provideCommunicationTypeArgs")
    void externalToInternalMapsAllFieldsCorrectly(it.pagopa.pn.timelineservice.generated.openapi.server.v1.dto.CommunicationType generatedCommunicationType, CommunicationType expectedCommunicationType) {
        TimelineElementMapper mapper = new TimelineElementMapper();

        StatusInfo statusInfo = new StatusInfo()
                .actual("ACCEPTED")
                .statusChanged(true)
                .statusChangeTimestamp(Instant.now());

        LegalFactsId legalFactsId = new LegalFactsId()
                .key("key1")
                .category(LegalFactsId.CategoryEnum.ANALOG_DELIVERY);

        TimelineElementDetails details = buildAarGenerationDetails();

        TimelineElement timelineElement = new TimelineElement()
                .iun("iun")
                .elementId("elementId")
                .category(TimelineCategory.AAR_GENERATION)
                .timestamp(Instant.now())
                .details(details)
                .legalFactsIds(List.of(legalFactsId))
                .statusInfo(statusInfo)
                .notificationSentAt(Instant.now())
                .paId("paId")
                .reworkId("reworkId")
                .reworkRequestType(it.pagopa.pn.timelineservice.generated.openapi.server.v1.dto.TimelineElement.ReworkRequestTypeEnum.REWORK)
                .communicationType(generatedCommunicationType);

        TimelineElementInternal result = mapper.externalToInternal(timelineElement);

        assertEquals("iun", result.getIun());
        assertEquals("elementId", result.getElementId());
        assertEquals(TimelineElementCategoryInt.AAR_GENERATION, result.getCategory());
        assertEquals(timelineElement.getTimestamp(), result.getTimestamp());
        assertEquals(timelineElement.getNotificationSentAt(), result.getNotificationSentAt());
        assertEquals("paId", result.getPaId());
        assertEquals("reworkId", result.getReworkId());
        assertEquals(ReworkRequestTypeEnum.REWORK, result.getReworkRequestType());
        assertEquals(expectedCommunicationType, result.getCommunicationType());
        assertNotNull(result.getStatusInfo());
        assertEquals("ACCEPTED", result.getStatusInfo().getActual());
        assertTrue(result.getStatusInfo().isStatusChanged());
        assertNotNull(result.getLegalFactsIds());
        assertEquals(1, result.getLegalFactsIds().size());
        assertEquals("key1", result.getLegalFactsIds().getFirst().getKey());
        assertEquals(LegalFactCategoryInt.ANALOG_DELIVERY, result.getLegalFactsIds().getFirst().getCategory());
    }

    private AarGenerationDetails buildAarGenerationDetails() {
        AarGenerationDetails aarGenerationDetails = new AarGenerationDetails();
        aarGenerationDetails.setGeneratedAarUrl("http://example.com/aar");
        aarGenerationDetails.setCategoryType("AAR_GENERATION");
        aarGenerationDetails.setNumberOfPages(3);
        aarGenerationDetails.setRecIndex(0);
        return aarGenerationDetails;
    }

    @Test
    void externalToInternalWithNullLegalFactsReturnsNullLegalFactsList() {
        TimelineElementMapper mapper = new TimelineElementMapper();

        TimelineElement timelineElement = new TimelineElement()
                .iun("iun")
                .elementId("elementId")
                .category(TimelineCategory.AAR_GENERATION)
                .timestamp(Instant.now())
                .details(buildAarGenerationDetails())
                .legalFactsIds(null);

        TimelineElementInternal result = mapper.externalToInternal(timelineElement);

        assertNull(result.getLegalFactsIds());
    }

    @Test
    void externalToInternalWithNullStatusInfoReturnsNullStatusInfo() {
        TimelineElementMapper mapper = new TimelineElementMapper();

        TimelineElement timelineElement = new TimelineElement()
                .iun("iun")
                .elementId("elementId")
                .category(TimelineCategory.AAR_GENERATION)
                .timestamp(Instant.now())
                .details(buildAarGenerationDetails())
                .statusInfo(null);

        TimelineElementInternal result = mapper.externalToInternal(timelineElement);

        assertNull(result.getStatusInfo());
    }

    @Test
    void externalToInternalWithNullReworkRequestTypeReturnsNullReworkRequestType() {
        TimelineElementMapper mapper = new TimelineElementMapper();

        TimelineElement timelineElement = new TimelineElement()
                .iun("iun")
                .elementId("elementId")
                .category(TimelineCategory.AAR_GENERATION)
                .timestamp(Instant.now())
                .details(buildAarGenerationDetails())
                .reworkRequestType(null);

        TimelineElementInternal result = mapper.externalToInternal(timelineElement);

        assertNull(result.getReworkRequestType());
    }


    @Test
    void externalToInternalWithStatusChangedFalseMapsCorrectly() {
        TimelineElementMapper mapper = new TimelineElementMapper();

        StatusInfo statusInfo = new StatusInfo()
                .actual("IN_VALIDATION")
                .statusChanged(false)
                .statusChangeTimestamp(Instant.now());

        TimelineElement timelineElement = new TimelineElement()
                .iun("iun")
                .elementId("elementId")
                .category(TimelineCategory.AAR_GENERATION)
                .timestamp(Instant.now())
                .details(buildAarGenerationDetails())
                .statusInfo(statusInfo);

        TimelineElementInternal result = mapper.externalToInternal(timelineElement);

        assertFalse(result.getStatusInfo().isStatusChanged());
    }

    @Test
    void externalToInternalMapsMultipleLegalFactsCorrectly() {
        TimelineElementMapper mapper = new TimelineElementMapper();

        List<LegalFactsId> legalFactsIds = List.of(
                new LegalFactsId().key("key1").category(LegalFactsId.CategoryEnum.ANALOG_DELIVERY),
                new LegalFactsId().key("key2").category(LegalFactsId.CategoryEnum.DIGITAL_DELIVERY)
        );

        TimelineElement timelineElement = new TimelineElement()
                .iun("iun")
                .elementId("elementId")
                .category(TimelineCategory.AAR_GENERATION)
                .timestamp(Instant.now())
                .details(buildAarGenerationDetails())
                .legalFactsIds(legalFactsIds);

        TimelineElementInternal result = mapper.externalToInternal(timelineElement);

        assertEquals(2, result.getLegalFactsIds().size());
        assertEquals("key1", result.getLegalFactsIds().get(0).getKey());
        assertEquals(LegalFactCategoryInt.ANALOG_DELIVERY, result.getLegalFactsIds().get(0).getCategory());
        assertEquals("key2", result.getLegalFactsIds().get(1).getKey());
        assertEquals(LegalFactCategoryInt.DIGITAL_DELIVERY, result.getLegalFactsIds().get(1).getCategory());
    }
}