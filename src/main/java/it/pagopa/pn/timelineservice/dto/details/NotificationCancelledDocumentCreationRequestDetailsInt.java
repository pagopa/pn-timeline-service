package it.pagopa.pn.timelineservice.dto.details;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder(toBuilder = true)
@EqualsAndHashCode
@ToString
public class NotificationCancelledDocumentCreationRequestDetailsInt implements TimelineElementDetailsInt{
    private String legalFactId;

    @Override
    public String toLog() {
        return String.format(
                "legalFactId=%s",
                legalFactId
        );
    }
}
