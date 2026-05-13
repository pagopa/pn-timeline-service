package it.pagopa.pn.timelineservice.strategy;

import it.pagopa.pn.commons.exceptions.PnInternalException;
import it.pagopa.pn.timelineservice.dto.timeline.CommunicationType;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

@Component
public class TimelineStrategyResolver {
    private static final String CODE_ERROR = "TIMELINE_STRATEGY_RESOLUTION";
    private final Map<CommunicationType, TimelineStrategyBundle> bundles;

    public TimelineStrategyResolver(List<TimelineStrategyBundle> allBundles) {
        this.bundles = allBundles.stream()
                .collect(
                    toMap(
                        TimelineStrategyBundle::supportedType,
                        identity(),
                        (existing, duplicate) -> {
                            String duplicateErrMsg = String.format(
                                    "Duplicate TimelineStrategyBundle for communication type %s: %s and %s",
                                    existing.supportedType(),
                                    existing.getClass().getName(),
                                    duplicate.getClass().getName()
                            );
                            throw new PnInternalException(duplicateErrMsg, CODE_ERROR);
                        }
                    )
                );
    }

    public TimelineStrategyBundle resolve(CommunicationType type) {
        return Optional.ofNullable(bundles.get(type))
                .orElseThrow(() -> new PnInternalException("Unsupported communication type: " + type, CODE_ERROR));
    }
}
