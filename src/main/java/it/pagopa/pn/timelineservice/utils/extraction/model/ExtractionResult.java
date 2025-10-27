package it.pagopa.pn.timelineservice.utils.extraction.model;

import it.pagopa.pn.commons.exceptions.PnInternalException;
import it.pagopa.pn.timelineservice.utils.extraction.extractor.ExtractorKey;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Risultato dell'estrazione con accesso type-safe tramite chiavi
 */
public class ExtractionResult {
    private static final String INVALID_EXTRACTOR_ERROR_CODE = "invalid_extractor_key";
    private final Map<ExtractorKey<?>, Optional<?>> results;

    public ExtractionResult(Map<ExtractorKey<?>, Optional<?>> results) {
        this.results = Collections.unmodifiableMap(results);
    }

    /**
     * Recupera un valore in modo type-safe usando la chiave tipizzata
     */
    @SuppressWarnings("unchecked")
    public <T> Optional<T> get(ExtractorKey<T> key) {
        Optional<?> result = results.get(key);
        if (result == null) {
            throw new PnInternalException(
                    "Nessun estrattore registrato per la chiave: " + key.getName(), INVALID_EXTRACTOR_ERROR_CODE
            );
        }
        return (Optional<T>) result;
    }

    /**
     * Recupera un valore con default se non presente
     */
    public <T> T getOrDefault(ExtractorKey<T> key, T defaultValue) {
        return get(key).orElse(defaultValue);
    }

    /**
     * Verifica se una chiave è presente nei risultati
     */
    public boolean hasKey(ExtractorKey<?> key) {
        return results.containsKey(key);
    }

    /**
     * Verifica se una chiave ha un valore presente (non empty)
     */
    public boolean hasValue(ExtractorKey<?> key) {
        return get(key).isPresent();
    }

    /**
     * Ottiene tutte le chiavi disponibili
     */
    public Set<ExtractorKey<?>> getKeys() {
        return results.keySet();
    }
}
