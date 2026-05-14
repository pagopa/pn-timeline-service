package it.pagopa.pn.timelineservice.service.impl;

import it.pagopa.pn.commons.exceptions.PnInternalException;
import it.pagopa.pn.timelineservice.dto.notification.status.NotificationStatusHistoryElementInt;
import it.pagopa.pn.timelineservice.dto.timeline.CommunicationType;
import it.pagopa.pn.timelineservice.dto.timeline.TimelineElementInternal;
import it.pagopa.pn.timelineservice.service.StatusHistoryService;
import it.pagopa.pn.timelineservice.strategy.TimelineStrategyResolver;
import it.pagopa.pn.timelineservice.strategy.common.StatusHistoryStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static it.pagopa.pn.timelineservice.exceptions.PnTimelineServiceExceptionCodes.ERROR_CODE_TIMELINESERVICE_UNKNOWN_COMMUNICATION_TYPE;

@Service
@RequiredArgsConstructor
public class StatusHistoryServiceImpl implements StatusHistoryService {
    private final TimelineStrategyResolver timelineStrategyResolver;

    @Override
    public List<NotificationStatusHistoryElementInt> getStatusHistory(Set<TimelineElementInternal> timelineElementList, int numberOfRecipients, Instant notificationCreatedAt) {
        CommunicationType communicationType;
        /*
        Se il set è vuoto, non importa quale implementazione di strategy utilizzare, poichè entrambe per un set vuoto restituiscono lo stesso stato iniziale.
        In questo caso, per semplicità, si può scegliere di utilizzare la strategy LEGAL.
        */
        if(timelineElementList == null || timelineElementList.isEmpty()) {
            communicationType = CommunicationType.LEGAL;
        } else {
            communicationType = retrieveCommunicationTypeFromTimelineElementList(timelineElementList);
        }

        StatusHistoryStrategy statusHistoryStrategy = timelineStrategyResolver.resolve(communicationType).statusHistory();
        return statusHistoryStrategy.getStatusHistory(timelineElementList, numberOfRecipients, notificationCreatedAt);
    }

    private CommunicationType retrieveCommunicationTypeFromTimelineElementList(Set<TimelineElementInternal> timelineElementList) {
        /*
        Se il set invece è presente si assume sia sempre valorizzata la communicationType di tutti gli elementi allo stesso modo,
        quindi si prende il primo elemento che ha la communicationType valorizzata e si restituisce quella.
        In caso non fosse presente alcun elemento con communicationType valorizzata, si lancia un'eccezione, poichè non è possibile determinare quale strategy utilizzare.
         */
        return timelineElementList.stream().map(TimelineElementInternal::getCommunicationType)
                .filter(Objects::nonNull)
                .findFirst()
                .orElseThrow(() -> new PnInternalException("Communication type not found in timeline elements", ERROR_CODE_TIMELINESERVICE_UNKNOWN_COMMUNICATION_TYPE));
    }
}
