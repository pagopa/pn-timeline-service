package it.pagopa.pn.timelineservice.dto.timeline.details;

import it.pagopa.pn.timelineservice.dto.address.PhysicalAddressInt;
import it.pagopa.pn.timelineservice.dto.ext.externalchannel.CategorizedAttachmentsResultInt;
import it.pagopa.pn.timelineservice.dto.ext.externalchannel.ResultFilterInt;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

class SimpleRegisteredLetterDetailsIntTest {

    private SimpleRegisteredLetterDetailsInt detailsInt;

    @BeforeEach
    void setup() {
        detailsInt = SimpleRegisteredLetterDetailsInt.builder()
                .recIndex(0)
                .foreignState("001")
                .analogCost(100)
                .physicalAddress(PhysicalAddressInt.builder().addressDetails("000").build())
                .categorizedAttachmentsResult(CategorizedAttachmentsResultInt.builder().acceptedAttachments(List.of(ResultFilterInt.builder().fileKey("fileKey").build())).build())
                .build();
    }

    @Test
    void toLog() {
        String log = detailsInt.toLog();
        Assertions.assertEquals("recIndex=0 physicalAddress='Sensitive information' analogCost=100 productType=null prepareRequestId=null f24Attachments=[] vat=null", log);
    }

    @Test
    void getRecIndex() {
        int rec = detailsInt.getRecIndex();
        Assertions.assertEquals(0, rec);
    }

    @Test
    void getPhysicalAddress() {
        PhysicalAddressInt addressInt = detailsInt.getPhysicalAddress();
        Assertions.assertEquals("000", addressInt.getAddressDetails());
    }

    @Test
    void getForeignState() {
        String state = detailsInt.getForeignState();
        Assertions.assertEquals("001", state);
    }

    @Test
    void getAnalogCost() {
        Integer cost = detailsInt.getAnalogCost();
        Assertions.assertEquals(100, cost);
    }

    @Test
    void getCategorizedAttachmentsResult(){
        Assertions.assertEquals(CategorizedAttachmentsResultInt.builder().acceptedAttachments(List.of(ResultFilterInt.builder().fileKey("fileKey").build())).build(), detailsInt.getCategorizedAttachmentsResult());
    }

}