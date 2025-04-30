package it.pagopa.pn.timelineservice.service.mapper;


import it.pagopa.pn.timelineservice.dto.ext.notification.NotificationStatusHistoryElementV26;
import it.pagopa.pn.timelineservice.dto.ext.notification.NotificationStatusV26;
import it.pagopa.pn.timelineservice.dto.ext.notification.status.NotificationStatusHistoryElementInt;

public class NotificationStatusHistoryElementMapper {
    private NotificationStatusHistoryElementMapper(){}
    
    public static NotificationStatusHistoryElementV26 internalToExternal(NotificationStatusHistoryElementInt dtoInt){
        return NotificationStatusHistoryElementV26.builder()
                .activeFrom(dtoInt.getActiveFrom())
                .relatedTimelineElements(dtoInt.getRelatedTimelineElements())
                .status(dtoInt.getStatus() != null ? NotificationStatusV26.valueOf(dtoInt.getStatus().getValue()) : null )
                .build();
    }
}
