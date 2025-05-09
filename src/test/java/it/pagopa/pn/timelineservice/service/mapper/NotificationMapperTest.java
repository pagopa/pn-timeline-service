package it.pagopa.pn.timelineservice.service.mapper;


import it.pagopa.pn.timelineservice.dto.ext.notification.NotificationInt;
import it.pagopa.pn.timelineservice.generated.openapi.msclient.delivery.model.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;

class NotificationMapperTest {


    @Test
    void externalToInternal() {
        SentNotificationV24 expected = getExternalNotification();

        NotificationInt internal = NotificationMapper.externalToInternal( expected );
        
        Assertions.assertNotNull(internal);
    }

    private SentNotificationV24 getExternalNotification() {
        return new SentNotificationV24()
                .iun("IUN_01")
                .paProtocolNumber("protocol_01")
                .subject("Subject 01")
                .senderPaId( "pa_02" )
                .physicalCommunicationType(SentNotificationV24.PhysicalCommunicationTypeEnum.REGISTERED_LETTER_890)
                .amount(18)
                .paymentExpirationDate("2022-10-22")
                .notificationFeePolicy(NotificationFeePolicy.DELIVERY_MODE)
                .recipients( Collections.singletonList(
                       new NotificationRecipientV23()
                                .taxId("Codice Fiscale 01")
                                .recipientType(NotificationRecipientV23.RecipientTypeEnum.PF)
                                .denomination("Nome Cognome/Ragione Sociale")
                               .digitalDomicile(
                                       new NotificationDigitalAddress()
                                               .address("address")
                                               .type(NotificationDigitalAddress.TypeEnum.PEC)
                               )
                               .physicalAddress(
                                       new NotificationPhysicalAddress()
                                               .address("physicalAddress")
                                               .municipality("municipality")
                               )
                ))
                .documents(Arrays.asList(
                        new NotificationDocument()
                                .ref( new NotificationAttachmentBodyRef()
                                        .key("doc00")
                                        .versionToken("v01_doc00")
                                )
                                .digests(new NotificationAttachmentDigests()
                                        .sha256("sha256_doc00")
                                ),
                        new NotificationDocument()
                                .ref(  new NotificationAttachmentBodyRef()
                                        .key("doc01")
                                        .versionToken("v01_doc01")
                                )
                                .digests(new NotificationAttachmentDigests()
                                        .sha256("sha256_doc01")
                                )
                ));
    }
}