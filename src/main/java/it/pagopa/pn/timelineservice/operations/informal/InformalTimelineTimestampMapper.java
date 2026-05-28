package it.pagopa.pn.timelineservice.operations.informal;

import it.pagopa.pn.timelineservice.dto.timeline.TimelineElementInternal;
import it.pagopa.pn.timelineservice.operations.common.TimelineTimestampBaseMapper;
import it.pagopa.pn.timelineservice.operations.common.TimelineTimestampMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
@RequiredArgsConstructor
@Slf4j
public class InformalTimelineTimestampMapper implements TimelineTimestampMapper {
    private final TimelineTimestampBaseMapper timelineTimestampBaseMapper;

    @Override
    public TimelineElementInternal mapTimelineTimestamps(TimestampMapperPayload payload) {
        TimelineElementInternal source = payload.timelineElementInternal();
        if(source == null) return null;

        //Viene recuperato il timestamp originale, prima di effettuare un qualsiasi remapping
        Instant ingestionTimestamp = source.getTimestamp();

        // Viene effettuato il mapping dell'elemento di timeline per andare a leggere l'eventuale eventTimestamp presente nei dettagli e sovrascrivere il timestamp dell'elemento di timeline con questo valore.
        // Se non è presente un eventTimestamp nei dettagli, il timestamp rimane invariato.
        TimelineElementInternal result = timelineTimestampBaseMapper.mapTimelineInternal(source);

        //Se è presente un eventTimestamp nei dettagli, questo è stato mappato nel campo timestamp del risultato, altrimenti è rimasto invariato.
        //In entrambi i casi, per sicurezza, sovrascriviamo l'eventTimestamp con il timestamp dell'evento.
        result.setEventTimestamp(result.getTimestamp());
        result.setIngestionTimestamp(ingestionTimestamp);
        return result;
    }
}
