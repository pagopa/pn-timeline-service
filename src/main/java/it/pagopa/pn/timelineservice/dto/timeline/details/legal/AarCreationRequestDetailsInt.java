package it.pagopa.pn.timelineservice.dto.timeline.details.legal;

import it.pagopa.pn.timelineservice.dto.timeline.details.common.CategoryTypeTimelineElementDetailsInt;
import it.pagopa.pn.timelineservice.dto.timeline.details.common.RecipientRelatedTimelineElementDetails;
import it.pagopa.pn.timelineservice.legalfacts.AarTemplateType;
import lombok.*;
import lombok.experimental.SuperBuilder;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@SuperBuilder(toBuilder = true)
@EqualsAndHashCode(callSuper = true)
@ToString
public class AarCreationRequestDetailsInt extends CategoryTypeTimelineElementDetailsInt implements RecipientRelatedTimelineElementDetails{
    private int recIndex;
    private String aarKey;
    private Integer numberOfPages; //Nota il campo potrÃ  essere eliminato in futuro dal momento che il numero di pagine viene calcolato da paperChannel
    private AarTemplateType aarTemplateType;
    
    public String toLog() {
        return String.format(
                "recIndex=%d aarKey=%s numberOfPages=%s aarTemplateType=%s",
                recIndex,
                aarKey,
                numberOfPages,
                aarTemplateType
        );
    }
}
