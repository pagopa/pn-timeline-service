package it.pagopa.pn.timelineservice.operations.legal;

import it.pagopa.pn.commons.exceptions.PnInternalException;
import it.pagopa.pn.commons.log.PnAuditLogBuilder;
import it.pagopa.pn.commons.log.PnAuditLogEvent;
import it.pagopa.pn.commons.log.PnAuditLogEventType;
import it.pagopa.pn.timelineservice.config.PnTimelineServiceConfigs;
import it.pagopa.pn.timelineservice.dto.notification.NotificationInfoInt;
import it.pagopa.pn.timelineservice.dto.timeline.ReworkFilteringResult;
import it.pagopa.pn.timelineservice.dto.timeline.TimelineElementInternal;
import it.pagopa.pn.timelineservice.dto.timeline.TimelineEventIdParser;
import it.pagopa.pn.timelineservice.operations.common.TimelineElementPersistenceStrategy;
import it.pagopa.pn.timelineservice.service.mapper.SmartMapper;
import it.pagopa.pn.timelineservice.utils.CompletedDeliveryWorkflowCategory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.time.Instant;
import java.util.*;

import static it.pagopa.pn.timelineservice.dto.timeline.details.TimelineElementCategoryInt.NOTIFICATION_TIMELINE_REWORKED;
import static it.pagopa.pn.timelineservice.exceptions.PnTimelineServiceExceptionCodes.ERROR_CODE_TIMELINESERVICE_ADDTIMELINEFAILED;
import static it.pagopa.pn.timelineservice.utils.NotificationReworkUtils.checkReworkAttemptAndReturnSuffix;

@Component
@RequiredArgsConstructor
@Slf4j
public class LegalTimelineElementPersistenceStrategy implements TimelineElementPersistenceStrategy {
    private final SmartMapper smartMapper;
    private final PnTimelineServiceConfigs pnTimelineServiceConfigs;

    @Override
    public PnAuditLogEvent buildAuditLogEvent(TimelineElementInternal dto, PnAuditLogBuilder builder) {
        String auditLog = String.format("Timeline event inserted with: CATEGORY=%s IUN=%s {DETAILS: %s} TIMELINEID=%s paId=%s TIMESTAMP=%s communicationType=%s",
                dto.getCategory(),
                dto.getIun(),
                dto.getDetails() != null ? dto.getDetails().toLog() : null,
                dto.getElementId(),
                dto.getPaId(),
                dto.getTimestamp(),
                dto.getCommunicationType()
                );
        return builder
                .before(PnAuditLogEventType.AUD_NT_TIMELINE, auditLog)
                .iun(dto.getIun())
                .build();
    }

    @Override
    public TimelineElementInternal enrichWithRework(TimelineElementInternal dto, Set<TimelineElementInternal> currentTimeline) {
        List<TimelineElementInternal> sortedTimeline = new ArrayList<>(currentTimeline);

        //Ordino la lista in base al timestamp e poi la inverto per avere al primo posto l'evento con requestTimestamp più recente
        sortedTimeline.sort(Comparator.comparing(TimelineElementInternal::getTimestamp).reversed());

        if (pnTimelineServiceConfigs.getInvalidableCategories().contains(dto.getCategory().name())) {
            List<TimelineElementInternal> reworkTimelineElements = getReworkElementsFromTimeline(sortedTimeline, dto);
            TimelineEventIdParser parser = TimelineEventIdParser.parse(dto.getElementId());
            if(CollectionUtils.isEmpty(reworkTimelineElements) || parser.reworkIndexFull().isPresent()){
                return dto;
            }
            ReworkFilteringResult reworkFilteringResult = checkReworkAttemptAndReturnSuffix(reworkTimelineElements, dto.getElementId());
            dto.setElementId(reworkFilteringResult.getTimelineElementId());
            dto.setReworkId(reworkFilteringResult.getReworkId());
            log.info("enriched timeline element with rework info from {} for elementId={}", reworkFilteringResult.getReworkId(), dto.getElementId());
        }
        return dto;
    }

    private List<TimelineElementInternal> getReworkElementsFromTimeline(List<TimelineElementInternal> currentTimeline, TimelineElementInternal dto) {
        Optional<Integer> dtoRecIndex = TimelineEventIdParser.parse(dto.getElementId()).recIndex();
        if(dtoRecIndex.isEmpty()) {
            log.error("No recIndex found in timeline element with elementId: {}", dto.getElementId());
            throw new PnInternalException("No recIndex in element with elementId: " + dto.getElementId(), ERROR_CODE_TIMELINESERVICE_ADDTIMELINEFAILED);
        }
        return currentTimeline.stream()
                .filter(elem -> NOTIFICATION_TIMELINE_REWORKED.equals(elem.getCategory()))
                .filter(timelineElementInternal -> dtoRecIndex.get().equals(TimelineEventIdParser.parse(timelineElementInternal.getElementId()).recIndex()
                        .orElse(null)))
                .toList();
    }

    @Override
    public TimelineElementInternal applyBusinessTimestamp(TimelineElementInternal dto, Set<TimelineElementInternal> currentTimeline) {
        Instant cachedTimestamp = dto.getTimestamp();
        // calcolo e aggiungo il businessTimestamp
        dto = smartMapper.mapTimelineInternal(dto, currentTimeline);
        dto.setTimestamp(cachedTimestamp);
        return dto;
    }

    @Override
    public boolean requiresCriticalPath(TimelineElementInternal dto, NotificationInfoInt notification) {
        return notification.getNumberOfRecipients() > 1
                && CompletedDeliveryWorkflowCategory.isCompletedWorkflowCategory(dto.getCategory());
    }
}
