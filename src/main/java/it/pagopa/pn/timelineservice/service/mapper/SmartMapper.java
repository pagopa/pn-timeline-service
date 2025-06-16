package it.pagopa.pn.timelineservice.service.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.pagopa.pn.commons.exceptions.PnInternalException;
import it.pagopa.pn.timelineservice.dto.timeline.TimelineElementInternal;
import it.pagopa.pn.timelineservice.dto.timeline.details.ElementTimestampTimelineElementDetails;
import it.pagopa.pn.timelineservice.generated.openapi.server.v1.dto.TimelineElement;
import it.pagopa.pn.timelineservice.utils.FeatureEnabledUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiFunction;


@Slf4j
@Component
@RequiredArgsConstructor
public class SmartMapper {

    private final TimelineMapperFactory timelineMapperFactory;
    private static ModelMapper modelMapper;
    private final ObjectMapper objectMapper;
    private final FeatureEnabledUtils featureEnabledUtils;
    private static BiFunction postMappingTransformer;

    public static <S,T> T mapToClass(S source, Class<T> destinationClass ){
        T result;
        if( source != null) {
            result = modelMapper.map(source, destinationClass );
        } else {
            result = null;
        }
        return result;
    }

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
        modelMapper.getConfiguration()
                .setMatchingStrategy(MatchingStrategies.STANDARD)
                .setFieldMatchingEnabled(true)
                .setFieldAccessLevel(org.modelmapper.config.Configuration.AccessLevel.PRIVATE);
        modelMapper.createTypeMap(TimelineElementInternal.class, TimelineElementInternal.class).setPostConverter(timelineElementInternalTimestampConverter);
    }


    public  <S,T> T mapToClassWithObjectMapper(S source, Class<T> destinationClass ){
        try {
            objectMapper.addMixIn(Object.class, IgnoreFieldsMixin.class);
            var obj = objectMapper.readValue(objectMapper.writeValueAsBytes(source), destinationClass);
            if(TimelineElement.class.isAssignableFrom(destinationClass)){
                TimelineElement timelineElement = (TimelineElement) obj;
                if(Objects.isNull(timelineElement.getLegalFactsIds())) {
                    timelineElement.legalFactsIds(new ArrayList<>());
                }
            }
            return obj;
        } catch (IOException e) {
            throw new PnInternalException("Errore durante il mapping del dettaglio", e);
        }
    }

    private  TimelineElementInternal mapTimelineInternal(TimelineElementInternal source ){
        TimelineElementInternal result;
        if( source != null) {
            TimelineElementInternal elementToMap = source.toBuilder().build();
            result = modelMapper.map(elementToMap, TimelineElementInternal.class );
        } else {
            result = null;
        }
        return result;
    }

    public TimelineElementInternal mapTimelineInternal(TimelineElementInternal source, Set<TimelineElementInternal> timelineElementInternalSet) {
        //Viene recuperato il timestamp originale, prima di effettuare un qualsiasi remapping
        Instant ingestionTimestamp = source.getTimestamp();

        //Viene effettuato un primo remapping degli elementi di timeline e dei relativi timestamp in particolare viene effettuato il remapping di tutti
        // i timestamp che non dipendono da ulteriori elementi di timeline, cioè hanno l'eventTimestamp già storicizzato nei details
        TimelineElementInternal result = mapTimelineInternal(source);

        TimelineMapper timelineMapper = timelineMapperFactory.getTimelineMapper(source.getNotificationSentAt());
        boolean isPfNewWorkflowEnabled = featureEnabledUtils.isPfNewWorkflowEnabled(source.getNotificationSentAt());
        timelineMapper.remapSpecificTimelineElementData(timelineElementInternalSet, result, ingestionTimestamp, isPfNewWorkflowEnabled);

        return result;
    }

}

