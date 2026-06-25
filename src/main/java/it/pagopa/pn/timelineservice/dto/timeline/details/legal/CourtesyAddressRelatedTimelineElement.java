package it.pagopa.pn.timelineservice.dto.timeline.details.legal;

import it.pagopa.pn.timelineservice.dto.address.CourtesyDigitalAddressInt;
import it.pagopa.pn.timelineservice.dto.timeline.details.common.ConfidentialInformationTimelineElement;

public interface CourtesyAddressRelatedTimelineElement extends ConfidentialInformationTimelineElement {
    CourtesyDigitalAddressInt getDigitalAddress();
    void setDigitalAddress(CourtesyDigitalAddressInt digitalAddressInt);
}
