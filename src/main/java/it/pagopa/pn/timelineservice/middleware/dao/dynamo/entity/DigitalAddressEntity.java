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
public class DigitalAddressEntity {
    @Getter(onMethod=@__({@DynamoDbAttribute("type")}))  private TypeEnum type;
    @Getter(onMethod=@__({@DynamoDbAttribute("address")}))  private String address;

    public enum TypeEnum {
        PEC("PEC"),
        EMAIL("EMAIL"),
        SMS("SMS"),
        APPIO("APPIO"),
        SERCQ("SERCQ"),
        TPP("TPP");

        private final String value;

        TypeEnum(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        @Override
        public String toString() {
            return String.valueOf(value);
        }
    }
}
