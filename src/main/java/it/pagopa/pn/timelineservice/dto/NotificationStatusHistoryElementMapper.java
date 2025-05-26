package it.pagopa.pn.timelineservice.dto;

import it.pagopa.pn.timelineservice.dto.ext.notification.status.NotificationStatusHistoryElementInt;

public class NotificationStatusHistoryElementMapper {
    private NotificationStatusHistoryElementMapper(){}
    
    public static NotificationStatusHistoryElement internalToExternal(NotificationStatusHistoryElementInt dtoInt){
        return NotificationStatusHistoryElement.builder()
                .activeFrom(dtoInt.getActiveFrom())
                .relatedTimelineElements(dtoInt.getRelatedTimelineElements())
                .status(dtoInt.getStatus() != null ? NotificationStatusV26.valueOf(dtoInt.getStatus().getValue()) : null )
                .build();
    }
}
