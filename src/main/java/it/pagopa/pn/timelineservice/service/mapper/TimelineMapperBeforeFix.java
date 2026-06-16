package it.pagopa.pn.timelineservice.service.mapper;

import it.pagopa.pn.commons.exceptions.PnInternalException;
import it.pagopa.pn.timelineservice.dto.timeline.TimelineElementInternal;
import it.pagopa.pn.timelineservice.dto.timeline.TimelineEventIdParser;
import it.pagopa.pn.timelineservice.dto.timeline.details.RecipientRelatedTimelineElementDetails;
import it.pagopa.pn.timelineservice.dto.timeline.details.TimelineElementCategoryInt;
import it.pagopa.pn.timelineservice.exceptions.PnTimelineServiceExceptionCodes;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.util.Objects;
import java.util.Set;

@Slf4j
public class TimelineMapperBeforeFix extends TimelineMapper {

    public void remapSpecificTimelineElementData(Set<TimelineElementInternal> timelineElementInternalSet, TimelineElementInternal result, Instant ingestionTimestamp, boolean isPfNewWorkflowEnabled) {
        if (result != null) {
            //L'ingestion timestamp viene settato con il timestamp originale dell'evento (dunque timestamp evento per SEND)
            result.setIngestionTimestamp(ingestionTimestamp);

            //Nello switch case invece vengono effettuati ulteriori remapping dei timestamp, questi non dipendono dal singolo elemento, ma necessitano di tutta la timeline
            switch (result.getCategory()) {
                case SCHEDULE_REFINEMENT -> {
                    Instant endAnalogWorkflowBusinessDate = computeEndAnalogWorkflowBusinessData((RecipientRelatedTimelineElementDetails) result.getDetails(), timelineElementInternalSet, result.getIun());
                    if (endAnalogWorkflowBusinessDate != null) {
                        log.debug("MAP TIMESTAMP: elem category {}, elem previous timestamp {}, elem new timestamp {} ", result.getCategory(), result.getTimestamp(), endAnalogWorkflowBusinessDate);
                        result.setTimestamp(endAnalogWorkflowBusinessDate);
                    }
                }
                case ANALOG_SUCCESS_WORKFLOW, ANALOG_FAILURE_WORKFLOW, COMPLETELY_UNREACHABLE_CREATION_REQUEST, COMPLETELY_UNREACHABLE, ANALOG_WORKFLOW_RECIPIENT_DECEASED -> {
                    Instant endAnalogWorkflowBusinessDate = computeEndAnalogWorkflowBusinessData((RecipientRelatedTimelineElementDetails) result.getDetails(), timelineElementInternalSet, result.getIun());
                    if (endAnalogWorkflowBusinessDate != null) {
                        log.debug("MAP TIMESTAMP: elem category {}, elem previous timestamp {}, elem new timestamp {} ", result.getCategory(), result.getTimestamp(), endAnalogWorkflowBusinessDate);
                        result.setTimestamp(endAnalogWorkflowBusinessDate);
                    } else {
                        log.error("SEARCH LAST SEND_ANALOG_FEEDBACK DETAILS NULL element {}", result);
                        throw new PnInternalException("SEND_ANALOG_FEEDBACK NOT PRESENT, ERROR IN MAPPING", PnTimelineServiceExceptionCodes.ERROR_CODE_TIMELINESERVICE_TIMELINE_ELEMENT_NOT_PRESENT);
                    }
                }
                case REFINEMENT -> caseRefinement(timelineElementInternalSet, result);
                case SEND_DIGITAL_DOMICILE -> caseSendDigitalDomicile(timelineElementInternalSet, result, isPfNewWorkflowEnabled);
                case SEND_DIGITAL_FEEDBACK -> caseSendDigitalFeedback(timelineElementInternalSet, result, isPfNewWorkflowEnabled);
                case NOTIFICATION_TIMELINE_REWORKED -> caseNotificationTimelineReworked(timelineElementInternalSet, result);
                default -> {
                    //nothing to do
                }
            }

            //In ultima istanza viene settato l'eventTimestamp con il timestamp rimappato (avranno dunque in uscita sempre lo stesso valore)
            result.setEventTimestamp(result.getTimestamp());
        }
    }

    private void caseNotificationTimelineReworked(Set<TimelineElementInternal> timelineElementInternalSet, TimelineElementInternal result) {
        TimelineEventIdParser reworkedEventIdParser = TimelineEventIdParser.parse(result.getElementId());
        Integer reworkRecIndex = reworkedEventIdParser.recIndex().orElse(null);
        Integer attempt = reworkedEventIdParser.sentAttemptMade().orElse(null);

        timelineElementInternalSet.stream()
                .filter(timelineElementInternal -> timelineElementInternal.getCategory().equals(TimelineElementCategoryInt.SEND_ANALOG_DOMICILE))
                .filter(timelineElementInternal -> {
                    TimelineEventIdParser parser = TimelineEventIdParser.parse(timelineElementInternal.getElementId());
                    return parser.recIndex()
                            .map(integer -> integer.equals(reworkRecIndex)).orElse(false) &&
                            parser.sentAttemptMade().map(integer -> integer.equals(attempt)).orElse(false);
                })
                .findFirst()
                .ifPresentOrElse(
                        (timelineElementInternal ) -> {
                            Instant timestamp = checkTimestamp(result, timelineElementInternal);
                            result.setEventTimestamp(timestamp);
                            result.setTimestamp(timestamp);},
                        () -> result.setTimestamp(result.getEventTimestamp())
                );
    }

    private Instant checkTimestamp(TimelineElementInternal reworkedElement, TimelineElementInternal sendAnalogElement) {
        Instant reworkedTimestamp = reworkedElement.getTimestamp();
        Instant sendAnalogTimestamp = sendAnalogElement.getTimestamp();
        if (Objects.nonNull(reworkedTimestamp) && Objects.nonNull(sendAnalogTimestamp)) {
            return sendAnalogTimestamp.isBefore(reworkedTimestamp) ? sendAnalogTimestamp : reworkedElement.getEventTimestamp();
        }
        return sendAnalogTimestamp;
    }

}
