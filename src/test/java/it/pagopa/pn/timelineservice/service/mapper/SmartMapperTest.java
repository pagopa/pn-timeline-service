package it.pagopa.pn.timelineservice.service.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.pagopa.pn.timelineservice.config.PnTimelineServiceConfigs;
import it.pagopa.pn.timelineservice.dto.address.DigitalAddressSourceInt;
import it.pagopa.pn.timelineservice.dto.address.LegalDigitalAddressInt;
import it.pagopa.pn.timelineservice.dto.address.PhysicalAddressInt;
import it.pagopa.pn.timelineservice.dto.timeline.TimelineElementInternal;
import it.pagopa.pn.timelineservice.dto.timeline.details.*;
import it.pagopa.pn.timelineservice.generated.openapi.server.v1.dto.*;
import it.pagopa.pn.timelineservice.utils.FeatureEnabledUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.Instant;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;

class SmartMapperTest {

    private SmartMapper smartMapper;
    private FeatureEnabledUtils featureEnabledUtils;
    private PnTimelineServiceConfigs pnTimelineServiceConfigs;


    @BeforeEach
    void setUp() {
        pnTimelineServiceConfigs = mock(PnTimelineServiceConfigs.class);
        Mockito.when(pnTimelineServiceConfigs.getFeatureUnreachableRefinementPostAARStartDate()).thenReturn(Instant.now());
        featureEnabledUtils = mock(FeatureEnabledUtils.class);
        ObjectMapper objectMapper = new ObjectMapper();
        smartMapper = new SmartMapper(new TimelineMapperFactory(pnTimelineServiceConfigs), objectMapper, featureEnabledUtils);
        Mockito.when(featureEnabledUtils.isPfNewWorkflowEnabled(any())).thenReturn(false);
    }

    @Test
    void fromInternalToExternalSendDigitalDetails() {
        SendDigitalDetailsInt sendDigitalDetails = SendDigitalDetailsInt.builder()
                .recIndex(0)
                .digitalAddressSource(DigitalAddressSourceInt.PLATFORM)
                .digitalAddress(LegalDigitalAddressInt.builder()
                        .type(LegalDigitalAddressInt.LEGAL_DIGITAL_ADDRESS_TYPE.PEC)
                        .address("testAddress@gmail.com")
                        .build())
                .retryNumber(0)
                .categoryType("SEND_DIGITAL_DOMICILE")
                .downstreamId(DownstreamIdInt.builder()
                        .messageId("messageId")
                        .systemId("systemId")
                        .build())
                .build();

        var details = smartMapper.mapToClassWithObjectMapper(sendDigitalDetails, TimelineElementDetails.class);
        var sendDigitalDetailsExt = (SendDigitalDetails) details;
        Assertions.assertEquals(sendDigitalDetails.getRecIndex(),  sendDigitalDetailsExt.getRecIndex());
        Assertions.assertEquals(sendDigitalDetails.getDigitalAddress().getAddress(), sendDigitalDetailsExt.getDigitalAddress().getAddress() );
    }

    @Test
    void fromExternalToInternalSendDigitalDetails() {
        var timelineElementDetails = new SendDigitalDetails()
                .recIndex(0)
                .digitalAddressSource(DigitalAddressSource.PLATFORM)
                .digitalAddress(new DigitalAddress()
                        .type("PEC")
                        .address("testAddress@gmail.com"))
                .retryNumber(0);

        SendDigitalDetailsInt details = SmartMapper.mapToClass(timelineElementDetails, SendDigitalDetailsInt.class);

        Assertions.assertEquals(timelineElementDetails.getRecIndex(), details.getRecIndex());
        Assertions.assertEquals(timelineElementDetails.getDigitalAddress().getAddress(), details.getDigitalAddress().getAddress() );
    }

    @Test
    void fromInternalToPrepareAnalogDomicileFailureDetails() {
        PrepareAnalogDomicileFailureDetailsInt sendDigitalDetails = PrepareAnalogDomicileFailureDetailsInt.builder()
                .recIndex(0)
                .foundAddress(PhysicalAddressInt.builder()
                        .foreignState("ITALIA")
                        .zip("30000")
                        .address("Via casa mia")
                        .province("MI")
                        .build())
                .build();

        var details = SmartMapper.mapToClass(sendDigitalDetails, PrepareAnalogDomicileFailureDetails.class);

        Assertions.assertEquals(sendDigitalDetails.getRecIndex(), details.getRecIndex());
        Assertions.assertEquals(sendDigitalDetails.getFoundAddress().getAddress(), details.getFoundAddress().getAddress() );
    }

    @Test
    void testTimelineElementInternalMappingTransformer(){
        Instant elementTimestamp = Instant.EPOCH.plusMillis(100);

        Instant eventTimestamp = Instant.EPOCH.plusMillis(10);

        TimelineElementInternal source = TimelineElementInternal.builder()
                .elementId("elementid")
                .iun("iun")
                .timestamp(elementTimestamp)
                .details(SendDigitalFeedbackDetailsInt.builder()
                        .notificationDate(eventTimestamp)
                        .build())
                .build();

        TimelineElementInternal ret = SmartMapper.mapToClass(source, TimelineElementInternal.class);

        Assertions.assertNotSame(ret, source);
        Assertions.assertEquals(eventTimestamp, ret.getTimestamp());
    }


    @Test
    void testTimelineElementInternalMappingTransformerNo1(){
        Instant elementTimestamp = Instant.EPOCH.plusMillis(100);

        TimelineElementInternal source = TimelineElementInternal.builder()
                .elementId("elementid")
                .iun("iun")
                .timestamp(elementTimestamp)
                .details(SendDigitalFeedbackDetailsInt.builder()
                        .notificationDate(null)
                        .build())
                .build();

        TimelineElementInternal ret = SmartMapper.mapToClass(source, TimelineElementInternal.class);


        Assertions.assertEquals(elementTimestamp, ret.getTimestamp());
    }

    @Test
    void testTimelineElementInternalMappingTransformerNo2(){
        Instant elementTimestamp = Instant.EPOCH.plusMillis(100);

        TimelineElementInternal source = TimelineElementInternal.builder()
                .elementId("elementid")
                .iun("iun")
                .timestamp(elementTimestamp)
                .details(AarGenerationDetailsInt.builder()
                        .build())
                .build();

        TimelineElementInternal ret = SmartMapper.mapToClass(source, TimelineElementInternal.class);


        Assertions.assertEquals(elementTimestamp, ret.getTimestamp());
    }

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

        TimelineElementInternal ret = smartMapper.mapTimelineInternal(sendDigitalFeedback, Set.of(sendDigitalFeedback, sendDigitalDomiclie));

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


        TimelineElementInternal ret = smartMapper.mapTimelineInternal(sendDigitalFeedback, Set.of(sendDigitalFeedback, sendDigitalDomiclie));

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


        TimelineElementInternal ret = smartMapper.mapTimelineInternal(sendDigitalFeedback, Set.of(sendDigitalFeedback));

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


        TimelineElementInternal feedback = smartMapper.mapTimelineInternal(sendDigitalFeedback, Set.of(sendDigitalFeedback, sendDigitalDomiclie));
        TimelineElementInternal domiclie = smartMapper.mapTimelineInternal(sendDigitalDomiclie, Set.of(sendDigitalFeedback, sendDigitalDomiclie));

        Assertions.assertEquals(sourceIngestionTimestamp, feedback.getIngestionTimestamp());
        Assertions.assertEquals(sourceIngestionTimestamp, feedback.getEventTimestamp());
        Assertions.assertEquals(sourceIngestionTimestamp, feedback.getTimestamp());

        Assertions.assertEquals(digitalDomicileTimestamp, domiclie.getIngestionTimestamp());
        Assertions.assertEquals(digitalDomicileTimestamp, domiclie.getEventTimestamp());
        Assertions.assertEquals(digitalDomicileTimestamp, domiclie.getTimestamp());
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


        TimelineElementInternal feedback = smartMapper.mapTimelineInternal(sendDigitalFeedback, Set.of(sendDigitalFeedback, sendDigitalDomiclie));
        TimelineElementInternal domiclie = smartMapper.mapTimelineInternal(sendDigitalDomiclie, Set.of(sendDigitalFeedback, sendDigitalDomiclie));

        Assertions.assertEquals(digitalDomicileTimestamp, feedback.getIngestionTimestamp());
        Assertions.assertEquals(sourceIngestionTimestamp, feedback.getEventTimestamp());
        Assertions.assertEquals(sourceIngestionTimestamp, feedback.getTimestamp());

        Assertions.assertEquals(sourceIngestionTimestamp, domiclie.getIngestionTimestamp());
        Assertions.assertEquals(digitalDomicileTimestamp, domiclie.getEventTimestamp());
        Assertions.assertEquals(digitalDomicileTimestamp, domiclie.getTimestamp());
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


        TimelineElementInternal feedback = smartMapper.mapTimelineInternal(sendDigitalFeedback, Set.of(sendDigitalFeedback, sendDigitalDomiclie));
        TimelineElementInternal domiclie = smartMapper.mapTimelineInternal(sendDigitalDomiclie, Set.of(sendDigitalFeedback, sendDigitalDomiclie));

        Assertions.assertEquals(digitalDomicileTimestamp, feedback.getIngestionTimestamp());
        Assertions.assertEquals(digitalDomicileTimestamp, feedback.getEventTimestamp());
        Assertions.assertEquals(digitalDomicileTimestamp, feedback.getTimestamp());

        Assertions.assertEquals(sourceIngestionTimestamp, domiclie.getIngestionTimestamp());
        Assertions.assertEquals(sourceIngestionTimestamp, domiclie.getEventTimestamp());
        Assertions.assertEquals(sourceIngestionTimestamp, domiclie.getTimestamp());
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

        TimelineElementInternal ret = smartMapper.mapTimelineInternal(sendDigitalFeedback, Set.of(sendDigitalFeedback));

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


        TimelineElementInternal feedback = smartMapper.mapTimelineInternal(sendDigitalFeedback, Set.of(sendDigitalFeedback, sendDigitalDomiclie));
        TimelineElementInternal domiclie = smartMapper.mapTimelineInternal(sendDigitalDomiclie, Set.of(sendDigitalFeedback, sendDigitalDomiclie));

        Assertions.assertEquals(sourceIngestionTimestamp, feedback.getIngestionTimestamp());
        Assertions.assertEquals(sourceIngestionTimestamp, feedback.getEventTimestamp());
        Assertions.assertEquals(sourceIngestionTimestamp, feedback.getTimestamp());

        Assertions.assertEquals(digitalDomicileTimestamp, domiclie.getIngestionTimestamp());
        Assertions.assertEquals(digitalDomicileTimestamp, domiclie.getEventTimestamp());
        Assertions.assertEquals(digitalDomicileTimestamp, domiclie.getTimestamp());
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


        TimelineElementInternal feedback = smartMapper.mapTimelineInternal(sendDigitalFeedback, Set.of(sendDigitalFeedback, sendDigitalDomiclie));
        TimelineElementInternal domiclie = smartMapper.mapTimelineInternal(sendDigitalDomiclie, Set.of(sendDigitalFeedback, sendDigitalDomiclie));

        Assertions.assertEquals(digitalDomicileTimestamp, feedback.getIngestionTimestamp());
        Assertions.assertEquals(sourceIngestionTimestamp, feedback.getEventTimestamp());
        Assertions.assertEquals(sourceIngestionTimestamp, feedback.getTimestamp());

        Assertions.assertEquals(sourceIngestionTimestamp, domiclie.getIngestionTimestamp());
        Assertions.assertEquals(digitalDomicileTimestamp, domiclie.getEventTimestamp());
        Assertions.assertEquals(digitalDomicileTimestamp, domiclie.getTimestamp());
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

        TimelineElementInternal ret = smartMapper.mapTimelineInternal(sendDigitalFeedback, Set.of(sendDigitalFeedback));

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


        TimelineElementInternal ret = smartMapper.mapTimelineInternal(sendAnalogProgress, Set.of(sendAnalogProgress));

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

        TimelineElementInternal ret = smartMapper.mapTimelineInternal(sendAnalogFeedback, Set.of(sendAnalogFeedback));

        Assertions.assertNotSame(ret , sendAnalogFeedback);
        Assertions.assertNotEquals(ret.getTimestamp(),sendAnalogFeedback.getTimestamp());
        Assertions.assertEquals(sourceIngestionTimestamp, ret.getIngestionTimestamp());
        Assertions.assertEquals(sourceEventTimestamp, ret.getEventTimestamp());
        Assertions.assertEquals(sourceEventTimestamp, ret.getTimestamp());
    }

    @Test
    void testMapTimelineInternalTransformer(){
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

        TimelineElementInternal ret = smartMapper.mapTimelineInternal(refinementElement, Set.of(scheduleRefinementElement));

        Assertions.assertNotSame(ret , refinementElement);
        Assertions.assertNotEquals(ret.getTimestamp(),refinementElement.getTimestamp());
        Assertions.assertNotEquals(refinementTimestamp, ret.getTimestamp());
        Assertions.assertNotEquals(eventTimestamp, ret.getTimestamp());
        Assertions.assertEquals(scheduleRefinementTimestamp, ret.getTimestamp());
    }


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


        TimelineElementInternal ret = smartMapper.mapTimelineInternal(notificationViewedElement, Set.of(notificationViewedElement));

        Assertions.assertNotSame(ret , notificationViewedElement);
        Assertions.assertEquals(eventTimestamp, ret.getTimestamp());
    }

    @Test
    void testMapTimelineInternalNotificationViewNotificationSentAtNotNull(){
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
                .notificationSentAt(Instant.now().plusSeconds(3600))
                .build();


        TimelineElementInternal ret = smartMapper.mapTimelineInternal(notificationViewedElement, Set.of(notificationViewedElement));

        Assertions.assertNotSame(ret , notificationViewedElement);
        Assertions.assertEquals(eventTimestamp, ret.getTimestamp());
    }


}