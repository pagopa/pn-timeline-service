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
public class NotificationViewedDetailsInt extends CategoryTypeTimelineElementDetailsInt implements RecipientRelatedTimelineElementDetails, PersonalInformationRelatedTimelineElement, ElementTimestampTimelineElementDetails{
    private int recIndex;
    private Integer notificationCost;
    private String raddType;
    private String raddTransactionId;
    private DelegateInfoInt delegateInfo;
    private Instant eventTimestamp;
    
    public String toLog() {
        return String.format(
                "recIndex=%d eventTimestamp=%s",
                recIndex,
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

    @Override
    public Instant getElementTimestamp() {
        return eventTimestamp;
    }
}
