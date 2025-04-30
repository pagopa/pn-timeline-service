package it.pagopa.pn.timelineservice.dto.ext;

import it.pagopa.pn.timelineservice.dto.address.LegalDigitalAddressInt;
import it.pagopa.pn.timelineservice.dto.address.PhysicalAddressInt;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder(toBuilder = true)
@EqualsAndHashCode
public class NationalRegistriesResponse {
    private String correlationId;
    private LegalDigitalAddressInt digitalAddress;
    private PhysicalAddressInt physicalAddress;
}
