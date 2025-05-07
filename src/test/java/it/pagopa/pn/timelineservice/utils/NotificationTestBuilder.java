package it.pagopa.pn.timelineservice.utils;

import it.pagopa.pn.commons.utils.DateFormatUtils;
import it.pagopa.pn.timelineservice.dto.address.LegalDigitalAddressInt;
import it.pagopa.pn.timelineservice.dto.ext.notification.*;
import org.jetbrains.annotations.NotNull;
import org.springframework.util.Base64Utils;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;


public class NotificationTestBuilder {
    public static final String EXT_CHANNEL_WORKS = "works";
    private String iun;
    private String paId;
    private List<NotificationRecipientInt> recipients;
    private NotificationFeePolicy notificationFeePolicy;
    private Instant sentAt;
    private List<NotificationDocumentInt> notificationDocument;
    private PagoPaIntMode pagoPaIntMode;
    private Integer paFee;

    private String group;
    
    public NotificationTestBuilder() {
        sentAt = Instant.now();
        recipients = Collections.emptyList();
        notificationDocument = Collections.emptyList();
    }

    public static NotificationTestBuilder builder() {
        return new NotificationTestBuilder();
    }
    
    public NotificationTestBuilder withIun(String iun) {
        this.iun = iun;
        return this;
    }

    public NotificationTestBuilder withPaId(String paId) {
        this.paId = paId;
        return this;
    }

    public NotificationTestBuilder withNotificationFeePolicy(NotificationFeePolicy notificationFeePolicy) {
        this.notificationFeePolicy = notificationFeePolicy;
        return this;
    }

    public NotificationTestBuilder withNotificationRecipient(NotificationRecipientInt recipient) {
        this.recipients = Collections.singletonList(
                recipient
        );
        return this;
    }

    public NotificationTestBuilder withNotificationRecipients(List<NotificationRecipientInt> recipientCollections) {
        this.recipients = recipientCollections;
        return this;
    }

    public NotificationTestBuilder withSentAt(Instant sentAt) {
        this.sentAt = sentAt;
        return this;
    }

    public NotificationTestBuilder withNotificationDocuments(List<NotificationDocumentInt> documents) {
        this.notificationDocument = documents;
        return this;
    }

    public NotificationTestBuilder withPagoPaIntMode(PagoPaIntMode pagoPaIntMode) {
        this.pagoPaIntMode = pagoPaIntMode;
        return this;
    }

    public NotificationTestBuilder withPaFee(int paFee) {
        this.paFee = paFee;
        return this;
    }

    public NotificationTestBuilder withGroup(String group1) {
        this.group = group1;
        return this;
    }

    public NotificationInt build() {
        if(iun == null){
            iun = getRandomIun(4);
        }
        
        if(paId == null){
            paId = "generatedPaId";
        }
        
        if( notificationDocument.isEmpty() ){
            String fileDoc = "sha256_doc00";

            notificationDocument = List.of(
                    NotificationDocumentInt.builder()
                            .ref(NotificationDocumentInt.Ref.builder()
                                    .key(Base64Utils.encodeToString(fileDoc.getBytes()))
                                    .versionToken("v01_doc00")
                                    .build()
                            )
                            .digests(NotificationDocumentInt.Digests.builder()
                                    .sha256(Base64Utils.encodeToString(fileDoc.getBytes()))
                                    .build()
                            )
                            .build()
            );
        }

        if(notificationFeePolicy == null) {
            notificationFeePolicy = NotificationFeePolicy.FLAT_RATE;
        }

        if(pagoPaIntMode == null) {
            pagoPaIntMode = PagoPaIntMode.SYNC;
        }
        
        if(recipients.isEmpty()){
            recipients = new ArrayList<>();
            String EXTCHANNEL_SEND_SUCCESS = "OK";
            recipients.add(NotificationRecipientTestBuilder.builder()
                    .withTaxId("testTaxId")
                    .withInternalId("ANON_testTaxId")
                    .withDigitalDomicile(LegalDigitalAddressInt.builder()
                            .address("digitalDomicile@" + EXT_CHANNEL_WORKS)
                            .type(LegalDigitalAddressInt.LEGAL_DIGITAL_ADDRESS_TYPE.PEC)
                            .build()
                    )
                    .withPhysicalAddress(
                            PhysicalAddressBuilder.builder()
                                    .withAddress(EXTCHANNEL_SEND_SUCCESS + "_Via Nuova")
                                    .build()
                    )
                    .build()
            );
        }
        
        return NotificationInt.builder()
                .iun(iun)
                .paProtocolNumber("protocol_01")
                .subject("subject not very long but not too short")
                .sentAt(Instant.now())
                .amount(18)
                .paymentExpirationDate(DateFormatUtils.parseDate("2002-08-12").toInstant())
                .physicalCommunicationType(ServiceLevelTypeInt.AR_REGISTERED_LETTER)
                .sender(NotificationSenderInt.builder()
                        .paId(paId)
                        .paDenomination("Denominazione pa con id " + paId)
                        .paTaxId("CFPA-" + paId)
                        .build()
                )
                .notificationFeePolicy(notificationFeePolicy)
                .sentAt( sentAt )
                .recipients(recipients)
                .documents(notificationDocument)
                .pagoPaIntMode(pagoPaIntMode)
                .paFee(paFee)
                .group(group)
                .build();
    }

    private static String getRandomIun(int level) {
        String callerMethod = getMethodName(level);
        return getIun(callerMethod);
    }

    @NotNull
    private static String getIun(String callerMethod) {
        Random rand = new Random();
        int upperbound = 10000;
        int int_random = rand.nextInt(upperbound);
        return "iun-" + callerMethod + "_" + int_random;
    }
    public static String getMethodName(final int depth) {
        final StackTraceElement[] ste = Thread.currentThread().getStackTrace();
        return ste[depth].getMethodName();
    }

}
