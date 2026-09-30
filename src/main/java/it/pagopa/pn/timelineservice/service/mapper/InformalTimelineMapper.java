package it.pagopa.pn.timelineservice.service.mapper;

import it.pagopa.pn.commons.exceptions.PnInternalException;
import it.pagopa.pn.timelineservice.dto.address.InformalDigitalAddressInt;
import it.pagopa.pn.timelineservice.dto.timeline.TimelineElementInternal;
import it.pagopa.pn.timelineservice.dto.timeline.details.TimelineElementCategoryInt;
import it.pagopa.pn.timelineservice.dto.timeline.details.common.RecipientRelatedTimelineElementDetails;
import it.pagopa.pn.timelineservice.dto.timeline.details.informal.SendDigitalMessageDetailsInt;
import it.pagopa.pn.timelineservice.dto.timeline.details.informal.SendDigitalMessageFeedbackDetailsInt;
import it.pagopa.pn.timelineservice.exceptions.PnTimelineServiceExceptionCodes;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Objects;
import java.util.Set;

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
            if (Objects.requireNonNull(result.getCategory()) == TimelineElementCategoryInt.SEND_DIGITAL_MESSAGE) {
                caseSendDigitalMessage(timelineElementInternalSet, result);
            }

            //In ultima istanza viene settato l'eventTimestamp con il timestamp rimappato (avranno dunque in uscita sempre lo stesso valore)
            result.setEventTimestamp(result.getTimestamp());
        }
    }

    void caseSendDigitalMessage(Set<TimelineElementInternal> timelineElementInternalSet, TimelineElementInternal result) {
        //caso implementato solo per la gestione del digital message in caso di SERCQ
        SendDigitalMessageDetailsInt details = (SendDigitalMessageDetailsInt) result.getDetails();
        if (InformalDigitalAddressInt.INFORMAL_DIGITAL_ADDRESS_TYPE.SERCQ.equals(details.getDigitalAddress().getType())) {
            setDigitalMessageTimestamp(timelineElementInternalSet, result);
        }
    }

    private void setDigitalMessageTimestamp(Set<TimelineElementInternal> timelineElementInternalSet, TimelineElementInternal result) {
        Instant sendDigitalMessageFeedbackTimestamp = findDigitalMessageFeedbackTimestamp((RecipientRelatedTimelineElementDetails) result.getDetails(), timelineElementInternalSet);
        if (Objects.nonNull(sendDigitalMessageFeedbackTimestamp) && sendDigitalMessageFeedbackTimestamp.isBefore(result.getIngestionTimestamp())) {
            result.setTimestamp(sendDigitalMessageFeedbackTimestamp);
            result.setEventTimestamp(sendDigitalMessageFeedbackTimestamp);
        } else {
            result.setTimestamp(result.getIngestionTimestamp());
            result.setEventTimestamp(result.getIngestionTimestamp());
        }
    }

    private Instant findDigitalMessageFeedbackTimestamp(RecipientRelatedTimelineElementDetails elementDetails, Set<TimelineElementInternal> timelineElementInternalSet) {
        if (elementDetails == null) {
            throw new PnInternalException(ELEMENT_DETAILS_NULL, PnTimelineServiceExceptionCodes.ERROR_CODE_TIMELINESERVICE_TIMELINE_ELEMENT_NOT_PRESENT);
        }
        int recIndex = elementDetails.getRecIndex();

        return timelineElementInternalSet.stream().filter(e ->
                e.getCategory() == TimelineElementCategoryInt.SEND_DIGITAL_MESSAGE_FEEDBACK &&
                        e.getDetails() instanceof RecipientRelatedTimelineElementDetails sendDigitalMessageFeedbackElementDetails &&
                        sendDigitalMessageFeedbackElementDetails.getRecIndex() == recIndex &&
                        isSendDigitalMessageFeedbackSercQ(e)
        ).findFirst().map(element -> ((SendDigitalMessageFeedbackDetailsInt) element.getDetails()).getElementTimestamp()).orElse(null);
    }

    private static boolean isSendDigitalMessageFeedbackSercQ(TimelineElementInternal e) {
        return e.getDetails() instanceof SendDigitalMessageFeedbackDetailsInt sendDigitalFeedbackDetailsInt &&
                InformalDigitalAddressInt.INFORMAL_DIGITAL_ADDRESS_TYPE.SERCQ.equals(sendDigitalFeedbackDetailsInt.getDigitalAddress().getType());
    }
}
