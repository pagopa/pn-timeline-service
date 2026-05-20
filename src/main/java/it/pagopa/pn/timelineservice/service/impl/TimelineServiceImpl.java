package it.pagopa.pn.timelineservice.service.impl;

import it.pagopa.pn.timelineservice.dto.ext.datavault.ConfidentialTimelineElementDtoInt;
import it.pagopa.pn.timelineservice.dto.notification.NotificationHistoryInt;
import it.pagopa.pn.timelineservice.dto.notification.status.NotificationStatusHistoryElementInt;
import it.pagopa.pn.timelineservice.dto.notification.status.NotificationStatusHistoryInvalidatedElementInt;
import it.pagopa.pn.timelineservice.dto.notification.status.NotificationStatusInt;
import it.pagopa.pn.timelineservice.dto.timeline.CommunicationType;
import it.pagopa.pn.timelineservice.dto.timeline.ElementIdPrefix;
import it.pagopa.pn.timelineservice.dto.timeline.TimelineElementInternal;
import it.pagopa.pn.timelineservice.dto.timeline.details.*;
import it.pagopa.pn.timelineservice.exceptions.PnNotFoundException;
import it.pagopa.pn.timelineservice.generated.openapi.server.v1.dto.AarResponse;
import it.pagopa.pn.timelineservice.generated.openapi.server.v1.dto.CancellationRequestResponse;
import it.pagopa.pn.timelineservice.generated.openapi.server.v1.dto.DeliveryInformationResponse;
import it.pagopa.pn.timelineservice.generated.openapi.server.v1.dto.RequestRefusedResponse;
import it.pagopa.pn.timelineservice.middleware.dao.TimelineCounterEntityDao;
import it.pagopa.pn.timelineservice.middleware.dao.TimelineDao;
import it.pagopa.pn.timelineservice.middleware.dao.dynamo.entity.TimelineCounterEntity;
import it.pagopa.pn.timelineservice.operations.CommunicationTypeClassifier;
import it.pagopa.pn.timelineservice.operations.TimelineOperationsResolver;
import it.pagopa.pn.timelineservice.operations.common.TimelineTimestampMapper;
import it.pagopa.pn.timelineservice.service.ConfidentialInformationService;
import it.pagopa.pn.timelineservice.service.StatusHistoryService;
import it.pagopa.pn.timelineservice.service.TimelineService;
import it.pagopa.pn.timelineservice.service.mapper.SmartMapper;
import it.pagopa.pn.timelineservice.utils.StatusUtils;
import it.pagopa.pn.timelineservice.utils.extraction.TimelineDataExtractionEngine;
import it.pagopa.pn.timelineservice.utils.extraction.mapper.DeliveryInfoMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

import static it.pagopa.pn.timelineservice.dto.timeline.details.TimelineElementCategoryInt.NOTIFICATION_TIMELINE_REWORKED;
import static it.pagopa.pn.timelineservice.exceptions.PnTimelineServiceExceptionCodes.ERROR_CODE_TIMELINESERVICE_TIMELINE_ELEMENT_NOT_PRESENT;
import static it.pagopa.pn.timelineservice.exceptions.PnTimelineServiceExceptionCodes.ERROR_CODE_TIMELINESERVICE_TIMELINE_NOT_PRESENT_FOR_CURRENT_IUN;
import static it.pagopa.pn.timelineservice.service.mapper.ConfidentialDetailEnricher.enrichTimelineElementWithConfidentialInformation;


@Service
@Slf4j
@RequiredArgsConstructor
public class TimelineServiceImpl implements TimelineService {

    private final TimelineDao timelineDao;
    private final TimelineCounterEntityDao timelineCounterEntityDao;
    private final StatusHistoryService statusHistoryService;
    private final ConfidentialInformationService confidentialInformationService;
    private final CommunicationTypeClassifier communicationTypeClassifier;
    private final TimelineOperationsResolver timelineOperationsResolver;

    @Override
    public Mono<TimelineElementInternal> getTimelineElement(String iun, String timelineId, boolean strongly) {
        log.debug("GetTimelineElement - IUN={} and timelineId={}", iun, timelineId);

        return timelineDao.getTimelineElement(iun, timelineId, strongly)
                .flatMap(timelineElement -> addConfidentialInformationIfTimelineElementIsPresent(iun, timelineId, timelineElement));
    }

    private Mono<TimelineElementInternal> addConfidentialInformationIfTimelineElementIsPresent(String iun, String timelineId, TimelineElementInternal timelineElement) {
        if (NOTIFICATION_TIMELINE_REWORKED.equals(timelineElement.getCategory()) && timelineElement.getDetails() instanceof NotificationTimelineReworkedDetailsInt) {
            return setConfidentialInfo(iun, timelineElement);
        } else {
            return confidentialInformationService.getTimelineElementConfidentialInformation(iun, timelineId)
                    .map(confidentialDto -> enrichTimelineElementWithConfidentialInformation(
                            timelineElement.getDetails(), confidentialDto
                    ))
                    .thenReturn(timelineElement);
        }
    }

    public Mono<Long> retrieveAndIncrementCounterForTimelineEvent(String timelineId) {
        return this.timelineCounterEntityDao.getCounter(timelineId)
                .map(TimelineCounterEntity::getCounter);
    }

    @Override
    public Mono<TimelineElementDetailsInt> getTimelineElementDetails(String iun, String timelineId) {
        log.debug("GetTimelineElement - IUN={} and timelineId={}", iun, timelineId);

        return this.timelineDao.getTimelineElement(iun, timelineId, false)
                .flatMap(timelineElement -> {
                    if (NOTIFICATION_TIMELINE_REWORKED.equals(timelineElement.getCategory()) && timelineElement.getDetails() instanceof NotificationTimelineReworkedDetailsInt) {
                        return setConfidentialInfo(iun, timelineElement)
                                .map(TimelineElementInternal::getDetails);
                    } else {
                        return confidentialInformationService
                                .getTimelineElementConfidentialInformation(iun, timelineId)
                                .map(confidentialDto -> enrichTimelineElementWithConfidentialInformation(
                                        timelineElement.getDetails(), confidentialDto
                                ))
                                .thenReturn(timelineElement.getDetails());
                    }
                });
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
                        if (NOTIFICATION_TIMELINE_REWORKED.equals(timelineElement.getCategory()) && timelineElement.getDetails() instanceof NotificationTimelineReworkedDetailsInt) {
                            return setConfidentialInfo(iun, timelineElement)
                                    .map(TimelineElementInternal::getDetails);
                        } else {
                            return confidentialInformationService.getTimelineElementConfidentialInformation(iun, timelineElement.getElementId())
                                    .map(confidentialDto -> enrichTimelineElementWithConfidentialInformation(
                                            timelineElement.getDetails(), confidentialDto
                                    ))
                                    .thenReturn(timelineElement.getDetails());
                        }
                    } else {
                        return Mono.just(timelineElement.getDetails());
                    }
                });
    }


    public Flux<TimelineElementInternal> getTimeline(String iun, String timelineId, boolean confidentialInfoRequired, boolean strongly) {
        log.debug("getTimeline - iun={} timelineId={} strongly={}", iun, timelineId, strongly);

        Flux<TimelineElementInternal> setTimelineElements;

        if (timelineId != null) {
            setTimelineElements = timelineDao.getTimelineFilteredByElementId(iun, timelineId, strongly);
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
                            setTimelineElements.map(element -> enrichWithConfidentialInformation(element, confidentialMap))
                    )
                    .switchIfEmpty(setTimelineElements);
        } else {
            return setTimelineElements;
        }
    }

    private Mono<TimelineElementInternal> setConfidentialInfo(String iun, TimelineElementInternal element) {
        return confidentialInformationService.getTimelineConfidentialInformation(iun)
                .map(confidentialMap ->
                        enrichWithConfidentialInformation(element, confidentialMap)
                )
                .switchIfEmpty(Mono.just(element));
    }

    private TimelineElementInternal enrichWithConfidentialInformation(TimelineElementInternal element, Map<String, ConfidentialTimelineElementDtoInt> confidentialMap) {
        if (NOTIFICATION_TIMELINE_REWORKED.equals(element.getCategory()) && element.getDetails() instanceof NotificationTimelineReworkedDetailsInt reworkDetail) {
            enrichReworkDetailWithConfidentialInformation(reworkDetail, confidentialMap);
        }else {
            ConfidentialTimelineElementDtoInt dtoInt = confidentialMap.get(element.getElementId());
            if (dtoInt != null) {
                enrichTimelineElementWithConfidentialInformation(element.getDetails(), dtoInt);
            }
        }
        return element;
    }

    private void enrichReworkDetailWithConfidentialInformation(NotificationTimelineReworkedDetailsInt reworkDetail, Map<String, ConfidentialTimelineElementDtoInt> confidentialMap) {
        reworkDetail.getInvalidatedTimelineAndStatusHistory().stream()
                .map(NotificationStatusHistoryInvalidatedElementInt::getRelatedTimelineElements)
                .flatMap(Collection::stream)
                .forEach(timelineElementInternal -> {
                    ConfidentialTimelineElementDtoInt dtoInt = confidentialMap.get(timelineElementInternal.getElementId());
                    if (dtoInt != null) {
                        enrichTimelineElementWithConfidentialInformation(timelineElementInternal.getDetails(),dtoInt);
                    }
                });
    }

    @Override
    public Mono<NotificationHistoryInt> getTimelineAndStatusHistory(String iun, int numberOfRecipients, Instant createdAt) {
        log.debug("getTimelineAndStatusHistory Start - iun={} ", iun);

        return getTimeline(iun, null, true, false)
                .collect(Collectors.toList())
                .map(this::classifyCommunicationType)
                .map(timelineWithCommunicationType -> this.buildNotificationHistory(timelineWithCommunicationType, numberOfRecipients, createdAt));
    }

    public record TimelineElementsWithCommunicationType(List<TimelineElementInternal> timelineElements, CommunicationType communicationType) {}

    private TimelineElementsWithCommunicationType classifyCommunicationType(List<TimelineElementInternal> timelineElements) {
        CommunicationType communicationType = communicationTypeClassifier.resolveFromTimelineElements(timelineElements);
        return new TimelineElementsWithCommunicationType(timelineElements, communicationType);
    }

    private NotificationHistoryInt buildNotificationHistory(TimelineElementsWithCommunicationType timelineElementsWithCommunicationType, int numberOfRecipients, Instant createdAt) {
        List<TimelineElementInternal> elements = timelineElementsWithCommunicationType.timelineElements;
        CommunicationType communicationType = timelineElementsWithCommunicationType.communicationType;

        List<NotificationStatusHistoryElementInt> statusHistory = getStatusHistory(elements, numberOfRecipients, createdAt, communicationType);
        List<TimelineElementInternal> remappedTimeline = remapAndSortTimelineElements(elements, communicationType);
        NotificationStatusInt currentStatus = StatusUtils.getCurrentStatus(statusHistory);

        NotificationHistoryInt result = new NotificationHistoryInt();
        result.setTimeline(remappedTimeline);
        result.setNotificationStatusHistory(statusHistory);
        result.setNotificationStatus(currentStatus);
        return result;
    }

    private List<NotificationStatusHistoryElementInt> getStatusHistory(List<TimelineElementInternal> timelineElements, int numberOfRecipients, Instant createdAt, CommunicationType communicationType) {
        List<NotificationStatusHistoryElementInt> statusHistory = statusHistoryService.getStatusHistory(new HashSet<>(timelineElements), numberOfRecipients, createdAt, communicationType);
        removeNotToBeReturnedElements(statusHistory);
        return statusHistory;
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

    private List<TimelineElementInternal> remapAndSortTimelineElements(List<TimelineElementInternal> timelineElementInternals, CommunicationType communicationType) {
        TimelineTimestampMapper timelineTimestampMapper = timelineOperationsResolver.resolve(communicationType).timelineTimestampMapper();
        return timelineElementInternals.stream()
                .map(timelineElement -> new TimelineTimestampMapper.TimestampMapperPayload(timelineElement, new HashSet<>(timelineElementInternals)))
                .map(timelineTimestampMapper::mapTimelineTimestamps)
                .sorted(Comparator.naturalOrder())
                .toList();
    }

    @Override
    public Mono<DeliveryInformationResponse> getDeliveryInformation(String iun, Integer recIndex) {
        log.debug("getDeliveryInformation Start - iun={} recIndex={} ", iun, recIndex);
        return getTimeline(iun, null, false, false)
                .collectList()
                .doOnNext(this::checkTimelineForCurrentIun)
                .map(timelineElements -> new TimelineDataExtractionEngine.EngineBuilder()
                        .executeAndMap(timelineElements, new DeliveryInfoMapper(recIndex)));
    }

    @Override
    public Mono<RequestRefusedResponse> getRequestRefused(String iun) {
        return this.timelineDao.getTimelineFilteredByElementId(iun, ElementIdPrefix.REQUEST_REFUSED.getValue(), false)
            .filter(element -> TimelineElementCategoryInt.REQUEST_REFUSED.equals(element.getCategory()))
            .next()
            .map(element -> SmartMapper.mapToClass(element.getDetails(), RequestRefusedResponse.class))
            .switchIfEmpty(Mono.error(new PnNotFoundException(
                "Request refused not found",
                "No REQUEST_REFUSED element found for the given IUN",
                ERROR_CODE_TIMELINESERVICE_TIMELINE_ELEMENT_NOT_PRESENT
            )));
    }
  
    @Override
    public Mono<CancellationRequestResponse> getCancellationRequest(String iun) {
        return this.timelineDao.getTimelineFilteredByElementId(iun, ElementIdPrefix.NOTIFICATION_CANCELLATION_REQUEST.getValue(), false)
                .filter(element -> element.getCategory() == TimelineElementCategoryInt.NOTIFICATION_CANCELLATION_REQUEST)
                .next()
                .map(element -> new CancellationRequestResponse().timestamp(element.getTimestamp()))
                .switchIfEmpty(Mono.error(new PnNotFoundException(
                        "Cancellation request not found",
                        "No cancellation request element found for the given IUN",
                        ERROR_CODE_TIMELINESERVICE_TIMELINE_ELEMENT_NOT_PRESENT
                  )));
    }
  
    @Override
    public Mono<AarResponse> getAarForRecipient(String iun, Integer recIndex) {
        return getTimelineElementForSpecificRecipient(iun, recIndex, TimelineElementCategoryInt.AAR_GENERATION)
                .map(timelineElement -> {
                    AarGenerationDetailsInt aarDetails = (AarGenerationDetailsInt) timelineElement.getDetails();
                    AarResponse aarData = new AarResponse();
                    aarData.setUrl(aarDetails.getGeneratedAarUrl());
                    aarData.setNumberOfPages(aarDetails.getNumberOfPages());
                    return aarData;
                }).switchIfEmpty(Mono.error(new PnNotFoundException(
                        "AAR not found",
                        "No AAR element found for the given IUN and recipient index",
                        ERROR_CODE_TIMELINESERVICE_TIMELINE_ELEMENT_NOT_PRESENT
                )));
    }

    private void checkTimelineForCurrentIun(List<TimelineElementInternal> timelineList) {
        if (timelineList.isEmpty()) {
            throw new PnNotFoundException("IUN not found", "No timeline elements found for the given IUN", ERROR_CODE_TIMELINESERVICE_TIMELINE_NOT_PRESENT_FOR_CURRENT_IUN);
        }
    }
}
