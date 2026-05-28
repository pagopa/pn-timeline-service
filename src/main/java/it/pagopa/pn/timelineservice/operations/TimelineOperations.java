package it.pagopa.pn.timelineservice.operations;

import it.pagopa.pn.timelineservice.dto.timeline.CommunicationType;
import it.pagopa.pn.timelineservice.operations.common.StatusHistoryCalculator;
import it.pagopa.pn.timelineservice.operations.common.TimelineElementPersistenceStrategy;
import it.pagopa.pn.timelineservice.operations.common.TimelineTimestampMapper;

/**
 * Raggruppa l'insieme delle operazioni sulla timeline di una notifica che
 * variano in funzione del {@link CommunicationType}.
 * <p>
 * Ogni implementazione di questa interfaccia rappresenta una variante completa
 * e coesa di tali operazioni, specifica per un singolo {@link CommunicationType}
 * (es. {@code LEGAL}, {@code INFORMAL}). Le implementazioni espongono, tramite
 * metodi factory, i componenti specializzati che incarnano le singole capability
 * (calcolo dello storico stati, persistenza della timeline, ecc.).
 * <p>
 * L'astrazione esiste per due motivi:
 * <ul>
 *   <li><b>Coesione:</b> tutte le capability che variano in base al tipo di
 *       comunicazione sono raggruppate in un'unica entità polimorfica,
 *       evitando la dispersione della logica di selezione in più punti del
 *       codice.</li>
 *   <li><b>Consistenza:</b> una volta risolta l'implementazione corretta per
 *       un dato {@link CommunicationType}, tutte le operazioni successive
 *       avvengono sulla stessa istanza, garantendo strutturalmente che non
 *       si mischino logiche di tipi diversi all'interno della stessa unità
 *       di lavoro.</li>
 * </ul>
 * <p>
 * <b>Non iniettare direttamente le implementazioni concrete.</b> L'accesso
 * a un'istanza di {@code TimelineOperations} deve sempre avvenire tramite
 * {@link TimelineOperationsResolver}, che è l'unico componente autorizzato
 * a effettuare la selezione in base al {@link CommunicationType}.
 *
 * @see TimelineOperationsResolver
 * @see CommunicationType
 */
public interface TimelineOperations {
    TimelineElementPersistenceStrategy persistenceStrategy();
    StatusHistoryCalculator statusHistoryCalculator();
    TimelineTimestampMapper timelineTimestampMapper();
    CommunicationType supportedType();
}
