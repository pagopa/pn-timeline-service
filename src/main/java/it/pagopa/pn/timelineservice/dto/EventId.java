package it.pagopa.pn.timelineservice.dto;

import it.pagopa.pn.timelineservice.dto.address.CourtesyDigitalAddressInt;
import it.pagopa.pn.timelineservice.dto.address.DigitalAddressSourceInt;
import it.pagopa.pn.timelineservice.dto.timeline.details.ContactPhaseInt;
import it.pagopa.pn.timelineservice.dto.timeline.details.DeliveryModeInt;
import it.pagopa.pn.timelineservice.dto.documentcreation.DocumentCreationTypeInt;
import lombok.*;


@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder(toBuilder = true)
@EqualsAndHashCode
@ToString
public class EventId {
    private String iun;
    private Integer recIndex;
    private DigitalAddressSourceInt source;
    private ContactPhaseInt contactPhase;
    private Integer sentAttemptMade;
    private DeliveryModeInt deliveryMode;
    private Integer progressIndex;
    private DocumentCreationTypeInt documentCreationType;
    private CourtesyDigitalAddressInt.COURTESY_DIGITAL_ADDRESS_TYPE_INT courtesyAddressType;
    private String creditorTaxId;
    private String noticeCode;
    private Boolean isFirstSendRetry;
    private String relatedTimelineId;
    private Boolean optin;
}
