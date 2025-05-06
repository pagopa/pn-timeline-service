package it.pagopa.pn.timelineservice.dto.ext.datavault;

import it.pagopa.pn.timelineservice.dto.address.DigitalAddressInt;
import it.pagopa.pn.timelineservice.dto.address.PhysicalAddressInt;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
@EqualsAndHashCode
@ToString
public class NotificationRecipientAddressesDtoInt {
    private String denomination;
    private DigitalAddressInt digitalAddress;
    private PhysicalAddressInt physicalAddress;
}
