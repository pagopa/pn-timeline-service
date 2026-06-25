package it.pagopa.pn.timelineservice.middleware.dao.dynamo.entity;

import lombok.*;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;

import java.time.Instant;

@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@DynamoDbBean
public class DeliveryDetailsEntity {
    @Getter(onMethod = @__({@DynamoDbAttribute("failureCause")}))
    private String failureCause;
    @Getter(onMethod = @__({@DynamoDbAttribute("eventTimestamp")}))
    private Instant eventTimestamp;
    @Getter(onMethod = @__({@DynamoDbAttribute("code")}))
    private String code;
}
