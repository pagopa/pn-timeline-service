package it.pagopa.pn.timelineservice.utils;

import it.pagopa.pn.timelineservice.config.PnTimelineServiceConfigs;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;

@AllArgsConstructor
@Component
public class FeatureEnabledUtils {

    private final PnTimelineServiceConfigs configs;

    public boolean isPfNewWorkflowEnabled(Instant notificationSentAt) {
        boolean isEnabled = false;
        Instant startDate = Instant.parse(configs.getPfNewWorkflowStart());
        Instant endDate = Instant.parse(configs.getPfNewWorkflowStop());
        if (notificationSentAt.compareTo(startDate) >= 0 && notificationSentAt.compareTo(endDate) <= 0) {
            isEnabled = true;
        }
        return isEnabled;
    }

}
