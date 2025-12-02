package it.pagopa.pn.timelineservice.dto.timeline;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ReworkFilteringResult {
    private String timelineElementId;
    private String reworkId;
}
