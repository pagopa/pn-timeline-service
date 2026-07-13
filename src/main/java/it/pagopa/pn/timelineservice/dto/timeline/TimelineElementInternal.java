package it.pagopa.pn.timelineservice.dto.timeline;

import it.pagopa.pn.timelineservice.dto.legalfacts.LegalFactsIdInt;
import it.pagopa.pn.timelineservice.dto.timeline.details.TimelineElementCategoryInt;
import it.pagopa.pn.timelineservice.dto.timeline.details.common.TimelineElementDetailsInt;
import it.pagopa.pn.timelineservice.generated.openapi.server.v1.dto.TimelineElement;
import lombok.*;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.util.List;

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
    private String campaignId;
    private ReworkRequestTypeEnum reworkRequestType;
    private Instant notificationSentAt;
    private Instant ingestionTimestamp; //Questo campo viene valorizzato solo ed esclusivamente in uscita per api e webhook dal mapper
    private Instant eventTimestamp; //Questo campo viene valorizzato solo ed esclusivamente in uscita per api e webhook dal mapper
    private CommunicationType communicationType; //Campo sempre presente nel modello interno; se assente nei mapping da DB viene valorizzato a LEGAL ed è usato come discriminatore per selezionare il corretto bundle di operazioni (LEGAL/INFORMAL)

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
