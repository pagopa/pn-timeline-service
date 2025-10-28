package it.pagopa.pn.timelineservice.utils.extraction.model;

import it.pagopa.pn.commons.exceptions.PnInternalException;
import it.pagopa.pn.timelineservice.utils.extraction.extractor.ExtractorKey;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ExtractionResultTest {

    @Test
    void getReturnsValueForExistingKey() {
        ExtractorKey<String> key = ExtractorKey.of("testKey", String.class);
        ExtractionResult result = new ExtractionResult(Map.of(key, Optional.of("value")));

        assertEquals(Optional.of("value"), result.get(key));
    }

    @Test
    void getThrowsForMissingKey() {
        ExtractorKey<String> key = ExtractorKey.of("missingKey", String.class);
        ExtractionResult result = new ExtractionResult(Collections.emptyMap());

        assertThrows(PnInternalException.class, () -> result.get(key));
    }

    @Test
    void getOrDefaultReturnsValueIfPresent() {
        ExtractorKey<Integer> key = ExtractorKey.of("intKey", Integer.class);
        ExtractionResult result = new ExtractionResult(Map.of(key, Optional.of(42)));

        assertEquals(42, result.getOrDefault(key, 0));
    }

    @Test
    void getOrDefaultReturnsDefaultIfAbsent() {
        ExtractorKey<Integer> key = ExtractorKey.of("intKey", Integer.class);
        ExtractionResult result = new ExtractionResult(Map.of(key, Optional.empty()));

        assertEquals(99, result.getOrDefault(key, 99));
    }

    @Test
    void hasKeyReturnsTrueIfKeyPresent() {
        ExtractorKey<Boolean> key = ExtractorKey.of("boolKey", Boolean.class);
        ExtractionResult result = new ExtractionResult(Map.of(key, Optional.of(true)));

        assertTrue(result.hasKey(key));
    }

    @Test
    void hasKeyReturnsFalseIfKeyAbsent() {
        ExtractorKey<Boolean> key = ExtractorKey.of("boolKey", Boolean.class);
        ExtractionResult result = new ExtractionResult(Collections.emptyMap());

        assertFalse(result.hasKey(key));
    }

    @Test
    void hasValueReturnsTrueIfValuePresent() {
        ExtractorKey<String> key = ExtractorKey.of("strKey", String.class);
        ExtractionResult result = new ExtractionResult(Map.of(key, Optional.of("abc")));

        assertTrue(result.hasValue(key));
    }

    @Test
    void hasValueReturnsFalseIfValueEmpty() {
        ExtractorKey<String> key = ExtractorKey.of("strKey", String.class);
        ExtractionResult result = new ExtractionResult(Map.of(key, Optional.empty()));

        assertFalse(result.hasValue(key));
    }

    @Test
    void getKeysReturnsAllKeys() {
        ExtractorKey<String> key1 = ExtractorKey.of("k1", String.class);
        ExtractorKey<Integer> key2 = ExtractorKey.of("k2", Integer.class);
        ExtractionResult result = new ExtractionResult(Map.of(
                key1, Optional.of("v"),
                key2, Optional.of(1)
        ));

        Set<ExtractorKey<?>> keys = result.getKeys();
        assertTrue(keys.contains(key1));
        assertTrue(keys.contains(key2));
        assertEquals(2, keys.size());
    }
}
