package it.pagopa.pn.timelineservice.service.impl;

import it.pagopa.pn.commons.exceptions.PnInternalException;
import it.pagopa.pn.timelineservice.dto.address.LegalDigitalAddressInt;
import it.pagopa.pn.timelineservice.dto.timeline.TimelineElementInternal;
import it.pagopa.pn.timelineservice.dto.timeline.details.*;
import it.pagopa.pn.timelineservice.service.mapper.TimelineMapperBeforeFix;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAmount;
import java.time.temporal.TemporalUnit;
import java.util.Set;

class TimelineMapperBeforeFixTest {

    private TimelineMapperBeforeFix timelineMapperBeforeFix;

    @BeforeEach
    void setUp() {
        timelineMapperBeforeFix = new TimelineMapperBeforeFix();
    }

    @Test
    void testMapNotificationReworkedAttempt0RecIndex0() {
        Instant sourceEventTimestamp = Instant.EPOCH;
        Instant sourceIngestionTimestamp = Instant.now();

        TimelineElementInternal notificationReworked = TimelineElementInternal.builder()
                .category(TimelineElementCategoryInt.NOTIFICATION_TIMELINE_REWORKED)
                .details(new NotificationTimelineReworkedDetailsInt())
                .elementId("NOTIFICATION_TIMELINE_REWORKED.IUN_ABC.RECINDEX_0.ATTEMPT_0.REWORK_0")
                .timestamp(sourceIngestionTimestamp)
                .notificationSentAt(Instant.now().plusSeconds(3600))
                .build();
        Set<TimelineElementInternal> timelineElementInternalSet = Set.of(
                TimelineElementInternal.builder()
                        .category(TimelineElementCategoryInt.SEND_ANALOG_DOMICILE)
                        .elementId("SEND_ANALOG_DOMICILE.IUN_ABC.RECINDEX_1.ATTEMPT_1")
                        .timestamp(sourceEventTimestamp)
                        .eventTimestamp(sourceIngestionTimestamp.minus(1, ChronoUnit.DAYS))
                        .build(),
                TimelineElementInternal.builder()
                        .category(TimelineElementCategoryInt.SEND_ANALOG_DOMICILE)
                        .elementId("SEND_ANALOG_DOMICILE.IUN_ABC.RECINDEX_1.ATTEMPT_0")
                        .timestamp(sourceEventTimestamp)
                        .eventTimestamp(sourceIngestionTimestamp.minus(1, ChronoUnit.DAYS))
                        .build(),
                TimelineElementInternal.builder()
                        .category(TimelineElementCategoryInt.SEND_ANALOG_DOMICILE)
                        .elementId("SEND_ANALOG_DOMICILE.IUN_ABC.RECINDEX_0.ATTEMPT_1")
                        .timestamp(sourceEventTimestamp)
                        .eventTimestamp(sourceIngestionTimestamp.minus(1, ChronoUnit.DAYS))
                        .build(),
                TimelineElementInternal.builder()
                        .category(TimelineElementCategoryInt.SEND_ANALOG_DOMICILE)
                        .elementId("SEND_ANALOG_DOMICILE.IUN_ABC.RECINDEX_0.ATTEMPT_0")
                        .timestamp(sourceEventTimestamp)
                        .eventTimestamp(sourceEventTimestamp)
                        .build());

        timelineMapperBeforeFix.remapSpecificTimelineElementData(timelineElementInternalSet, notificationReworked, sourceIngestionTimestamp, false);

        Assertions.assertEquals(sourceIngestionTimestamp, notificationReworked.getIngestionTimestamp());
        Assertions.assertEquals(sourceEventTimestamp, notificationReworked.getTimestamp());
        Assertions.assertEquals(sourceEventTimestamp, notificationReworked.getEventTimestamp());
    }

    @Test
    void testMapNotificationReworkedAttempt1RecIndex1() {
        Instant sourceEventTimestamp = Instant.EPOCH;
        Instant sourceIngestionTimestamp = Instant.now();

        TimelineElementInternal notificationReworked = TimelineElementInternal.builder()
                .category(TimelineElementCategoryInt.NOTIFICATION_TIMELINE_REWORKED)
                .details(new NotificationTimelineReworkedDetailsInt())
                .elementId("NOTIFICATION_TIMELINE_REWORKED.IUN_ABC.RECINDEX_1.ATTEMPT_1.REWORK_0")
                .timestamp(sourceIngestionTimestamp)
                .notificationSentAt(Instant.now().plusSeconds(3600))
                .build();
        Set<TimelineElementInternal> timelineElementInternalSet = Set.of(
                TimelineElementInternal.builder()
                        .category(TimelineElementCategoryInt.SEND_ANALOG_DOMICILE)
                        .elementId("SEND_ANALOG_DOMICILE.IUN_ABC.RECINDEX_1.ATTEMPT_1")
                        .timestamp(sourceEventTimestamp)
                        .eventTimestamp(sourceEventTimestamp)
                        .build(),
                TimelineElementInternal.builder()
                        .category(TimelineElementCategoryInt.SEND_ANALOG_DOMICILE)
                        .elementId("SEND_ANALOG_DOMICILE.IUN_ABC.RECINDEX_1.ATTEMPT_0")
                        .timestamp(sourceEventTimestamp)
                        .eventTimestamp(sourceIngestionTimestamp.minus(1, ChronoUnit.DAYS))
                        .build(),
                TimelineElementInternal.builder()
                        .category(TimelineElementCategoryInt.SEND_ANALOG_DOMICILE)
                        .elementId("SEND_ANALOG_DOMICILE.IUN_ABC.RECINDEX_0.ATTEMPT_1")
                        .timestamp(sourceEventTimestamp)
                        .eventTimestamp(sourceIngestionTimestamp.minus(1, ChronoUnit.DAYS))
                        .build(),
                TimelineElementInternal.builder()
                        .category(TimelineElementCategoryInt.SEND_ANALOG_DOMICILE)
                        .elementId("SEND_ANALOG_DOMICILE.IUN_ABC.RECINDEX_0.ATTEMPT_0")
                        .timestamp(sourceEventTimestamp)
                        .eventTimestamp(sourceIngestionTimestamp.minus(1, ChronoUnit.DAYS))
                        .build()
        );

        timelineMapperBeforeFix.remapSpecificTimelineElementData(timelineElementInternalSet, notificationReworked, sourceIngestionTimestamp, false);

        Assertions.assertEquals(sourceIngestionTimestamp, notificationReworked.getIngestionTimestamp());
        Assertions.assertEquals(sourceEventTimestamp, notificationReworked.getTimestamp());
        Assertions.assertEquals(sourceEventTimestamp, notificationReworked.getEventTimestamp());
    }

    @Test
    void testMapScheduleRefinement() {
        Instant sourceEventTimestamp = Instant.EPOCH;
        Instant sourceIngestionTimestamp = Instant.now();

        TimelineElementInternal scheduleRefinement = TimelineElementInternal.builder()
                .category(TimelineElementCategoryInt.SCHEDULE_REFINEMENT)
                .details(ScheduleRefinementDetailsInt.builder().recIndex(0).build())
                .timestamp(sourceIngestionTimestamp)
                .notificationSentAt(Instant.now().plusSeconds(3600))
                .build();
        Set<TimelineElementInternal> timelineElementInternalSet = Set.of(
                TimelineElementInternal.builder()
                        .category(TimelineElementCategoryInt.SEND_ANALOG_FEEDBACK)
                        .timestamp(sourceEventTimestamp)
                        .details(SendAnalogFeedbackDetailsInt.builder().recIndex(0).notificationDate(sourceEventTimestamp).build())
                        .build()
        );

        timelineMapperBeforeFix.remapSpecificTimelineElementData(timelineElementInternalSet, scheduleRefinement, sourceIngestionTimestamp, false);

        Assertions.assertEquals(sourceIngestionTimestamp, scheduleRefinement.getIngestionTimestamp());
        Assertions.assertEquals(sourceEventTimestamp, scheduleRefinement.getTimestamp());
        Assertions.assertEquals(sourceEventTimestamp, scheduleRefinement.getEventTimestamp());
    }

    @Test
    void testMapAnalogSuccessWorkflow() {
        // valido anche per ANALOG_FAILURE_WORKFLOW, COMPLETELY_UNREACHABLE_CREATION_REQUEST, COMPLETELY_UNREACHABLE, ANALOG_WORKFLOW_RECIPIENT_DECEASED
        Instant sourceEventTimestamp = Instant.EPOCH;
        Instant sourceIngestionTimestamp = Instant.now();

        TimelineElementInternal analogSuccessWorkflow = TimelineElementInternal.builder()
                .category(TimelineElementCategoryInt.ANALOG_SUCCESS_WORKFLOW)
                .details(AnalogSuccessWorkflowDetailsInt.builder().recIndex(0).build())
                .timestamp(sourceIngestionTimestamp)
                .notificationSentAt(Instant.now().plusSeconds(3600))
                .build();
        Set<TimelineElementInternal> timelineElementInternalSet = Set.of(
                TimelineElementInternal.builder()
                        .category(TimelineElementCategoryInt.SEND_ANALOG_FEEDBACK)
                        .timestamp(sourceEventTimestamp)
                        .details(SendAnalogFeedbackDetailsInt.builder().recIndex(0).notificationDate(sourceEventTimestamp).build())
                        .build()
        );

        timelineMapperBeforeFix.remapSpecificTimelineElementData(timelineElementInternalSet, analogSuccessWorkflow, sourceIngestionTimestamp, false);

        Assertions.assertEquals(sourceIngestionTimestamp, analogSuccessWorkflow.getIngestionTimestamp());
        Assertions.assertEquals(sourceEventTimestamp, analogSuccessWorkflow.getTimestamp());
        Assertions.assertEquals(sourceEventTimestamp, analogSuccessWorkflow.getEventTimestamp());
    }

    @Test
    void testMapAnalogSuccessWorkflow_analogWorkflowBusinessDateNull() {
        // valido anche per ANALOG_FAILURE_WORKFLOW, COMPLETELY_UNREACHABLE_CREATION_REQUEST, COMPLETELY_UNREACHABLE, ANALOG_WORKFLOW_RECIPIENT_DECEASED
        Instant sourceEventTimestamp = Instant.EPOCH;
        Instant sourceIngestionTimestamp = Instant.now();

        TimelineElementInternal analogSuccessWorkflow = TimelineElementInternal.builder()
                .category(TimelineElementCategoryInt.ANALOG_SUCCESS_WORKFLOW)
                .details(AnalogSuccessWorkflowDetailsInt.builder().recIndex(0).build())
                .timestamp(sourceIngestionTimestamp)
                .notificationSentAt(Instant.now().plusSeconds(3600))
                .build();
        Set<TimelineElementInternal> timelineElementInternalSet = Set.of(
                TimelineElementInternal.builder()
                        .category(TimelineElementCategoryInt.SEND_ANALOG_FEEDBACK)
                        .timestamp(sourceEventTimestamp)
                        .details(SendAnalogFeedbackDetailsInt.builder().recIndex(1).build())
                        .build()
        );

        Assertions.assertThrows(PnInternalException.class, () -> timelineMapperBeforeFix.remapSpecificTimelineElementData(timelineElementInternalSet, analogSuccessWorkflow, sourceIngestionTimestamp, false));
    }

    @Test
    void testMapRefinement() {
        Instant sourceEventTimestamp = Instant.EPOCH;
        Instant sourceIngestionTimestamp = Instant.now();
        Instant schedulingDate = Instant.now().minusSeconds(3600);

        TimelineElementInternal refinement = TimelineElementInternal.builder()
                .category(TimelineElementCategoryInt.REFINEMENT)
                .details(RefinementDetailsInt.builder().recIndex(0).build())
                .timestamp(sourceIngestionTimestamp)
                .notificationSentAt(Instant.now().plusSeconds(3600))
                .build();
        Set<TimelineElementInternal> timelineElementInternalSet = Set.of(
                TimelineElementInternal.builder()
                        .category(TimelineElementCategoryInt.SCHEDULE_REFINEMENT)
                        .timestamp(sourceEventTimestamp)
                        .details(ScheduleRefinementDetailsInt.builder().recIndex(0).schedulingDate(schedulingDate).build())
                        .build()
        );

        timelineMapperBeforeFix.remapSpecificTimelineElementData(timelineElementInternalSet, refinement, sourceIngestionTimestamp, false);

        Assertions.assertEquals(sourceIngestionTimestamp, refinement.getIngestionTimestamp());
        Assertions.assertEquals(schedulingDate, refinement.getTimestamp());
        Assertions.assertEquals(schedulingDate, refinement.getEventTimestamp());
    }

    @Test
    void testMapSendDigitalDomicile() {
        Instant sourceEventTimestamp = Instant.EPOCH;
        Instant sourceIngestionTimestamp = Instant.now();

        TimelineElementInternal sendDigitalDomicile = TimelineElementInternal.builder()
                .category(TimelineElementCategoryInt.SEND_DIGITAL_DOMICILE)
                .details(SendDigitalDetailsInt.builder().
                        digitalAddress(LegalDigitalAddressInt.builder().type(LegalDigitalAddressInt.LEGAL_DIGITAL_ADDRESS_TYPE.SERCQ).build()).
                        recIndex(0).build())
                .timestamp(sourceIngestionTimestamp)
                .notificationSentAt(Instant.now().plusSeconds(3600))
                .build();
        Set<TimelineElementInternal> timelineElementInternalSet = Set.of(
                TimelineElementInternal.builder()
                        .category(TimelineElementCategoryInt.AAR_GENERATION)
                        .timestamp(sourceEventTimestamp)
                        .details(AarGenerationDetailsInt.builder().recIndex(0).build())
                        .build()
        );

        timelineMapperBeforeFix.remapSpecificTimelineElementData(timelineElementInternalSet, sendDigitalDomicile, sourceIngestionTimestamp, false);

        Assertions.assertEquals(sourceIngestionTimestamp, sendDigitalDomicile.getIngestionTimestamp());
        Assertions.assertEquals(sourceEventTimestamp, sendDigitalDomicile.getTimestamp());
        Assertions.assertEquals(sourceEventTimestamp, sendDigitalDomicile.getEventTimestamp());
    }

    @Test
    void testMapSendDigitalDomicileWithPfNewWorkflowEnabled() {
        Instant sourceEventTimestamp = Instant.EPOCH;
        Instant sourceIngestionTimestamp = Instant.now();

        TimelineElementInternal sendDigitalDomicile = TimelineElementInternal.builder()
                .category(TimelineElementCategoryInt.SEND_DIGITAL_DOMICILE)
                .details(SendDigitalDetailsInt.builder().
                        digitalAddress(LegalDigitalAddressInt.builder().type(LegalDigitalAddressInt.LEGAL_DIGITAL_ADDRESS_TYPE.SERCQ).build()).
                        recIndex(0).build())
                .timestamp(sourceIngestionTimestamp)
                .notificationSentAt(Instant.now().plusSeconds(3600))
                .build();
        Set<TimelineElementInternal> timelineElementInternalSet = Set.of(
                TimelineElementInternal.builder()
                        .category(TimelineElementCategoryInt.AAR_GENERATION)
                        .timestamp(sourceEventTimestamp.plusSeconds(3600))
                        .details(AarGenerationDetailsInt.builder().recIndex(0).build())
                        .build()
        );

        timelineMapperBeforeFix.remapSpecificTimelineElementData(timelineElementInternalSet, sendDigitalDomicile, sourceIngestionTimestamp, true);

        Assertions.assertEquals(sourceIngestionTimestamp, sendDigitalDomicile.getIngestionTimestamp());
        Assertions.assertEquals(sourceIngestionTimestamp, sendDigitalDomicile.getTimestamp());
        Assertions.assertEquals(sourceIngestionTimestamp, sendDigitalDomicile.getEventTimestamp());
    }


}
