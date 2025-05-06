package it.pagopa.pn.timelineservice.dto.timeline.details;

import it.pagopa.pn.timelineservice.dto.address.PhysicalAddressInt;

public interface NewAddressRelatedTimelineElement extends ConfidentialInformationTimelineElement{
    PhysicalAddressInt getNewAddress();
    void setNewAddress(PhysicalAddressInt digitalAddressInt);
}
