package it.pagopa.pn.timelineservice.strategy.informal;

import it.pagopa.pn.timelineservice.dto.notification.status.NotificationStatusInt;
import it.pagopa.pn.timelineservice.dto.timeline.details.TimelineElementCategoryInt;
import it.pagopa.pn.timelineservice.strategy.common.AbstractStateMap;

public class InformalTimelineStateMap extends AbstractStateMap {

    @Override
    protected void configureTransitions() {
        // Received state
        this.fromState(NotificationStatusInt.IN_VALIDATION)
                //STATE UNCHANGE
                .withTimelineGoToState(TimelineElementCategoryInt.VALIDATE_NORMALIZE_ADDRESSES_REQUEST, NotificationStatusInt.IN_VALIDATION, SINGLE_RECIPINET)
                .withTimelineGoToState(TimelineElementCategoryInt.NORMALIZED_ADDRESS, NotificationStatusInt.IN_VALIDATION, SINGLE_RECIPINET)
                .withTimelineGoToState(TimelineElementCategoryInt.PUBLIC_REGISTRY_VALIDATION_CALL, NotificationStatusInt.IN_VALIDATION, SINGLE_RECIPINET)
                .withTimelineGoToState(TimelineElementCategoryInt.PUBLIC_REGISTRY_VALIDATION_RESPONSE, NotificationStatusInt.IN_VALIDATION, SINGLE_RECIPINET)

                //STATE CHANGE
                .withTimelineGoToState(TimelineElementCategoryInt.REQUEST_ACCEPTED, NotificationStatusInt.ACCEPTED, SINGLE_RECIPINET)
                .withTimelineGoToState(TimelineElementCategoryInt.REQUEST_REFUSED, NotificationStatusInt.REFUSED, SINGLE_RECIPINET)
        ;

        this.fromState(NotificationStatusInt.ACCEPTED);
        this.fromState(NotificationStatusInt.REFUSED);

    }
}
