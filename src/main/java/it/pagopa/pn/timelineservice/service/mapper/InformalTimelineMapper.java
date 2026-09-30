package it.pagopa.pn.timelineservice.service.mapper;

import it.pagopa.pn.commons.exceptions.PnInternalException;
import it.pagopa.pn.timelineservice.dto.address.InformalDigitalAddressInt;
import it.pagopa.pn.timelineservice.dto.notification.status.NotificationStatusHistoryInvalidatedElementInt;
import it.pagopa.pn.timelineservice.dto.timeline.TimelineElementInternal;
import it.pagopa.pn.timelineservice.dto.timeline.TimelineEventIdParser;
import it.pagopa.pn.timelineservice.dto.timeline.details.TimelineElementCategoryInt;
import it.pagopa.pn.timelineservice.dto.timeline.details.common.RecipientRelatedTimelineElementDetails;
import it.pagopa.pn.timelineservice.dto.timeline.details.informal.SendDigitalMessageDetailsInt;
import it.pagopa.pn.timelineservice.dto.timeline.details.informal.SendDigitalMessageFeedbackDetailsInt;
import it.pagopa.pn.timelineservice.dto.timeline.details.legal.NotificationTimelineReworkedDetailsInt;
import it.pagopa.pn.timelineservice.exceptions.PnTimelineServiceExceptionCodes;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static it.pagopa.pn.timelineservice.dto.timeline.ReworkRequestTypeEnum.INVALIDATE_ELEMENTS;
import static it.pagopa.pn.timelineservice.service.mapper.TimelineMapper.ELEMENT_DETAILS_NULL;

@Slf4j
@Component
public class InformalTimelineMapper {

    public void remapSpecificTimelineElementData(Set<TimelineElementInternal> timelineElementInternalSet, TimelineElementInternal result, Instant ingestionTimestamp) {
        if (result != null) {
            //L'ingestion timestamp viene settato con il timestamp originale dell'evento (dunque timestamp evento per SEND)
            result.setIngestionTimestamp(ingestionTimestamp);

            //Nello switch case invece vengono effettuati ulteriori remapping dei timestamp, questi non dipendono dal singolo elemento, ma necessitano di tutta la timeline
            //nothing to do
            if (Objects.requireNonNull(result.getCategory()) == TimelineElementCategoryInt.SEND_DIGITAL_MESSAGE_FEEDBACK) {
                caseSendDigitalMessageFeedback(timelineElementInternalSet, result);
            }

            //In ultima istanza viene settato l'eventTimestamp con il timestamp rimappato (avranno dunque in uscita sempre lo stesso valore)
            result.setEventTimestamp(result.getTimestamp());
        }
    }

    private Instant findSendMessageTimestamp(RecipientRelatedTimelineElementDetails elementDetails, Set<TimelineElementInternal> timelineElementInternalSet) {
        if (elementDetails == null) {
            throw new PnInternalException(ELEMENT_DETAILS_NULL, PnTimelineServiceExceptionCodes.ERROR_CODE_TIMELINESERVICE_TIMELINE_ELEMENT_NOT_PRESENT);
        }
        int recIndex = elementDetails.getRecIndex();

        return timelineElementInternalSet.stream().filter(e ->
                e.getCategory() == TimelineElementCategoryInt.SEND_DIGITAL_MESSAGE &&
                        e.getDetails() instanceof RecipientRelatedTimelineElementDetails sendDigitalMessageElementDetails &&
                        sendDigitalMessageElementDetails.getRecIndex() == recIndex &&
                        (isSendDigitalMessageFeedbackSercQ(e) || isSendDigitalMessageSercQ(e))
        ).findFirst().map(TimelineElementInternal::getTimestamp).orElse(null);
    }

    private static boolean isSendDigitalMessageSercQ(TimelineElementInternal e) {
        return e.getDetails() instanceof SendDigitalMessageDetailsInt sendDigitalDetailsInt &&
                InformalDigitalAddressInt.INFORMAL_DIGITAL_ADDRESS_TYPE.SERCQ.equals(sendDigitalDetailsInt.getDigitalAddress().getType());
    }

    private static boolean isSendDigitalMessageFeedbackSercQ(TimelineElementInternal e) {
        return e.getDetails() instanceof SendDigitalMessageFeedbackDetailsInt sendDigitalFeedbackDetailsInt &&
                InformalDigitalAddressInt.INFORMAL_DIGITAL_ADDRESS_TYPE.SERCQ.equals(sendDigitalFeedbackDetailsInt.getDigitalAddress().getType());
    }

    void caseSendDigitalMessageFeedback(Set<TimelineElementInternal> timelineElementInternalSet, TimelineElementInternal result) {
        //caso implementato solo per la gestione del digital feedback in caso di SERCQ e nuovo workflow di recupero domicili digitali attivo
        SendDigitalMessageFeedbackDetailsInt details = (SendDigitalMessageFeedbackDetailsInt) result.getDetails();
        if (InformalDigitalAddressInt.INFORMAL_DIGITAL_ADDRESS_TYPE.SERCQ.equals(details.getDigitalAddress().getType())) {
            setDigitalMessageFeedbackTimestamp(timelineElementInternalSet, result);
        }
    }

    private void setDigitalMessageFeedbackTimestamp(Set<TimelineElementInternal> timelineElementInternalSet, TimelineElementInternal result) {
        Instant sendDigitalMessageTimestamp = findSendMessageTimestamp((RecipientRelatedTimelineElementDetails) result.getDetails(), timelineElementInternalSet);
        if (Objects.nonNull(sendDigitalMessageTimestamp) && sendDigitalMessageTimestamp.isAfter(result.getIngestionTimestamp())) {
            result.setTimestamp(sendDigitalMessageTimestamp);
            result.setEventTimestamp(sendDigitalMessageTimestamp);
        } else {
            result.setTimestamp(result.getIngestionTimestamp());
            result.setEventTimestamp(result.getIngestionTimestamp());
        }
    }
}
