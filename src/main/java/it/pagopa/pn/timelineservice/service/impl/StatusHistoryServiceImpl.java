package it.pagopa.pn.timelineservice.service.impl;

import it.pagopa.pn.timelineservice.dto.notification.status.NotificationStatusHistoryElementInt;
import it.pagopa.pn.timelineservice.dto.timeline.CommunicationType;
import it.pagopa.pn.timelineservice.dto.timeline.TimelineElementInternal;
import it.pagopa.pn.timelineservice.operations.TimelineOperationsResolver;
import it.pagopa.pn.timelineservice.operations.common.StatusHistoryCalculator;
import it.pagopa.pn.timelineservice.service.StatusHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class StatusHistoryServiceImpl implements StatusHistoryService {
    private final TimelineOperationsResolver timelineOperationsResolver;

    @Override
    public List<NotificationStatusHistoryElementInt> getStatusHistory(
            Set<TimelineElementInternal> timelineElementList,
            int numberOfRecipients,
            Instant notificationCreatedAt,
            CommunicationType communicationType
    ) {
        StatusHistoryCalculator statusHistoryCalculator = timelineOperationsResolver.resolve(communicationType).statusHistoryCalculator();
        return statusHistoryCalculator.getStatusHistory(timelineElementList, numberOfRecipients, notificationCreatedAt);
    }
}
