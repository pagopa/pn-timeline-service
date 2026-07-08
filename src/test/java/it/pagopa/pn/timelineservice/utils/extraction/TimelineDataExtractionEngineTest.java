package it.pagopa.pn.timelineservice.utils.extraction;

import it.pagopa.pn.commons.exceptions.PnInternalException;
import it.pagopa.pn.timelineservice.dto.timeline.TimelineElementInternal;
import it.pagopa.pn.timelineservice.dto.timeline.details.legal.ExtendedDeliveryModeInt;
import it.pagopa.pn.timelineservice.dto.timeline.details.legal.ScheduleAnalogWorkflowDetailsInt;
import it.pagopa.pn.timelineservice.dto.timeline.details.legal.SendDigitalDetailsInt;
import it.pagopa.pn.timelineservice.dto.timeline.details.TimelineElementCategoryInt;
import it.pagopa.pn.timelineservice.utils.extraction.extractor.DeliveryModeExtractor;
import it.pagopa.pn.timelineservice.utils.extraction.mapper.ExtractionMapper;
import it.pagopa.pn.timelineservice.utils.extraction.model.ExtractionResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class TimelineDataExtractionEngineTest {

    private ExtractionMapper<String> mapper;

    @BeforeEach
    void setUp() {
        mapper = mock(ExtractionMapper.class);
        Mockito.when(mapper.extractors()).thenReturn(List.of(new DeliveryModeExtractor(0)));
        Mockito.when(mapper.map(Mockito.any())).thenReturn(ExtendedDeliveryModeInt.DIGITAL.getValue());
    }

    @Test
    void extractReturnsEmptyResultIfNoExtractors() {
        TimelineDataExtractionEngine engine = new TimelineDataExtractionEngine();
        List<TimelineElementInternal> timeline = List.of();
        ExtractionResult result = engine.extract(timeline, List.of());
        assertTrue(result.getKeys().isEmpty());
    }

    @Test
    void extractDeliveryModeDigital() {
        TimelineDataExtractionEngine engine = new TimelineDataExtractionEngine();
        int recIndex = 0;
        SendDigitalDetailsInt details = new SendDigitalDetailsInt();
        details.setRecIndex(recIndex);
        TimelineElementInternal element = TimelineElementInternal.builder()
                .details(details)
                .category(TimelineElementCategoryInt.SEND_DIGITAL_DOMICILE)
                .build();
        DeliveryModeExtractor extractor = new DeliveryModeExtractor(recIndex);
        ExtractionResult result = engine.extract(List.of(element), List.of(extractor));
        assertEquals(Optional.of(ExtendedDeliveryModeInt.DIGITAL), result.get(DeliveryModeExtractor.KEY));
    }

    @Test
    void extractDeliveryModeAnalog() {
        TimelineDataExtractionEngine engine = new TimelineDataExtractionEngine();
        int recIndex = 1;
        ScheduleAnalogWorkflowDetailsInt details = new ScheduleAnalogWorkflowDetailsInt();
        details.setRecIndex(recIndex);
        TimelineElementInternal element = TimelineElementInternal.builder()
                .details(details)
                .category(TimelineElementCategoryInt.SCHEDULE_ANALOG_WORKFLOW)
                .build();
        DeliveryModeExtractor extractor = new DeliveryModeExtractor(recIndex);
        ExtractionResult result = engine.extract(List.of(element), List.of(extractor));
        assertEquals(Optional.of(ExtendedDeliveryModeInt.ANALOG), result.get(DeliveryModeExtractor.KEY));
    }

    @Test
    void extractDeliveryModeUnknown() {
        TimelineDataExtractionEngine engine = new TimelineDataExtractionEngine();
        int recIndex = 2;
        TimelineElementInternal element = TimelineElementInternal.builder()
                .details(null)
                .build();
        DeliveryModeExtractor extractor = new DeliveryModeExtractor(recIndex);
        ExtractionResult result = engine.extract(List.of(element), List.of(extractor));
        assertEquals(Optional.of(ExtendedDeliveryModeInt.UNKNOWN), result.get(DeliveryModeExtractor.KEY));
    }

    @Test
    void engineBuilderThrowsOnDuplicateExtractorKey() {
        TimelineDataExtractionEngine.EngineBuilder builder = new TimelineDataExtractionEngine.EngineBuilder();
        DeliveryModeExtractor extractor1 = new DeliveryModeExtractor(0);
        DeliveryModeExtractor extractor2 = new DeliveryModeExtractor(0); // Same key
        builder.add(extractor1);
        PnInternalException ex = assertThrows(PnInternalException.class, () -> builder.add(extractor2));
        assertTrue(ex.getProblem().getDetail().contains("Duplicate extractor"));
    }

    @Test
    void engineBuilderExecuteReturnsExtractionResult() {
        TimelineDataExtractionEngine.EngineBuilder builder = new TimelineDataExtractionEngine.EngineBuilder();
        int recIndex = 0;
        SendDigitalDetailsInt details = new SendDigitalDetailsInt();
        details.setRecIndex(recIndex);
        TimelineElementInternal element = TimelineElementInternal.builder()
                .details(details)
                .category(TimelineElementCategoryInt.SEND_DIGITAL_DOMICILE)
                .build();
        DeliveryModeExtractor extractor = new DeliveryModeExtractor(recIndex);
        builder.add(extractor);
        ExtractionResult result = builder.execute(List.of(element));
        assertEquals(Optional.of(ExtendedDeliveryModeInt.DIGITAL), result.get(DeliveryModeExtractor.KEY));
    }

    @Test
    void engineBuilderExecuteAndMapReturnsMappedValue() {
        TimelineDataExtractionEngine.EngineBuilder builder = new TimelineDataExtractionEngine.EngineBuilder();
        int recIndex = 0;
        SendDigitalDetailsInt details = new SendDigitalDetailsInt();
        details.setRecIndex(recIndex);
        TimelineElementInternal element = TimelineElementInternal.builder()
                .details(details)
                .build();

        String mappedValue = builder.executeAndMap(List.of(element), mapper);
        assertEquals(ExtendedDeliveryModeInt.DIGITAL.getValue(), mappedValue);
    }

}
