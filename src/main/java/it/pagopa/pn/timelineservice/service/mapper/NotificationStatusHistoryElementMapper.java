package it.pagopa.pn.timelineservice.service.mapper;

import it.pagopa.pn.timelineservice.dto.notification.status.NotificationStatusHistoryElementInt;

public class NotificationStatusHistoryElementMapper {
    private NotificationStatusHistoryElementMapper(){}
    
    public static NotificationStatusHistoryElementInt internalToExternal(NotificationStatusHistoryElementInt dtoInt){
        NotificationStatusHistoryElementInt notificationStatusHistoryElement = new NotificationStatusHistoryElementInt();
        notificationStatusHistoryElement.setStatus(dtoInt.getStatus());
        notificationStatusHistoryElement.setActiveFrom(dtoInt.getActiveFrom());
        notificationStatusHistoryElement.setRelatedTimelineElements(dtoInt.getRelatedTimelineElements());
        return notificationStatusHistoryElement;
    }
}
