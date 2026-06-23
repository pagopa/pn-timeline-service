package it.pagopa.pn.timelineservice.utils;

import it.pagopa.pn.timelineservice.dto.timeline.details.TimelineElementCategoryInt;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CompletedDeliveryWorkflowCategoryTest {

    @Test
    void returnsTrueIfCategoryIsCompletedWorkflow() {
        assertTrue(CompletedDeliveryWorkflowCategory.isCompletedWorkflowCategory(
                TimelineElementCategoryInt.DIGITAL_DELIVERY_CREATION_REQUEST));
        assertTrue(CompletedDeliveryWorkflowCategory.isCompletedWorkflowCategory(
                TimelineElementCategoryInt.ANALOG_SUCCESS_WORKFLOW));
        assertTrue(CompletedDeliveryWorkflowCategory.isCompletedWorkflowCategory(
                TimelineElementCategoryInt.COMPLETELY_UNREACHABLE));
        assertTrue(CompletedDeliveryWorkflowCategory.isCompletedWorkflowCategory(
                TimelineElementCategoryInt.ANALOG_WORKFLOW_RECIPIENT_DECEASED));
    }

    @Test
    void returnsFalseIfCategoryIsNotCompletedWorkflow() {
        assertFalse(CompletedDeliveryWorkflowCategory.isCompletedWorkflowCategory(
                TimelineElementCategoryInt.valueOf("NORMALIZED_ADDRESS")));
    }

    @Test
    void pickCategoryByPriorityReturnsFirstMatchingCategory() {
        var categories = List.of(
                TimelineElementCategoryInt.ANALOG_WORKFLOW_RECIPIENT_DECEASED,
                TimelineElementCategoryInt.DIGITAL_DELIVERY_CREATION_REQUEST
        );
        var result = CompletedDeliveryWorkflowCategory.pickCategoryByPriority(categories);
        assertEquals(TimelineElementCategoryInt.DIGITAL_DELIVERY_CREATION_REQUEST, result);
    }

    @Test
    void pickCategoryByPriorityReturnsFirstMatchingCategory2() {
        var categories = List.of(
                TimelineElementCategoryInt.ANALOG_WORKFLOW_RECIPIENT_DECEASED,
                TimelineElementCategoryInt.COMPLETELY_UNREACHABLE
        );
        var result = CompletedDeliveryWorkflowCategory.pickCategoryByPriority(categories);
        assertEquals(TimelineElementCategoryInt.COMPLETELY_UNREACHABLE, result);
    }

    @Test
    void pickCategoryByPriorityThrowsIfNoMatch() {
        var categories = List.of(TimelineElementCategoryInt.NORMALIZED_ADDRESS);
        assertThrows(it.pagopa.pn.commons.exceptions.PnInternalException.class, () ->
                CompletedDeliveryWorkflowCategory.pickCategoryByPriority(categories));
    }

}