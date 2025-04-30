package it.pagopa.pn.timelineservice.dto.details;

import it.pagopa.pn.timelineservice.dto.address.CourtesyDigitalAddressInt;

public interface CourtesyAddressRelatedTimelineElement extends ConfidentialInformationTimelineElement{
    CourtesyDigitalAddressInt getDigitalAddress();
    void setDigitalAddress(CourtesyDigitalAddressInt digitalAddressInt);
}
