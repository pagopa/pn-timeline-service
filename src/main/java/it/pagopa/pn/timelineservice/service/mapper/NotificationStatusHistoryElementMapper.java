package it.pagopa.pn.timelineservice.service.mapper;

import it.pagopa.pn.timelineservice.dto.ext.notification.status.NotificationStatusHistoryElementInt;
import it.pagopa.pn.timelineservice.dto.notification.NotificationStatus;
import it.pagopa.pn.timelineservice.dto.notification.NotificationStatusHistoryElement;

public class NotificationStatusHistoryElementMapper {
    private NotificationStatusHistoryElementMapper(){}
    
    public static NotificationStatusHistoryElement internalToExternal(NotificationStatusHistoryElementInt dtoInt){
        return NotificationStatusHistoryElement.builder()
                .activeFrom(dtoInt.getActiveFrom())
                .relatedTimelineElements(dtoInt.getRelatedTimelineElements())
                .status(dtoInt.getStatus() != null ? NotificationStatus.valueOf(dtoInt.getStatus().getValue()) : null )
                .build();
    }
}
