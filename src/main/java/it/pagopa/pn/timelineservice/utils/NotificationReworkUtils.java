package it.pagopa.pn.timelineservice.utils;

import it.pagopa.pn.commons.exceptions.PnInternalException;
import it.pagopa.pn.timelineservice.dto.timeline.ReworkFilteringResult;
import it.pagopa.pn.timelineservice.dto.timeline.TimelineElementInternal;
import it.pagopa.pn.timelineservice.dto.timeline.TimelineEventIdParser;
import it.pagopa.pn.timelineservice.dto.timeline.details.NotificationTimelineReworkedDetailsInt;
import it.pagopa.pn.timelineservice.dto.timeline.details.TimelineElementCategoryInt;
import it.pagopa.pn.timelineservice.middleware.dao.dynamo.entity.TimelineElementEntity;

import java.util.List;
import java.util.Objects;

import static it.pagopa.pn.timelineservice.dto.timeline.details.TimelineElementCategoryInt.PREPARE_ANALOG_DOMICILE;
import static it.pagopa.pn.timelineservice.dto.timeline.details.TimelineElementCategoryInt.SEND_ANALOG_DOMICILE;
import static it.pagopa.pn.timelineservice.exceptions.PnTimelineServiceExceptionCodes.ERROR_CODE_TIMELINESERVICE_INVALIDTIMELINEID;

public class NotificationReworkUtils {

    public static List<TimelineElementInternal> getNotInvalidatedTimelineElements(List<TimelineElementInternal> timelineByTimestampSorted) {
        List<String> invalidatedTimelineElements = timelineByTimestampSorted.stream()
                .filter(e -> e.getCategory().equals(TimelineElementCategoryInt.NOTIFICATION_TIMELINE_REWORKED))
                .flatMap(e -> ((NotificationTimelineReworkedDetailsInt) e.getDetails())
                        .getInvalidatedTimelineAndStatusHistory().stream())
                .flatMap(timelineElem -> timelineElem.getRelatedTimelineElements().stream())
                .toList();

        return timelineByTimestampSorted.stream()
                .filter(elem -> !invalidatedTimelineElements.contains(elem.getElementId()))
                .toList();
    }

    public static List<TimelineElementEntity> removeInvalidatedElement(List<TimelineElementInternal> reworkElementsInternal, List<TimelineElementEntity> timelineByTimestampSorted) {
        List<String> invalidatedTimelineElements = reworkElementsInternal.stream()
                .map(timelineElementInternal -> (NotificationTimelineReworkedDetailsInt) timelineElementInternal.getDetails())
                .flatMap(e -> e.getInvalidatedTimelineAndStatusHistory().stream())
                .flatMap(timelineElem -> timelineElem.getRelatedTimelineElements().stream())
                .toList();

        return timelineByTimestampSorted.stream()
                .filter(elem -> !invalidatedTimelineElements.contains(elem.getTimelineElementId()))
                .toList();
    }

    public static ReworkFilteringResult checkReworkAttemptAndReturnSuffix(List<TimelineElementInternal> reworkTimelineElements, String timelineId) {
        TimelineEventIdParser newElementId = TimelineEventIdParser.parse(timelineId);
        Integer newElementAttempt = newElementId.sentAttemptMade().orElse(null);
        
        List<String> invalidatedTimelineElements = reworkTimelineElements.stream()
                .map(timelineElementInternal -> (NotificationTimelineReworkedDetailsInt) timelineElementInternal.getDetails())
                .flatMap(e -> e.getInvalidatedTimelineAndStatusHistory().stream())
                .flatMap(timelineElem -> timelineElem.getRelatedTimelineElements().stream())
                .toList();
        
        for(TimelineElementInternal reworkItem : reworkTimelineElements) {
            TimelineEventIdParser parser = TimelineEventIdParser.parse(reworkItem.getElementId());
            String reworkSuffix = parser.reworkIndexFull().orElse(null);
            NotificationTimelineReworkedDetailsInt notificationTimelineReworkedDetailsInt = (NotificationTimelineReworkedDetailsInt) reworkItem.getDetails();
            if(validAttempt(newElementAttempt, notificationTimelineReworkedDetailsInt.getSentAttemptMade()) && !isPrepareOrSendAttempt0(newElementId) && isInvalidatedByRework(timelineId, invalidatedTimelineElements)){
                return new ReworkFilteringResult(timelineId + "." + reworkSuffix, reworkItem.getReworkId());
            }
        }
        return new ReworkFilteringResult(timelineId, null);
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
        return (PREPARE_ANALOG_DOMICILE.name().equalsIgnoreCase(newElementId.category().orElse(null)) ||
                SEND_ANALOG_DOMICILE.name().equalsIgnoreCase(newElementId.category().orElse(null))) &&
                (newElementId.sentAttemptMade().isPresent() && newElementId.sentAttemptMade().get() == 0);
    }

    private static boolean validAttempt(Integer newElementAttempt, Integer sentAttemptMade) {
        return Objects.isNull(newElementAttempt) || newElementAttempt >= sentAttemptMade;
    }

}
