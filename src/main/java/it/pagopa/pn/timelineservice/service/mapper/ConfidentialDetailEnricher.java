package it.pagopa.pn.timelineservice.service.mapper;

import it.pagopa.pn.timelineservice.dto.address.CourtesyDigitalAddressInt;
import it.pagopa.pn.timelineservice.dto.address.LegalDigitalAddressInt;
import it.pagopa.pn.timelineservice.dto.address.PhysicalAddressInt;
import it.pagopa.pn.timelineservice.dto.ext.datavault.ConfidentialTimelineElementDtoInt;
import it.pagopa.pn.timelineservice.dto.timeline.details.*;

public class ConfidentialDetailEnricher {
    private ConfidentialDetailEnricher() {

    }

    public static void enrichTimelineElementWithConfidentialInformation(TimelineElementDetailsInt details,
                                                                  ConfidentialTimelineElementDtoInt confidentialDto) {

        if (details instanceof CourtesyAddressRelatedTimelineElement courtesyAddressRelatedTimelineElement && confidentialDto.getDigitalAddress() != null) {
            CourtesyDigitalAddressInt address = courtesyAddressRelatedTimelineElement.getDigitalAddress();

            address = getCourtesyDigitalAddress(confidentialDto, address);
            courtesyAddressRelatedTimelineElement.setDigitalAddress(address);
        }

        if (details instanceof DigitalAddressRelatedTimelineElement digitalAddressRelatedTimelineElement && confidentialDto.getDigitalAddress() != null) {

            LegalDigitalAddressInt address = digitalAddressRelatedTimelineElement.getDigitalAddress();

            address = getDigitalAddress(confidentialDto, address);

            digitalAddressRelatedTimelineElement.setDigitalAddress(address);
        }

        if (details instanceof PhysicalAddressRelatedTimelineElement physicalAddressRelatedTimelineElement && confidentialDto.getPhysicalAddress() != null) {
            PhysicalAddressInt physicalAddress = physicalAddressRelatedTimelineElement.getPhysicalAddress();

            physicalAddress = getPhysicalAddress(physicalAddress, confidentialDto.getPhysicalAddress());

            physicalAddressRelatedTimelineElement.setPhysicalAddress(physicalAddress);
        }

        if (details instanceof NewAddressRelatedTimelineElement newAddressRelatedTimelineElement && confidentialDto.getNewPhysicalAddress() != null) {

            PhysicalAddressInt newAddress = newAddressRelatedTimelineElement.getNewAddress();

            newAddress = getPhysicalAddress(newAddress, confidentialDto.getNewPhysicalAddress());

            newAddressRelatedTimelineElement.setNewAddress(newAddress);
        }

        if (details instanceof PersonalInformationRelatedTimelineElement personalInformationRelatedTimelineElement) {
            personalInformationRelatedTimelineElement.setTaxId(confidentialDto.getTaxId());
            personalInformationRelatedTimelineElement.setDenomination(confidentialDto.getDenomination());
        }
    }

    private static LegalDigitalAddressInt getDigitalAddress(ConfidentialTimelineElementDtoInt confidentialDto, LegalDigitalAddressInt address) {
        if (address == null) {
            address = LegalDigitalAddressInt.builder().build();
        }

        address = address.toBuilder().address(confidentialDto.getDigitalAddress()).build();
        return address;
    }

    private static CourtesyDigitalAddressInt getCourtesyDigitalAddress(ConfidentialTimelineElementDtoInt confidentialDto, CourtesyDigitalAddressInt address) {
        if (address == null) {
            address = CourtesyDigitalAddressInt.builder().build();
        }

        address = address.toBuilder().address(confidentialDto.getDigitalAddress()).build();
        return address;
    }

    private static PhysicalAddressInt getPhysicalAddress(PhysicalAddressInt physicalAddress, PhysicalAddressInt confidentialAddressInfo) {
        if (physicalAddress == null) {
            physicalAddress = PhysicalAddressInt.builder().build();
        }

        return physicalAddress.toBuilder()
                .at(confidentialAddressInfo.getAt())
                .address(confidentialAddressInfo.getAddress())
                .municipality(confidentialAddressInfo.getMunicipality())
                .province(confidentialAddressInfo.getProvince())
                .addressDetails(confidentialAddressInfo.getAddressDetails())
                .zip(confidentialAddressInfo.getZip())
                .municipalityDetails(confidentialAddressInfo.getMunicipalityDetails())
                .foreignState(confidentialAddressInfo.getForeignState())
                .build();
    }
}
