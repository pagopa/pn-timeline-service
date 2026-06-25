package it.pagopa.pn.timelineservice.dto.timeline.details.legal;

import it.pagopa.pn.timelineservice.dto.timeline.details.common.CategoryTypeTimelineElementDetailsInt;
import it.pagopa.pn.timelineservice.dto.timeline.details.common.TimelineElementDetailsInt;
import lombok.*;
import lombok.experimental.SuperBuilder;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@SuperBuilder(toBuilder = true)
@EqualsAndHashCode(callSuper = true)
@ToString
public class SenderAckCreationRequestDetailsInt extends CategoryTypeTimelineElementDetailsInt implements TimelineElementDetailsInt{
    private String legalFactId;

    @Override
    public String toLog() {
        return String.format(
                "legalFactId=%s",
                legalFactId
        );
    }
}
