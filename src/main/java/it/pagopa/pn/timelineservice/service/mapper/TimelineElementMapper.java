package it.pagopa.pn.timelineservice.service.mapper;

public class TimelineElementMapper {
    private TimelineElementMapper(){}

    //TODO: rivedere quando definita openapi
//    public static TimelineElementV26 internalToExternal(TimelineElementInternal internalDto) {
//        var builder = TimelineElementV26.builder()
//                .category(internalDto.getCategory() != null ? TimelineElementCategory.fromValue( internalDto.getCategory().getValue() ) : null)
//                .elementId(internalDto.getElementId())
//                .timestamp(internalDto.getTimestamp())
//                .notificationSentAt(internalDto.getNotificationSentAt())
//                .ingestionTimestamp(internalDto.getIngestionTimestamp())
//                .eventTimestamp(internalDto.getEventTimestamp())
//                .details( SmartMapper.mapToClass(internalDto.getDetails(), TimelineElementDetailsV26.class) );
//
//        if(internalDto.getLegalFactsIds() != null){
//            builder.legalFactsIds(
//                    internalDto.getLegalFactsIds().stream()
//                            .map(LegalFactIdMapper::internalToExternal)
//                            .toList()
//            );
//        }
//
//        return builder.build();
//    }


}
