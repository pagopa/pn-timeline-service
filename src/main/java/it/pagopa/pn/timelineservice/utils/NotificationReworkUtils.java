package it.pagopa.pn.timelineservice.utils;

import it.pagopa.pn.commons.exceptions.PnInternalException;
import it.pagopa.pn.timelineservice.dto.timeline.ReworkFilteringResult;
import it.pagopa.pn.timelineservice.dto.timeline.TimelineElementInternal;
import it.pagopa.pn.timelineservice.dto.timeline.TimelineEventIdParser;
import it.pagopa.pn.timelineservice.dto.timeline.details.NotificationTimelineReworkedDetailsInt;
import it.pagopa.pn.timelineservice.middleware.dao.dynamo.entity.TimelineElementCategoryEntity;
import it.pagopa.pn.timelineservice.middleware.dao.dynamo.entity.TimelineElementEntity;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import static it.pagopa.pn.timelineservice.dto.timeline.details.TimelineElementCategoryInt.PREPARE_ANALOG_DOMICILE;
import static it.pagopa.pn.timelineservice.dto.timeline.details.TimelineElementCategoryInt.SEND_ANALOG_DOMICILE;
import static it.pagopa.pn.timelineservice.exceptions.PnTimelineServiceExceptionCodes.ERROR_CODE_TIMELINESERVICE_INVALIDTIMELINEID;

public class NotificationReworkUtils {

    public static List<String> getInvalidatedTimelineElementsIds(List<TimelineElementEntity> entities) {
        return entities.stream()
                .filter(e -> e.getCategory().equals(TimelineElementCategoryEntity.NOTIFICATION_TIMELINE_REWORKED))
                .flatMap(e -> e.getDetails()
                        .getInvalidatedTimelineAndStatusHistory().stream())
                .flatMap(timelineElem -> timelineElem.getRelatedTimelineElementIds().stream())
                .toList();
    }

    public static List<TimelineElementEntity> removeInvalidatedElement(List<TimelineElementInternal> reworkElementsInternal, List<TimelineElementEntity> timelineByTimestampSorted) {
        List<String> invalidatedIds = extractInvalidatedIds(reworkElementsInternal);
        return timelineByTimestampSorted.stream()
                .filter(e -> !invalidatedIds.contains(e.getTimelineElementId()))
                .toList();
    }

    public static ReworkFilteringResult checkReworkAttemptAndReturnSuffix(List<TimelineElementInternal> reworkElements, String timelineId) {

        TimelineEventIdParser newParser = TimelineEventIdParser.parse(timelineId);
        Integer newAttempt = newParser.sentAttemptMade().orElse(null);

        if (isPrepareOrSendAttempt0(newParser)) {
            return new ReworkFilteringResult(timelineId, null);
        }

        List<String> invalidatedIds = extractInvalidatedIds(reworkElements);

        if (!isInvalidatedByRework(timelineId, invalidatedIds)) {
            return new ReworkFilteringResult(timelineId, null);
        }

        return reworkElements.stream()
                .filter(r -> validAttempt(newAttempt, ((NotificationTimelineReworkedDetailsInt) r.getDetails()).getSentAttemptMade()))
                .findFirst()
                .map(r -> new ReworkFilteringResult(
                        timelineId + "." + TimelineEventIdParser.parse(r.getElementId())
                                .reworkIndexFull()
                                .orElse(null),
                        r.getReworkId()))
                .orElseGet(() -> new ReworkFilteringResult(timelineId, null));
    }

    private static List<String> extractInvalidatedIds(List<TimelineElementInternal> reworkElements) {
        return reworkElements.stream()
                .map(e -> (NotificationTimelineReworkedDetailsInt) e.getDetails())
                .flatMap(d -> d.getInvalidatedTimelineAndStatusHistory().stream())
                .flatMap(h -> h.getRelatedTimelineElementIds().stream())
                .toList();
    }

    private static boolean isInvalidatedByRework(String timelineId, List<String> invalidatedTimelineElements) {
        TimelineEventIdParser newElementParser = TimelineEventIdParser.parse(timelineId);
        String category = newElementParser.category()
                .orElseThrow(() -> new PnInternalException("Invalid timeline element id: missing category -> " + timelineId, ERROR_CODE_TIMELINESERVICE_INVALIDTIMELINEID));
        Integer sentAttemptMade = newElementParser.sentAttemptMade().orElse(null);

        return invalidatedTimelineElements.stream().anyMatch(timelineElementId -> {
            TimelineEventIdParser invalidatedParser = TimelineEventIdParser.parse(timelineElementId);
            String invalidatedCategory = invalidatedParser.category()
                    .orElseThrow(() -> new PnInternalException("Invalid timeline element id: missing category -> " + timelineId, ERROR_CODE_TIMELINESERVICE_INVALIDTIMELINEID));
            Integer invalidatedSentAttemptMade = invalidatedParser.sentAttemptMade().orElse(null);
            return Objects.equals(category, invalidatedCategory) && (Objects.isNull(sentAttemptMade) || Objects.equals(sentAttemptMade, invalidatedSentAttemptMade));
        });

    }

    private static boolean isPrepareOrSendAttempt0(TimelineEventIdParser newElementId) {
        return isPrepareOrSend(newElementId.category().orElse(null))
                && newElementId.sentAttemptMade().orElse(-1) == 0;
    }

    private static boolean isPrepareOrSend(String category) {
        return PREPARE_ANALOG_DOMICILE.name().equalsIgnoreCase(category)
                || SEND_ANALOG_DOMICILE.name().equalsIgnoreCase(category);
    }

    private static boolean validAttempt(Integer newElementAttempt, Integer sentAttemptMade) {
        return Objects.isNull(newElementAttempt) || newElementAttempt >= sentAttemptMade;
    }

    public static boolean isNotInvalidated(TimelineElementInternal timelineElementInternal, Map<String, TimelineElementInternal> invalidatedTimelineElements) {
        return !invalidatedTimelineElements.containsKey(timelineElementInternal.getElementId());
    }

}
