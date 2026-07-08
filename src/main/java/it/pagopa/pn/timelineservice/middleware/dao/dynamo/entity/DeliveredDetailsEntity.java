package it.pagopa.pn.timelineservice.middleware.dao.dynamo.entity;

import lombok.*;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;

@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@DynamoDbBean
public class DeliveredDetailsEntity {
    @Getter(onMethod = @__({@DynamoDbAttribute("channel")}))
    private String channel;
    @Getter(onMethod = @__({@DynamoDbAttribute("sourceElementId")}))
    private String sourceElementId;
    @Getter(onMethod = @__({@DynamoDbAttribute("recIndex")}))
    private int recIndex;
}
