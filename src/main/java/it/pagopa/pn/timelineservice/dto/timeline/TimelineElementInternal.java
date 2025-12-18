package it.pagopa.pn.timelineservice.dto.timeline;

import it.pagopa.pn.timelineservice.dto.timeline.details.TimelineElementCategoryInt;
import it.pagopa.pn.timelineservice.dto.timeline.details.TimelineElementDetailsInt;
import it.pagopa.pn.timelineservice.dto.legalfacts.LegalFactsIdInt;
import lombok.*;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@Builder(toBuilder = true)
@EqualsAndHashCode
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class TimelineElementInternal implements Comparable<TimelineElementInternal> {
    private String iun;
    private String elementId;
    private Instant timestamp;
    private String paId;
    private List<LegalFactsIdInt> legalFactsIds;
    private TimelineElementCategoryInt category;
    private TimelineElementDetailsInt details;
    private StatusInfoInternal statusInfo;
    private String reworkId;
    private Instant notificationSentAt;
    private Instant ingestionTimestamp; //Questo campo viene valorizzato solo ed esclusivamente in uscita per api e webhook dal mapper
    private Instant eventTimestamp; //Questo campo viene valorizzato solo ed esclusivamente in uscita per api e webhook dal mapper
    private Map<String,TimelineElementInternal> invalidatedTimelineElements; //questo campo viene usato solo internamente per tenere traccia degli elementi invalidati in caso di rework

    @Override
    public int compareTo(@NotNull TimelineElementInternal o) {
        int order = this.timestamp.compareTo(o.getTimestamp());
        if (order == 0)
            order = this.category.getPriority() - o.getCategory().getPriority();
        if(order == 0)
            order = this.elementId.compareTo(o.getElementId());
        return order;
    }
}
