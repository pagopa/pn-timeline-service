package it.pagopa.pn.timelineservice.dto.timeline.details;

import it.pagopa.pn.timelineservice.dto.timeline.details.common.CategoryTypeTimelineElementDetailsInt;
import it.pagopa.pn.timelineservice.dto.timeline.details.common.TimelineElementDetailsInt;
import it.pagopa.pn.timelineservice.dto.timeline.details.legal.DeliveryModeInt;
import lombok.*;
import lombok.experimental.SuperBuilder;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@SuperBuilder(toBuilder = true)
@EqualsAndHashCode(callSuper = true)
@ToString
public class CourtesyChannelFailedDetailsInt extends CategoryTypeTimelineElementDetailsInt implements TimelineElementDetailsInt {

    private String channelType;
    private DeliveryModeInt deliveryMode;
    private CourtesyChannelFailureReasonInt failureReason;

    @Override
    public String toLog() {
        return String.format(
                "channelType=%s deliveryMode=%s failureReason=%s",
                channelType,
                deliveryMode,
                failureReason
        );
    }
}
