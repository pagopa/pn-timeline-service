package it.pagopa.pn.timelineservice.middleware.dao.dynamo.entity;

import it.pagopa.pn.timelineservice.dto.notification.status.NotificationStatusInt;
import lombok.*;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;

import java.time.Instant;
import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Setter
@EqualsAndHashCode
@DynamoDbBean
public class NotificationStatusHistoryElementEntity {
    @Getter(onMethod = @__({@DynamoDbAttribute("status")}))
    private NotificationStatusInt status;
    @Getter(onMethod = @__({@DynamoDbAttribute("activeFrom")}))
    private Instant activeFrom;
    @Getter(onMethod = @__({@DynamoDbAttribute("relatedTimelineElements")}))
    private List<String> relatedTimelineElements;
}
