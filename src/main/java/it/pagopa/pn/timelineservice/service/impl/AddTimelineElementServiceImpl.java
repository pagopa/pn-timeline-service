package it.pagopa.pn.timelineservice.service.impl;

import it.pagopa.pn.commons.exceptions.PnIdConflictException;
import it.pagopa.pn.commons.exceptions.PnInternalException;
import it.pagopa.pn.commons.log.PnAuditLogBuilder;
import it.pagopa.pn.commons.log.PnAuditLogEvent;
import it.pagopa.pn.commons.utils.MDCUtils;
import it.pagopa.pn.timelineservice.config.PnTimelineServiceConfigs;
import it.pagopa.pn.timelineservice.dto.notification.NotificationInfoInt;
import it.pagopa.pn.timelineservice.dto.timeline.StatusInfoInternal;
import it.pagopa.pn.timelineservice.dto.timeline.TimelineElementInternal;
import it.pagopa.pn.timelineservice.exceptions.PnLockReserved;
import it.pagopa.pn.timelineservice.middleware.dao.TimelineDao;
import it.pagopa.pn.timelineservice.service.*;
import it.pagopa.pn.timelineservice.operations.TimelineOperationsResolver;
import it.pagopa.pn.timelineservice.operations.common.TimelineElementPersistenceStrategy;
import it.pagopa.pn.timelineservice.utils.CommunicationTypeUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.core.LockConfiguration;
import net.javacrumbs.shedlock.core.LockProvider;
import net.javacrumbs.shedlock.core.SimpleLock;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.Instant;
import java.util.*;

import static it.pagopa.pn.timelineservice.exceptions.PnTimelineServiceExceptionCodes.ERROR_CODE_TIMELINESERVICE_ADDTIMELINEFAILED;

@Service
@Slf4j
@RequiredArgsConstructor
public class AddTimelineElementServiceImpl implements AddTimelineElementService {
    private final TimelineService timelineService;
    private final TimelineDao timelineDao;
    private final ConfidentialInformationService confidentialInformationService;
    private final StatusService statusService;
    private final LockProvider lockProvider;
    private final PnTimelineServiceConfigs pnTimelineServiceConfigs;
    private final TimelineOperationsResolver timelineOperationsResolver;

    public Mono<String> addTimelineElement(TimelineElementInternal dto, NotificationInfoInt notification) {
        log.debug("addTimelineElement - IUN={} and timelineId={}", dto.getIun(), dto.getElementId());

        TimelineElementPersistenceStrategy strategy = timelineOperationsResolver.resolve(dto.getCommunicationType()).persistenceStrategy();
        PnAuditLogEvent logEvent = strategy.buildAuditLogEvent(dto, new PnAuditLogBuilder());
        logEvent.log();

        Mono<TimelineElementInternal> persistFlow = strategy.requiresCriticalPath(dto, notification)
                ? addCriticalTimelineElement(dto, notification, logEvent, strategy)
                : addStandardTimelineElement(dto, notification, logEvent, strategy);

        return persistFlow
                .map(TimelineElementInternal::getElementId)
                .doFinally(signal -> MDC.remove(MDCUtils.MDC_PN_CTX_TOPIC));

    }

    private Mono<TimelineElementInternal> addCriticalTimelineElement(
            TimelineElementInternal dto,
            NotificationInfoInt notification,
            PnAuditLogEvent logEvent,
            TimelineElementPersistenceStrategy strategy
    ) {
        log.debug("addCriticalTimelineElement - IUN={} and timelineId={}", dto.getIun(), dto.getElementId());

        return Mono.fromCallable(() -> lockProvider.lock(
                        new LockConfiguration(Instant.now(), notification.getIun(), pnTimelineServiceConfigs.getTimelineLockDuration(), Duration.ZERO)))
                .flatMap(optSimpleLock -> {
                    if (optSimpleLock.isEmpty()) {
                        String lockNotAcquiredMessage = "Lock not acquired for iun=" + notification.getIun() + " and timelineId=" + dto.getElementId();
                        logEvent.generateFailure(lockNotAcquiredMessage).log();
                        return Mono.error(new PnLockReserved(ERROR_CODE_TIMELINESERVICE_ADDTIMELINEFAILED, lockNotAcquiredMessage));
                    }
                    SimpleLock simpleLock = optSimpleLock.get();
                    return processTimelinePersistence(dto, notification, logEvent, strategy)
                            .onErrorMap(ex -> mapPersistenceError(ex, dto, notification, logEvent, "addCriticalTimelineElement"))
                            .doFinally(signalType -> simpleLock.unlock());
                });
    }

    private Mono<TimelineElementInternal> addStandardTimelineElement(
            TimelineElementInternal dto,
            NotificationInfoInt notification,
            PnAuditLogEvent logEvent,
            TimelineElementPersistenceStrategy strategy
    ) {
        return processTimelinePersistence(dto, notification, logEvent, strategy)
                .onErrorMap(ex -> mapPersistenceError(ex, dto, notification, logEvent, "addStandardTimelineElement"));
    }

    private Throwable mapPersistenceError(Throwable ex, TimelineElementInternal dto,
                                          NotificationInfoInt notification, PnAuditLogEvent logEvent,
                                          String op) {
        if (ex instanceof PnIdConflictException) return ex;
        logEvent.generateFailure("Exception in " + op, ex).log();
        return new PnInternalException("Exception in " + op + " - iun=" + notification.getIun()
                + " elementId=" + dto.getElementId(), ERROR_CODE_TIMELINESERVICE_ADDTIMELINEFAILED, ex);
    }

    private Mono<TimelineElementInternal> processTimelinePersistence(
            TimelineElementInternal dto,
            NotificationInfoInt notification,
            PnAuditLogEvent logEvent,
            TimelineElementPersistenceStrategy strategy
    ) {
        return timelineService.getTimeline(dto.getIun(), null, true, false)
                .collectList()
                .flatMap(list -> {
                    Set<TimelineElementInternal> currentTimeline = new HashSet<>(list);
                    CommunicationTypeUtils.validateCommunicationTypeConsistency(dto, currentTimeline);
                    StatusService.NotificationStatusUpdate notificationStatusUpdate = statusService.getStatus(dto, currentTimeline, notification, dto.getCommunicationType());
                    TimelineElementInternal enrichedDto = enrichWithStatusInfo(dto, currentTimeline, notificationStatusUpdate, notification.getSentAt());
                    TimelineElementInternal enrichedDtoWithRework = strategy.enrichWithRework(enrichedDto, currentTimeline);
                    return confidentialInformationService.saveTimelineConfidentialInformation(enrichedDtoWithRework)
                            .thenReturn(enrichedDtoWithRework)
                            .map(dtoWithStatusInfo -> strategy.applyBusinessTimestamp(dtoWithStatusInfo, currentTimeline))
                            .flatMap(finalDto -> persistTimelineElement(finalDto).thenReturn(finalDto))
                            .doOnSuccess(finalDto -> logAndCleanMdc(finalDto, logEvent, false))
                            .doOnError(PnIdConflictException.class, ex -> {
                                logAndCleanMdc(dto, logEvent, true);
                                log.warn("Exception idconflict is expected for retry, letting flow continue");
                            });
                });
    }

    private static void logAndCleanMdc(TimelineElementInternal dto, PnAuditLogEvent logEvent, boolean timelineInsertSkipped) {
        String alreadyInsertMsg = "Timeline event was already inserted before - timelineId=" + dto.getElementId();
        String successMsg = String.format("Timeline event inserted with: CATEGORY=%s IUN=%s {DETAILS: %s} TIMELINEID=%s paId=%s TIMESTAMP=%s",
                dto.getCategory(),
                dto.getIun(),
                dto.getDetails() != null ? dto.getDetails().toLog() : null,
                dto.getElementId(),
                dto.getPaId(),
                dto.getTimestamp());
        logEvent.generateSuccess(timelineInsertSkipped ? alreadyInsertMsg : successMsg).log();
        MDC.remove(MDCUtils.MDC_PN_CTX_TOPIC);
    }


    private Mono<Void> persistTimelineElement(TimelineElementInternal dtoWithStatusInfo) {
        return timelineDao.addTimelineElementIfAbsent(dtoWithStatusInfo);
    }

    private TimelineElementInternal enrichWithStatusInfo(TimelineElementInternal dto, Set<TimelineElementInternal> currentTimeline,
                                                         StatusService.NotificationStatusUpdate notificationStatuses, Instant notificationSentAt) {
        Instant timestampLastTimelineElement = getTimestampLastUpdateStatus(currentTimeline, notificationSentAt);
        StatusInfoInternal statusInfo = buildStatusInfo(notificationStatuses, timestampLastTimelineElement);
        return dto.toBuilder().statusInfo(statusInfo).build();
    }

    private Instant getTimestampLastUpdateStatus(Set<TimelineElementInternal> currentTimeline, Instant notificationSentAt) {
        Optional<StatusInfoInternal> max = currentTimeline.stream()
                .map(TimelineElementInternal::getStatusInfo)
                .filter(Objects::nonNull)
                .max(Comparator.comparing(StatusInfoInternal::getStatusChangeTimestamp));

        return max.map(StatusInfoInternal::getStatusChangeTimestamp).orElse(notificationSentAt);

    }

    protected StatusInfoInternal buildStatusInfo(StatusService.NotificationStatusUpdate notificationStatuses,
                                                 Instant timestampLastUpdateStatus) {
        Instant statusChangeTimestamp;
        boolean statusChanged = false;

        if (isStatusChanged(notificationStatuses)) {
            statusChanged = true;
            statusChangeTimestamp = Instant.now();
        } else {
            statusChangeTimestamp = timestampLastUpdateStatus;
        }

        return StatusInfoInternal.builder()
                .statusChanged(statusChanged)
                .statusChangeTimestamp(statusChangeTimestamp)
                .actual(notificationStatuses.getNewStatus().getValue())
                .build();
    }

    private boolean isStatusChanged(StatusService.NotificationStatusUpdate notificationStatuses) {
        return notificationStatuses.getOldStatus() != notificationStatuses.getNewStatus();
    }

}
