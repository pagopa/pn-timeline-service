package it.pagopa.pn.timelineservice.dto.timeline.details.informal;

import it.pagopa.pn.timelineservice.dto.address.InformalDigitalAddressInt;
import it.pagopa.pn.timelineservice.dto.timeline.details.common.ConfidentialInformationTimelineElement;

public interface InformalDigitalAddressRelatedTimelineElement extends ConfidentialInformationTimelineElement {
    InformalDigitalAddressInt getDigitalAddress();
    void setDigitalAddress(InformalDigitalAddressInt digitalAddressInt);
}
