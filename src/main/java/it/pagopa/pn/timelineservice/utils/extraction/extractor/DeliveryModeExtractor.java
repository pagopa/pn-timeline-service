package it.pagopa.pn.timelineservice.utils.extraction.extractor;

import it.pagopa.pn.timelineservice.dto.timeline.TimelineElementInternal;
import it.pagopa.pn.timelineservice.dto.timeline.details.legal.ExtendedDeliveryModeInt;
import it.pagopa.pn.timelineservice.dto.timeline.details.legal.TimelineElementCategoryInt;

import java.util.Optional;

public class DeliveryModeExtractor implements TimelineDataExtractor<ExtendedDeliveryModeInt> {
    public static final ExtractorKey<ExtendedDeliveryModeInt> KEY = ExtractorKey.of("deliveryMode", ExtendedDeliveryModeInt.class);
    private final int recIndex;
    private ExtendedDeliveryModeInt result = ExtendedDeliveryModeInt.UNKNOWN;
    private boolean isSendDigitalPresent = false;
    private boolean isProbableDateAnalogWorkflowPresent = false;
    private boolean isScheduleAnalogWorkflowPresent = false;

    public DeliveryModeExtractor(int recIndex) {
        this.recIndex = recIndex;
    }

    @Override
    public ExtractorKey<ExtendedDeliveryModeInt> getKey() {
        return KEY;
    }

    @Override
    public boolean process(TimelineElementInternal element) {
        if(ExtractorUtils.isRelatedToRecipient(element, recIndex)) {
            TimelineElementCategoryInt categoryInt = element.getCategory();

            if (categoryInt == TimelineElementCategoryInt.SEND_DIGITAL_DOMICILE) {
                isSendDigitalPresent = true;
            } else if (categoryInt == TimelineElementCategoryInt.SCHEDULE_ANALOG_WORKFLOW) {
                isScheduleAnalogWorkflowPresent = true;
            } else if (categoryInt == TimelineElementCategoryInt.PROBABLE_SCHEDULING_ANALOG_DATE) {
                isProbableDateAnalogWorkflowPresent = true;
            }
        }

        return false;
    }

    @Override
    public void postProcess() {
        if(isSendDigitalPresent) {
            this.result = ExtendedDeliveryModeInt.DIGITAL;
        } else if(isScheduleAnalogWorkflowPresent || isProbableDateAnalogWorkflowPresent) {
            this.result = ExtendedDeliveryModeInt.ANALOG;
        } else {
            this.result = ExtendedDeliveryModeInt.UNKNOWN;
        }
    }

    @Override
    public Optional<ExtendedDeliveryModeInt> getResult() {
        return Optional.ofNullable(this.result);
    }
}
