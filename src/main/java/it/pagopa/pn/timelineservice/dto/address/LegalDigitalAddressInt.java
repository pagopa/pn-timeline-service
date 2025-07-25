package it.pagopa.pn.timelineservice.dto.address;

import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@NoArgsConstructor
@SuperBuilder(toBuilder = true)
@EqualsAndHashCode(callSuper = true)
@ToString
public class LegalDigitalAddressInt extends DigitalAddressInt{
    
    public enum LEGAL_DIGITAL_ADDRESS_TYPE{
        PEC("PEC"),
        SERCQ("SERCQ");

        private final String value;

        LEGAL_DIGITAL_ADDRESS_TYPE(String value) {
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

    private LEGAL_DIGITAL_ADDRESS_TYPE type;
}
