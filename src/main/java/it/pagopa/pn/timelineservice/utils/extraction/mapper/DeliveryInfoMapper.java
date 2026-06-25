package it.pagopa.pn.timelineservice.utils.extraction.mapper;

import it.pagopa.pn.timelineservice.dto.timeline.details.legal.ExtendedDeliveryModeInt;
import it.pagopa.pn.timelineservice.generated.openapi.server.v1.dto.DeliveryInformationResponse;
import it.pagopa.pn.timelineservice.generated.openapi.server.v1.dto.ExtendedDeliveryMode;
import it.pagopa.pn.timelineservice.generated.openapi.server.v1.dto.RefinementOrViewedDateDetail;
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
        RefinementOrViewDateExtractor.Result extraction = result.get(RefinementOrViewDateExtractor.KEY).orElse(null);
        if (extraction != null) {
            deliveryInformationResponse.refinementOrViewedDate(extraction.getLowestDate());
            RefinementOrViewedDateDetail refinementOrViewedDateDetail = new RefinementOrViewedDateDetail()
                    .refinementDate(extraction.getRefinementDate())
                    .viewedDate(extraction.getViewDate());
            deliveryInformationResponse.refinementOrViewedDateDetail(refinementOrViewedDateDetail);
        }
        deliveryInformationResponse.deliveryMode(mapToExtendedDeliveryMode(result.get(DeliveryModeExtractor.KEY).orElse(null)));
        deliveryInformationResponse.isNotificationAccepted(result.get(IsAcceptedExtractor.KEY).orElse(false));
        return deliveryInformationResponse;
    }

    @Override
    public List<TimelineDataExtractor<?>> extractors() {
        return List.of(
                new RefinementOrViewDateExtractor(recIndex),
                new SchedulingAnalogDateExtractor(recIndex),
                new DeliveryModeExtractor(recIndex),
                new IsCancelledExtractor(),
                new IsAcceptedExtractor()
        );
    }

    private ExtendedDeliveryMode mapToExtendedDeliveryMode(ExtendedDeliveryModeInt deliveryModeInt) {
        return deliveryModeInt != null ? ExtendedDeliveryMode.valueOf(deliveryModeInt.name()) : null;
    }
}
