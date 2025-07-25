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
@ToString
public class AttachmentDetailsEntity {
    @Getter(onMethod=@__({@DynamoDbAttribute("id")}))  private String id;
    @Getter(onMethod=@__({@DynamoDbAttribute("documentType")}))  private String documentType;
    @Getter(onMethod=@__({@DynamoDbAttribute("url")}))  private String url;
    @Getter(onMethod=@__({@DynamoDbAttribute("date")}))  private Instant date;
}
