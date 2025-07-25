package it.pagopa.pn.timelineservice.dto.timeline.details;

import it.pagopa.pn.timelineservice.dto.mandate.DelegateInfoInt;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.Instant;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@SuperBuilder( toBuilder = true )
@EqualsAndHashCode(callSuper = true)
@ToString
public class NotificationViewedCreationRequestDetailsInt extends CategoryTypeTimelineElementDetailsInt implements RecipientRelatedTimelineElementDetails, PersonalInformationRelatedTimelineElement{
    private int recIndex;
    private String legalFactId;
    private String raddType;
    private String raddTransactionId;
    private DelegateInfoInt delegateInfo;
    private Instant eventTimestamp;
    private String sourceChannel;
    private String sourceChannelDetails;
    
    public String toLog() {
        return String.format(
                "recIndex=%d legalFactId=%s raddType=%s raddTransactionId=%s delegateInfo=%s sourceChannel=%s sourceChannelDetails=%s eventTimestamp=%s" ,
                recIndex,
                legalFactId,
                raddType,
                raddTransactionId,
                delegateInfo,
                sourceChannel,
                sourceChannelDetails,
                eventTimestamp
        );
    }
    
    @Override
    public String getTaxId() {
        return delegateInfo != null ? delegateInfo.getTaxId() : null;
    }

    @Override
    public void setTaxId(String taxId) {
        if(delegateInfo != null){
            delegateInfo.setTaxId(taxId);
        }
    }

    @Override
    public String getDenomination() {
        return delegateInfo != null ? delegateInfo.getDenomination() : null;
    }

    @Override
    public void setDenomination(String denomination) {
        if(delegateInfo != null){
            delegateInfo.setDenomination(denomination);
        }
    }
}
