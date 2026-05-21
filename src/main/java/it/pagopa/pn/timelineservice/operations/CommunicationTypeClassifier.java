package it.pagopa.pn.timelineservice.operations;

import it.pagopa.pn.commons.exceptions.PnInternalException;
import it.pagopa.pn.timelineservice.dto.timeline.CommunicationType;
import it.pagopa.pn.timelineservice.dto.timeline.TimelineElementInternal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Objects;

import static it.pagopa.pn.timelineservice.exceptions.PnTimelineServiceExceptionCodes.ERROR_CODE_TIMELINESERVICE_UNKNOWN_COMMUNICATION_TYPE;

@Component
@Slf4j
public class CommunicationTypeClassifier {
    private static final CommunicationType DEFAULT_TYPE = CommunicationType.LEGAL;

    public CommunicationType resolveFromTimelineElements(Collection<TimelineElementInternal> elements) {
        if (elements == null || elements.isEmpty()) {
            log.debug("Timeline elements collection is null or empty, returning default communication type: {}", DEFAULT_TYPE);
            return DEFAULT_TYPE;
        }
        return elements.stream()
                .map(TimelineElementInternal::getCommunicationType)
                .filter(Objects::nonNull)
                .findFirst()
                .orElseThrow(() -> new PnInternalException(
                        "Communication type not found in timeline elements",
                        ERROR_CODE_TIMELINESERVICE_UNKNOWN_COMMUNICATION_TYPE
                ));
    }
}
