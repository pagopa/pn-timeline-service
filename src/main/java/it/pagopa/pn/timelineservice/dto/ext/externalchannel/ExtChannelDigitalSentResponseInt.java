package it.pagopa.pn.timelineservice.dto.ext.externalchannel;

import lombok.*;

import java.time.Instant;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder(toBuilder = true)
@EqualsAndHashCode
@ToString
public class ExtChannelDigitalSentResponseInt {
    private String requestId;
    private String iun;
    private ExtChannelProgressEventCat status;
    private String eventDetails;
    private Instant eventTimestamp;
    private EventCodeInt eventCode;
    private DigitalMessageReferenceInt generatedMessage;

}
