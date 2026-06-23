package it.pagopa.pn.timelineservice.dto.timeline.details.legal;

import it.pagopa.pn.timelineservice.dto.timeline.details.common.CategoryTypeTimelineElementDetailsInt;
import it.pagopa.pn.timelineservice.dto.timeline.details.common.RecipientRelatedTimelineElementDetails;
import lombok.*;
import lombok.experimental.SuperBuilder;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@SuperBuilder(toBuilder = true)
@EqualsAndHashCode(callSuper = true)
@ToString
public class AarGenerationDetailsInt extends CategoryTypeTimelineElementDetailsInt implements RecipientRelatedTimelineElementDetails{
  private int recIndex;
  private String generatedAarUrl;
  private Integer numberOfPages; //Nota il campo potrÃ  essere eliminato in futuro dal momento che il numero di pagine viene calcolato da paperChannel

  public String toLog() {
    return String.format(
            "recIndex=%d",
            recIndex
    );
  }
  
}