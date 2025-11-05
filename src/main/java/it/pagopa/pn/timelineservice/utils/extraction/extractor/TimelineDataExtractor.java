package it.pagopa.pn.timelineservice.utils.extraction.extractor;

import it.pagopa.pn.timelineservice.dto.timeline.TimelineElementInternal;

import java.util.Optional;

/**
 * Interfaccia per l'estrazione di dati dalla timeline.
 * L'estrattore processa gli elementi della timeline uno ad uno, e può decidere di terminare l'estrazione in anticipo
 * se ritiene di aver raccolto tutte le informazioni necessarie e settato il valore desiderato nell'attributo result.
 * In caso fosse necessario, può eseguire operazioni di post-processing per settare il valore desiderato dopo che tutti gli elementi sono stati processati.
 * @param <T> Tipo di dato estratto
 */
public interface TimelineDataExtractor<T> {
    /**
     * La chiave associata a questo estrattore
     */
    ExtractorKey<T> getKey();

    /**
     * Processa un elemento della timeline
     * @return true se l'estrazione è completa, false altrimenti
     */
    boolean process(TimelineElementInternal element);

    /**
     * Eseguito dopo che tutti gli elementi sono stati processati
     */
    void postProcess();

    /**
     * Ottiene il risultato dell'estrazione
     */
    Optional<T> getResult();
}
