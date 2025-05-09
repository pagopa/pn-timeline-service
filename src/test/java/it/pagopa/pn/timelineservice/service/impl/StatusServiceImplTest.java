package it.pagopa.pn.timelineservice.service.impl;

import it.pagopa.pn.timelineservice.dto.address.PhysicalAddressInt;
import it.pagopa.pn.timelineservice.dto.ext.notification.NotificationInt;
import it.pagopa.pn.timelineservice.dto.ext.notification.NotificationRecipientInt;
import it.pagopa.pn.timelineservice.dto.ext.notification.NotificationSenderInt;
import it.pagopa.pn.timelineservice.dto.ext.notification.status.NotificationStatusHistoryElementInt;
import it.pagopa.pn.timelineservice.dto.ext.notification.status.NotificationStatusInt;
import it.pagopa.pn.timelineservice.dto.timeline.TimelineElementInternal;
import it.pagopa.pn.timelineservice.dto.timeline.details.NotificationRequestAcceptedDetailsInt;
import it.pagopa.pn.timelineservice.dto.timeline.details.SendAnalogDetailsInt;
import it.pagopa.pn.timelineservice.dto.timeline.details.SendAnalogFeedbackDetailsInt;
import it.pagopa.pn.timelineservice.dto.timeline.details.TimelineElementCategoryInt;
import it.pagopa.pn.timelineservice.service.StatusService;
import it.pagopa.pn.timelineservice.utils.StatusUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

class StatusServiceImplTest {
    private StatusUtils statusUtils;
    
    private StatusService statusService;
    
    @BeforeEach
    void setup() {
        statusUtils = Mockito.mock( StatusUtils.class );

        statusService = new StatusServiceImpl(statusUtils);
    }

    @Test
    void updateStatus() {
        // GIVEN
        String iun = "202109-eb10750e-e876-4a5a-8762-c4348d679d35";
        
        List<NotificationStatusHistoryElementInt> firstListReturn = new ArrayList<>();
        NotificationStatusHistoryElementInt element = NotificationStatusHistoryElementInt.builder()
                .status(NotificationStatusInt.DELIVERING)
                .build();
        firstListReturn.add(element);

        NotificationStatusHistoryElementInt element2 = NotificationStatusHistoryElementInt.builder()
                .status(NotificationStatusInt.ACCEPTED)
                .build();
        List<NotificationStatusHistoryElementInt> secondListReturn = new ArrayList<>(firstListReturn);
        secondListReturn.add(element2);

        Mockito.when(statusUtils.getStatusHistory(Mockito.any(), Mockito.anyInt(), Mockito.any() ))
                .thenReturn(firstListReturn)
                .thenReturn(secondListReturn)
                .thenReturn(firstListReturn)
                .thenReturn(secondListReturn);

        //Mockito.when(pnDeliveryClient.updateStatus(Mockito.any(RequestUpdateStatusDto.class))).thenReturn(ResponseEntity.ok().body(null));
                
        NotificationInt notification = getNotification(iun);
        
        
        String id1 = "sender_ack";
        TimelineElementInternal dto = TimelineElementInternal.builder()
                .iun(iun)
                .elementId(id1)
                .category(TimelineElementCategoryInt.REQUEST_ACCEPTED)
                .details(NotificationRequestAcceptedDetailsInt.builder().build())
                .timestamp(Instant.now())
                .build();

        List<TimelineElementInternal> timelineElementList  =  getListTimelineElementInternal(iun);
        HashSet<TimelineElementInternal> hashSet = new HashSet<>(timelineElementList);
        
        //WHEN
        StatusService.NotificationStatusUpdate statuses = statusService.getStatus(dto, hashSet, notification);
        
        //THEN
        Assertions.assertNotEquals(statuses.getOldStatus(), statuses.getNewStatus()); // changed status
    }

    @Test
    void notUpdateStatus() {
        // GIVEN
        String iun = "202109-eb10750e-e876-4a5a-8762-c4348d679d35";

        List<NotificationStatusHistoryElementInt> firstListReturn = new ArrayList<>();
        NotificationStatusHistoryElementInt element = NotificationStatusHistoryElementInt.builder()
                .status(NotificationStatusInt.ACCEPTED)
                .build();
        firstListReturn.add(element);

        NotificationStatusHistoryElementInt element2 = NotificationStatusHistoryElementInt.builder()
                .status(NotificationStatusInt.ACCEPTED)
                .build();
        List<NotificationStatusHistoryElementInt> secondListReturn = new ArrayList<>(firstListReturn);
        secondListReturn.add(element2);

        Mockito.when(statusUtils.getStatusHistory(Mockito.any(), Mockito.anyInt(), Mockito.any() ))
                .thenReturn(firstListReturn)
                .thenReturn(secondListReturn)
                .thenReturn(firstListReturn)
                .thenReturn(secondListReturn);

       // Mockito.when(pnDeliveryClient.updateStatus(Mockito.any(RequestUpdateStatusDto.class))).thenReturn(ResponseEntity.ok().body(null));

        NotificationInt notification = getNotification(iun);


        String id1 = "sender_ack";
        TimelineElementInternal dto = TimelineElementInternal.builder()
                .iun(iun)
                .elementId(id1)
                .category(TimelineElementCategoryInt.REQUEST_ACCEPTED)
                .details( NotificationRequestAcceptedDetailsInt.builder().build() )
                .timestamp(Instant.now())
                .build();

        List<TimelineElementInternal> timelineElementList  =  getListTimelineElementInternal(iun);
        HashSet<TimelineElementInternal> hashSet = new HashSet<>(timelineElementList);

        //WHEN
        StatusService.NotificationStatusUpdate statuses = statusService.getStatus(dto, hashSet, notification);

        //THEN
        Assertions.assertEquals(statuses.getOldStatus(), statuses.getNewStatus()); // same status (didn't change)
    }
    
    private List<TimelineElementInternal> getListTimelineElementInternal(String iun){
        List<TimelineElementInternal> timelineElementList = new ArrayList<>();
         SendAnalogDetailsInt details =  SendAnalogDetailsInt.builder()
                .physicalAddress(
                        PhysicalAddressInt.builder()
                                .province("province")
                                .municipality("munic")
                                .at("at")
                                .build()
                )
                .relatedRequestId("abc")
                 .analogCost(100)
                .recIndex(0)
                .sentAttemptMade(0)
                .build();
        TimelineElementInternal timelineElementInternal = TimelineElementInternal.builder()
                .iun(iun)
                .details( details )
                .build();

        timelineElementList.add(timelineElementInternal);

        return timelineElementList;
    }

    private TimelineElementInternal getTimelineElement(String iun) {
         SendAnalogFeedbackDetailsInt details =  SendAnalogFeedbackDetailsInt.builder()
                .newAddress(
                        PhysicalAddressInt.builder()
                                .province("province")
                                .municipality("munic")
                                .at("at")
                                .build()
                )
                .recIndex(0)
                .sentAttemptMade(0)
                .build();
        return TimelineElementInternal.builder()
                .iun(iun)
                .details( details )
                .build();
    }


    private NotificationInt getNotification(String iun) {
        return NotificationInt.builder()
                .iun(iun)
                .paProtocolNumber("protocol_01")
                .sender(NotificationSenderInt.builder()
                        .paId(" pa_02")
                        .build()
                )
                .recipients(Collections.singletonList(
                        NotificationRecipientInt.builder()
                                .taxId("testIdRecipient")
                                .denomination("Nome Cognome/Ragione Sociale")
                                .build()
                ))
                .build();
    }
}