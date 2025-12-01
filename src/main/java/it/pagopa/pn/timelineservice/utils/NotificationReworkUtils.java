package it.pagopa.pn.timelineservice.utils;

import it.pagopa.pn.timelineservice.dto.timeline.TimelineElementInternal;
import it.pagopa.pn.timelineservice.dto.timeline.details.NotificationTimelineReworkedDetailsInt;
import it.pagopa.pn.timelineservice.dto.timeline.details.TimelineElementCategoryInt;
import it.pagopa.pn.timelineservice.middleware.dao.dynamo.entity.TimelineElementEntity;

import java.util.List;

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

    public static List<TimelineElementEntity> removeInvalidatedElement(List<TimelineElementEntity> timelineElementEntities, List<TimelineElementEntity> timelineByTimestampSorted) {
        List<String> invalidatedTimelineElements = timelineElementEntities.stream()
                .flatMap(e -> e.getDetails()
                        .getInvalidatedTimelineAndStatusHistory().stream())
                .flatMap(timelineElem -> timelineElem.getRelatedTimelineElements().stream())
                .toList();

        return timelineByTimestampSorted.stream()
                .filter(elem -> !invalidatedTimelineElements.contains(elem.getTimelineElementId()))
                .toList();
    }

}
