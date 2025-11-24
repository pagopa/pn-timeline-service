package it.pagopa.pn.timelineservice.utils.extraction.mapper;

import it.pagopa.pn.timelineservice.utils.extraction.extractor.ExtractorKey;
import it.pagopa.pn.timelineservice.utils.extraction.extractor.TimelineDataExtractor;
import it.pagopa.pn.timelineservice.utils.extraction.model.ExtractionResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

class ExtractionMapperTest {

    private TimelineDataExtractor extractor;

    @BeforeEach
    void setUp() {
        extractor = mock(TimelineDataExtractor.class);
    }

    @Test
    void ofShouldMapResultAndReturnExtractors() {
        ExtractorKey<String> key = ExtractorKey.of("testKey", String.class);

        ExtractionMapper<String> mapper = ExtractionMapper.of(
                result -> result.get(key).orElse("default"),
                () -> List.of(extractor)
        );

        ExtractionResult result = new ExtractionResult(Map.of(key, Optional.of("mappedValue")));

        assertEquals("mappedValue", mapper.map(result));
        List<TimelineDataExtractor<?>> extractors = mapper.extractors();
        assertEquals(1, extractors.size());
        assertEquals(extractor, extractors.getFirst());
    }
}
