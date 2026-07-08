package it.pagopa.pn.timelineservice.dto.timeline.details.legal;

import it.pagopa.pn.timelineservice.dto.timeline.details.common.ConfidentialInformationTimelineElement;

public interface PersonalInformationRelatedTimelineElement extends ConfidentialInformationTimelineElement {
    String getTaxId();
    void setTaxId(String taxId);

    String getDenomination();
    void setDenomination(String denomination);
}
