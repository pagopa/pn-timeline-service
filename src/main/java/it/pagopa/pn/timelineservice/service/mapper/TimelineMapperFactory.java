package it.pagopa.pn.timelineservice.service.mapper;

import it.pagopa.pn.timelineservice.config.PnDeliveryPushConfigs;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
@RequiredArgsConstructor
public class TimelineMapperFactory {

    private final PnDeliveryPushConfigs configs;

    public TimelineMapper getTimelineMapper(Instant notificationSentAt) {
        Instant fixReleaseDate = getFixReleaseDate();

        if (fixReleaseDate == null)
            return new TimelineMapperBeforeFix();

        return notificationSentAt != null && (notificationSentAt.isAfter(fixReleaseDate) || notificationSentAt.equals(fixReleaseDate))
                ? new TimelineMapperAfterFix()
                : new TimelineMapperBeforeFix();
    }

    private Instant getFixReleaseDate() {
        return configs.getFeatureUnreachableRefinementPostAARStartDate();
    }


}
