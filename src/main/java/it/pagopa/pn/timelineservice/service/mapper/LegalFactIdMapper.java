package it.pagopa.pn.timelineservice.service.mapper;


import it.pagopa.pn.timelineservice.dto.legalfacts.LegalFactCategoryV20;
import it.pagopa.pn.timelineservice.dto.legalfacts.LegalFactsIdInt;
import it.pagopa.pn.timelineservice.dto.legalfacts.LegalFactsIdV20;

public class LegalFactIdMapper {
    private LegalFactIdMapper(){}
    
    public static LegalFactsIdV20 internalToExternal(LegalFactsIdInt dtoInt){
        return LegalFactsIdV20.builder()
                .key(dtoInt.getKey())
                .category( dtoInt.getCategory() != null ? LegalFactCategoryV20.fromValue(dtoInt.getCategory().getValue()): null)
                .build();
    }
        
}
