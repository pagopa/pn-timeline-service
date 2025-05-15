package it.pagopa.pn.timelineservice.service.impl;

import it.pagopa.pn.commons.exceptions.PnHttpResponseException;
import it.pagopa.pn.timelineservice.dto.ext.datavault.ConfidentialTimelineElementDtoInt;
import it.pagopa.pn.timelineservice.dto.timeline.TimelineElementInternal;
import it.pagopa.pn.timelineservice.dto.timeline.details.*;
import it.pagopa.pn.timelineservice.generated.openapi.msclient.datavault.model.ConfidentialTimelineElementDto;
import it.pagopa.pn.timelineservice.middleware.externalclient.datavault.PnDataVaultClientReactive;
import it.pagopa.pn.timelineservice.middleware.externalclient.datavault.PnDataVaultClientReactiveImpl;
import it.pagopa.pn.timelineservice.service.ConfidentialInformationService;
import it.pagopa.pn.timelineservice.service.mapper.ConfidentialTimelineElementDtoMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ConfidentialInformationServiceImpl implements ConfidentialInformationService {
    private final PnDataVaultClientReactive pnDataVaultClientReactive;

    public ConfidentialInformationServiceImpl(PnDataVaultClientReactive pnDataVaultClientReactive) {
        this.pnDataVaultClientReactive = pnDataVaultClientReactive;
    }

    @Override
    public Mono<Void> saveTimelineConfidentialInformation(TimelineElementInternal timelineElement) {
        String iun = timelineElement.getIun();

        if (timelineElement.getDetails() instanceof ConfidentialInformationTimelineElement) {

            ConfidentialTimelineElementDtoInt dtoInt = getConfidentialDtoFromTimeline(timelineElement);

            ConfidentialTimelineElementDto dtoExt = ConfidentialTimelineElementDtoMapper.internalToExternal(dtoInt);

            return pnDataVaultClientReactive.updateNotificationTimelineByIunAndTimelineElementId(iun, dtoExt)
                    .doOnSuccess(unused -> log.debug("UpdateNotificationTimelineByIunAndTimelineElementId OK for - iun {} timelineElementId {}", iun, dtoInt.getTimelineElementId()))
                    .then();
        }

        return Mono.empty();
    }

    private ConfidentialTimelineElementDtoInt getConfidentialDtoFromTimeline(TimelineElementInternal timelineElement) {
        TimelineElementDetailsInt details = timelineElement.getDetails();

        ConfidentialTimelineElementDtoInt.ConfidentialTimelineElementDtoIntBuilder builder = ConfidentialTimelineElementDtoInt.builder()
                .timelineElementId(timelineElement.getElementId());

        if (details instanceof CourtesyAddressRelatedTimelineElement courtesyDetails && courtesyDetails.getDigitalAddress() != null) {
            builder.digitalAddress(courtesyDetails.getDigitalAddress().getAddress());
        }

        if (details instanceof DigitalAddressRelatedTimelineElement digitalDetails && digitalDetails.getDigitalAddress() != null) {
            builder.digitalAddress(digitalDetails.getDigitalAddress().getAddress());
        }

        if (details instanceof PhysicalAddressRelatedTimelineElement physicalDetails && physicalDetails.getPhysicalAddress() != null) {
            builder.physicalAddress(physicalDetails.getPhysicalAddress());
        }

        if (details instanceof NewAddressRelatedTimelineElement newAddressDetails && newAddressDetails.getNewAddress() != null) {
            builder.newPhysicalAddress(newAddressDetails.getNewAddress());
        }
        
        if(details instanceof PersonalInformationRelatedTimelineElement personalInfoDetails){
            if(personalInfoDetails.getTaxId() != null){
                builder.taxId(personalInfoDetails.getTaxId());
            }
            if(personalInfoDetails.getDenomination() != null){
                builder.denomination(personalInfoDetails.getDenomination());
            }
        }

        return builder.build();
    }

    @Override
    public Mono<ConfidentialTimelineElementDtoInt> getTimelineElementConfidentialInformation(String iun, String timelineElementId) {
        return pnDataVaultClientReactive.getNotificationTimelineByIunAndTimelineElementId(iun, timelineElementId)
                .map(ConfidentialTimelineElementDtoMapper::externalToInternal)
                .doOnNext(dto -> log.debug("getTimelineElementConfidentialInformation OK for - iun {} timelineElementId {}", iun, timelineElementId))
                .switchIfEmpty(Mono.defer(() -> {
                    log.debug("getTimelineElementConfidentialInformation haven't confidential information for - iun {} timelineElementId {}", iun, timelineElementId);
                    return Mono.empty();
                }));
    }

    @Override
    public Mono<Map<String, ConfidentialTimelineElementDtoInt>> getTimelineConfidentialInformation(String iun) {
        return pnDataVaultClientReactive.getNotificationTimelineByIun(iun)
                .collectList()
                .flatMap(listDtoExt -> {
                    if (listDtoExt != null && !listDtoExt.isEmpty()) {
                        Map<String, ConfidentialTimelineElementDtoInt> result = listDtoExt.stream()
                                .map(ConfidentialTimelineElementDtoMapper::externalToInternal)
                                .collect(Collectors.toMap(ConfidentialTimelineElementDtoInt::getTimelineElementId, Function.identity()));
                        log.debug("getTimelineConfidentialInformation OK for - iun {} ", iun);
                        return Mono.just(result);
                    } else {
                        log.debug("getTimelineConfidentialInformation haven't confidential information for - iun {} ", iun);
                        return Mono.error(new PnHttpResponseException("No confidential information found", 404));
                    }
                });
    }
}
