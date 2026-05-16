package it.pagopa.pn.timelineservice.operations;

import it.pagopa.pn.commons.exceptions.PnInternalException;
import it.pagopa.pn.timelineservice.dto.timeline.CommunicationType;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

/**
 * Componente centralizzato per la risoluzione delle {@link TimelineOperations}
 * a partire da un {@link CommunicationType}.
 * <p>
 * Costituisce l'unico punto del sistema in cui avviene la selezione
 * dell'implementazione di {@link TimelineOperations} corrispondente a un dato
 * tipo di comunicazione. Centralizzare questa scelta garantisce che:
 * <ul>
 *   <li>la logica di mapping {@code CommunicationType -> TimelineOperations}
 *       esista in un solo punto del codice, evitandone la duplicazione nei
 *       vari service applicativi;</li>
 *   <li>l'aggiunta di un nuovo {@link CommunicationType} richieda
 *       esclusivamente la creazione di una nuova implementazione di
 *       {@link TimelineOperations}, senza interventi sul resolver stesso
 *       né sui suoi client;</li>
 *   <li>tutti i flussi che operano su una stessa notification utilizzino
 *       la medesima implementazione di {@link TimelineOperations}, evitando
 *       incoerenze tra livelli diversi dello stack applicativo.</li>
 * </ul>
 * <p>
 * Le implementazioni di {@link TimelineOperations} disponibili vengono
 * raccolte automaticamente all'avvio del contesto Spring e indicizzate per
 * {@link CommunicationType}. La presenza di più implementazioni per lo stesso
 * tipo è considerata un errore di configurazione e causa il fallimento
 * dell'inizializzazione.
 * <p>
 * <b>Uso tipico:</b> i service applicativi che fungono da entry point
 * (ad esempio le implementazioni di {@code StatusHistoryService}) deducono
 * il {@link CommunicationType} dai dati in ingresso, invocano questo
 * resolver per ottenere l'istanza di {@link TimelineOperations} appropriata,
 * e delegano ad essa l'esecuzione delle operazioni di dominio.
 *
 * @see TimelineOperations
 * @see CommunicationType
 */
@Component
public class TimelineOperationsResolver {
    private static final String CODE_ERROR = "TIMELINE_STRATEGY_RESOLUTION";
    private final Map<CommunicationType, TimelineOperations> operationsMap;

    public TimelineOperationsResolver(List<TimelineOperations> timelineOperations) {
        this.operationsMap = timelineOperations.stream()
                .collect(
                    toMap(
                        TimelineOperations::supportedType,
                        identity(),
                        (existing, duplicate) -> {
                            String duplicateErrMsg = String.format(
                                    "Duplicate TimelineStrategyBundle for communication type %s: %s and %s",
                                    existing.supportedType(),
                                    existing.getClass().getName(),
                                    duplicate.getClass().getName()
                            );
                            throw new PnInternalException(duplicateErrMsg, CODE_ERROR);
                        }
                    )
                );
    }

    public TimelineOperations resolve(CommunicationType type) {
        return Optional.ofNullable(operationsMap.get(type))
                .orElseThrow(() -> new PnInternalException("Unsupported communication type: " + type, CODE_ERROR));
    }
}
