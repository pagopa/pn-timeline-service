package it.pagopa.pn.timelineservice.dto.legalfacts;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Builder
@Getter
@Setter
@ToString
public class LegalFactsIdInt {
    private String key;
    private LegalFactCategoryInt category;
}
