package it.pagopa.pn.timelineservice.utils.extraction;

import it.pagopa.pn.commons.exceptions.PnInternalException;
import it.pagopa.pn.timelineservice.dto.timeline.TimelineElementInternal;
import it.pagopa.pn.timelineservice.utils.extraction.extractor.ExtractorKey;
import it.pagopa.pn.timelineservice.utils.extraction.extractor.TimelineDataExtractor;
import it.pagopa.pn.timelineservice.utils.extraction.mapper.ExtractionMapper;
import it.pagopa.pn.timelineservice.utils.extraction.model.ExtractionResult;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * Engine per l'estrazione di dati da una timeline usando estrattori personalizzati.
 * <h4>Esempio di utilizzo tramite un ExtractionMapper</h4>
 * In questo scenario viene utilizzato un mapper che dichiara internamente quali estrattori vuole utilizzare e in che modo
 * verrà effettuata la mappatura dei dati estratti:
 * <pre>{@code
 * // Il DeliveryInfoMapper restituirà un oggetto DeliveryInfo popolato con i dati estratti
 * DeliveryInfo deliveryInfo = new TimelineDataExtractionEngine.EngineBuilder()
 *  .executeAndMap(timelineElements, new DeliveryInfoMapper(recIndex))
 * }</pre>
 * <h4>Esempio di utilizzo senza Mapper</h4>
 * In questo scenario è necessario istanziare manualmente gli estrattori e cercare di ottenere i risultati:
 * <pre>{@code
 *  ExtractionResult result = new TimelineDataExtractionEngine.EngineBuilder()
 *      .add(new SchedulingAnalogDateExtractor(recIndex))
 *      .execute(timelineElements)
 *  // Sfrutto la chiave dell'estrattore aggiunto per ottenere il risultato
 *  DeliveryModeInt deliveryMode = result.get(DeliveryModeExtractor.KEY).orElse(null);
 *  System.out.println("Delivery Mode: " + result.getDeliveryMode());
 * }</pre>
 *
 * @see TimelineDataExtractor
 * @see ExtractionMapper
 *
 */
@Slf4j
public class TimelineDataExtractionEngine {

    public ExtractionResult extract(
            List<TimelineElementInternal> timeline,
            List<TimelineDataExtractor<?>> extractors) {

        if (extractors.isEmpty()) {
            return new ExtractionResult(Collections.emptyMap());
        }

        log.debug("Starting timeline data extraction with {} extractors", joinExtractorNames(extractors));
        Set<TimelineDataExtractor<?>> activeExtractors = new HashSet<>(extractors);

        for (TimelineElementInternal element : timeline) {
            Iterator<TimelineDataExtractor<?>> it = activeExtractors.iterator();

            while (it.hasNext()) {
                TimelineDataExtractor<?> extractor = it.next();
                boolean completed = extractor.process(element);

                if (completed) {
                    log.debug("Extractor {} completed extraction early", extractor.getKey().getName());
                    it.remove();
                }
            }

            if (activeExtractors.isEmpty()) {
                log.debug("All extractors have completed extraction early, stopping iteration over timeline elements");
                break;
            }
        }


        log.debug("Post-processing {} active extractors", joinExtractorNames(activeExtractors));
        activeExtractors.forEach(TimelineDataExtractor::postProcess);

        // Costruisce il risultato usando le chiavi degli estrattori
        Map<ExtractorKey<?>, Optional<?>> results = new HashMap<>();
        for (TimelineDataExtractor<?> extractor : extractors) {
            results.put(extractor.getKey(), extractor.getResult());
        }

        return new ExtractionResult(results);
    }

    private static @NotNull String joinExtractorNames(Collection<TimelineDataExtractor<?>> extractors) {
        return extractors.stream()
                .map(e -> e.getKey().getName())
                .reduce((a, b) -> a + ", " + b).orElse("");
    }

    public static class EngineBuilder {
        private static final String DUPLICATE_EXTRACTOR_KEY_ERROR_CODE = "duplicate_extractor_key_error_code";
        private final List<TimelineDataExtractor<?>> extractors = new ArrayList<>();

        /**
         * Aggiunge un estrattore alla lista di quelli da eseguire. (Non sono permessi estrattori duplicati con la stessa chiave.)
         * <strong>Non dovrebbe essere richiamato se si ha intenzione di utilizzare l'engine tramite il metodo executeAndMap</strong>
         */
        public EngineBuilder add(TimelineDataExtractor<?> extractor) {
            boolean duplicate = extractors.stream()
                    .anyMatch(e -> e.getKey().equals(extractor.getKey()));
            if (duplicate) {
                throw new PnInternalException("Duplicate extractor with key: " + extractor.getKey(), DUPLICATE_EXTRACTOR_KEY_ERROR_CODE);
            }
            extractors.add(extractor);
            return this;
        }

        /**
         * Esegue l'estrazione dei dati richiesti.
         *
         * @param timeline Lista degli elementi della timeline da cui estrarre i dati.
         * @return ExtractionResult con i dati estratti.
         */
        public ExtractionResult execute(List<TimelineElementInternal> timeline) {
            TimelineDataExtractionEngine engine = new TimelineDataExtractionEngine();
            // Convert set to list for engine.extract
            return engine.extract(timeline, extractors);
        }

        /**
         * Esegue l'estrazione e la mappatura usando l'istanza di tipo ExtractionMapper fornita.
         *
         * @param timeline Lista degli elementi della timeline da cui estrarre i dati.
         * @param mapper Mapper che definisce gli estrattori da usare e la logica di mappatura.
         * @return Oggetto di dominio mappato con i dati estratti.
         */
        public <T> T executeAndMap(List<TimelineElementInternal> timeline, ExtractionMapper<T> mapper) {
            mapper.extractors().forEach(this::add);
            ExtractionResult result = execute(timeline);
            return mapper.map(result);
        }
    }
}
