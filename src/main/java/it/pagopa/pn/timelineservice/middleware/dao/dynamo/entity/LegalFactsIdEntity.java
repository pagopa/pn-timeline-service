package it.pagopa.pn.timelineservice.middleware.dao.dynamo.entity;

import lombok.*;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
@DynamoDbBean
public class LegalFactsIdEntity {
    @Getter(onMethod=@__({@DynamoDbAttribute("key")})) private String key;
    @Getter(onMethod=@__({@DynamoDbAttribute("category")})) private LegalFactCategoryEntity category;
}
