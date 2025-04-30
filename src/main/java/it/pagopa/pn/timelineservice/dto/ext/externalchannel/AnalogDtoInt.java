package it.pagopa.pn.timelineservice.dto.ext.externalchannel;

import it.pagopa.pn.timelineservice.dto.SendResponse;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
public class AnalogDtoInt {
    private SendResponse sendResponse;
    private String productType;
    private String prepareRequestId;
    private String relatedRequestId;
    private int sentAttemptMade;
}
