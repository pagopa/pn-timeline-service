package it.pagopa.pn.timelineservice.utils.extraction.mapper;

import it.pagopa.pn.timelineservice.dto.timeline.details.ExtendedDeliveryModeInt;
import it.pagopa.pn.timelineservice.generated.openapi.server.v1.dto.DeliveryInformationResponse;
import it.pagopa.pn.timelineservice.generated.openapi.server.v1.dto.ExtendedDeliveryMode;
import it.pagopa.pn.timelineservice.utils.extraction.extractor.*;
import it.pagopa.pn.timelineservice.utils.extraction.model.ExtractionResult;

import java.util.List;

public class DeliveryInfoMapper implements ExtractionMapper<DeliveryInformationResponse> {
    private final Integer recIndex;

    public DeliveryInfoMapper(Integer recIndex) {
        this.recIndex = recIndex;
    }

    @Override
    public DeliveryInformationResponse map(ExtractionResult result) {
        DeliveryInformationResponse deliveryInformationResponse = new DeliveryInformationResponse();
        deliveryInformationResponse.schedulingAnalogDate(result.get(SchedulingAnalogDateExtractor.KEY).orElse(null));
        deliveryInformationResponse.isNotificationCancelled(result.get(IsCancelledExtractor.KEY).orElse(false));
        deliveryInformationResponse.refinementOrViewedDate(result.get(RefinementOrViewDateExtractor.KEY).orElse(null));
        deliveryInformationResponse.deliveryMode(mapToExtendedDeliveryMode(result.get(DeliveryModeExtractor.KEY).orElse(null)));
        return deliveryInformationResponse;
    }

    @Override
    public List<TimelineDataExtractor<?>> extractors() {
        return List.of(
                new RefinementOrViewDateExtractor(recIndex),
                new SchedulingAnalogDateExtractor(recIndex),
                new DeliveryModeExtractor(recIndex),
                new IsCancelledExtractor()
        );
    }

    private ExtendedDeliveryMode mapToExtendedDeliveryMode(ExtendedDeliveryModeInt deliveryModeInt) {
        return deliveryModeInt != null ? ExtendedDeliveryMode.valueOf(deliveryModeInt.name()) : null;
    }
}
