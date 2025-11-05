package it.pagopa.pn.timelineservice.utils.extraction.extractor;

import lombok.Data;

@Data
public class ExtractorKey<T> {
    private final String name;
    private final Class<T> type;

    private ExtractorKey(String name, Class<T> type) {
        this.name = name;
        this.type = type;
    }

    public static <T> ExtractorKey<T> of(String name, Class<T> type) {
        return new ExtractorKey<>(name, type);
    }
}
