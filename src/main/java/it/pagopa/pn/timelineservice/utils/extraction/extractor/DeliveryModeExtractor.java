package it.pagopa.pn.timelineservice.utils.extraction.extractor;

import it.pagopa.pn.timelineservice.dto.timeline.TimelineElementInternal;
import it.pagopa.pn.timelineservice.dto.timeline.details.*;

import java.util.Optional;

public class DeliveryModeExtractor implements TimelineDataExtractor<DeliveryModeInt> {
    public static final ExtractorKey<DeliveryModeInt> KEY = ExtractorKey.of("deliveryMode", DeliveryModeInt.class);
    private final int recIndex;
    private DeliveryModeInt result = DeliveryModeInt.UNKNOWN;
    private boolean isSendDigitalPresent = false;
    private boolean isProbableDateAnalogWorkflowPresent = false;
    private boolean isScheduleAnalogWorkflowPresent = false;

    public DeliveryModeExtractor(int recIndex) {
        this.recIndex = recIndex;
    }

    @Override
    public ExtractorKey<DeliveryModeInt> getKey() {
        return KEY;
    }

    @Override
    public boolean process(TimelineElementInternal element) {
        if(ExtractorUtils.isRelatedToRecipient(element, recIndex)) {
            TimelineElementDetailsInt detailsInt = element.getDetails();

            if (detailsInt instanceof SendDigitalDetailsInt) {
                isSendDigitalPresent = true;
            } else if (detailsInt instanceof ScheduleAnalogWorkflowDetailsInt) {
                isScheduleAnalogWorkflowPresent = true;
            } else if (detailsInt instanceof ProbableDateAnalogWorkflowDetailsInt) {
                isProbableDateAnalogWorkflowPresent = true;
            }
        }

        return false;
    }

    @Override
    public void postProcess() {
        if(isSendDigitalPresent) {
            this.result = DeliveryModeInt.DIGITAL;
        } else if((isScheduleAnalogWorkflowPresent || isProbableDateAnalogWorkflowPresent)) {
            this.result = DeliveryModeInt.ANALOG;
        } else {
            this.result = DeliveryModeInt.UNKNOWN;
        }
    }

    @Override
    public Optional<DeliveryModeInt> getResult() {
        return Optional.ofNullable(this.result);
    }
}
