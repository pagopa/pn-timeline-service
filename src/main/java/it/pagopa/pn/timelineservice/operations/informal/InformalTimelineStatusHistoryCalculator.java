package it.pagopa.pn.timelineservice.operations.informal;

import it.pagopa.pn.timelineservice.dto.notification.status.NotificationStatusHistoryElementInt;
import it.pagopa.pn.timelineservice.dto.notification.status.NotificationStatusInt;
import it.pagopa.pn.timelineservice.dto.timeline.TimelineElementInternal;
import it.pagopa.pn.timelineservice.dto.timeline.details.TimelineElementCategoryInt;
import it.pagopa.pn.timelineservice.dto.transition.TransitionRequest;
import it.pagopa.pn.timelineservice.service.mapper.SmartMapper;
import it.pagopa.pn.timelineservice.operations.common.StatusHistoryCalculator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

import static it.pagopa.pn.timelineservice.utils.StatusUtils.INITIAL_STATUS;

@Component
@RequiredArgsConstructor
public class InformalTimelineStatusHistoryCalculator implements StatusHistoryCalculator {

    private final InformalTimelineStateMap stateMap = new InformalTimelineStateMap();
    private final SmartMapper smartMapper;

    public List<NotificationStatusHistoryElementInt> getStatusHistory(Set<TimelineElementInternal> timelineElementList,
                                                                      int numberOfRecipients,
                                                                      Instant notificationCreatedAt) {

        //Map TimelineElementInternal per cambio timestamp con business timestamp
        Set<TimelineElementInternal> timelineElementListMapped = timelineElementList.stream()
                .map(smartMapper::mapTimelineInternalWithEventTimestamp)
                .collect(Collectors.toSet());


        //La timeline ricevuta in ingresso è relativa a tutta la notifica e non al singolo recipient
        List<TimelineElementInternal> timelineByTimestampSorted = timelineElementListMapped.stream()
                .sorted(Comparator.naturalOrder())
                .toList();

        List<NotificationStatusHistoryElementInt> timelineHistory = new ArrayList<>();

        List<String> relatedTimelineElements = new ArrayList<>();

        Instant creationDateCurrentState = notificationCreatedAt;
        NotificationStatusInt currentState = INITIAL_STATUS;

        for (TimelineElementInternal timelineElement : timelineByTimestampSorted) {

            TimelineElementCategoryInt category = timelineElement.getCategory();

            boolean multiRecipient = numberOfRecipients > 1;
            NotificationStatusInt nextState = stateMap.getStateTransition(TransitionRequest.builder()
                    .fromStatus(currentState)
                    .timelineRowType(category)
                    .multiRecipient(multiRecipient)
                    .build());

            //Se lo stato corrente è diverso dal prossimo stato
            if (!Objects.equals(currentState, nextState)) {

                NotificationStatusHistoryElementInt statusHistoryElement = NotificationStatusHistoryElementInt.builder()
                        .status( currentState )
                        .activeFrom( creationDateCurrentState )
                        .relatedTimelineElements( relatedTimelineElements )
                        .build();

                //Viene aggiunto alla status history lo stato "precedente"
                timelineHistory.add(statusHistoryElement);
                //Viene azzerata la relatedTimelineElement
                relatedTimelineElements = new ArrayList<>();
                //Ed aggiornata la creationDate
                creationDateCurrentState = timelineElement.getTimestamp();
            }

            //Viene aggiunto alla relatedTimelineElement l'elemento di timeline
            relatedTimelineElements.add( timelineElement.getElementId() );

            //Viene aggiornato il currentState nel caso in cui sia cambiato
            currentState = nextState;
        }

        NotificationStatusHistoryElementInt statusHistoryElement = NotificationStatusHistoryElementInt.builder()
                .status( currentState )
                .activeFrom( creationDateCurrentState )
                .relatedTimelineElements( relatedTimelineElements )
                .build();
        timelineHistory.add(statusHistoryElement);

        return timelineHistory;
    }

}
