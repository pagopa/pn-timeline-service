package it.pagopa.pn.timelineservice.utils.extraction.extractor;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ExtractorKeyTest {

    @Test
    void createsExtractorKeyWithCorrectNameAndType() {
        ExtractorKey<String> key = ExtractorKey.of("testKey", String.class);
        Assertions.assertEquals("testKey", key.getName());
        Assertions.assertEquals(String.class, key.getType());
    }

    @Test
    void extractorKeysWithSameNameAndTypeAreEqual() {
        ExtractorKey<Integer> key1 = ExtractorKey.of("number", Integer.class);
        ExtractorKey<Integer> key2 = ExtractorKey.of("number", Integer.class);
        Assertions.assertEquals(key1, key2);
        Assertions.assertEquals(key1.hashCode(), key2.hashCode());
    }

    @Test
    void extractorKeysWithDifferentNamesAreNotEqual() {
        ExtractorKey<Integer> key1 = ExtractorKey.of("number1", Integer.class);
        ExtractorKey<Integer> key2 = ExtractorKey.of("number2", Integer.class);
        Assertions.assertNotEquals(key1, key2);
    }

    @Test
    void extractorKeysWithDifferentTypesAreNotEqual() {
        ExtractorKey<String> key1 = ExtractorKey.of("key", String.class);
        ExtractorKey<Integer> key2 = ExtractorKey.of("key", Integer.class);
        Assertions.assertNotEquals(key1, key2);
    }
}
