package it.pagopa.pn.timelineservice.service.impl;

import it.pagopa.pn.commons.exceptions.PnIdConflictException;
import it.pagopa.pn.commons.exceptions.PnInternalException;
import it.pagopa.pn.commons.log.PnAuditLogBuilder;
import it.pagopa.pn.commons.log.PnAuditLogEvent;
import it.pagopa.pn.commons.log.PnAuditLogEventType;
import it.pagopa.pn.commons.utils.MDCUtils;
import it.pagopa.pn.timelineservice.config.PnTimelineServiceConfigs;
import it.pagopa.pn.timelineservice.dto.ext.datavault.ConfidentialTimelineElementDtoInt;
import it.pagopa.pn.timelineservice.dto.notification.NotificationHistoryInt;
import it.pagopa.pn.timelineservice.dto.notification.NotificationInfoInt;
import it.pagopa.pn.timelineservice.dto.notification.status.NotificationStatusHistoryElementInt;
import it.pagopa.pn.timelineservice.dto.notification.status.NotificationStatusInt;
import it.pagopa.pn.timelineservice.dto.timeline.StatusInfoInternal;
import it.pagopa.pn.timelineservice.dto.timeline.TimelineElementInternal;
import it.pagopa.pn.timelineservice.dto.timeline.details.RecipientRelatedTimelineElementDetails;
import it.pagopa.pn.timelineservice.dto.timeline.details.TimelineElementCategoryInt;
import it.pagopa.pn.timelineservice.dto.timeline.details.TimelineElementDetailsInt;
import it.pagopa.pn.timelineservice.exceptions.PnLockReserved;
import it.pagopa.pn.timelineservice.middleware.dao.TimelineCounterEntityDao;
import it.pagopa.pn.timelineservice.middleware.dao.TimelineDao;
import it.pagopa.pn.timelineservice.middleware.dao.dynamo.entity.TimelineCounterEntity;
import it.pagopa.pn.timelineservice.service.ConfidentialInformationService;
import it.pagopa.pn.timelineservice.service.StatusService;
import it.pagopa.pn.timelineservice.service.TimelineService;
import it.pagopa.pn.timelineservice.service.mapper.SmartMapper;
import it.pagopa.pn.timelineservice.utils.StatusUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.core.LockConfiguration;
import net.javacrumbs.shedlock.core.LockProvider;
import net.javacrumbs.shedlock.core.SimpleLock;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

import static it.pagopa.pn.timelineservice.exceptions.PnTimelineServiceExceptionCodes.ERROR_CODE_TIMELINESERVICE_ADDTIMELINEFAILED;
import static it.pagopa.pn.timelineservice.service.mapper.ConfidentialDetailEnricher.enrichTimelineElementWithConfidentialInformation;
import static it.pagopa.pn.timelineservice.utils.StatusUtils.COMPLETED_DELIVERY_WORKFLOW_CATEGORY;


@Service
@Slf4j
@RequiredArgsConstructor
public class TimelineServiceImpl implements TimelineService {
    private final TimelineDao timelineDao;
    private final TimelineCounterEntityDao timelineCounterEntityDao;
    private final StatusUtils statusUtils;
    private final ConfidentialInformationService confidentialInformationService;
    private final StatusService statusService;
    private final SmartMapper smartMapper;
    private final LockProvider lockProvider;
    private final PnTimelineServiceConfigs pnTimelineServiceConfigs;

    @Override
    public Mono<Void> addTimelineElement(TimelineElementInternal dto, NotificationInfoInt notification) {
        log.debug("addTimelineElement - IUN={} and timelineId={}", dto.getIun(), dto.getElementId());
        PnAuditLogBuilder auditLogBuilder = new PnAuditLogBuilder();

        PnAuditLogEvent logEvent = getPnAuditLogEvent(dto, auditLogBuilder);
        logEvent.log();

        boolean isMultiRecipient = notification.getNumberOfRecipients() > 1;
        boolean isCriticalTimelineElement = COMPLETED_DELIVERY_WORKFLOW_CATEGORY.contains(dto.getCategory());

        return Mono.just(isMultiRecipient && isCriticalTimelineElement)
                .flatMap(aBoolean -> {
                    if (Boolean.TRUE.equals(aBoolean)) {
                        return addCriticalTimelineElement(dto, notification, logEvent);
                    } else {
                        return addTimelineElement(dto, notification, logEvent);
                    }
                })
                .doFinally(signal -> MDC.remove(MDCUtils.MDC_PN_CTX_TOPIC));

    }

    private Mono<Void> addCriticalTimelineElement(TimelineElementInternal dto, NotificationInfoInt notification, PnAuditLogEvent logEvent) {
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
                    return processTimelinePersistence(dto, notification, logEvent)
                            .doOnError(throwable -> logEvent.generateFailure("IdConflictException in addCriticalTimelineElement", throwable).log())
                            .onErrorMap(ex -> {
                                if( ex instanceof PnIdConflictException) {
                                    return ex;
                                }
                                return new PnInternalException("Exception in addCriticalTimelineElement - iun=" + notification.getIun() + " elementId=" + dto.getElementId(), ERROR_CODE_TIMELINESERVICE_ADDTIMELINEFAILED, ex);
                            })
                            .doFinally(signalType -> simpleLock.unlock());
                });
    }

    private Mono<Void> addTimelineElement(TimelineElementInternal dto, NotificationInfoInt notification, PnAuditLogEvent logEvent) {
        return processTimelinePersistence(dto, notification, logEvent)
                .doOnError(throwable -> logEvent.generateFailure("IdConflictException in addTimelineElement", throwable).log())
                .onErrorMap(ex -> {
                    if( ex instanceof PnIdConflictException) {
                        return ex;
                    }
                    return new PnInternalException("Exception in addTimelineElement - iun=" + notification.getIun() + " elementId=" + dto.getElementId(), ERROR_CODE_TIMELINESERVICE_ADDTIMELINEFAILED, ex);
                });
    }

    private Mono<Void> processTimelinePersistence(TimelineElementInternal dto, NotificationInfoInt notification, PnAuditLogEvent logEvent) {
        return getTimeline(dto.getIun(), null, true, false)
                .collectList()
                .flatMap(list -> {
                    Set<TimelineElementInternal> currentTimeline = new HashSet<>(list);
                    StatusService.NotificationStatusUpdate notificationStatusUpdate = statusService.getStatus(dto, currentTimeline, notification);
                    return confidentialInformationService.saveTimelineConfidentialInformation(dto)
                            .thenReturn(enrichWithStatusInfo(dto, currentTimeline, notificationStatusUpdate, notification.getSentAt()))
                            .flatMap(dtoWithStatusInfo -> checkAndAddBusinessTimestamp(dtoWithStatusInfo, currentTimeline))
                            .flatMap(this::persistTimelineElement)
                            .doOnSuccess(item -> logAndCleanMdc(dto, logEvent, false))
                            .doOnError(PnIdConflictException.class, ex -> {
                                logAndCleanMdc(dto, logEvent, true);
                                log.warn("Exception idconflict is expected for retry, letting flow continue");
                            });
                });
    }

    private static void logAndCleanMdc(TimelineElementInternal dto, PnAuditLogEvent logEvent, Boolean timelineInsertSkipped) {
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

    private Mono<TimelineElementInternal> checkAndAddBusinessTimestamp(TimelineElementInternal dtoWithStatusInfo, Set<TimelineElementInternal> currentTimeline) {
        if (shouldWriteBusinessTimestamp()) {
            Instant cachedTimestamp = dtoWithStatusInfo.getTimestamp();
            // calcolo e aggiungo il businessTimestamp
            dtoWithStatusInfo = smartMapper.mapTimelineInternal(dtoWithStatusInfo, currentTimeline);
            dtoWithStatusInfo.setTimestamp(cachedTimestamp);
        }
        return Mono.just(dtoWithStatusInfo);
    }

    private boolean shouldWriteBusinessTimestamp() {
        Instant now = Instant.now();
        return now.isAfter(pnTimelineServiceConfigs.getStartWriteBusinessTimestamp()) && now.isBefore(pnTimelineServiceConfigs.getStopWriteBusinessTimestamp());
    }


    private Mono<Void> persistTimelineElement(TimelineElementInternal dtoWithStatusInfo) {
        return timelineDao.addTimelineElementIfAbsent(dtoWithStatusInfo);
    }

    private PnAuditLogEvent getPnAuditLogEvent(TimelineElementInternal dto, PnAuditLogBuilder auditLogBuilder) {
        String auditLog = String.format("Timeline event inserted with: CATEGORY=%s IUN=%s {DETAILS: %s} TIMELINEID=%s paId=%s TIMESTAMP=%s",
                dto.getCategory(),
                dto.getIun(),
                dto.getDetails() != null ? dto.getDetails().toLog() : null,
                dto.getElementId(),
                dto.getPaId(),
                dto.getTimestamp());
        return auditLogBuilder
                .before(PnAuditLogEventType.AUD_NT_TIMELINE, auditLog)
                .iun(dto.getIun())
                .build();
    }

    @Override
    public Mono<TimelineElementInternal> getTimelineElement(String iun, String timelineId, boolean strongly) {
        log.debug("GetTimelineElement - IUN={} and timelineId={}", iun, timelineId);

        return timelineDao.getTimelineElement(iun, timelineId, strongly)
                .flatMap(timelineElement -> addConfidentialInformationIfTimelineElementIsPresent(iun, timelineId, timelineElement));
    }

    private Mono<TimelineElementInternal> addConfidentialInformationIfTimelineElementIsPresent(String iun, String timelineId, TimelineElementInternal timelineElement) {
        return confidentialInformationService.getTimelineElementConfidentialInformation(iun, timelineId)
                .map(confidentialDto -> enrichTimelineElementWithConfidentialInformation(
                        timelineElement.getDetails(), confidentialDto
                ))
                .thenReturn(timelineElement);
    }

    public Mono<Long> retrieveAndIncrementCounterForTimelineEvent(String timelineId) {
        return this.timelineCounterEntityDao.getCounter(timelineId)
                .map(TimelineCounterEntity::getCounter);
    }

    @Override
    public Mono<TimelineElementDetailsInt> getTimelineElementDetails(String iun, String timelineId) {
        log.debug("GetTimelineElement - IUN={} and timelineId={}", iun, timelineId);

        return this.timelineDao.getTimelineElement(iun, timelineId, false)
                .flatMap(timelineElement -> confidentialInformationService
                        .getTimelineElementConfidentialInformation(iun, timelineId)
                        .map(confidentialDto -> enrichTimelineElementWithConfidentialInformation(
                                timelineElement.getDetails(), confidentialDto
                        ))
                        .thenReturn(timelineElement.getDetails()));
    }

    @Override
    public Mono<TimelineElementInternal> getTimelineElementForSpecificRecipient(String iun, int recIndex, TimelineElementCategoryInt category) {
        log.debug("getTimelineElementForSpecificRecipient - IUN={} and recIndex={}", iun, recIndex);

        return this.timelineDao.getTimeline(iun)
                .filter(x -> x.getCategory().equals(category))
                .filter(x -> {
                    if (x.getDetails() instanceof RecipientRelatedTimelineElementDetails recRelatedTimelineElementDetails) {
                        return recRelatedTimelineElementDetails.getRecIndex() == recIndex;
                    }
                    return false;
                })
                .next();
    }

    @Override
    public Mono<TimelineElementDetailsInt> getTimelineElementDetailForSpecificRecipient(String iun, int recIndex, boolean confidentialInfoRequired, TimelineElementCategoryInt category) {
        log.debug("getTimelineElementDetailForSpecificIndex - IUN={} and recIndex={}", iun, recIndex);

        return this.timelineDao.getTimeline(iun)
                .filter(x -> x.getCategory().equals(category))
                .filter(x -> {
                    if (category.getDetailsJavaClass().isInstance(x.getDetails()) && x.getDetails() instanceof RecipientRelatedTimelineElementDetails recRelatedTimelineElementDetails) {
                        return recRelatedTimelineElementDetails.getRecIndex() == recIndex;
                    }
                    return false;
                })
                .next()
                .flatMap(timelineElement -> {
                    if (confidentialInfoRequired) {
                        return confidentialInformationService.getTimelineElementConfidentialInformation(iun, timelineElement.getElementId())
                                .map(confidentialDto -> enrichTimelineElementWithConfidentialInformation(
                                        timelineElement.getDetails(), confidentialDto
                                ))
                                .thenReturn(timelineElement.getDetails());
                    } else {
                        return Mono.just(timelineElement.getDetails());
                    }
                });
    }


    public Flux<TimelineElementInternal> getTimeline(String iun, String timelineId, boolean confidentialInfoRequired, boolean strongly) {
        log.debug("getTimeline - iun={} timelineId={} strongly={}", iun, timelineId, strongly);

        Flux<TimelineElementInternal> setTimelineElements;

        if (timelineId != null) {
            setTimelineElements = timelineDao.getTimelineFilteredByElementId(iun, timelineId);
        } else if (strongly) {
            setTimelineElements = timelineDao.getTimelineStrongly(iun);
        } else {
            setTimelineElements = timelineDao.getTimeline(iun);
        }

        return setConfidentialInfo(iun, confidentialInfoRequired, setTimelineElements);
    }

    private Flux<TimelineElementInternal> setConfidentialInfo(String iun, boolean confidentialInfoRequired, Flux<TimelineElementInternal> setTimelineElements) {
        if (confidentialInfoRequired) {
            return confidentialInformationService.getTimelineConfidentialInformation(iun)
                    .flatMapMany(confidentialMap ->
                            setTimelineElements.map(element -> {
                                ConfidentialTimelineElementDtoInt dtoInt = confidentialMap.get(element.getElementId());
                                if (dtoInt != null) {
                                    enrichTimelineElementWithConfidentialInformation(element.getDetails(), dtoInt);
                                }
                                return element;
                            })
                    )
                    .switchIfEmpty(setTimelineElements);
        } else {
            return setTimelineElements;
        }
    }

    @Override
    public Mono<NotificationHistoryInt> getTimelineAndStatusHistory(String iun, int numberOfRecipients, Instant createdAt) {
        log.debug("getTimelineAndStatusHistory Start - iun={} ", iun);
        NotificationHistoryInt notificationHistoryInt = new NotificationHistoryInt();

        return getTimeline(iun, null, true, false)
                .collect(Collectors.toList())
                .flatMap(timelineElements -> {
                    if (timelineElements.isEmpty()) {
                        return Mono.empty();
                    }
                    notificationHistoryInt.setTimeline(timelineElements);
                    return Mono.just(getAndSetStatusHistory(timelineElements, numberOfRecipients, createdAt, notificationHistoryInt));
                })
                .map(this::getAndSetCurrentStatus)
                .map(this::remapTimelineElements);
    }

    private NotificationHistoryInt getAndSetCurrentStatus(NotificationHistoryInt notificationHistoryInt) {
        notificationHistoryInt.setNotificationStatus(statusUtils.getCurrentStatus(notificationHistoryInt.getNotificationStatusHistory()));
        return notificationHistoryInt;
    }

    private NotificationHistoryInt getAndSetStatusHistory(List<TimelineElementInternal> timelineElements, int numberOfRecipients, Instant createdAt, NotificationHistoryInt notificationHistoryInt) {
        List<NotificationStatusHistoryElementInt> statusHistory = statusUtils.getStatusHistory(new HashSet<>(timelineElements), numberOfRecipients, createdAt);
        removeNotToBeReturnedElements(statusHistory);
        notificationHistoryInt.setNotificationStatusHistory(statusHistory);
        return notificationHistoryInt;
    }


    private NotificationHistoryInt remapTimelineElements(NotificationHistoryInt notificationHistoryInt) {

        notificationHistoryInt.setTimeline(notificationHistoryInt.getTimeline().stream()
                .map(t -> smartMapper.mapTimelineInternal(t, new HashSet<>(notificationHistoryInt.getTimeline())))
                .sorted(Comparator.naturalOrder())
                .toList());

        return notificationHistoryInt;
    }

    private void removeNotToBeReturnedElements(List<NotificationStatusHistoryElementInt> statusHistory) {
        // Viene eliminato l'elemento InValidation dalla response
        Optional<NotificationStatusHistoryElementInt> inValidationElementOpt = statusHistory.stream()
                .filter(element -> NotificationStatusInt.IN_VALIDATION.equals(element.getStatus()))
                .findFirst();

        if (inValidationElementOpt.isPresent()) {
            NotificationStatusHistoryElementInt inValidationElement = inValidationElementOpt.get();
            Instant inValidationStatusActiveFrom = inValidationElement.getActiveFrom();
            statusHistory.remove(inValidationElement);

            // Viene sostituito il campo ActiveFrom dell'elemento ACCEPTED con quella dell'elemento eliminato IN_VALIDATION
            statusHistory.stream()
                    .filter(statusHistoryElement -> NotificationStatusInt.ACCEPTED.equals(statusHistoryElement.getStatus()))
                    .findFirst()
                    .ifPresent(el -> el.setActiveFrom(inValidationStatusActiveFrom));
        }
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
