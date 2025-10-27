package it.pagopa.pn.timelineservice.utils.extraction.mapper;

import it.pagopa.pn.timelineservice.utils.extraction.TimelineDataExtractionEngine;
import it.pagopa.pn.timelineservice.utils.extraction.extractor.TimelineDataExtractor;
import it.pagopa.pn.timelineservice.utils.extraction.model.ExtractionResult;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Interfaccia da implementare per dichiarare delle classi che gestiscono il processo di estrazione dei dati da una timeline
 * mediante l'utilizzo di estrattori personalizzati e la modalità di mapping in un oggetto del dominio specifico.
 * @param <T> tipo di oggetto di dominio risultante dall'estrazione
 * @see TimelineDataExtractor
 * @see TimelineDataExtractionEngine
 */
public interface ExtractionMapper <T> {
    /**
     * Mappa i risultati di estrazione sull’oggetto di dominio.
     */
    T map(ExtractionResult result);

    /**
     * Restituisce gli extractor da eseguire per popolare i dati richiesti dal mapper.
     */
    List<TimelineDataExtractor<?>> extractors();

    /**
     * Factory per lambda lightweight con Supplier (nessun parametro).
     * <br/>
     * Permette di costruire un ExtractionMapper senza dover creare una classe dedicata.
     *
     * <h4>Esempio di utilizzo:</h4>
     * <pre>{@code
     *  // Mapper utilizzabile per ottenere la refinementOrViewDate di un record specifico
     *  ExtractionMapper.of(
     *      result -> result.get(RefinementOrViewDateExtractor.KEY).orElse(null),
     *      () -> List.of(new RefinementOrViewDateExtractor(recIndex))
     *  ))
     * }
     *
     * @param mapperFn funzione di mapping da ExtractionResult a T
     * @param extractorsSupplier fornitore della lista di estrattori
     * @param <T> tipo di oggetto di dominio risultante
     * @return istanza di ExtractionMapper che utilizza le funzioni fornite
     */
    static <T> ExtractionMapper<T> of(
            Function<ExtractionResult, T> mapperFn,
            Supplier<List<TimelineDataExtractor<?>>> extractorsSupplier
    ) {
        return new ExtractionMapper<>() {
            @Override
            public T map(ExtractionResult result) {
                return mapperFn.apply(result);
            }

            @Override
            public List<TimelineDataExtractor<?>> extractors() {
                return extractorsSupplier.get();
            }
        };
    }
}
