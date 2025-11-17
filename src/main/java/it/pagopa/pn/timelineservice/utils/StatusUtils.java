package it.pagopa.pn.timelineservice.utils;

import it.pagopa.pn.timelineservice.dto.notification.status.NotificationStatusHistoryElementInt;
import it.pagopa.pn.timelineservice.dto.notification.status.NotificationStatusInt;
import it.pagopa.pn.timelineservice.dto.timeline.TimelineElementInternal;
import it.pagopa.pn.timelineservice.dto.timeline.details.AnalogWorfklowRecipientDeceasedDetailsInt;
import it.pagopa.pn.timelineservice.dto.timeline.details.NotificationViewedDetailsInt;
import it.pagopa.pn.timelineservice.dto.timeline.details.TimelineElementCategoryInt;
import it.pagopa.pn.timelineservice.dto.timeline.details.TimelineElementDetailsInt;
import it.pagopa.pn.timelineservice.dto.transition.TransitionRequest;
import it.pagopa.pn.timelineservice.service.mapper.SmartMapper;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class StatusUtils {
    private final StateMap stateMap;
    private final SmartMapper smartMapper;

    public StatusUtils(SmartMapper smartMapper){
        this.smartMapper = smartMapper;
        this.stateMap = new StateMap();
    }
    
    private static final NotificationStatusInt INITIAL_STATUS = NotificationStatusInt.IN_VALIDATION;

    public NotificationStatusInt getCurrentStatus(List<NotificationStatusHistoryElementInt> statusHistory) {
        if (!statusHistory.isEmpty()) {
            return statusHistory.getLast().getStatus();
        } else {
            return INITIAL_STATUS;
        }
    }
    
    public List<NotificationStatusHistoryElementInt> getStatusHistory(Set<TimelineElementInternal> timelineElementList,
                                                                      int numberOfRecipients, 
                                                                      Instant notificationCreatedAt) {

        //Map TimelineElementInternal per cambio timestamp con business timestamp
        Set<TimelineElementInternal> timelineElementListMapped = timelineElementList.stream()
                .map(elem -> smartMapper.mapTimelineInternal(elem, timelineElementList)).collect(Collectors.toSet());


        //La timeline ricevuta in ingresso è relativa a tutta la notifica e non al singolo recipient
        List<TimelineElementInternal> timelineByTimestampSorted = timelineElementListMapped.stream()
                .sorted(Comparator.naturalOrder())
                .toList();
    
        List<NotificationStatusHistoryElementInt> timelineHistory = new ArrayList<>();

        List<String> relatedTimelineElements = new ArrayList<>();
        List<TimelineElementCategoryInt> relatedCategoryElements = new ArrayList<>();

        Instant creationDateCurrentState = notificationCreatedAt;
        NotificationStatusInt currentState = INITIAL_STATUS;
        int numberOfCompletedWorkflow = 0;

        for (TimelineElementInternal timelineElement : timelineByTimestampSorted) {

            TimelineElementCategoryInt category = timelineElement.getCategory();


            if( CompletedDeliveryWorkflowCategory.isCompletedWorkflowCategory( category ) ) {
                //Vengono contati il numero di workflow completate per tutti i recipient, sia in caso di successo che di fallimento
                numberOfCompletedWorkflow += 1;
            }

            relatedCategoryElements.add( category );

            NotificationStatusInt nextState = computeStateAfterEvent(
                    timelineElement.getDetails(),
                    currentState,
                    category,
                    numberOfCompletedWorkflow,
                    numberOfRecipients,
                    relatedCategoryElements,
                    timelineByTimestampSorted
            );

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
                relatedCategoryElements = new ArrayList<>();
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

    private NotificationStatusInt getNextState(NotificationStatusInt currentState, List<TimelineElementCategoryInt> relatedCategoryElements, int numberOfRecipient) {
        boolean multiRecipient = numberOfRecipient > 1;

        TimelineElementCategoryInt category = CompletedDeliveryWorkflowCategory.pickCategoryByPriority(relatedCategoryElements);

        return stateMap.getStateTransition(TransitionRequest.builder()
                .fromStatus(currentState)
                .timelineRowType(category)
                .multiRecipient(multiRecipient)
                .build());
    }

    private NotificationStatusInt computeStateAfterEvent(
            TimelineElementDetailsInt details,
            NotificationStatusInt currentState,
            TimelineElementCategoryInt timelineElementCategory,
            int numberOfCompletedWorkflow,
            int numberOfRecipients,
            List<TimelineElementCategoryInt> relatedCategoryElements,
            List<TimelineElementInternal> timelineByTimestampSorted
    ) {
        NotificationStatusInt nextState;

        boolean multiRecipient = numberOfRecipients > 1;

        //(Gli stati ACCEPTED e DELIVERING sono gli stati in cui ci sono differenze di gestione per il multi destinatario, dunque prevedono una logica ad-hoc per il cambio stato)
        // Se sono nello stato ACCEPTED o DELIVERING e l'elemento di timeline preso in considerazione è uno degli stati di successo o fallimento del workflow ...
        if ( ( currentState.equals(NotificationStatusInt.ACCEPTED) || currentState.equals(NotificationStatusInt.DELIVERING) )
                &&
                ( CompletedDeliveryWorkflowCategory.isCompletedWorkflowCategory(timelineElementCategory) )
        ) {
            //... e il workflow è stato completato per tutti i recipient della notifica
            if( numberOfCompletedWorkflow == numberOfRecipients ){
                //... può essere ottenuto il nextState
                nextState =  getNextState(currentState, relatedCategoryElements, numberOfRecipients);
            }else {
                //... Altrimenti lo stato non cambia, bisogna attendere la fine del workflow per tutti i recipient
                nextState = currentState;
            }
        } else if(timelineElementCategory == TimelineElementCategoryInt.NOTIFICATION_VIEWED && multiRecipient) {
            return computeStateAfterViewEvent(details, currentState, timelineElementCategory, timelineByTimestampSorted);
        } else {
            //... Altrimenti lo stato viene calcolato normalmente dalla mappa
            nextState = stateMap.getStateTransition(
                    TransitionRequest.builder()
                            .fromStatus(currentState)
                            .timelineRowType(timelineElementCategory)
                            .multiRecipient(multiRecipient)
                            .build()
            );
        }

        return nextState;
    }

    private NotificationStatusInt computeStateAfterViewEvent(TimelineElementDetailsInt details, NotificationStatusInt currentState, TimelineElementCategoryInt timelineElementCategory, List<TimelineElementInternal> timelineByTimestampSorted) {
        int viewRecIdx = ((NotificationViewedDetailsInt) details).getRecIndex();

        if(isViewedByDeceasedRecipient(viewRecIdx, timelineByTimestampSorted)) {
            return currentState;
        }

        //... Altrimenti lo stato viene calcolato normalmente dalla mappa
        return stateMap.getStateTransition(
                TransitionRequest.builder()
                        .fromStatus(currentState)
                        .timelineRowType(timelineElementCategory)
                        .multiRecipient(true) // Questo metodo è raggiunto solo per multi recipient
                        .build()
        );
    }

    private boolean isViewedByDeceasedRecipient(int recIdx, List<TimelineElementInternal> timelineByTimestampSorted) {
        return timelineByTimestampSorted.stream()
                .filter(elementInternal -> elementInternal.getCategory().equals(TimelineElementCategoryInt.ANALOG_WORKFLOW_RECIPIENT_DECEASED))
                .map(elementInternal -> ((AnalogWorfklowRecipientDeceasedDetailsInt)elementInternal.getDetails()).getRecIndex())
                .anyMatch(deceasedRecIdx -> deceasedRecIdx == recIdx);
    }

}
