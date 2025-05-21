package it.pagopa.pn.timelineservice.service.impl;

import it.pagopa.pn.commons.exceptions.PnIdConflictException;
import it.pagopa.pn.commons.exceptions.PnInternalException;
import it.pagopa.pn.commons.log.PnAuditLogBuilder;
import it.pagopa.pn.commons.log.PnAuditLogEvent;
import it.pagopa.pn.commons.log.PnAuditLogEventType;
import it.pagopa.pn.commons.utils.MDCUtils;
import it.pagopa.pn.timelineservice.config.PnTimelineServiceConfigs;
import it.pagopa.pn.timelineservice.dto.*;
import it.pagopa.pn.timelineservice.dto.ext.datavault.ConfidentialTimelineElementDtoInt;
import it.pagopa.pn.timelineservice.dto.ext.notification.NotificationInt;
import it.pagopa.pn.timelineservice.dto.ext.notification.status.NotificationStatusHistoryElementInt;
import it.pagopa.pn.timelineservice.dto.ext.notification.status.NotificationStatusInt;
import it.pagopa.pn.timelineservice.dto.notification.NotificationHistoryResponse;
import it.pagopa.pn.timelineservice.dto.notification.NotificationStatus;
import it.pagopa.pn.timelineservice.dto.timeline.StatusInfoInternal;
import it.pagopa.pn.timelineservice.dto.timeline.TimelineElementInternal;
import it.pagopa.pn.timelineservice.dto.timeline.details.*;
import it.pagopa.pn.timelineservice.exceptions.PnNotFoundException;
import it.pagopa.pn.timelineservice.middleware.dao.TimelineCounterEntityDao;
import it.pagopa.pn.timelineservice.middleware.dao.TimelineDao;
import it.pagopa.pn.timelineservice.middleware.dao.dynamo.entity.TimelineCounterEntity;
import it.pagopa.pn.timelineservice.service.ConfidentialInformationService;
import it.pagopa.pn.timelineservice.service.StatusService;
import it.pagopa.pn.timelineservice.service.TimelineService;
import it.pagopa.pn.timelineservice.service.mapper.NotificationStatusHistoryElementMapper;
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

import static it.pagopa.pn.timelineservice.dto.timeline.details.TimelineElementCategoryInt.PROBABLE_SCHEDULING_ANALOG_DATE;
import static it.pagopa.pn.timelineservice.exceptions.PnDeliveryPushExceptionCodes.ERROR_CODE_DELIVERYPUSH_ADDTIMELINEFAILED;
import static it.pagopa.pn.timelineservice.exceptions.PnDeliveryPushExceptionCodes.ERROR_CODE_DELIVERYPUSH_STATUSNOTFOUND;
import static it.pagopa.pn.timelineservice.service.mapper.ConfidentialDetailEnricher.enrichTimelineElementWithConfidentialInformation;
import static it.pagopa.pn.timelineservice.utils.StatusUtils.COMPLETED_DELIVERY_WORKFLOW_CATEGORY;


@Service
@Slf4j
@RequiredArgsConstructor
public class TimeLineServiceImpl implements TimelineService {
    private final TimelineDao timelineDao;
    private final TimelineCounterEntityDao timelineCounterEntityDao;
    private final StatusUtils statusUtils;
    private final ConfidentialInformationService confidentialInformationService;
    private final StatusService statusService;
    private final SmartMapper smartMapper;
    private final LockProvider lockProvider;
    private final PnTimelineServiceConfigs pnTimelineServiceConfigs;

    @Override
    public Mono<Boolean> addTimelineElement(TimelineElementInternal dto, NotificationInt notification) {
        log.debug("addTimelineElement - IUN={} and timelineId={}", dto.getIun(), dto.getElementId());
        PnAuditLogBuilder auditLogBuilder = new PnAuditLogBuilder();

        PnAuditLogEvent logEvent = getPnAuditLogEvent(dto, auditLogBuilder);
        logEvent.log();

        if (notification != null) {
            boolean isMultiRecipient = notification.getRecipientsCount() > 1;
            boolean isCriticalTimelineElement = COMPLETED_DELIVERY_WORKFLOW_CATEGORY.contains(dto.getCategory());
            if (isMultiRecipient && isCriticalTimelineElement) {
                return addCriticalTimelineElement(dto, notification, logEvent)
                        .doFinally(signal -> MDC.remove(MDCUtils.MDC_PN_CTX_TOPIC));
            }

            return addTimelineElement(dto, notification, logEvent)
                    .doFinally(signal -> MDC.remove(MDCUtils.MDC_PN_CTX_TOPIC));
        } else {
            MDC.remove(MDCUtils.MDC_PN_CTX_TOPIC);
            logEvent.generateFailure("Try to update Timeline and Status for non existing iun={}", dto.getIun());
            return Mono.error(new PnInternalException("Try to update Timeline and Status for non existing iun " + dto.getIun(), ERROR_CODE_DELIVERYPUSH_ADDTIMELINEFAILED));
        }
    }

    private Mono<Boolean> addCriticalTimelineElement(TimelineElementInternal dto, NotificationInt notification, PnAuditLogEvent logEvent) {
            log.debug("addCriticalTimelineElement - IUN={} and timelineId={}", dto.getIun(), dto.getElementId());

            return Mono.fromCallable(() -> lockProvider.lock(
                    new LockConfiguration(Instant.now(), notification.getIun(), pnTimelineServiceConfigs.getTimelineLockDuration(), Duration.ZERO)))
                .flatMap(optSimpleLock -> {
                    if (optSimpleLock.isEmpty()) {
                        logEvent.generateFailure("Lock not acquired for iun={} and timelineId={}", notification.getIun(), dto.getElementId()).log();
                        return Mono.error(new PnInternalException("Lock not acquired for iun " + notification.getIun(), ERROR_CODE_DELIVERYPUSH_ADDTIMELINEFAILED));
                    }
                    SimpleLock simpleLock = optSimpleLock.get();
                    return processTimelinePersistence(dto, notification, logEvent)
                            .onErrorMap(ex -> {
                                MDC.remove(MDCUtils.MDC_PN_CTX_TOPIC);
                                logEvent.generateFailure("Exception in addCriticalTimelineElement", ex).log();
                                return new PnInternalException("Exception in addCriticalTimelineElement - iun=" + notification.getIun() + " elementId=" + dto.getElementId(), ERROR_CODE_DELIVERYPUSH_ADDTIMELINEFAILED, ex);
                            })
                            .doFinally(signalType -> simpleLock.unlock());
                });
    }

    private Mono<Boolean> addTimelineElement(TimelineElementInternal dto, NotificationInt notification, PnAuditLogEvent logEvent) {
        return processTimelinePersistence(dto, notification, logEvent)
                .onErrorMap(ex -> {
                    MDC.remove(MDCUtils.MDC_PN_CTX_TOPIC);
                    logEvent.generateFailure("Exception in addTimelineElement", ex).log();
                    return new PnInternalException("Exception in addTimelineElement - iun=" + notification.getIun() + " elementId=" + dto.getElementId(), ERROR_CODE_DELIVERYPUSH_ADDTIMELINEFAILED, ex);
                });
    }

    private Mono<Boolean> processTimelinePersistence(TimelineElementInternal dto, NotificationInt notification, PnAuditLogEvent logEvent) {
        return getTimeline(dto.getIun(), null, true, false)
                .collectList()
                .flatMap(list -> {
                    Set<TimelineElementInternal> currentTimeline = new HashSet<>(list);
                    StatusService.NotificationStatusUpdate notificationStatusUpdate = statusService.getStatus(dto, currentTimeline, notification);
                    return confidentialInformationService.saveTimelineConfidentialInformation(dto)
                            .then(Mono.defer(() -> {
                                TimelineElementInternal dtoWithStatusInfo = enrichWithStatusInfo(dto, currentTimeline, notificationStatusUpdate, notification.getSentAt());

                                if(shouldWriteBusinessTimestamp()) {
                                    writeBusinessTimestamp(dtoWithStatusInfo, currentTimeline);
                                }

                                return persistTimelineElement(dtoWithStatusInfo)
                                        .map(timelineInsertSkipped -> {
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

                                            return timelineInsertSkipped;
                                        });
                            }));
                });
    }

    private boolean shouldWriteBusinessTimestamp() {
        Instant now = Instant.now();
        return now.isAfter(pnTimelineServiceConfigs.getStartWriteBusinessTimestamp()) && now.isBefore(pnTimelineServiceConfigs.getStopWriteBusinessTimestamp());
    }

    private void writeBusinessTimestamp(TimelineElementInternal dtoWithStatusInfo, Set<TimelineElementInternal> currentTimeline) {
        Instant cachedTimestamp = dtoWithStatusInfo.getTimestamp();
        dtoWithStatusInfo = smartMapper.mapTimelineInternal(dtoWithStatusInfo, currentTimeline);
        dtoWithStatusInfo.setTimestamp(cachedTimestamp);
    }

    private Mono<Boolean> persistTimelineElement(TimelineElementInternal dtoWithStatusInfo) {
         return timelineDao.addTimelineElementIfAbsent(dtoWithStatusInfo)
                .thenReturn(false)
                .onErrorResume(PnIdConflictException.class, ex -> {
                    log.warn("Exception idconflict is expected for retry, letting flow continue");
                    return Mono.just(true);
                });
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
                .doOnNext(confidentialDto -> enrichTimelineElementWithConfidentialInformation(
                        timelineElement.getDetails(), confidentialDto
                ))
                .thenReturn(timelineElement);
    }

    public Mono<Long> retrieveAndIncrementCounterForTimelineEvent(String timelineId) {
        return this.timelineCounterEntityDao.getCounter(timelineId)
                .map(TimelineCounterEntity::getCounter);
    }

    @Override
    public <T> Mono<T> getTimelineElementDetails(String iun, String timelineId, Class<T> timelineDetailsClass) {
        log.debug("GetTimelineElement - IUN={} and timelineId={}", iun, timelineId);

        return this.timelineDao.getTimelineElement(iun, timelineId, false)
                .flatMap(timelineElement -> confidentialInformationService
                        .getTimelineElementConfidentialInformation(iun, timelineId)
                        .doOnNext(confidentialDto -> enrichTimelineElementWithConfidentialInformation(
                                timelineElement.getDetails(), confidentialDto
                        ))
                        .thenReturn(timelineDetailsClass.cast(timelineElement.getDetails()))
                );
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
    public <T> Mono<T> getTimelineElementDetailForSpecificRecipient(String iun, int recIndex, boolean confidentialInfoRequired, TimelineElementCategoryInt category, Class<T> timelineDetailsClass) {
        log.debug("getTimelineElementDetailForSpecificIndex - IUN={} and recIndex={}", iun, recIndex);

        return this.timelineDao.getTimeline(iun)
                .filter(x -> x.getCategory().equals(category))
                .filter(x -> {
                    if (timelineDetailsClass.isInstance(x.getDetails()) && x.getDetails() instanceof RecipientRelatedTimelineElementDetails recRelatedTimelineElementDetails) {
                        return recRelatedTimelineElementDetails.getRecIndex() == recIndex;
                    }
                    return false;
                })
                .next()
                .flatMap(timelineElement -> {
                    if (confidentialInfoRequired) {
                        return confidentialInformationService.getTimelineElementConfidentialInformation(iun, timelineElement.getElementId())
                                .doOnNext(confidentialDto -> enrichTimelineElementWithConfidentialInformation(
                                        timelineElement.getDetails(), confidentialDto
                                ))
                                .thenReturn(timelineDetailsClass.cast(timelineElement.getDetails()));
                    } else {
                        return Mono.just(timelineDetailsClass.cast(timelineElement.getDetails()));
                    }
                });
    }

    @Override
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

        if (confidentialInfoRequired) {
            return setTimelineElements
                .collect(Collectors.toSet())
                .flatMapMany(set -> confidentialInformationService.getTimelineConfidentialInformation(iun)
                    .flatMapMany(mapConf -> Flux.fromIterable(set)
                        .doOnNext(timelineElementInt -> {
                            ConfidentialTimelineElementDtoInt dtoInt = mapConf.get(timelineElementInt.getElementId());
                            if (dtoInt != null) {
                                enrichTimelineElementWithConfidentialInformation(timelineElementInt.getDetails(), dtoInt);
                            }
                        })
                    )
                );
        } else {
            return setTimelineElements;
        }
    }

    @Override
    public Mono<NotificationHistoryResponse> getTimelineAndStatusHistory(String iun, int numberOfRecipients, Instant createdAt) {
        log.debug("getTimelineAndStatusHistory Start - iun={} ", iun);

        return getTimeline(iun, null, true, false)
                .collect(Collectors.toSet())
                .map(timelineElements -> {
                    List<NotificationStatusHistoryElementInt> statusHistory = statusUtils
                            .getStatusHistory(timelineElements, numberOfRecipients, createdAt);

                    removeNotToBeReturnedElements(statusHistory);

                    NotificationStatusInt currentStatus = statusUtils.getCurrentStatus(statusHistory);

                    log.debug("getTimelineAndStatusHistory Ok - iun={} ", iun);

                    return createResponse(timelineElements, statusHistory, currentStatus);
                });
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

    private NotificationHistoryResponse createResponse(Set<TimelineElementInternal> timelineElements, List<NotificationStatusHistoryElementInt> statusHistory,
                                                       NotificationStatusInt currentStatus) {

        var timelineList = timelineElements.stream()
                .map(t -> smartMapper.mapTimelineInternal(t, timelineElements)) // rimappo su se stessa, per sistemare eventuali campi interni
                .sorted(Comparator.naturalOrder())
                .filter(this::isNotDiagnosticTimelineElement)
                .toList();

        return NotificationHistoryResponse.builder()
                .timeline(timelineList)
                .notificationStatusHistory(
                        statusHistory.stream().map(
                                NotificationStatusHistoryElementMapper::internalToExternal
                        ).toList()
                )
                .notificationStatus(currentStatus != null ? NotificationStatus.valueOf(currentStatus.getValue()) : null)
                .build();
    }

    public boolean isNotDiagnosticTimelineElement(TimelineElementInternal timelineElementInternal) {
        if (timelineElementInternal.getCategory() == null) {
            return true;
        }
        String internalCategory = timelineElementInternal.getCategory().name();
        return Arrays.stream(TimelineElementCategory.values())
                .anyMatch(TimelineElementCategory -> TimelineElementCategory.getValue().equalsIgnoreCase(internalCategory));

    }

    @Override
    public Mono<ProbableSchedulingAnalogDateDto> getSchedulingAnalogDate(String iun, int recIndex) {
        return getTimelineElementDetailForSpecificRecipient(
                        iun,
                        recIndex,
                        false,
                        PROBABLE_SCHEDULING_ANALOG_DATE,
                        ProbableDateAnalogWorkflowDetailsInt.class
                ).map(details -> new ProbableSchedulingAnalogDateDto()
                        .iun(iun)
                        .recIndex(details.getRecIndex())
                                .schedulingAnalogDate(details.getSchedulingAnalogDate()))
                        .switchIfEmpty(Mono.error(() -> {
                            String message = String.format("ProbableSchedulingDateAnalog not found for iun: %s, recIndex: %d", iun, recIndex);
                            return new PnNotFoundException("Not found", message, ERROR_CODE_DELIVERYPUSH_STATUSNOTFOUND);
                        }));
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
