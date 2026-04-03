package it.pagopa.pn.timelineservice.utils.extraction.mapper;

import it.pagopa.pn.timelineservice.dto.timeline.details.ExtendedDeliveryModeInt;
import it.pagopa.pn.timelineservice.generated.openapi.server.v1.dto.DeliveryInformationResponse;
import it.pagopa.pn.timelineservice.generated.openapi.server.v1.dto.ExtendedDeliveryMode;
import it.pagopa.pn.timelineservice.utils.extraction.extractor.*;
import it.pagopa.pn.timelineservice.utils.extraction.model.ExtractionResult;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class DeliveryInfoMapperTest {

    @Test
    void mapShouldReturnCorrectDeliveryInformationResponse() {
        Integer recIndex = 1;
        DeliveryInfoMapper mapper = new DeliveryInfoMapper(recIndex);

        OffsetDateTime schedulingAnalogDate = OffsetDateTime.now();
        OffsetDateTime refinementDate = OffsetDateTime.now().plusDays(1);
        OffsetDateTime viewDate = OffsetDateTime.now().plusDays(2);
        Instant lowestDate = refinementDate.toInstant().isBefore(viewDate.toInstant()) ? refinementDate.toInstant() : viewDate.toInstant();

        RefinementOrViewDateExtractor.Result resultObj = new RefinementOrViewDateExtractor.Result(lowestDate, refinementDate.toInstant(), viewDate.toInstant());
        ExtractionResult result = new ExtractionResult(Map.of(
                SchedulingAnalogDateExtractor.KEY, Optional.of(schedulingAnalogDate.toInstant()),
                IsCancelledExtractor.KEY, Optional.of(true),
                RefinementOrViewDateExtractor.KEY, Optional.of(resultObj),
                DeliveryModeExtractor.KEY, Optional.of(ExtendedDeliveryModeInt.DIGITAL),
                IsAcceptedExtractor.KEY, Optional.of(false)
        ));

        DeliveryInformationResponse response = mapper.map(result);

        assertEquals(schedulingAnalogDate.toInstant(), response.getSchedulingAnalogDate());
        assertTrue(response.getIsNotificationCancelled());
        assertEquals(lowestDate, response.getRefinementOrViewedDate());
        assertEquals(ExtendedDeliveryMode.DIGITAL, response.getDeliveryMode());

        assertNotNull(response.getRefinementOrViewedDateDetail());
        assertEquals(refinementDate.toInstant(), response.getRefinementOrViewedDateDetail().getRefinementDate());
        assertEquals(viewDate.toInstant(), response.getRefinementOrViewedDateDetail().getViewedDate());
    }

    @Test
    void mapShouldHandleNullValues() {
        Integer recIndex = 2;
        DeliveryInfoMapper mapper = new DeliveryInfoMapper(recIndex);

        ExtractionResult result = new ExtractionResult(Map.of(
                SchedulingAnalogDateExtractor.KEY, Optional.empty(),
                IsCancelledExtractor.KEY, Optional.empty(),
                RefinementOrViewDateExtractor.KEY, Optional.empty(),
                DeliveryModeExtractor.KEY, Optional.empty(),
                IsAcceptedExtractor.KEY, Optional.empty()
        ));

        DeliveryInformationResponse response = mapper.map(result);

        assertNull(response.getSchedulingAnalogDate());
        assertFalse(response.getIsNotificationCancelled());
        assertNull(response.getRefinementOrViewedDate());
        assertNull(response.getDeliveryMode());
        assertFalse(response.getIsNotificationAccepted());
        assertNull(response.getRefinementOrViewedDateDetail());
    }
}
