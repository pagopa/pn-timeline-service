package it.pagopa.pn.timelineservice.utils.extraction.mapper;

import it.pagopa.pn.timelineservice.dto.timeline.details.DeliveryModeInt;
import it.pagopa.pn.timelineservice.generated.openapi.server.v1.dto.DeliveryInformationResponse;
import it.pagopa.pn.timelineservice.generated.openapi.server.v1.dto.DeliveryMode;
import it.pagopa.pn.timelineservice.utils.extraction.extractor.*;
import it.pagopa.pn.timelineservice.utils.extraction.model.ExtractionResult;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;

public class DeliveryInfoMapperTest {

    @Test
    void mapShouldReturnCorrectDeliveryInformationResponse() {
        Integer recIndex = 1;
        DeliveryInfoMapper mapper = new DeliveryInfoMapper(recIndex);

        OffsetDateTime schedulingAnalogDate = OffsetDateTime.now();
        OffsetDateTime refinementOrViewedDate = OffsetDateTime.now().plusDays(1);

        ExtractionResult result = new ExtractionResult(Map.of(
                SchedulingAnalogDateExtractor.KEY, Optional.of(schedulingAnalogDate.toInstant()),
                IsCancelledExtractor.KEY, Optional.of(true),
                RefinementOrViewDateExtractor.KEY, Optional.of(refinementOrViewedDate.toInstant()),
                DeliveryModeExtractor.KEY, Optional.of(DeliveryModeInt.DIGITAL)
        ));

        DeliveryInformationResponse response = mapper.map(result);

        assertEquals(schedulingAnalogDate.toInstant(), response.getSchedulingAnalogDate());
        assertTrue(response.getIsNotificationCancelled());
        assertEquals(refinementOrViewedDate.toInstant(), response.getRefinementOrViewedDate());
        assertEquals(DeliveryMode.DIGITAL, response.getDeliveryMode());
    }

    @Test
    void mapShouldHandleNullValues() {
        Integer recIndex = 2;
        DeliveryInfoMapper mapper = new DeliveryInfoMapper(recIndex);

        ExtractionResult result = new ExtractionResult(Map.of(
                SchedulingAnalogDateExtractor.KEY, Optional.empty(),
                IsCancelledExtractor.KEY, Optional.empty(),
                RefinementOrViewDateExtractor.KEY, Optional.empty(),
                DeliveryModeExtractor.KEY, Optional.empty()
        ));

        DeliveryInformationResponse response = mapper.map(result);

        assertNull(response.getSchedulingAnalogDate());
        assertFalse(response.getIsNotificationCancelled());
        assertNull(response.getRefinementOrViewedDate());
        assertNull(response.getDeliveryMode());
    }
}
