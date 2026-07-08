package it.pagopa.pn.timelineservice.dto.timeline.details.legal;

import it.pagopa.pn.timelineservice.dto.address.LegalDigitalAddressInt;
import it.pagopa.pn.timelineservice.dto.timeline.details.common.ConfidentialInformationTimelineElement;

public interface DigitalAddressRelatedTimelineElement extends ConfidentialInformationTimelineElement {
    LegalDigitalAddressInt getDigitalAddress();
    void setDigitalAddress(LegalDigitalAddressInt digitalAddressInt);
}
