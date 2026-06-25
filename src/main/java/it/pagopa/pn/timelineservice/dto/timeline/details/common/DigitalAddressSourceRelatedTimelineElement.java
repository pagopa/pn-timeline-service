package it.pagopa.pn.timelineservice.dto.timeline.details.common;

import it.pagopa.pn.timelineservice.dto.address.DigitalAddressSourceInt;

public interface DigitalAddressSourceRelatedTimelineElement extends RecipientRelatedTimelineElementDetails {
    DigitalAddressSourceInt getDigitalAddressSource();
    void setDigitalAddressSource(DigitalAddressSourceInt digitalAddressInt);
}
