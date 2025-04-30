package it.pagopa.pn.timelineservice.dto.details;

import it.pagopa.pn.timelineservice.dto.address.DigitalAddressSourceInt;

public interface DigitalAddressSourceRelatedTimelineElement extends RecipientRelatedTimelineElementDetails {
    DigitalAddressSourceInt getDigitalAddressSource();
    void setDigitalAddressSource(DigitalAddressSourceInt digitalAddressInt);
}
