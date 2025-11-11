package it.pagopa.pn.timelineservice.dto.timeline;

import jakarta.validation.constraints.NotNull;

import javax.annotation.Nullable;
import java.util.Objects;

public class TimelineEventIdBuilder {

    public static final String DELIMITER = ".";
    public static final String IUN = "IUN_";
    public static final String RECINDEX = "RECINDEX_";
    public static final String ATTEMPT = "ATTEMPT_";
    public static final String REWORK = "REWORK_";

    private String baseId = "";
    private StringBuilder suffix = new StringBuilder();

    private TimelineEventIdBuilder() {}

    /**
     * Inizializza il builder con un ID già esistente (opzionale)
     */
    public static TimelineEventIdBuilder from(@Nullable String existingId) {
        TimelineEventIdBuilder builder = new TimelineEventIdBuilder();
        if (Objects.nonNull(existingId) && !existingId.isBlank()) {
            builder.baseId = existingId;
        }
        return builder;
    }

    public void withCategory(@NotNull String category) {
        // Se non c'è baseId, la categoria diventa la base
        if (baseId.isEmpty()) {
            baseId = category;
        }
    }

    public void withIun(@Nullable String iun) {
        if (Objects.nonNull(iun))
            suffix.append(DELIMITER).append(IUN).append(iun);
    }

    public void withRecIndex(@Nullable Integer recIndex) {
        if (Objects.nonNull(recIndex))
            suffix.append(DELIMITER).append(RECINDEX).append(recIndex);
    }

    public void withSentAttemptMade(@Nullable Integer sentAttemptMade) {
        if (Objects.nonNull(sentAttemptMade) && sentAttemptMade >= 0)
            suffix.append(DELIMITER).append(ATTEMPT).append(sentAttemptMade);
    }

    public void withReworkIdx(@Nullable Integer reworkIdx) {
        if (Objects.nonNull(reworkIdx) && reworkIdx >= 0)
            suffix.append(DELIMITER).append(REWORK).append(reworkIdx);
    }

    public String build() {
        return baseId + suffix.toString();
    }
}
