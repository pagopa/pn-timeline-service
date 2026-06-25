package it.pagopa.pn.timelineservice.operations.common;

import it.pagopa.pn.timelineservice.dto.timeline.TimelineElementInternal;
import it.pagopa.pn.timelineservice.dto.timeline.details.common.ElementTimestampTimelineElementDetails;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.stereotype.Component;

@Component
public class TimelineTimestampBaseMapper {
    private static final ModelMapper modelMapper;

    static Converter<TimelineElementInternal, TimelineElementInternal> timelineElementInternalTimestampConverter =
            ctx -> {
                // se il detail estende l'interfaccia e l'elementTimestamp non è nullo, lo sovrascrivo nel source originale
                if (ctx.getSource().getDetails() instanceof ElementTimestampTimelineElementDetails elementTimestampTimelineElementDetails
                        && elementTimestampTimelineElementDetails.getElementTimestamp() != null)
                {
                    return ctx.getSource().toBuilder()
                            .timestamp(elementTimestampTimelineElementDetails.getElementTimestamp())
                            .build();
                }

                return ctx.getSource();
            };

    static{
        modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        modelMapper.createTypeMap(TimelineElementInternal.class, TimelineElementInternal.class).setPostConverter(timelineElementInternalTimestampConverter);
    }

    /*
        Metodo interno che per i details che lo prevedono (quelli che implementano l'interfaccia ElementTimestampTimelineElementDetails)
        sovrascrive con il timestamp presente nei details (elementTimestamp) il campo timestamp dell'elemento di timeline.
    */
    public TimelineElementInternal mapTimelineInternal(TimelineElementInternal source ) {
        if (source == null) {
            return null;
        }
        TimelineElementInternal elementToMap = source.toBuilder().build(); // copia per non modificare l'originale
        return modelMapper.map(elementToMap, TimelineElementInternal.class );
    }
}
