package it.pagopa.pn.timelineservice.operations.legal;

import it.pagopa.pn.timelineservice.config.PnTimelineServiceConfigs;
import it.pagopa.pn.timelineservice.dto.address.LegalDigitalAddressInt;
import it.pagopa.pn.timelineservice.dto.timeline.TimelineElementInternal;
import it.pagopa.pn.timelineservice.dto.timeline.details.*;
import it.pagopa.pn.timelineservice.operations.common.TimelineTimestampBaseMapper;
import it.pagopa.pn.timelineservice.operations.common.TimelineTimestampMapper;
import it.pagopa.pn.timelineservice.service.mapper.TimelineMapperFactory;
import it.pagopa.pn.timelineservice.utils.FeatureEnabledUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.Instant;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;

class LegalTimelineTimestampMapperTest {
    private FeatureEnabledUtils featureEnabledUtils;
    private PnTimelineServiceConfigs pnTimelineServiceConfigs;
    private LegalTimelineTimestampMapper legalTimelineTimestampMapper;

    @BeforeEach
    void setUp() {
        // Mock dei componenti necessari, ma alcune dipendenze vengono comunque istanziate realmente per testare l'integrazione reale di LegalTimelineTimestampMapper con TimelineTimestampBaseMapper e TimelineMapperFactory
        pnTimelineServiceConfigs = mock(PnTimelineServiceConfigs.class);
        Mockito.when(pnTimelineServiceConfigs.getFeatureUnreachableRefinementPostAARStartDate()).thenReturn(Instant.now());
        featureEnabledUtils = mock(FeatureEnabledUtils.class);
        Mockito.when(featureEnabledUtils.isPfNewWorkflowEnabled(any())).thenReturn(false);
        TimelineTimestampBaseMapper timelineTimestampBaseMapper = new TimelineTimestampBaseMapper();
        legalTimelineTimestampMapper = new LegalTimelineTimestampMapper(timelineTimestampBaseMapper, featureEnabledUtils, new TimelineMapperFactory(pnTimelineServiceConfigs));
    }

    // -- INIZIO TEST CHE EFFETTUANO REMAPPING BASATI SUL SET DI ELEMENTI DI TIMELINE --
    @Test
    void testMapSendDigitalFeedbackPecNewWorkflow(){
        Instant sourceEventTimestamp = Instant.EPOCH;
        Instant sourceIngestionTimestamp = Instant.now();

        TimelineElementInternal sendDigitalFeedback = TimelineElementInternal.builder()
                .category(TimelineElementCategoryInt.SEND_DIGITAL_FEEDBACK)
                .details(SendDigitalFeedbackDetailsInt.builder()
                        .recIndex(0)
                        .digitalAddress(LegalDigitalAddressInt.builder().type(LegalDigitalAddressInt.LEGAL_DIGITAL_ADDRESS_TYPE.PEC).build())
                        .notificationDate(sourceEventTimestamp)
                        .build())
                .timestamp(sourceIngestionTimestamp)
                .notificationSentAt(Instant.now().plusSeconds(3600))
                .build();

        TimelineElementInternal sendDigitalDomiclie = TimelineElementInternal.builder()
                .category(TimelineElementCategoryInt.SEND_DIGITAL_DOMICILE)
                .details(SendDigitalDetailsInt.builder()
                        .recIndex(0)
                        .digitalAddress(LegalDigitalAddressInt.builder().type(LegalDigitalAddressInt.LEGAL_DIGITAL_ADDRESS_TYPE.PEC).build())
                        .build())
                .timestamp(sourceIngestionTimestamp)
                .notificationSentAt(Instant.now().plusSeconds(3600))
                .build();

        TimelineTimestampMapper.TimestampMapperPayload payload = new TimelineTimestampMapper.TimestampMapperPayload(sendDigitalFeedback, Set.of(sendDigitalFeedback, sendDigitalDomiclie));
        TimelineElementInternal ret = legalTimelineTimestampMapper.mapTimelineTimestamps(payload);

        Assertions.assertNotSame(ret , sendDigitalFeedback);
        Assertions.assertEquals(sourceIngestionTimestamp, ret.getIngestionTimestamp());
        Assertions.assertEquals(sourceEventTimestamp, ret.getEventTimestamp());
        Assertions.assertEquals(sourceEventTimestamp, ret.getTimestamp());
    }

    @Test
    void testMapSendDigitalFeedbackSercQOldWorkflowMapperBeforeFix(){
        Mockito.when(pnTimelineServiceConfigs.getFeatureUnreachableRefinementPostAARStartDate()).thenReturn(null);
        Instant sourceEventTimestamp = Instant.EPOCH;
        Instant sourceIngestionTimestamp = Instant.now();

        TimelineElementInternal sendDigitalFeedback = TimelineElementInternal.builder()
                .category(TimelineElementCategoryInt.SEND_DIGITAL_FEEDBACK)
                .details(SendDigitalFeedbackDetailsInt.builder()
                        .recIndex(0)
                        .digitalAddress(LegalDigitalAddressInt.builder().type(LegalDigitalAddressInt.LEGAL_DIGITAL_ADDRESS_TYPE.SERCQ).build())
                        .notificationDate(sourceEventTimestamp)
                        .build())
                .timestamp(sourceIngestionTimestamp)
                .notificationSentAt(Instant.now().plusSeconds(3600))
                .build();

        TimelineElementInternal sendDigitalDomiclie = TimelineElementInternal.builder()
                .category(TimelineElementCategoryInt.SEND_DIGITAL_DOMICILE)
                .details(SendDigitalDetailsInt.builder()
                        .recIndex(0)
                        .digitalAddress(LegalDigitalAddressInt.builder().type(LegalDigitalAddressInt.LEGAL_DIGITAL_ADDRESS_TYPE.PEC).build())
                        .build())
                .timestamp(sourceIngestionTimestamp)
                .notificationSentAt(Instant.now().plusSeconds(3600))
                .build();


        TimelineTimestampMapper.TimestampMapperPayload payload = new TimelineTimestampMapper.TimestampMapperPayload(sendDigitalFeedback, Set.of(sendDigitalFeedback, sendDigitalDomiclie));
        TimelineElementInternal ret = legalTimelineTimestampMapper.mapTimelineTimestamps(payload);

        Assertions.assertNotSame(ret , sendDigitalFeedback);
        Assertions.assertNotEquals(ret.getTimestamp(),sendDigitalFeedback.getTimestamp());
        Assertions.assertEquals(sourceIngestionTimestamp, ret.getIngestionTimestamp());
        Assertions.assertEquals(sourceEventTimestamp, ret.getEventTimestamp());
        Assertions.assertEquals(sourceEventTimestamp, ret.getTimestamp());
    }


    @Test
    void testMapSendDigitalFeedbackSercQOldWorkflow(){
        Instant sourceEventTimestamp = Instant.EPOCH;
        Instant sourceIngestionTimestamp = Instant.now();

        TimelineElementInternal sendDigitalFeedback = TimelineElementInternal.builder()
                .category(TimelineElementCategoryInt.SEND_DIGITAL_FEEDBACK)
                .details(SendDigitalFeedbackDetailsInt.builder()
                        .recIndex(0)
                        .digitalAddress(LegalDigitalAddressInt.builder().type(LegalDigitalAddressInt.LEGAL_DIGITAL_ADDRESS_TYPE.SERCQ).build())
                        .notificationDate(sourceEventTimestamp)
                        .build())
                .timestamp(sourceIngestionTimestamp)
                .notificationSentAt(Instant.now().plusSeconds(3600))
                .build();

        TimelineTimestampMapper.TimestampMapperPayload payload = new TimelineTimestampMapper.TimestampMapperPayload(sendDigitalFeedback, Set.of(sendDigitalFeedback));
        TimelineElementInternal ret = legalTimelineTimestampMapper.mapTimelineTimestamps(payload);

        Assertions.assertNotSame(ret , sendDigitalFeedback);
        Assertions.assertNotEquals(ret.getTimestamp(),sendDigitalFeedback.getTimestamp());
        Assertions.assertEquals(sourceIngestionTimestamp, ret.getIngestionTimestamp());
        Assertions.assertEquals(sourceEventTimestamp, ret.getEventTimestamp());
        Assertions.assertEquals(sourceEventTimestamp, ret.getTimestamp());
    }

    @Test
    void testMapSendDigitalFeedbackSercQNewWorkflowDomicileBeforeFeedback(){
        Mockito.when(featureEnabledUtils.isPfNewWorkflowEnabled(any())).thenReturn(true);

        Instant sourceEventTimestamp = Instant.EPOCH;
        Instant sourceIngestionTimestamp = Instant.now();
        Instant digitalDomicileTimestamp = sourceIngestionTimestamp.minusSeconds(3600);

        TimelineElementInternal sendDigitalFeedback = TimelineElementInternal.builder()
                .category(TimelineElementCategoryInt.SEND_DIGITAL_FEEDBACK)
                .details(SendDigitalFeedbackDetailsInt.builder()
                        .recIndex(0)
                        .digitalAddress(LegalDigitalAddressInt.builder().type(LegalDigitalAddressInt.LEGAL_DIGITAL_ADDRESS_TYPE.SERCQ).build())
                        .notificationDate(sourceEventTimestamp)
                        .build())
                .timestamp(sourceIngestionTimestamp)
                .notificationSentAt(Instant.now().plusSeconds(3600))
                .build();

        TimelineElementInternal sendDigitalDomiclie = TimelineElementInternal.builder()
                .category(TimelineElementCategoryInt.SEND_DIGITAL_DOMICILE)
                .details(SendDigitalDetailsInt.builder()
                        .recIndex(0)
                        .digitalAddress(LegalDigitalAddressInt.builder().type(LegalDigitalAddressInt.LEGAL_DIGITAL_ADDRESS_TYPE.SERCQ).build())
                        .build())
                .timestamp(digitalDomicileTimestamp)
                .notificationSentAt(Instant.now().plusSeconds(3600))
                .build();

        TimelineTimestampMapper.TimestampMapperPayload payload = new TimelineTimestampMapper.TimestampMapperPayload(sendDigitalFeedback, Set.of(sendDigitalFeedback, sendDigitalDomiclie));
        TimelineElementInternal feedback = legalTimelineTimestampMapper.mapTimelineTimestamps(payload);
        TimelineTimestampMapper.TimestampMapperPayload payload2 = new TimelineTimestampMapper.TimestampMapperPayload(sendDigitalDomiclie, Set.of(sendDigitalFeedback, sendDigitalDomiclie));
        TimelineElementInternal domicile = legalTimelineTimestampMapper.mapTimelineTimestamps(payload2);

        Assertions.assertEquals(sourceIngestionTimestamp, feedback.getIngestionTimestamp());
        Assertions.assertEquals(sourceIngestionTimestamp, feedback.getEventTimestamp());
        Assertions.assertEquals(sourceIngestionTimestamp, feedback.getTimestamp());

        Assertions.assertEquals(digitalDomicileTimestamp, domicile.getIngestionTimestamp());
        Assertions.assertEquals(digitalDomicileTimestamp, domicile.getEventTimestamp());
        Assertions.assertEquals(digitalDomicileTimestamp, domicile.getTimestamp());
    }

    @Test
    void testMapSendDigitalFeedbackSercQNewWorkflowDomicileAfterFeedback(){
        Mockito.when(featureEnabledUtils.isPfNewWorkflowEnabled(any())).thenReturn(true);

        Instant sourceIngestionTimestamp = Instant.now();
        Instant digitalDomicileTimestamp = sourceIngestionTimestamp.minusSeconds(3600);

        TimelineElementInternal sendDigitalFeedback = TimelineElementInternal.builder()
                .category(TimelineElementCategoryInt.SEND_DIGITAL_FEEDBACK)
                .details(SendDigitalFeedbackDetailsInt.builder()
                        .recIndex(0)
                        .digitalAddress(LegalDigitalAddressInt.builder().type(LegalDigitalAddressInt.LEGAL_DIGITAL_ADDRESS_TYPE.SERCQ).build())
                        .notificationDate(digitalDomicileTimestamp)
                        .build())
                .timestamp(digitalDomicileTimestamp)
                .notificationSentAt(Instant.now().plusSeconds(3600))
                .build();

        TimelineElementInternal sendDigitalDomiclie = TimelineElementInternal.builder()
                .category(TimelineElementCategoryInt.SEND_DIGITAL_DOMICILE)
                .details(SendDigitalDetailsInt.builder()
                        .recIndex(0)
                        .digitalAddress(LegalDigitalAddressInt.builder().type(LegalDigitalAddressInt.LEGAL_DIGITAL_ADDRESS_TYPE.SERCQ).build())
                        .build())
                .timestamp(sourceIngestionTimestamp)
                .notificationSentAt(Instant.now().plusSeconds(3600))
                .build();

        TimelineTimestampMapper.TimestampMapperPayload payload = new TimelineTimestampMapper.TimestampMapperPayload(sendDigitalFeedback, Set.of(sendDigitalFeedback, sendDigitalDomiclie));
        TimelineElementInternal feedback = legalTimelineTimestampMapper.mapTimelineTimestamps(payload);
        TimelineTimestampMapper.TimestampMapperPayload payload2 = new TimelineTimestampMapper.TimestampMapperPayload(sendDigitalDomiclie, Set.of(sendDigitalFeedback, sendDigitalDomiclie));
        TimelineElementInternal domicile = legalTimelineTimestampMapper.mapTimelineTimestamps(payload2);

        Assertions.assertEquals(digitalDomicileTimestamp, feedback.getIngestionTimestamp());
        Assertions.assertEquals(sourceIngestionTimestamp, feedback.getEventTimestamp());
        Assertions.assertEquals(sourceIngestionTimestamp, feedback.getTimestamp());

        Assertions.assertEquals(sourceIngestionTimestamp, domicile.getIngestionTimestamp());
        Assertions.assertEquals(digitalDomicileTimestamp, domicile.getEventTimestamp());
        Assertions.assertEquals(digitalDomicileTimestamp, domicile.getTimestamp());
    }

    @Test
    void testMapSendDigitalFeedbackSercQNewWorkflowDomicileAfterFeedbackPec(){
        Mockito.when(featureEnabledUtils.isPfNewWorkflowEnabled(any())).thenReturn(true);

        Instant sourceIngestionTimestamp = Instant.now();
        Instant digitalDomicileTimestamp = sourceIngestionTimestamp.minusSeconds(3600);

        TimelineElementInternal sendDigitalFeedback = TimelineElementInternal.builder()
                .category(TimelineElementCategoryInt.SEND_DIGITAL_FEEDBACK)
                .details(SendDigitalFeedbackDetailsInt.builder()
                        .recIndex(0)
                        .digitalAddress(LegalDigitalAddressInt.builder().type(LegalDigitalAddressInt.LEGAL_DIGITAL_ADDRESS_TYPE.SERCQ).build())
                        .notificationDate(digitalDomicileTimestamp)
                        .build())
                .timestamp(digitalDomicileTimestamp)
                .notificationSentAt(Instant.now().plusSeconds(3600))
                .build();

        TimelineElementInternal sendDigitalDomiclie = TimelineElementInternal.builder()
                .category(TimelineElementCategoryInt.SEND_DIGITAL_DOMICILE)
                .details(SendDigitalDetailsInt.builder()
                        .recIndex(0)
                        .digitalAddress(LegalDigitalAddressInt.builder().type(LegalDigitalAddressInt.LEGAL_DIGITAL_ADDRESS_TYPE.PEC).build())
                        .build())
                .timestamp(sourceIngestionTimestamp)
                .notificationSentAt(Instant.now().plusSeconds(3600))
                .build();

        TimelineTimestampMapper.TimestampMapperPayload payload = new TimelineTimestampMapper.TimestampMapperPayload(sendDigitalFeedback, Set.of(sendDigitalFeedback, sendDigitalDomiclie));
        TimelineElementInternal feedback = legalTimelineTimestampMapper.mapTimelineTimestamps(payload);
        TimelineTimestampMapper.TimestampMapperPayload payload2 = new TimelineTimestampMapper.TimestampMapperPayload(sendDigitalDomiclie, Set.of(sendDigitalFeedback, sendDigitalDomiclie));
        TimelineElementInternal domicile = legalTimelineTimestampMapper.mapTimelineTimestamps(payload2);

        Assertions.assertEquals(digitalDomicileTimestamp, feedback.getIngestionTimestamp());
        Assertions.assertEquals(digitalDomicileTimestamp, feedback.getEventTimestamp());
        Assertions.assertEquals(digitalDomicileTimestamp, feedback.getTimestamp());

        Assertions.assertEquals(sourceIngestionTimestamp, domicile.getIngestionTimestamp());
        Assertions.assertEquals(sourceIngestionTimestamp, domicile.getEventTimestamp());
        Assertions.assertEquals(sourceIngestionTimestamp, domicile.getTimestamp());
    }


    @Test
    void testMapSendDigitalFeedbackSercQNewWorkflow(){
        Mockito.when(featureEnabledUtils.isPfNewWorkflowEnabled(any())).thenReturn(true);

        Instant sourceEventTimestamp = Instant.EPOCH;
        Instant sourceIngestionTimestamp = Instant.now();

        TimelineElementInternal sendDigitalFeedback = TimelineElementInternal.builder()
                .category(TimelineElementCategoryInt.SEND_DIGITAL_FEEDBACK)
                .details(SendDigitalFeedbackDetailsInt.builder()
                        .recIndex(0)
                        .digitalAddress(LegalDigitalAddressInt.builder().type(LegalDigitalAddressInt.LEGAL_DIGITAL_ADDRESS_TYPE.SERCQ).build())
                        .notificationDate(sourceEventTimestamp)
                        .build())
                .timestamp(sourceIngestionTimestamp)
                .notificationSentAt(Instant.now().plusSeconds(3600))
                .build();

        TimelineTimestampMapper.TimestampMapperPayload payload = new TimelineTimestampMapper.TimestampMapperPayload(sendDigitalFeedback, Set.of(sendDigitalFeedback));
        TimelineElementInternal ret = legalTimelineTimestampMapper.mapTimelineTimestamps(payload);

        Assertions.assertNotSame(ret , sendDigitalFeedback);
        Assertions.assertEquals(sourceIngestionTimestamp, ret.getIngestionTimestamp());
        Assertions.assertEquals(sourceIngestionTimestamp, ret.getEventTimestamp());
        Assertions.assertEquals(sourceIngestionTimestamp, ret.getTimestamp());
    }

    @Test
    void testMapSendDigitalFeedbackSercQNewWorkflowDomicileBeforeFeedbackMapperBeforeFix(){
        Mockito.when(featureEnabledUtils.isPfNewWorkflowEnabled(any())).thenReturn(true);
        Mockito.when(pnTimelineServiceConfigs.getFeatureUnreachableRefinementPostAARStartDate()).thenReturn(null);

        Instant sourceEventTimestamp = Instant.EPOCH;
        Instant sourceIngestionTimestamp = Instant.now();
        Instant digitalDomicileTimestamp = sourceIngestionTimestamp.minusSeconds(3600);

        TimelineElementInternal sendDigitalFeedback = TimelineElementInternal.builder()
                .category(TimelineElementCategoryInt.SEND_DIGITAL_FEEDBACK)
                .details(SendDigitalFeedbackDetailsInt.builder()
                        .recIndex(0)
                        .digitalAddress(LegalDigitalAddressInt.builder().type(LegalDigitalAddressInt.LEGAL_DIGITAL_ADDRESS_TYPE.SERCQ).build())
                        .notificationDate(sourceEventTimestamp)
                        .build())
                .timestamp(sourceIngestionTimestamp)
                .notificationSentAt(Instant.now().plusSeconds(3600))
                .build();

        TimelineElementInternal sendDigitalDomiclie = TimelineElementInternal.builder()
                .category(TimelineElementCategoryInt.SEND_DIGITAL_DOMICILE)
                .details(SendDigitalDetailsInt.builder()
                        .recIndex(0)
                        .digitalAddress(LegalDigitalAddressInt.builder().type(LegalDigitalAddressInt.LEGAL_DIGITAL_ADDRESS_TYPE.SERCQ).build())
                        .build())
                .timestamp(digitalDomicileTimestamp)
                .notificationSentAt(Instant.now().plusSeconds(3600))
                .build();

        TimelineTimestampMapper.TimestampMapperPayload payload = new TimelineTimestampMapper.TimestampMapperPayload(sendDigitalFeedback, Set.of(sendDigitalFeedback, sendDigitalDomiclie));
        TimelineElementInternal feedback = legalTimelineTimestampMapper.mapTimelineTimestamps(payload);
        TimelineTimestampMapper.TimestampMapperPayload payload2 = new TimelineTimestampMapper.TimestampMapperPayload(sendDigitalDomiclie, Set.of(sendDigitalFeedback, sendDigitalDomiclie));
        TimelineElementInternal domicile = legalTimelineTimestampMapper.mapTimelineTimestamps(payload2);

        Assertions.assertEquals(sourceIngestionTimestamp, feedback.getIngestionTimestamp());
        Assertions.assertEquals(sourceIngestionTimestamp, feedback.getEventTimestamp());
        Assertions.assertEquals(sourceIngestionTimestamp, feedback.getTimestamp());

        Assertions.assertEquals(digitalDomicileTimestamp, domicile.getIngestionTimestamp());
        Assertions.assertEquals(digitalDomicileTimestamp, domicile.getEventTimestamp());
        Assertions.assertEquals(digitalDomicileTimestamp, domicile.getTimestamp());
    }

    @Test
    void testMapSendDigitalFeedbackSercQNewWorkflowDomicileAfterFeedbackMapperBeforeFix(){
        Mockito.when(featureEnabledUtils.isPfNewWorkflowEnabled(any())).thenReturn(true);
        Mockito.when(pnTimelineServiceConfigs.getFeatureUnreachableRefinementPostAARStartDate()).thenReturn(null);

        Instant sourceIngestionTimestamp = Instant.now();
        Instant digitalDomicileTimestamp = sourceIngestionTimestamp.minusSeconds(3600);

        TimelineElementInternal sendDigitalFeedback = TimelineElementInternal.builder()
                .category(TimelineElementCategoryInt.SEND_DIGITAL_FEEDBACK)
                .details(SendDigitalFeedbackDetailsInt.builder()
                        .recIndex(0)
                        .digitalAddress(LegalDigitalAddressInt.builder().type(LegalDigitalAddressInt.LEGAL_DIGITAL_ADDRESS_TYPE.SERCQ).build())
                        .notificationDate(digitalDomicileTimestamp)
                        .build())
                .timestamp(digitalDomicileTimestamp)
                .notificationSentAt(Instant.now().plusSeconds(3600))
                .build();

        TimelineElementInternal sendDigitalDomiclie = TimelineElementInternal.builder()
                .category(TimelineElementCategoryInt.SEND_DIGITAL_DOMICILE)
                .details(SendDigitalDetailsInt.builder()
                        .recIndex(0)
                        .digitalAddress(LegalDigitalAddressInt.builder().type(LegalDigitalAddressInt.LEGAL_DIGITAL_ADDRESS_TYPE.SERCQ).build())
                        .build())
                .timestamp(sourceIngestionTimestamp)
                .notificationSentAt(Instant.now().plusSeconds(3600))
                .build();

        TimelineTimestampMapper.TimestampMapperPayload payload = new TimelineTimestampMapper.TimestampMapperPayload(sendDigitalFeedback, Set.of(sendDigitalFeedback, sendDigitalDomiclie));
        TimelineElementInternal feedback = legalTimelineTimestampMapper.mapTimelineTimestamps(payload);
        TimelineTimestampMapper.TimestampMapperPayload payload2 = new TimelineTimestampMapper.TimestampMapperPayload(sendDigitalDomiclie, Set.of(sendDigitalFeedback, sendDigitalDomiclie));
        TimelineElementInternal domicile = legalTimelineTimestampMapper.mapTimelineTimestamps(payload2);

        Assertions.assertEquals(digitalDomicileTimestamp, feedback.getIngestionTimestamp());
        Assertions.assertEquals(sourceIngestionTimestamp, feedback.getEventTimestamp());
        Assertions.assertEquals(sourceIngestionTimestamp, feedback.getTimestamp());

        Assertions.assertEquals(sourceIngestionTimestamp, domicile.getIngestionTimestamp());
        Assertions.assertEquals(digitalDomicileTimestamp, domicile.getEventTimestamp());
        Assertions.assertEquals(digitalDomicileTimestamp, domicile.getTimestamp());
    }

    @Test
    void testMapSendDigitalFeedbackSercQNewWorkflowMapperBeforeFix(){
        Mockito.when(featureEnabledUtils.isPfNewWorkflowEnabled(any())).thenReturn(true);
        Mockito.when(pnTimelineServiceConfigs.getFeatureUnreachableRefinementPostAARStartDate()).thenReturn(null);

        Instant sourceEventTimestamp = Instant.EPOCH;
        Instant sourceIngestionTimestamp = Instant.now();

        TimelineElementInternal sendDigitalFeedback = TimelineElementInternal.builder()
                .category(TimelineElementCategoryInt.SEND_DIGITAL_FEEDBACK)
                .details(SendDigitalFeedbackDetailsInt.builder()
                        .recIndex(0)
                        .digitalAddress(LegalDigitalAddressInt.builder().type(LegalDigitalAddressInt.LEGAL_DIGITAL_ADDRESS_TYPE.SERCQ).build())
                        .notificationDate(sourceEventTimestamp)
                        .build())
                .timestamp(sourceIngestionTimestamp)
                .notificationSentAt(Instant.now().plusSeconds(3600))
                .build();

        TimelineTimestampMapper.TimestampMapperPayload payload = new TimelineTimestampMapper.TimestampMapperPayload(sendDigitalFeedback, Set.of(sendDigitalFeedback));
        TimelineElementInternal ret = legalTimelineTimestampMapper.mapTimelineTimestamps(payload);

        Assertions.assertNotSame(ret , sendDigitalFeedback);
        Assertions.assertEquals(sourceIngestionTimestamp, ret.getIngestionTimestamp());
        Assertions.assertEquals(sourceIngestionTimestamp, ret.getEventTimestamp());
        Assertions.assertEquals(sourceIngestionTimestamp, ret.getTimestamp());
    }

    @Test
    void testMapSendAnalogProgress(){
        Instant sourceEventTimestamp = Instant.EPOCH;
        Instant sourceIngestionTimestamp = Instant.now();

        TimelineElementInternal sendAnalogProgress = TimelineElementInternal.builder()
                .category(TimelineElementCategoryInt.SEND_ANALOG_PROGRESS)
                .elementId("elementid")
                .iun("iun")
                .timestamp(sourceIngestionTimestamp)
                .details( SendAnalogProgressDetailsInt.builder()
                        .recIndex(0)
                        .notificationDate(sourceEventTimestamp)
                        .build())
                .build();

        TimelineTimestampMapper.TimestampMapperPayload payload = new TimelineTimestampMapper.TimestampMapperPayload(sendAnalogProgress, Set.of(sendAnalogProgress));
        TimelineElementInternal ret = legalTimelineTimestampMapper.mapTimelineTimestamps(payload);

        Assertions.assertNotSame(ret , sendAnalogProgress);
        Assertions.assertNotEquals(ret.getTimestamp(),sendAnalogProgress.getTimestamp());
        Assertions.assertEquals(sourceIngestionTimestamp, ret.getIngestionTimestamp());
        Assertions.assertEquals(sourceEventTimestamp, ret.getEventTimestamp());
        Assertions.assertEquals(sourceEventTimestamp, ret.getTimestamp());
    }

    @Test
    void testMapSendAnalogFeedback(){
        Instant sourceEventTimestamp = Instant.EPOCH;
        Instant sourceIngestionTimestamp = Instant.now();

        TimelineElementInternal sendAnalogFeedback = TimelineElementInternal.builder()
                .category(TimelineElementCategoryInt.SEND_ANALOG_FEEDBACK)
                .elementId("elementid")
                .iun("iun")
                .timestamp(sourceIngestionTimestamp)
                .details( SendAnalogFeedbackDetailsInt.builder()
                        .recIndex(0)
                        .notificationDate(sourceEventTimestamp)
                        .build())
                .build();

        TimelineTimestampMapper.TimestampMapperPayload payload = new TimelineTimestampMapper.TimestampMapperPayload(sendAnalogFeedback, Set.of(sendAnalogFeedback));
        TimelineElementInternal ret = legalTimelineTimestampMapper.mapTimelineTimestamps(payload);

        Assertions.assertNotSame(ret , sendAnalogFeedback);
        Assertions.assertNotEquals(ret.getTimestamp(),sendAnalogFeedback.getTimestamp());
        Assertions.assertEquals(sourceIngestionTimestamp, ret.getIngestionTimestamp());
        Assertions.assertEquals(sourceEventTimestamp, ret.getEventTimestamp());
        Assertions.assertEquals(sourceEventTimestamp, ret.getTimestamp());
    }

    @Test
    void testMapScheduleRefinement(){
        Instant refinementTimestamp = Instant.EPOCH.plusMillis(100);
        Instant scheduleRefinementTimestamp = Instant.EPOCH.plusMillis(500);

        Instant eventTimestamp = Instant.EPOCH.plusMillis(10);


        TimelineElementInternal refinementElement = TimelineElementInternal.builder()
                .category(TimelineElementCategoryInt.REFINEMENT)
                .elementId("elementid")
                .iun("iun")
                .timestamp(refinementTimestamp)
                .details( RefinementDetailsInt.builder()
                        .recIndex(0)
                        .build())
                .build();

        TimelineElementInternal scheduleRefinementElement = TimelineElementInternal.builder()
                .category(TimelineElementCategoryInt.SCHEDULE_REFINEMENT)
                .elementId("elementid")
                .iun("iun")
                .timestamp(Instant.now())
                .details( ScheduleRefinementDetailsInt.builder()
                        .recIndex(0)
                        .schedulingDate(scheduleRefinementTimestamp)
                        .build())
                .build();

        TimelineTimestampMapper.TimestampMapperPayload payload = new TimelineTimestampMapper.TimestampMapperPayload(refinementElement, Set.of(scheduleRefinementElement));
        TimelineElementInternal ret = legalTimelineTimestampMapper.mapTimelineTimestamps(payload);

        Assertions.assertNotSame(ret , refinementElement);
        Assertions.assertNotEquals(ret.getTimestamp(),refinementElement.getTimestamp());
        Assertions.assertNotEquals(refinementTimestamp, ret.getTimestamp());
        Assertions.assertNotEquals(eventTimestamp, ret.getTimestamp());
        Assertions.assertEquals(scheduleRefinementTimestamp, ret.getTimestamp());
    }

    // -- FINE TEST CHE EFFETTUANO REMAPPING BASATI SUL SET DI ELEMENTI DI TIMELINE --
    @Test
    void testMapTimelineInternalMapTimelineInternaNotificationView(){
        Instant notificationViewedTimestamp = Instant.EPOCH.plusMillis(100);
        Instant eventTimestamp = Instant.EPOCH.plusMillis(10);


        TimelineElementInternal notificationViewedElement = TimelineElementInternal.builder()
                .category(TimelineElementCategoryInt.NOTIFICATION_VIEWED)
                .elementId("elementid")
                .iun("iun")
                .timestamp(notificationViewedTimestamp)
                .details( NotificationViewedDetailsInt.builder()
                        .recIndex(0)
                        .eventTimestamp(eventTimestamp)
                        .build())
                .build();

        TimelineTimestampMapper.TimestampMapperPayload payload = new TimelineTimestampMapper.TimestampMapperPayload(notificationViewedElement, Set.of(notificationViewedElement));
        TimelineElementInternal ret = legalTimelineTimestampMapper.mapTimelineTimestamps(payload);

        Assertions.assertNotSame(ret , notificationViewedElement);
        Assertions.assertEquals(eventTimestamp, ret.getTimestamp());
    }
}