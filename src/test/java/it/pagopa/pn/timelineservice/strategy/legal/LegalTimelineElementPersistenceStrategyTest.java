package it.pagopa.pn.timelineservice.strategy.legal;

import it.pagopa.pn.commons.exceptions.PnInternalException;
import it.pagopa.pn.commons.log.PnAuditLogBuilder;
import it.pagopa.pn.commons.log.PnAuditLogEvent;
import it.pagopa.pn.timelineservice.config.PnTimelineServiceConfigs;
import it.pagopa.pn.timelineservice.dto.address.PhysicalAddressInt;
import it.pagopa.pn.timelineservice.dto.notification.NotificationInfoInt;
import it.pagopa.pn.timelineservice.dto.notification.status.NotificationStatusHistoryInvalidatedElementInt;
import it.pagopa.pn.timelineservice.dto.timeline.TimelineElementInternal;
import it.pagopa.pn.timelineservice.dto.timeline.details.*;
import it.pagopa.pn.timelineservice.service.mapper.SmartMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class LegalTimelineElementPersistenceStrategyTest {

    private LegalTimelineElementPersistenceStrategy strategy;
    private SmartMapper smartMapper;
    private PnTimelineServiceConfigs configs;

    @BeforeEach
    void setup() {
        smartMapper = Mockito.mock(SmartMapper.class);
        configs = Mockito.mock(PnTimelineServiceConfigs.class);
        when(configs.getInvalidableCategories()).thenReturn(List.of("PREPARE_ANALOG_DOMICILE","PREPARE_ANALOG_DOMICILE_FAILURE","SEND_ANALOG_DOMICILE","SEND_ANALOG_PROGRESS","SEND_ANALOG_FEEDBACK","ANALOG_SUCCESS_WORKFLOW","ANALOG_FAILURE_WORKFLOW","SCHEDULE_REFINEMENT","REFINEMENT","COMPLETELY_UNREACHABLE_CREATION_REQUEST","COMPLETELY_UNREACHABLE","ANALOG_WORKFLOW_RECIPIENT_DECEASED"));
        strategy = new LegalTimelineElementPersistenceStrategy(smartMapper, configs);
    }

    @Test
    void buildAuditLogEventReturnsNonNullEvent() {
        TimelineElementInternal dto = TimelineElementInternal.builder()
                .iun("iun_123")
                .elementId("elementId_123")
                .category(TimelineElementCategoryInt.AAR_GENERATION)
                .timestamp(Instant.now())
                .build();

        PnAuditLogEvent event = strategy.buildAuditLogEvent(dto, new PnAuditLogBuilder());

        assertNotNull(event);
    }

    @Test
    void buildAuditLogEventWithNullDetailsDoesNotThrow() {
        TimelineElementInternal dto = TimelineElementInternal.builder()
                .iun("iun_123")
                .elementId("elementId_123")
                .category(TimelineElementCategoryInt.AAR_GENERATION)
                .timestamp(Instant.now())
                .details(null)
                .build();

        assertDoesNotThrow(() -> strategy.buildAuditLogEvent(dto, new PnAuditLogBuilder()));
    }

    @Test
    void enrichWithReworkReturnsUnmodifiedElementWhenCategoryNotInvalidable() {
        when(configs.getInvalidableCategories()).thenReturn(List.of());
        TimelineElementInternal dto = TimelineElementInternal.builder()
                .iun("iun_123")
                .elementId("SEND_DIGITAL_DOMICILE.IUN_iun_123.RECINDEX_0")
                .category(TimelineElementCategoryInt.SEND_DIGITAL_DOMICILE)
                .timestamp(Instant.now())
                .build();

        TimelineElementInternal result = strategy.enrichWithRework(dto, Set.of());

        assertEquals(dto.getElementId(), result.getElementId());
    }

    @Test
    void enrichWithReworkReturnsUnmodifiedElementWhenNoReworkElementsInTimeline() {
        when(configs.getInvalidableCategories()).thenReturn(List.of("SEND_DIGITAL_DOMICILE"));
        TimelineElementInternal dto = TimelineElementInternal.builder()
                .iun("iun_123")
                .elementId("SEND_DIGITAL_DOMICILE.IUN_iun_123.RECINDEX_0")
                .category(TimelineElementCategoryInt.SEND_DIGITAL_DOMICILE)
                .timestamp(Instant.now())
                .build();

        TimelineElementInternal result = strategy.enrichWithRework(dto, Set.of());

        assertEquals("SEND_DIGITAL_DOMICILE.IUN_iun_123.RECINDEX_0", result.getElementId());
    }

    @Test
    void enrichWithReworkThrowsExceptionWhenElementIdHasNoRecIndex() {
        when(configs.getInvalidableCategories()).thenReturn(List.of("SEND_DIGITAL_DOMICILE"));
        TimelineElementInternal reworkElement = TimelineElementInternal.builder()
                .iun("iun_123")
                .elementId("NOTIFICATION_TIMELINE_REWORKED.IUN_iun_123.RECINDEX_0")
                .category(TimelineElementCategoryInt.NOTIFICATION_TIMELINE_REWORKED)
                .timestamp(Instant.now())
                .build();
        TimelineElementInternal dto = TimelineElementInternal.builder()
                .iun("iun_123")
                .elementId("SEND_DIGITAL_DOMICILE.IUN_iun_123")
                .category(TimelineElementCategoryInt.SEND_DIGITAL_DOMICILE)
                .timestamp(Instant.now())
                .build();

        assertThrows(PnInternalException.class, () -> strategy.enrichWithRework(dto, Set.of(reworkElement)));
    }



    @Test
    void applyBusinessTimestampPreservesOriginalTimestamp() {
        Instant originalTimestamp = Instant.now();
        TimelineElementInternal dto = TimelineElementInternal.builder()
                .iun("iun_123")
                .elementId("elementId_123")
                .category(TimelineElementCategoryInt.AAR_GENERATION)
                .timestamp(originalTimestamp)
                .build();
        TimelineElementInternal mappedDto = TimelineElementInternal.builder()
                .iun("iun_123")
                .elementId("elementId_123")
                .timestamp(originalTimestamp.plusSeconds(10))
                .build();
        when(smartMapper.mapTimelineInternal(any(), any())).thenReturn(mappedDto);

        TimelineElementInternal result = strategy.applyBusinessTimestamp(dto, Set.of());

        assertEquals(originalTimestamp, result.getTimestamp());
    }

    @Test
    void requiresCriticalPathReturnsTrueForMultipleRecipientsAndCompletedWorkflowCategory() {
        TimelineElementInternal dto = TimelineElementInternal.builder()
                .iun("iun_123")
                .elementId("elementId_123")
                .category(TimelineElementCategoryInt.DIGITAL_DELIVERY_CREATION_REQUEST)
                .build();
        NotificationInfoInt notification = NotificationInfoInt.builder()
                .iun("iun_123")
                .numberOfRecipients(2)
                .build();

        boolean result = strategy.requiresCriticalPath(dto, notification);

        assertTrue(result);
    }

    @Test
    void requiresCriticalPathReturnsFalseForSingleRecipient() {
        TimelineElementInternal dto = TimelineElementInternal.builder()
                .iun("iun_123")
                .elementId("elementId_123")
                .category(TimelineElementCategoryInt.DIGITAL_DELIVERY_CREATION_REQUEST)
                .build();
        NotificationInfoInt notification = NotificationInfoInt.builder()
                .iun("iun_123")
                .numberOfRecipients(1)
                .build();

        boolean result = strategy.requiresCriticalPath(dto, notification);

        assertFalse(result);
    }

    @Test
    void requiresCriticalPathReturnsFalseForNonCompletedWorkflowCategory() {
        TimelineElementInternal dto = TimelineElementInternal.builder()
                .iun("iun_123")
                .elementId("elementId_123")
                .category(TimelineElementCategoryInt.AAR_GENERATION)
                .build();
        NotificationInfoInt notification = NotificationInfoInt.builder()
                .iun("iun_123")
                .numberOfRecipients(3)
                .build();

        boolean result = strategy.requiresCriticalPath(dto, notification);

        assertFalse(result);
    }



    @Test
    void addTimelineElementWithReworkElement() {
        // GIVEN
        String iun = "iun_12345";
        String elementId = "elementId_12345";

        Set<TimelineElementInternal> setTimelineElement = getNotificationReworkDetailsList(iun, elementId, false, true, true, false, 0, 0);

        TimelineElementInternal newElement = TimelineElementInternal.builder()
                .elementId(elementId+".RECINDEX_0.ATTEMPT_0")
                .category(TimelineElementCategoryInt.SEND_ANALOG_DOMICILE)
                .iun(iun)
                .timestamp(Instant.now())
                .build();

        // WHEN
        TimelineElementInternal result = strategy.enrichWithRework(newElement, setTimelineElement);

        // THEN
        Assertions.assertEquals(result.getTimestamp(), newElement.getTimestamp());
    }

    @Test
    void addTimelineElementWithUpdatedReworkElement() {
        // GIVEN
        String iun = "iun_12345";
        String elementId = "elementId_12345";

        Set<TimelineElementInternal> setTimelineElement = getNotificationReworkDetailsList(iun, elementId, false, true, true, false, 0, 0);


        NotificationTimelineReworkedDetailsInt detail = NotificationTimelineReworkedDetailsInt.builder()
                .recIndex(0)
                .sentAttemptMade(0)
                .build();
        TimelineElementInternal newElement = TimelineElementInternal.builder()
                .elementId(elementId+".RECINDEX_0.ATTEMPT_0.REWORK_0")
                .category(TimelineElementCategoryInt.NOTIFICATION_TIMELINE_REWORKED)
                .iun(iun)
                .timestamp(Instant.now())
                .details(detail)
                .build();

        // WHEN
        TimelineElementInternal result = strategy.enrichWithRework(newElement, setTimelineElement);

        // THEN
        Assertions.assertNull(result.getEventTimestamp());
    }

    @Test
    void addTimelineElementWithReworkElementWithSendAnalogFeedback() {
        // GIVEN
        String iun = "iun_12345";
        String elementId = "elementId_12345";

        Set<TimelineElementInternal> setTimelineElement = getNotificationReworkDetailsList(iun, elementId, true, true, true, false, 0, 0);

        TimelineElementInternal newElement = TimelineElementInternal.builder()
                .category(TimelineElementCategoryInt.SEND_ANALOG_DOMICILE)
                .elementId(elementId+".RECINDEX_0.ATTEMPT_0")
                .iun(iun)
                .timestamp(Instant.now())
                .build();

        // WHEN
        TimelineElementInternal result = strategy.enrichWithRework(newElement, setTimelineElement);

        // THEN
        Assertions.assertEquals(result.getTimestamp(), newElement.getTimestamp());
    }

    @Test
    void addTimelineElementWithReworkElementNotMatchingRecIndex() {
        // GIVEN
        String iun = "iun_12345";
        String elementId = "elementId_12345";

        Set<TimelineElementInternal> setTimelineElement = getNotificationReworkDetailsList(iun, elementId, true, true, true, false, 1, 0);

        TimelineElementInternal newElement = TimelineElementInternal.builder()
                .category(TimelineElementCategoryInt.SEND_ANALOG_DOMICILE)
                .elementId(elementId+".RECINDEX_0.ATTEMPT_0")
                .iun(iun)
                .timestamp(Instant.now())
                .build();

        // WHEN
        TimelineElementInternal result = strategy.enrichWithRework(newElement, setTimelineElement);

        // THEN
        Assertions.assertEquals(result.getTimestamp(), newElement.getTimestamp());
    }

    @Test
    void addTimelineElementWithReworkElementNullAttemptId() {
        // GIVEN
        String iun = "iun_12345";
        String elementId = "elementId_12345";
        Set<TimelineElementInternal> setTimelineElement = getNotificationReworkDetailsList(iun, elementId, false, true, true, false, 0, 0);

        TimelineElementInternal newElement = TimelineElementInternal.builder()
                .category(TimelineElementCategoryInt.SEND_ANALOG_DOMICILE)
                .elementId(elementId+".RECINDEX_0.ATTEMPT_0")
                .iun(iun)
                .timestamp(Instant.now())
                .build();

        // WHEN
        TimelineElementInternal result = strategy.enrichWithRework(newElement, setTimelineElement);

        // THEN
        Assertions.assertEquals(result.getTimestamp(), newElement.getTimestamp());
    }

    @Test
    void addTimelineElementWithReworkElementAttemptNullWithSendAnalogFeedback() {
        // GIVEN
        String iun = "iun_12345";
        String elementId = "PREPARE_ANALOG_DOMICILE.IUN_12345";
        Set<TimelineElementInternal> setTimelineElement = getNotificationReworkDetailsList(iun, elementId, true, true, true, false, 0, 0);

        TimelineElementInternal newElement = TimelineElementInternal.builder()
                .elementId(elementId+".RECINDEX_0")
                .category(TimelineElementCategoryInt.PREPARE_ANALOG_DOMICILE)
                .iun(iun)
                .timestamp(Instant.now())
                .build();

        // WHEN
        TimelineElementInternal result = strategy.enrichWithRework(newElement, setTimelineElement);

        // THEN
        Assertions.assertEquals(result.getTimestamp(), newElement.getTimestamp());
        Assertions.assertTrue(result.getElementId().contains("REWORK"));
    }

    @Test
    void addTimelineElementWithReworkElementAttemptNullWithPrepareAttemptOne() {
        // GIVEN
        String iun = "iun_12345";
        String elementId = "PREPARE_ANALOG_DOMICILE.IUN_12345";
        Set<TimelineElementInternal> setTimelineElement = getNotificationReworkDetailsList(iun, elementId, true, false, true, true, 0, 0);

        TimelineElementInternal newElement = TimelineElementInternal.builder()
                .elementId(elementId+".RECINDEX_0")
                .category(TimelineElementCategoryInt.PREPARE_ANALOG_DOMICILE)
                .iun(iun)
                .timestamp(Instant.now())
                .build();

        // WHEN
        TimelineElementInternal result = strategy.enrichWithRework(newElement, setTimelineElement);

        // THEN
        Assertions.assertEquals(result.getTimestamp(), newElement.getTimestamp());
        Assertions.assertTrue(result.getElementId().contains("REWORK"));
    }

    @Test
    void addTimelineElementWithReworkElementAttemptNullWithPrepareAttemptOneButAlsoReworkForAttemptOne() {
        // GIVEN
        String iun = "iun_12345";
        String elementId = "PREPARE_ANALOG_DOMICILE.IUN_12345";

        Set<TimelineElementInternal> setTimelineElement = getNotificationReworkDetailsList(iun, elementId, true, false, true, true, 0, 1);


        TimelineElementInternal newElement = TimelineElementInternal.builder()
                .elementId(elementId+".RECINDEX_0")
                .category(TimelineElementCategoryInt.PREPARE_ANALOG_DOMICILE)
                .iun(iun)
                .timestamp(Instant.now())
                .build();

        // WHEN
        TimelineElementInternal result = strategy.enrichWithRework(newElement, setTimelineElement);

        // THEN
        Assertions.assertEquals(result.getTimestamp(), newElement.getTimestamp());
        Assertions.assertTrue(result.getElementId().contains("REWORK"));
    }

    private Set<TimelineElementInternal> getNotificationReworkDetailsList(String iun,  String elementId, boolean withSendAnalogFeedback, boolean withSendAnalogFeedbackWithRework, boolean withSendAnalogDomicile, boolean withNextPrepareAnalogDomicile, int recIndex, Integer attemptId){
        List<TimelineElementInternal> timelineElementList = new ArrayList<>();
        TimelineElementInternal timelineElementInternal = getSendPaperDetailsTimelineElement(iun, elementId+".RECINDEX_0.ATTEMPT_0");
        TimelineElementInternal timelineElementInternalNotificationRework = getNotificationReworkDetailsTimelineElement(iun, "NOTIFICATION_TIMELINE_REWORKED.RECINDEX_0.ATTEMPT_0.REWORK_0", recIndex, attemptId);
        timelineElementList.add(timelineElementInternal);
        timelineElementList.add(timelineElementInternalNotificationRework);

        if (withSendAnalogFeedback) {
            TimelineElementInternal timelineElementInternalSendAnalogFeedback = getSendPaperFeedbackTimelineElement(iun, elementId+".RECINDEX_0.ATTEMPT_0", Instant.now(), withSendAnalogFeedbackWithRework);
            timelineElementList.add(timelineElementInternalSendAnalogFeedback);
        }

        if (withSendAnalogDomicile) {
            TimelineElementInternal timelineElementInternalSendAnalogDomicile = getSendAnalogDomicileTimelineElement(iun, elementId+".RECINDEX_0.ATTEMPT_0", Instant.now(), true);
            timelineElementList.add(timelineElementInternalSendAnalogDomicile);
        }

        if (withNextPrepareAnalogDomicile) {
            TimelineElementInternal nextPrepareAnalogDomicile = getPrepareAnalogDomicileTimelineElement(iun, elementId+".RECINDEX_0.ATTEMPT_1", Instant.now());
            timelineElementList.add(nextPrepareAnalogDomicile);
        }

        return new HashSet<>(timelineElementList);
    }

    private TimelineElementInternal getNotificationReworkDetailsTimelineElement(String iun, String timelineId, int recIndex, Integer attemptId) {
        NotificationStatusHistoryInvalidatedElementInt notificationStatusHistoryElementInt = new NotificationStatusHistoryInvalidatedElementInt();
        notificationStatusHistoryElementInt.setRelatedTimelineElementIds(List.of("PREPARE_ANALOG_DOMICILE.IUN_"+iun+"RECINDEX_0.ATTEMPT_0"));
        NotificationTimelineReworkedDetailsInt details = NotificationTimelineReworkedDetailsInt.builder()
                .recIndex(recIndex)
                .sentAttemptMade(attemptId)
                .invalidatedTimelineAndStatusHistory(List.of(notificationStatusHistoryElementInt))
                .build();
        return TimelineElementInternal.builder()
                .timestamp(Instant.now())
                .elementId(timelineId)
                .iun(iun)
                .reworkId("REWORK_0")
                .category(TimelineElementCategoryInt.NOTIFICATION_TIMELINE_REWORKED)
                .details( details )
                .build();
    }

    private TimelineElementInternal getSendPaperDetailsTimelineElement(String iun, String elementId) {
        SendAnalogDetailsInt details =  SendAnalogDetailsInt.builder()
                .physicalAddress(
                        PhysicalAddressInt.builder()
                                .province("province")
                                .municipality("munic")
                                .at("at")
                                .build()
                )
                .relatedRequestId("abc")
                .analogCost(100)
                .recIndex(0)
                .sentAttemptMade(0)
                .build();
        return TimelineElementInternal.builder()
                .timestamp(Instant.now())
                .elementId(elementId)
                .iun(iun)
                .details( details )
                .category(TimelineElementCategoryInt.SEND_ANALOG_DOMICILE )
                .build();
    }

    private TimelineElementInternal getSendPaperFeedbackTimelineElement(String iun, String elementId, Instant timestamp, boolean withReworkId) {
        SendAnalogFeedbackDetailsInt details =  SendAnalogFeedbackDetailsInt.builder()
                .notificationDate(timestamp)
                .newAddress(
                        PhysicalAddressInt.builder()
                                .province("province")
                                .municipality("munic")
                                .at("at")
                                .build()
                )
                .recIndex(0)
                .sentAttemptMade(0)
                .build();
        return TimelineElementInternal.builder()
                .elementId(elementId)
                .iun(iun)
                .category(TimelineElementCategoryInt.SEND_ANALOG_FEEDBACK)
                .timestamp(timestamp)
                .reworkId(withReworkId ? "REWORK_0" : null)
                .details( details )
                .build();
    }

    private TimelineElementInternal getSendAnalogDomicileTimelineElement(String iun, String elementId, Instant timestamp, boolean withReworkId) {
        SendAnalogDetailsInt details =  SendAnalogDetailsInt.builder()
                .recIndex(0)
                .sentAttemptMade(0)
                .build();
        return TimelineElementInternal.builder()
                .elementId(elementId)
                .iun(iun)
                .category(TimelineElementCategoryInt.SEND_ANALOG_DOMICILE)
                .timestamp(timestamp)
                .eventTimestamp(timestamp)
                .reworkId(withReworkId ? "REWORK_0" : null)
                .details( details )
                .build();
    }

    private TimelineElementInternal getPrepareAnalogDomicileTimelineElement(String iun, String elementId, Instant timestamp) {
        BaseAnalogDetailsInt details =  BaseAnalogDetailsInt.builder()
                .recIndex(0)
                .build();
        return TimelineElementInternal.builder()
                .elementId(elementId)
                .iun(iun)
                .category(TimelineElementCategoryInt.PREPARE_ANALOG_DOMICILE)
                .timestamp(timestamp)
                .eventTimestamp(timestamp)
                .reworkId(null)
                .details( details )
                .build();
    }

}