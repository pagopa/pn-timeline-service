package it.pagopa.pn.timelineservice.utils;

import it.pagopa.pn.commons.exceptions.PnInternalException;
import it.pagopa.pn.timelineservice.dto.timeline.details.TimelineElementCategoryInt;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;

import static it.pagopa.pn.timelineservice.exceptions.PnTimelineServiceExceptionCodes.ERROR_CODE_TIMELINESERVICE_NOTIFICATIONSTATUSFAILED;

@Getter
public enum CompletedDeliveryWorkflowCategory {
    DIGITAL_DELIVERY_CREATION_REQUEST(TimelineElementCategoryInt.DIGITAL_DELIVERY_CREATION_REQUEST),
    ANALOG_SUCCESS_WORKFLOW(TimelineElementCategoryInt.ANALOG_SUCCESS_WORKFLOW),
    COMPLETELY_UNREACHABLE(TimelineElementCategoryInt.COMPLETELY_UNREACHABLE),
    ANALOG_WORKFLOW_RECIPIENT_DECEASED(TimelineElementCategoryInt.ANALOG_WORKFLOW_RECIPIENT_DECEASED);

    private final TimelineElementCategoryInt category;

    CompletedDeliveryWorkflowCategory(TimelineElementCategoryInt category) {
        this.category = category;
    }

    /**
     * Verifica se una categoria appartiene ai workflow di consegna completati
     */
    public static boolean isCompletedWorkflowCategory(TimelineElementCategoryInt category) {
        return Arrays.stream(CompletedDeliveryWorkflowCategory.values())
                .anyMatch(c -> c.getCategory().equals(category));
    }

    /**
     * Trova la categoria con priorità più alta dalla lista fornita
     */
    public static TimelineElementCategoryInt pickCategoryByPriority(List<TimelineElementCategoryInt> relatedCategories) {
        return Arrays.stream(values())
                .map(CompletedDeliveryWorkflowCategory::getCategory)
                .filter(relatedCategories::contains)
                .findFirst()
                .orElseThrow(() -> new PnInternalException("No end workflow category found", ERROR_CODE_TIMELINESERVICE_NOTIFICATIONSTATUSFAILED));
    }
}
