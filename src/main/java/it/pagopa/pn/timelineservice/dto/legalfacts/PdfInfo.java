package it.pagopa.pn.timelineservice.dto.legalfacts;

import it.pagopa.pn.timelineservice.legalfacts.AarTemplateType;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class PdfInfo {
    private String key;
    private int numberOfPages;
    private AarTemplateType aarTemplateType;
}
