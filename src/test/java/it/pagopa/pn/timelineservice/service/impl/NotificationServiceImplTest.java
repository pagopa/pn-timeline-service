package it.pagopa.pn.timelineservice.service.impl;

import it.pagopa.pn.commons.exceptions.PnInternalException;
import it.pagopa.pn.timelineservice.dto.ext.notification.NotificationFeePolicy;
import it.pagopa.pn.timelineservice.dto.ext.notification.NotificationInt;
import it.pagopa.pn.timelineservice.dto.ext.notification.NotificationSenderInt;
import it.pagopa.pn.timelineservice.dto.ext.notification.ServiceLevelTypeInt;
import it.pagopa.pn.timelineservice.generated.openapi.msclient.delivery.model.SentNotificationV24;
import it.pagopa.pn.timelineservice.middleware.externalclient.delivery.PnDeliveryClientReactive;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Mono;

import java.util.Collections;

class NotificationServiceImplTest {

    @Mock
    private PnDeliveryClientReactive pnDeliveryClientReactive;

    private NotificationServiceImpl service;

    @BeforeEach
    public void setup() {
        service = new NotificationServiceImpl(pnDeliveryClientReactive);
    }

    @Test
    @ExtendWith(SpringExtension.class)
    void getNotificationByIunReactive() {
        NotificationInt expected = buildNotificationInt();
        SentNotificationV24 sentNotification = buildSentNotificationReactive();
        Mockito.when(pnDeliveryClientReactive.getSentNotification("001")).thenReturn(Mono.just(sentNotification));

        Mono<NotificationInt> actual = service.getNotificationByIunReactive("001");

        Assertions.assertEquals(expected, actual.block());
    }

    @Test
    @ExtendWith(SpringExtension.class)
    void getNotificationByIunNotFoundReactive() {

        String expectErrorMsg = "PN_DELIVERYPUSH_NOTIFICATIONFAILED";

        Mockito.when(pnDeliveryClientReactive.getSentNotification("001")).thenReturn(Mono.empty());

        service.getNotificationByIunReactive("001")
                .onErrorResume( error -> {
                    PnInternalException pnInternalException = (PnInternalException) error;
                    Assertions.assertEquals(expectErrorMsg, pnInternalException.getProblem().getErrors().get(0).getCode());
                    return Mono.empty();
                });
    }
    private SentNotificationV24 buildSentNotificationReactive() {
        SentNotificationV24 sentNotification = new SentNotificationV24();
        sentNotification.setIun("001");
        sentNotification.setPhysicalCommunicationType(SentNotificationV24.PhysicalCommunicationTypeEnum.REGISTERED_LETTER_890);
        sentNotification.setNotificationFeePolicy(it.pagopa.pn.timelineservice.generated.openapi.msclient.delivery.model.NotificationFeePolicy.DELIVERY_MODE);
        return sentNotification;
    }
    
    private NotificationInt buildNotificationInt() {
        return NotificationInt.builder()
                .iun("001")
                .recipients(Collections.emptyList())
                .documents(Collections.emptyList())
                .sender(NotificationSenderInt.builder().build())
                .notificationFeePolicy(NotificationFeePolicy.DELIVERY_MODE)
                .physicalCommunicationType(ServiceLevelTypeInt.REGISTERED_LETTER_890)
                .additionalLanguages(Collections.emptyList())
                .build();
    }
}