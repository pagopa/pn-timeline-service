package it.pagopa.pn.timelineservice.service.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.pagopa.pn.commons.exceptions.PnInternalException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.stereotype.Component;

import java.io.IOException;


@Slf4j
@Component
@RequiredArgsConstructor
public class SmartMapper {
    private static ModelMapper modelMapper;
    private final ObjectMapper objectMapper;

    public static <S,T> T mapToClass(S source, Class<T> destinationClass ){
        T result;
        if( source != null) {
            result = modelMapper.map(source, destinationClass );
        } else {
            result = null;
        }
        return result;
    }

    static{
        modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
    }


    public  <S,T> T mapToClassWithObjectMapper(S source, Class<T> destinationClass ){
        try {
            objectMapper.addMixIn(Object.class, IgnoreFieldsMixin.class);
            return objectMapper.readValue(objectMapper.writeValueAsBytes(source), destinationClass);
        } catch (IOException e) {
            throw new PnInternalException("Errore durante il mapping del dettaglio", "MAPPING_ERROR", e);
        }
    }

}

