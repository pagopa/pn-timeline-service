package it.pagopa.pn.timelineservice.service.mapper;

import it.pagopa.pn.commons.utils.DateFormatUtils;
import it.pagopa.pn.timelineservice.dto.ext.notification.*;
import it.pagopa.pn.timelineservice.dto.ext.notification.NotificationFeePolicy;
import it.pagopa.pn.timelineservice.generated.openapi.msclient.delivery.model.*;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;


public class NotificationMapper {
    private NotificationMapper(){}

    public static NotificationInt externalToInternal(SentNotificationV24 sentNotification) {

        List<NotificationRecipientInt> listNotificationRecipientInt = mapNotificationRecipient(sentNotification.getRecipients());
        List<NotificationDocumentInt> listNotificationDocumentIntInt = mapNotificationDocument(sentNotification.getDocuments());

        ServiceLevelTypeInt lvl =  ServiceLevelTypeInt.valueOf( sentNotification.getPhysicalCommunicationType().name());
        
        Instant paymentExpirationDate = null;
        if( sentNotification.getPaymentExpirationDate() != null ){
            ZonedDateTime dateTime = DateFormatUtils.parseDate(sentNotification.getPaymentExpirationDate());
            paymentExpirationDate = dateTime.toInstant();
        }
        
        return NotificationInt.builder()
                .iun(sentNotification.getIun())
                .subject(sentNotification.getSubject())
                .paProtocolNumber(sentNotification.getPaProtocolNumber())
                .physicalCommunicationType( lvl )
                .sentAt(sentNotification.getSentAt())
                .sender(
                        NotificationSenderInt.builder()
                                .paTaxId( sentNotification.getSenderTaxId() )
                                .paId(sentNotification.getSenderPaId())
                                .paDenomination(sentNotification.getSenderDenomination())
                                .build()
                )
                .paFee(sentNotification.getPaFee())
                .vat(sentNotification.getVat())
                .documents(listNotificationDocumentIntInt)
                .recipients(listNotificationRecipientInt)
                .notificationFeePolicy(NotificationFeePolicy.fromValue(sentNotification.getNotificationFeePolicy().getValue()))
                .amount(sentNotification.getAmount())
                .group(sentNotification.getGroup())
                .paymentExpirationDate(paymentExpirationDate)
                .pagoPaIntMode(sentNotification.getPagoPaIntMode() != null ? PagoPaIntMode.valueOf(sentNotification.getPagoPaIntMode().getValue()) : null)
                .version(sentNotification.getVersion())
                .additionalLanguages(sentNotification.getAdditionalLanguages())
                .build();
    }

    private static List<NotificationDocumentInt> mapNotificationDocument(List<NotificationDocument> documents) {
        List<NotificationDocumentInt> list = new ArrayList<>();

        for (NotificationDocument document : documents){
            NotificationDocumentInt notificationDocumentInt = NotificationDocumentInt.builder()
                    .digests(
                            NotificationDocumentInt.Digests.builder()
                                    .sha256(document.getDigests().getSha256())
                                    .build()
                    )
                    .ref(
                            NotificationDocumentInt.Ref.builder()
                                    .key(document.getRef().getKey())
                                    .versionToken(document.getRef().getVersionToken())
                                    .build()
                    )
                    .build();

            list.add(notificationDocumentInt);
        }

        return list;
    }

    private static List<NotificationRecipientInt> mapNotificationRecipient(List<NotificationRecipientV23> recipients) {
        List<NotificationRecipientInt> list = new ArrayList<>();

        for (NotificationRecipientV23 recipient : recipients){
            NotificationRecipientInt recipientInt = RecipientMapper.externalToInternal(recipient);
            list.add(recipientInt);
        }
        
        return list;
    }
    
}
