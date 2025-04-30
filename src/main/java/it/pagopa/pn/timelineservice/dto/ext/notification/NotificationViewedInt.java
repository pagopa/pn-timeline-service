package it.pagopa.pn.timelineservice.dto.ext.notification;

import it.pagopa.pn.timelineservice.dto.mandate.DelegateInfoInt;
import it.pagopa.pn.timelineservice.dto.RaddInfo;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

@Builder
@Getter
public class NotificationViewedInt {
    String iun;
    Integer recipientIndex;
    DelegateInfoInt delegateInfo;
    Instant viewedDate;
    RaddInfo raddInfo;
    String sourceChannel;
    String sourceChannelDetails;
}
