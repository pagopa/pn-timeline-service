package it.pagopa.pn.timelineservice.dto.timeline.details.legal;

import it.pagopa.pn.timelineservice.dto.address.DigitalAddressSourceInt;
import it.pagopa.pn.timelineservice.dto.address.LegalDigitalAddressInt;
import it.pagopa.pn.timelineservice.dto.timeline.details.common.RecipientRelatedTimelineElementDetails;

public interface DigitalSendTimelineElementDetails extends DigitalAddressRelatedTimelineElement, RecipientRelatedTimelineElementDetails {

    int getRecIndex();

    LegalDigitalAddressInt getDigitalAddress();
    void setDigitalAddress(LegalDigitalAddressInt digitalAddressInt);

    DigitalAddressSourceInt getDigitalAddressSource();
    void setDigitalAddressSource(DigitalAddressSourceInt digitalAddressSource);

    Integer getRetryNumber();
    void setRetryNumber(Integer retryNumber);
    
    Boolean getIsFirstSendRetry();

    String getRelatedFeedbackTimelineId();
}
