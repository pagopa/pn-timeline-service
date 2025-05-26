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
import it.pagopa.pn.timelineservice.dto.notification.ProbableSchedulingAnalogDateInt;
import it.pagopa.pn.timelineservice.dto.notification.status.NotificationStatusHistoryElementInt;
import it.pagopa.pn.timelineservice.dto.notification.status.NotificationStatusInt;
import it.pagopa.pn.timelineservice.dto.timeline.StatusInfoInternal;
import it.pagopa.pn.timelineservice.dto.timeline.TimelineElementInternal;
import it.pagopa.pn.timelineservice.dto.timeline.details.ProbableDateAnalogWorkflowDetailsInt;
import it.pagopa.pn.timelineservice.dto.timeline.details.RecipientRelatedTimelineElementDetails;
import it.pagopa.pn.timelineservice.dto.timeline.details.TimelineElementCategoryInt;
import it.pagopa.pn.timelineservice.exceptions.PnNotFoundException;
import it.pagopa.pn.timelineservice.middleware.dao.TimelineCounterEntityDao;
import it.pagopa.pn.timelineservice.middleware.dao.TimelineDao;
import it.pagopa.pn.timelineservice.service.ConfidentialInformationService;
import it.pagopa.pn.timelineservice.service.StatusService;
import it.pagopa.pn.timelineservice.service.TimelineService;
import it.pagopa.pn.timelineservice.service.mapper.SmartMapper;
import it.pagopa.pn.timelineservice.utils.MdcKey;
import it.pagopa.pn.timelineservice.utils.StatusUtils;
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
    public boolean addTimelineElement(TimelineElementInternal dto, NotificationInfoInt notification) {
        MDC.put(MDCUtils.MDC_PN_CTX_TOPIC, MdcKey.TIMELINE_KEY);

        log.debug("addTimelineElement - IUN={} and timelineId={}", dto.getIun(), dto.getElementId());
        PnAuditLogBuilder auditLogBuilder = new PnAuditLogBuilder();

        PnAuditLogEvent logEvent = getPnAuditLogEvent(dto, auditLogBuilder);
        logEvent.log();


        if (notification != null) {
            boolean isMultiRecipient = notification.getNumberOfRecipients() > 1;
            boolean isCriticalTimelineElement = COMPLETED_DELIVERY_WORKFLOW_CATEGORY.contains(dto.getCategory());
            if (isMultiRecipient && isCriticalTimelineElement) {
                return addCriticalTimelineElement(dto, notification, logEvent);
            }

            return addTimelineElement(dto, notification, logEvent);

        } else {
            MDC.remove(MDCUtils.MDC_PN_CTX_TOPIC);
            logEvent.generateFailure("Try to update Timeline and Status for non existing iun={}", dto.getIun());
            throw new PnInternalException("Try to update Timeline and Status for non existing iun " + dto.getIun(), ERROR_CODE_DELIVERYPUSH_ADDTIMELINEFAILED);
        }

    }

    private boolean addCriticalTimelineElement(TimelineElementInternal dto, NotificationInfoInt notification, PnAuditLogEvent logEvent) {
        log.debug("addCriticalTimelineElement - IUN={} and timelineId={}", dto.getIun(), dto.getElementId());

        Optional<SimpleLock> optSimpleLock = lockProvider.lock(new LockConfiguration(Instant.now(), notification.getIun(), pnTimelineServiceConfigs.getTimelineLockDuration(), Duration.ZERO));
        if (optSimpleLock.isEmpty()) {
            logEvent.generateFailure("Lock not acquired for iun={} and timelineId={}", notification.getIun(), dto.getElementId()).log();
            throw new PnInternalException("Lock not acquired for iun " + notification.getIun(), ERROR_CODE_DELIVERYPUSH_ADDTIMELINEFAILED);
        }

        SimpleLock simpleLock = optSimpleLock.get();

        try {
            return processTimelinePersistence(dto, notification, logEvent);
        } catch (Exception ex) {
            MDC.remove(MDCUtils.MDC_PN_CTX_TOPIC);
            logEvent.generateFailure("Exception in addTimelineElement", ex).log();
            throw new PnInternalException("Exception in addTimelineElement - iun=" + notification.getIun() + " elementId=" + dto.getElementId(), ERROR_CODE_DELIVERYPUSH_ADDTIMELINEFAILED, ex);
        } finally {
            simpleLock.unlock();
        }
    }

    private boolean addTimelineElement(TimelineElementInternal dto, NotificationInfoInt notification, PnAuditLogEvent logEvent) {
        try {
            return processTimelinePersistence(dto, notification, logEvent);
        } catch (Exception ex) {
            MDC.remove(MDCUtils.MDC_PN_CTX_TOPIC);
            logEvent.generateFailure("Exception in addTimelineElement", ex).log();
            throw new PnInternalException("Exception in addTimelineElement - iun=" + notification.getIun() + " elementId=" + dto.getElementId(), ERROR_CODE_DELIVERYPUSH_ADDTIMELINEFAILED, ex);
        }
    }

    private boolean processTimelinePersistence(TimelineElementInternal dto, NotificationInfoInt notification, PnAuditLogEvent logEvent) {
        boolean timelineInsertSkipped;
        Set<TimelineElementInternal> currentTimeline = getTimeline(dto.getIun(), null,false,false);
        StatusService.NotificationStatusUpdate notificationStatuses = statusService.getStatus(dto, currentTimeline, notification);

        // vengono salvate le informazioni confidenziali in sicuro, dal momento che successivamente non saranno salvate a DB
        confidentialInformationService.saveTimelineConfidentialInformation(dto);

        // aggiungo al DTO lo status info che poi verrà mappato sull'entity e salvato
        TimelineElementInternal dtoWithStatusInfo = enrichWithStatusInfo(dto, currentTimeline, notificationStatuses, notification.getSentAt());

        Instant now = Instant.now();
        if(now.isAfter(pnTimelineServiceConfigs.getStartWriteBusinessTimestamp()) && now.isBefore(pnTimelineServiceConfigs.getStopWriteBusinessTimestamp())) {
            Instant cachedTimestamp = dtoWithStatusInfo.getTimestamp();
            // calcolo e aggiungo il businessTimestamp
            dtoWithStatusInfo = smartMapper.mapTimelineInternal(dtoWithStatusInfo, currentTimeline);
            dtoWithStatusInfo.setTimestamp(cachedTimestamp);
        }

        timelineInsertSkipped = persistTimelineElement(dtoWithStatusInfo);

        // non schedulo più il webhook in questo punto (schedulerService.scheduleWebhookEvent), dato che la cosa viene fatta in maniera
        // asincrona da una lambda che opera partendo da stream Kinesis

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
    }

    private boolean persistTimelineElement(TimelineElementInternal dtoWithStatusInfo) {
        try {
            timelineDao.addTimelineElementIfAbsent(dtoWithStatusInfo);
        } catch (PnIdConflictException ex) {
            log.warn("Exception idconflict is expected for retry, letting flow continue");
            return true;
        }
        return false;
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

    private Optional<TimelineElementInternal> addConfidentialInformationIfTimelineElementIsPresent(String iun, String timelineId, Optional<TimelineElementInternal> timelineElementInternalOpt) {
        if (timelineElementInternalOpt.isPresent()) {
            TimelineElementInternal timelineElementInt = timelineElementInternalOpt.get();

            confidentialInformationService.getTimelineElementConfidentialInformation(iun, timelineId).ifPresent(
                    confidentialDto -> enrichTimelineElementWithConfidentialInformation(
                            timelineElementInt.getDetails(), confidentialDto
                    )
            );

            return Optional.of(timelineElementInt);
        }
        return Optional.empty();
    }

    public Long retrieveAndIncrementCounterForTimelineEvent(String timelineId) {
        return this.timelineCounterEntityDao.getCounter(timelineId).getCounter();
    }

    @Override
    public <T> Optional<T> getTimelineElementDetails(String iun, String timelineId, Class<T> timelineDetailsClass) {
        log.debug("GetTimelineElement - IUN={} and timelineId={}", iun, timelineId);

        Optional<TimelineElementInternal> timelineElementOpt = this.timelineDao.getTimelineElement(iun, timelineId, false);
        if (timelineElementOpt.isPresent()) {
            TimelineElementInternal timelineElement = timelineElementOpt.get();

            confidentialInformationService.getTimelineElementConfidentialInformation(iun, timelineId).ifPresent(
                    confidentialDto -> enrichTimelineElementWithConfidentialInformation(
                            timelineElement.getDetails(), confidentialDto
                    )
            );

            return Optional.of(timelineDetailsClass.cast(timelineElement.getDetails()));
        }

        return Optional.empty();
    }

    @Override
    public Optional<TimelineElementInternal> getTimelineElementForSpecificRecipient(String iun, int recIndex, TimelineElementCategoryInt category) {
        log.debug("getTimelineElementForSpecificRecipient - IUN={} and recIndex={}", iun, recIndex);

        return this.timelineDao.getTimeline(iun)
                .stream().filter(x -> x.getCategory().equals(category))
                .filter(x -> {

                    if (x.getDetails() instanceof RecipientRelatedTimelineElementDetails recRelatedTimelineElementDetails) {
                        return recRelatedTimelineElementDetails.getRecIndex() == recIndex;
                    }
                    return false;
                })
                .findFirst();
    }

    @Override
    public <T> Optional<T> getTimelineElementDetailForSpecificRecipient(String iun, int recIndex, boolean confidentialInfoRequired, TimelineElementCategoryInt category, Class<T> timelineDetailsClass) {
        log.debug("getTimelineElementDetailForSpecificIndex - IUN={} and recIndex={}", iun, recIndex);

        Optional<TimelineElementInternal> timelineElementOpt = this.timelineDao.getTimeline(iun)
                .stream().filter(x -> x.getCategory().equals(category))
                .filter(x -> {

                    if (timelineDetailsClass.isInstance(x.getDetails()) && x.getDetails() instanceof RecipientRelatedTimelineElementDetails recRelatedTimelineElementDetails) {
                        return recRelatedTimelineElementDetails.getRecIndex() == recIndex;
                    }
                    return false;
                })
                .findFirst();

        if (timelineElementOpt.isPresent()) {
            TimelineElementInternal timelineElement = timelineElementOpt.get();

            if (confidentialInfoRequired) {
                confidentialInformationService.getTimelineElementConfidentialInformation(iun, timelineElement.getElementId()).ifPresent(
                        confidentialDto -> enrichTimelineElementWithConfidentialInformation(
                                timelineElement.getDetails(), confidentialDto
                        )
                );
            }

            return Optional.of(timelineDetailsClass.cast(timelineElement.getDetails()));
        }

        return Optional.empty();
    }
    private void setConfidentialInfo(boolean confidentialInfoRequired, String iun, Set<TimelineElementInternal> setTimelineElements) {
        if (confidentialInfoRequired) {
            Optional<Map<String, ConfidentialTimelineElementDtoInt>> mapConfOtp;
            mapConfOtp = confidentialInformationService.getTimelineConfidentialInformation(iun);

            if (mapConfOtp.isPresent()) {
                Map<String, ConfidentialTimelineElementDtoInt> mapConf = mapConfOtp.get();

                setTimelineElements.forEach(
                        timelineElementInt -> {
                            ConfidentialTimelineElementDtoInt dtoInt = mapConf.get(timelineElementInt.getElementId());
                            if (dtoInt != null) {
                                enrichTimelineElementWithConfidentialInformation(timelineElementInt.getDetails(), dtoInt);
                            }
                        }
                );
            }
        }
    }

    @Override
    public Optional<TimelineElementInternal> getTimelineElement(String iun, String timelineId, boolean strongly) {
        log.debug("GetTimelineElement - IUN={} and timelineId={}", iun, timelineId);

        Optional<TimelineElementInternal> timelineElementInternalOpt = timelineDao.getTimelineElement(iun, timelineId, strongly);
        return addConfidentialInformationIfTimelineElementIsPresent(iun, timelineId, timelineElementInternalOpt);
    }

    @Override
    public Set<TimelineElementInternal> getTimeline(String iun, String timelineId, boolean confidentialInfoRequired, boolean strongly) {
        log.debug("getTimeline - iun={} timelineId={} strongly={}", iun, timelineId, strongly);

        Set<TimelineElementInternal> setTimelineElements;

        if (timelineId != null) {
            setTimelineElements = timelineDao.getTimelineFilteredByElementId(iun, timelineId);
        } else if (strongly) {
            setTimelineElements = timelineDao.getTimelineStrongly(iun);
        } else {
            setTimelineElements = timelineDao.getTimeline(iun);
        }

        setConfidentialInfo(confidentialInfoRequired, iun, setTimelineElements);
        return setTimelineElements;
    }

    @Override
    public NotificationHistoryInt getTimelineAndStatusHistory(String iun, int numberOfRecipients, Instant createdAt) {
        log.debug("getTimelineAndStatusHistory Start - iun={} ", iun);

        Set<TimelineElementInternal> timelineElements = getTimeline(iun, null, true,false);

        List<NotificationStatusHistoryElementInt> statusHistory = statusUtils
                .getStatusHistory(timelineElements, numberOfRecipients, createdAt);

        removeNotToBeReturnedElements(statusHistory);

        NotificationStatusInt currentStatus = statusUtils.getCurrentStatus(statusHistory);

        log.debug("getTimelineAndStatusHistory Ok - iun={} ", iun);

        return createResponse(timelineElements, statusHistory, currentStatus);
    }

    private void removeNotToBeReturnedElements(List<NotificationStatusHistoryElementInt> statusHistory) {

        //Viene eliminato l'elemento InValidation dalla response
        Optional<Instant> inValidationStatusActiveFromOpt = Optional.empty();

        for (NotificationStatusHistoryElementInt element : statusHistory) {

            if (NotificationStatusInt.IN_VALIDATION.equals(element.getStatus())) {
                inValidationStatusActiveFromOpt = Optional.of(element.getActiveFrom());
                statusHistory.remove(element);
                break;
            }
        }

        if (inValidationStatusActiveFromOpt.isPresent()) {

            //Viene sostituito il campo ActiveFrom dell'elemento ACCEPTED con quella dell'elemento eliminato IN_VALIDATION
            Instant inValidationStatusActiveFrom = inValidationStatusActiveFromOpt.get();

            statusHistory.stream()
                    .filter(
                            statusHistoryElement -> NotificationStatusInt.ACCEPTED.equals(statusHistoryElement.getStatus())
                    ).findFirst()
                    .ifPresent(
                            el -> el.setActiveFrom(inValidationStatusActiveFrom)
                    );
        }
    }

    private NotificationHistoryInt createResponse(Set<TimelineElementInternal> timelineElements, List<NotificationStatusHistoryElementInt> statusHistory,
                                                       NotificationStatusInt currentStatus) {

        var timelineList = timelineElements.stream()
                .map(t -> smartMapper.mapTimelineInternal(t, timelineElements))
                .sorted(Comparator.naturalOrder())
                .filter(this::isNotDiagnosticTimelineElement)
                .toList();

        NotificationHistoryInt notificationHistoryInt = new NotificationHistoryInt();
        notificationHistoryInt.setTimeline(timelineList);
        notificationHistoryInt.setNotificationStatus(currentStatus);
        notificationHistoryInt.setNotificationStatusHistory(statusHistory);

        return notificationHistoryInt;
    }

    public boolean isNotDiagnosticTimelineElement(TimelineElementInternal timelineElementInternal) {
        if (timelineElementInternal.getCategory() == null) {
            return true;
        }
        String internalCategory = timelineElementInternal.getCategory().name();
        return Arrays.stream(TimelineElementCategoryInt.values())
                .map(Enum::name)
                .anyMatch(category -> category.equalsIgnoreCase(internalCategory));

    }

    @Override
    public Mono<ProbableSchedulingAnalogDateInt> getSchedulingAnalogDate(String iun, int recIndex) {
        return Mono.justOrEmpty(getTimelineElementDetailForSpecificRecipient(
                iun,
                recIndex,
                false,
                PROBABLE_SCHEDULING_ANALOG_DATE,
                ProbableDateAnalogWorkflowDetailsInt.class
            ))
            .map(details -> ProbableSchedulingAnalogDateInt.builder()
                    .iun(iun)
                    .recIndex(details.getRecIndex())
                    .schedulingAnalogDate(details.getSchedulingAnalogDate())
                    .build())
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
