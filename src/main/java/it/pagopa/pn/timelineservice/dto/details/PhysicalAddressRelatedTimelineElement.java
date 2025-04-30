package it.pagopa.pn.timelineservice.dto.details;

import it.pagopa.pn.timelineservice.dto.address.PhysicalAddressInt;

public interface PhysicalAddressRelatedTimelineElement extends ConfidentialInformationTimelineElement{
    PhysicalAddressInt getPhysicalAddress();
    void setPhysicalAddress(PhysicalAddressInt physicalAddressInt);
}
