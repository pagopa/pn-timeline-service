package it.pagopa.pn.timelineservice.dto.timeline.details;
import it.pagopa.pn.timelineservice.dto.address.LegalDigitalAddressInt;
import it.pagopa.pn.timelineservice.dto.address.PhysicalAddressInt;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
class NationalRegistriesClientResponseDetailsIntTest {
    private PublicRegistryResponseDetailsInt detailsInt;
    @BeforeEach
    void setUp() {
        detailsInt = new PublicRegistryResponseDetailsInt();
        detailsInt.setDigitalAddress(LegalDigitalAddressInt.builder().address("add").build());
        detailsInt.setPhysicalAddress(PhysicalAddressInt.builder().addressDetails("add").build());
        detailsInt.setRecIndex(1);
        detailsInt.setRequestTimelineId("requestTimelineId");
    }
    @Test
    void toLog() {
        String expected = "recIndex=1 digitalAddress='Sensitive information' physicalAddress='Sensitive information' requestTimelineId=requestTimelineId";
        Assertions.assertEquals(expected, detailsInt.toLog());
    }
    @Test
    void testEquals() {
        PublicRegistryResponseDetailsInt expected = buildPublicRegistryResponseDetailsInt();
        Assertions.assertEquals(expected.getDigitalAddress(), detailsInt.getDigitalAddress());
        Assertions.assertEquals(expected.getPhysicalAddress(), detailsInt.getPhysicalAddress());
        Assertions.assertEquals(expected.getRecIndex(), detailsInt.getRecIndex());
        Assertions.assertEquals(expected.getRequestTimelineId(), detailsInt.getRequestTimelineId());
    }
    @Test
    void getRecIndex() {
        Assertions.assertEquals(1, detailsInt.getRecIndex());
    }
    @Test
    void getDigitalAddress() {
        Assertions.assertEquals(LegalDigitalAddressInt.builder().address("add").build(), detailsInt.getDigitalAddress());
    }
    @Test
    void getPhysicalAddress() {
        Assertions.assertEquals(PhysicalAddressInt.builder().addressDetails("add").build(), detailsInt.getPhysicalAddress());
    }
    @Test
    void testToString() {
        String expected = "PublicRegistryResponseDetailsInt(recIndex=1, digitalAddress=LegalDigitalAddressInt(type=null), physicalAddress=PhysicalAddressInt(fullname=null, at=null, address=null, addressDetails=add, zip=null, municipality=null, municipalityDetails=null, province=null, foreignState=null), requestTimelineId=requestTimelineId)";
        Assertions.assertEquals(expected, detailsInt.toString());
    }
    private PublicRegistryResponseDetailsInt buildPublicRegistryResponseDetailsInt(){
        return PublicRegistryResponseDetailsInt.builder()
                .digitalAddress(LegalDigitalAddressInt.builder().address("add").build())
                .physicalAddress(PhysicalAddressInt.builder().addressDetails("add").build())
                .recIndex(1)
                .requestTimelineId("requestTimelineId")
                .build();
    }
}