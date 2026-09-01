package it.pagopa.pn.timelineservice.dto.timeline.details.legal;

import it.pagopa.pn.timelineservice.dto.address.DigitalAddressInt;
import it.pagopa.pn.timelineservice.dto.address.DigitalAddressSourceInt;
import it.pagopa.pn.timelineservice.dto.address.LegalDigitalAddressInt;
import it.pagopa.pn.timelineservice.dto.informalnotification.DigitalChannelsInt;
import it.pagopa.pn.timelineservice.dto.timeline.details.common.CategoryTypeTimelineElementDetailsInt;
import it.pagopa.pn.timelineservice.dto.timeline.details.common.DigitalAddressSourceRelatedTimelineElement;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.Instant;


@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@SuperBuilder(toBuilder = true)
@EqualsAndHashCode(callSuper = true)
@ToString
public class GetAddressInfoDetailsInt extends CategoryTypeTimelineElementDetailsInt implements DigitalAddressSourceRelatedTimelineElement, DigitalAddressRelatedTimelineElement {
    private int recIndex;
    private DigitalAddressSourceInt digitalAddressSource;
    private Boolean isAvailable;
    private Instant attemptDate;
    private LegalDigitalAddressInt digitalAddress;
    private Boolean isTosAccepted;
    private DigitalChannelsInt channel;

    public String toLog() {
        return String.format(
                "recIndex=%d digitalAddressSource=%s isAvailable=%s isTosAccepted=%s channel=%s",
                recIndex,
                digitalAddressSource,
                isAvailable,
                isTosAccepted,
                channel
        );
    }
}
