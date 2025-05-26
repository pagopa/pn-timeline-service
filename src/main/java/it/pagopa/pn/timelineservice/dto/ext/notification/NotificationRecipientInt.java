package it.pagopa.pn.timelineservice.dto.ext.notification;



import com.fasterxml.jackson.annotation.JsonInclude;
import it.pagopa.pn.timelineservice.dto.address.LegalDigitalAddressInt;
import it.pagopa.pn.timelineservice.dto.address.PhysicalAddressInt;
import it.pagopa.pn.timelineservice.dto.ext.datavault.RecipientTypeInt;
import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder(toBuilder = true)
@EqualsAndHashCode
@ToString
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class NotificationRecipientInt {
    private String taxId;
    private String internalId;
    private String denomination;
    private LegalDigitalAddressInt digitalDomicile;
    private PhysicalAddressInt physicalAddress;
    private List<NotificationPaymentInfoInt> payments;
    private RecipientTypeInt recipientType;
}
