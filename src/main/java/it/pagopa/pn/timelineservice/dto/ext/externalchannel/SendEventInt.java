package it.pagopa.pn.timelineservice.dto.ext.externalchannel;

import it.pagopa.pn.timelineservice.dto.address.PhysicalAddressInt;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@SuperBuilder(toBuilder = true)
@EqualsAndHashCode(callSuper = true)
@ToString
public class SendEventInt extends PaperEventInt {

    private String statusDescription;
    private List<AttachmentDetailsInt> attachments = null;
    private PhysicalAddressInt discoveredAddress;
    private String deliveryFailureCause;

    private String registeredLetterCode;

}
