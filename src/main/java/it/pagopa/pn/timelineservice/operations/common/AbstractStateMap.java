package it.pagopa.pn.timelineservice.operations.common;

import it.pagopa.pn.timelineservice.dto.notification.status.NotificationStatusInt;
import it.pagopa.pn.timelineservice.dto.timeline.details.TimelineElementCategoryInt;
import it.pagopa.pn.timelineservice.dto.transition.TransitionRequest;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public abstract class AbstractStateMap {
    protected static final boolean ONLY_MULTI_RECIPIENT = true; // il multi-destinatario comprende transizioni di stato AGGIUNTIVI al singolo destinatario
    protected static final boolean SINGLE_RECIPINET = false;

    private final Map<MapKey, MapValue> mappings = new HashMap<>();

    protected AbstractStateMap() {
        configureTransitions();
    }

    protected abstract void configureTransitions();

    public NotificationStatusInt getStateTransition(TransitionRequest transitionRequest) {
        NotificationStatusInt fromStatus = transitionRequest.getFromStatus();
        TimelineElementCategoryInt timelineRowType = transitionRequest.getTimelineRowType();
        boolean multiRecipient = transitionRequest.isMultiRecipient();

        MapKey key = new MapKey(fromStatus, timelineRowType, multiRecipient);

        if (isValidTransition(key)) {
            return this.mappings.get(key).getStatus();
        } else {
            // se non è stata trovata la transizione nella mappa degli stati, controllo se siamo nel caso del multiRecipient,
            // perché potrebbe essere il caso in cui l'elemento è presente nella mappa con chiave multiRecipient = false
            // (StatiMultiDestinatario = StatiMonoDestinatario + statiAdHocMultiDestinatario)
            if (multiRecipient == ONLY_MULTI_RECIPIENT) {
                log.trace("Transition for only multiRecipient not found, trying for singleRecipient key");
                TransitionRequest transitionRequestForSingleRecipient = TransitionRequest.builder()
                        .fromStatus(transitionRequest.getFromStatus())
                        .timelineRowType(transitionRequest.getTimelineRowType())
                        .multiRecipient(SINGLE_RECIPINET)
                        .build();
                return getStateTransition(transitionRequestForSingleRecipient);
            }

            log.error("Illegal input \"" + timelineRowType + "\" in state \"" + fromStatus + "\"");
            return fromStatus;
        }
    }

    protected boolean isValidTransition(MapKey mapKey) {
        return this.mappings.containsKey(mapKey);
    }

    protected InputMapper fromState(NotificationStatusInt fromStatus) {
        return new InputMapper(fromStatus);
    }

    protected class InputMapper {

        private final NotificationStatusInt fromStatus;

        public InputMapper(NotificationStatusInt fromStatus) {
            this.fromStatus = fromStatus;
        }

        public InputMapper withTimelineGoToState(TimelineElementCategoryInt timelineRowType, NotificationStatusInt destinationStatus, boolean multiRecipient) {
            mappings.put(new MapKey(fromStatus, timelineRowType, multiRecipient), new MapValue(destinationStatus));
            return this;
        }
    }

    @Value
    protected static class MapKey {
        NotificationStatusInt status;
        TimelineElementCategoryInt timelineElementCategory;
        boolean multiRecipient;

    }

    @Value
    protected static class MapValue {
        NotificationStatusInt status;
    }
}
