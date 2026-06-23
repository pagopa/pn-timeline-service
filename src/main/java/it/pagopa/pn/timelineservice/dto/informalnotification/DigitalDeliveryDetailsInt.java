package it.pagopa.pn.timelineservice.dto.informalnotification;

import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@NoArgsConstructor
@SuperBuilder(toBuilder = true)
@EqualsAndHashCode(callSuper = true)
@ToString
public class DigitalDeliveryDetailsInt extends DeliveryDetailsInt {
}
