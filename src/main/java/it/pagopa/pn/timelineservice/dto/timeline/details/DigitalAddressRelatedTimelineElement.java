package it.pagopa.pn.timelineservice.dto.timeline.details;

import it.pagopa.pn.timelineservice.dto.address.LegalDigitalAddressInt;

public interface DigitalAddressRelatedTimelineElement extends ConfidentialInformationTimelineElement{
    LegalDigitalAddressInt getDigitalAddress();
    void setDigitalAddress(LegalDigitalAddressInt digitalAddressInt);
}
