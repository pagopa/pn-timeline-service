package it.pagopa.pn.timelineservice.service.impl;

    import it.pagopa.pn.timelineservice.dto.notification.NotificationInfoInt;
    import it.pagopa.pn.timelineservice.dto.notification.status.NotificationStatusHistoryElementInt;
    import it.pagopa.pn.timelineservice.dto.notification.status.NotificationStatusInt;
    import it.pagopa.pn.timelineservice.dto.timeline.TimelineElementInternal;
    import it.pagopa.pn.timelineservice.service.StatusService;
    import it.pagopa.pn.timelineservice.utils.StatusUtils;
    import lombok.extern.slf4j.Slf4j;
    import org.springframework.stereotype.Service;
    import java.time.Instant;
    import java.util.List;
    import java.util.Set;

    @Slf4j
    @Service
    public class StatusServiceImpl implements StatusService {
        private final StatusUtils statusUtils;

        public StatusServiceImpl(StatusUtils statusUtils) {
            this.statusUtils = statusUtils;
        }

    @Override
    public NotificationStatusUpdate computeStatusChange(TimelineElementInternal dto, Set<TimelineElementInternal> currentTimeline, NotificationInfoInt notification) {
        log.debug("computeStatusChange Notification is present paProtocolNumber {} for iun {}", notification.getPaProtocolNumber(), dto.getIun());

        // - Calcolare lo stato corrente
        NotificationStatusInt currentState = computeLastStatusHistoryElement(notification, currentTimeline).getStatus();
        log.debug("computeStatusChange CurrentState is {} for iun {}", currentState, dto.getIun());

        currentTimeline.add(dto);

        // - Calcolare il nuovo stato
        NotificationStatusHistoryElementInt nextState = computeLastStatusHistoryElement(notification, currentTimeline);

        log.debug("computeStatusChange Next state is {} for iun {}", nextState.getStatus(), dto.getIun());

        return new NotificationStatusUpdate(currentState, nextState.getStatus());
    }


    @Override
    public NotificationStatusUpdate getStatus(TimelineElementInternal dto, Set<TimelineElementInternal> currentTimeline, NotificationInfoInt notification) {
        log.debug("checkStatus is present paProtocolNumber {} for iun {}", notification.getPaProtocolNumber(), dto.getIun());

        NotificationStatusUpdate notificationStatusUpdate = computeStatusChange(dto, currentTimeline, notification);
        NotificationStatusInt currentState = notificationStatusUpdate.getOldStatus();
        NotificationStatusInt nextState = notificationStatusUpdate.getNewStatus();

        log.debug("checkStatus Next state is {} for iun {}", nextState, dto.getIun());

        return new NotificationStatusUpdate(currentState, nextState);
    }

    private NotificationStatusHistoryElementInt computeLastStatusHistoryElement(NotificationInfoInt notification, Set<TimelineElementInternal> currentTimeline) {
        int numberOfRecipient = notification.getNumberOfRecipients();
        Instant notificationCreatedAt = notification.getSentAt();
        List<NotificationStatusHistoryElementInt> historyElementList = statusUtils.getStatusHistory(
                currentTimeline,
                numberOfRecipient,
                notificationCreatedAt);

            return historyElementList.getLast();
        }
    }