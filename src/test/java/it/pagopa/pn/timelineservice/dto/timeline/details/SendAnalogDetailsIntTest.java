package it.pagopa.pn.timelineservice.dto.timeline.details;
import it.pagopa.pn.timelineservice.dto.address.PhysicalAddressInt;
import it.pagopa.pn.timelineservice.dto.ext.externalchannel.CategorizedAttachmentsResultInt;
import it.pagopa.pn.timelineservice.dto.ext.externalchannel.ResultFilterInt;
import it.pagopa.pn.timelineservice.dto.timeline.details.common.ReceiverAddressSourceTypeInt;
import it.pagopa.pn.timelineservice.dto.timeline.details.legal.SendAnalogDetailsInt;
import it.pagopa.pn.timelineservice.dto.timeline.details.common.ServiceLevelInt;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

class SendAnalogDetailsIntTest {
    private SendAnalogDetailsInt detailsInt;
    @BeforeEach
    void setUp() {
        detailsInt = new SendAnalogDetailsInt();
        detailsInt.setPhysicalAddress(PhysicalAddressInt.builder().address("address").build());
        detailsInt.setRecIndex(3);
        detailsInt.setRelatedRequestId("abc");
        detailsInt.setAnalogCost(100);
        detailsInt.setServiceLevel(ServiceLevelInt.REGISTERED_LETTER_890);
        detailsInt.setSentAttemptMade(2);
        detailsInt.setReceiverAddressSourceType(ReceiverAddressSourceTypeInt.MITTENTE);
        detailsInt.setCarrierInvestigationPerformed(true);
        detailsInt.setCategorizedAttachmentsResult(CategorizedAttachmentsResultInt.builder().acceptedAttachments(List.of(ResultFilterInt.builder().fileKey("fileKey").build())).build());
    }

    @Test
    void testEquals() {
        SendAnalogDetailsInt expected = buildSendAnalogDetailsInt();
        Assertions.assertEquals(expected.getAnalogCost(), detailsInt.getAnalogCost());
        Assertions.assertEquals(expected.getPhysicalAddress(), detailsInt.getPhysicalAddress());
        Assertions.assertEquals(expected.getRecIndex(), detailsInt.getRecIndex());
        Assertions.assertEquals(expected.getRelatedRequestId(), detailsInt.getRelatedRequestId());
        Assertions.assertEquals(expected.getServiceLevel(), detailsInt.getServiceLevel());
        Assertions.assertEquals(expected.getSentAttemptMade(), detailsInt.getSentAttemptMade());
        Assertions.assertEquals(expected.getCategorizedAttachmentsResult(), detailsInt.getCategorizedAttachmentsResult());
        Assertions.assertEquals(expected.getProductType(), detailsInt.getProductType());
        Assertions.assertEquals(expected.getPrepareRequestId(), detailsInt.getPrepareRequestId());
        Assertions.assertEquals(expected.getEnvelopeWeight(), detailsInt.getEnvelopeWeight());
        Assertions.assertEquals(expected.getVat(), detailsInt.getVat());
        Assertions.assertEquals(expected.getF24Attachments(), detailsInt.getF24Attachments());
        Assertions.assertEquals(expected.getNumberOfPages(), detailsInt.getNumberOfPages());
        Assertions.assertEquals(expected.getReceiverAddressSourceType(), detailsInt.getReceiverAddressSourceType());
        Assertions.assertEquals(expected.getCarrierInvestigationPerformed(), detailsInt.getCarrierInvestigationPerformed());

    }
    @Test
    void getRecIndex() {
        Assertions.assertEquals(3, detailsInt.getRecIndex());
    }
    @Test
    void getPhysicalAddress() {
        Assertions.assertEquals(PhysicalAddressInt.builder().address("address").build(), detailsInt.getPhysicalAddress());
    }
    @Test
    void getServiceLevel() {
        Assertions.assertEquals(ServiceLevelInt.REGISTERED_LETTER_890, detailsInt.getServiceLevel());
    }
    @Test
    void getSentAttemptMade() {
        Assertions.assertEquals(2, detailsInt.getSentAttemptMade());
    }
    @Test
    void getRelatedRequestId() {
        Assertions.assertEquals("abc", detailsInt.getRelatedRequestId());
    }
    @Test
    void getAnalogCost() {
        Assertions.assertEquals(100, detailsInt.getAnalogCost());
    }
    @Test
    void getCategorizedAttachmentsResult(){
        Assertions.assertEquals(CategorizedAttachmentsResultInt.builder().acceptedAttachments(List.of(ResultFilterInt.builder().fileKey("fileKey").build())).build(), detailsInt.getCategorizedAttachmentsResult());
    }
    @Test
    void getReceiverAddressSourceType() {
        Assertions.assertEquals(ReceiverAddressSourceTypeInt.MITTENTE, detailsInt.getReceiverAddressSourceType());
    }
    @Test
    void getCarrierInvestigationPerformed() {
        Assertions.assertEquals(true, detailsInt.getCarrierInvestigationPerformed());
    }

    private SendAnalogDetailsInt buildSendAnalogDetailsInt() {
        return SendAnalogDetailsInt.builder().serviceLevel(ServiceLevelInt.REGISTERED_LETTER_890).physicalAddress(PhysicalAddressInt.builder().address("address").build()).analogCost(100).relatedRequestId("abc").sentAttemptMade(2).recIndex(3).categorizedAttachmentsResult(CategorizedAttachmentsResultInt.builder().acceptedAttachments(List.of(ResultFilterInt.builder().fileKey("fileKey").build())).build()).carrierInvestigationPerformed(true).receiverAddressSourceType(ReceiverAddressSourceTypeInt.MITTENTE).build();
    }

    @Test
    void toLogWithAttachments() {
        detailsInt.setProductType("AR");
        detailsInt.setPrepareRequestId("prepareRequestId");
        detailsInt.setF24Attachments(List.of("f24-1", "f24-2"));
        detailsInt.setVat(22);

        Assertions.assertEquals(
                "recIndex=3 sentAttemptMade=2 relatedRequestId=abc "
                        + "physicalAddress='Sensitive information' analogCost=100 "
                        + "productType=AR prepareRequestId=prepareRequestId "
                        + "f24Attachments=f24-1,f24-2, receiverAddressSourceType=MITTENTE "
                        + "carrierInvestigationPerformed=true vat=22",
                detailsInt.toLog()
        );
    }
}