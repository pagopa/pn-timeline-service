package it.pagopa.pn.timelineservice;

import it.pagopa.pn.timelineservice.dto.address.DigitalAddressSourceInt;
import it.pagopa.pn.timelineservice.dto.ext.externalchannel.ResultFilterEnum;
import it.pagopa.pn.timelineservice.dto.io.IoSendMessageResultInt;
import it.pagopa.pn.timelineservice.dto.notification.status.NotificationStatusInt;
import it.pagopa.pn.timelineservice.dto.timeline.details.*;
import it.pagopa.pn.timelineservice.dto.timeline.details.EndWorkflowStatus;
import it.pagopa.pn.timelineservice.generated.openapi.server.v1.dto.*;
import it.pagopa.pn.timelineservice.legalfacts.AarTemplateType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class CheckInternalExternalEnumTest {

    @Test
    void checkExternalCategory(){
        //Nota il test fallisce probabilmente ci si potrebbe essere dimenticati di aggiungere nell'openapi un nuovo valore presente nel Dto interno
        assertDoesNotThrow( ()  ->{
            for (TimelineElementCategoryInt timelineElementCategoryInt : TimelineElementCategoryInt.values()) {
                TimelineCategory.valueOf(timelineElementCategoryInt.name());
            }
        });
    }

    @Test
    void checkInternalCategory(){
        //Nota il test fallisce probabilmente ci si potrebbe essere dimenticati di aggiungere nel Dto interno una category presente nell'openapi
        assertDoesNotThrow( ()  ->{
            for (TimelineCategory timelineCategory : TimelineCategory.values()) {
                TimelineElementCategoryInt.valueOf(timelineCategory.name());
            }
        });
    }

    @Test
    void checkExternalStatus(){
        //Nota il test fallisce probabilmente ci si potrebbe essere dimenticati di aggiungere nell'openapi un nuovo valore presente nel Dto interno
        assertDoesNotThrow( ()  ->{
            for (NotificationStatusInt notificationStatusInt : NotificationStatusInt.values()) {
                NotificationStatus.valueOf(notificationStatusInt.name());
            }
        });
    }

    @Test
    void checkInternalStatus(){
        //Nota il test fallisce probabilmente ci si potrebbe essere dimenticati di aggiungere nel Dto interno uno stato presente nell'openapi
        assertDoesNotThrow( ()  ->{
            for (NotificationStatus notificationStatus : NotificationStatus.values()) {
                NotificationStatusInt.valueOf(notificationStatus.name());
            }
        });
    }

    @Test
    void checkExternalContactPhase(){
        assertDoesNotThrow( ()  ->{
            for (ContactPhaseInt contactPhaseInt : ContactPhaseInt.values()) {
                ContactPhase.valueOf(contactPhaseInt.name());
            }
        });
    }

    @Test
    void checkInternalContactPhase(){
        assertDoesNotThrow( ()  ->{
            for (ContactPhase contactPhase : ContactPhase.values()) {
                ContactPhaseInt.valueOf(contactPhase.name());
            }
        });
    }

    @Test
    void checkExternalDeliveryMode(){
        assertDoesNotThrow( ()  ->{
            for (DeliveryModeInt deliveryModeInt : DeliveryModeInt.values()) {
                DeliveryMode.valueOf(deliveryModeInt.name());
            }
        });
    }

    @Test
    void checkInternalDeliveryMode(){
        assertDoesNotThrow( ()  ->{
            for (DeliveryMode deliveryMode : DeliveryMode.values()) {
                DeliveryModeInt.valueOf(deliveryMode.name());
            }
        });
    }

    @Test
    void checkExternalServiceLevel(){
        assertDoesNotThrow( ()  ->{
            for (ServiceLevelInt serviceLevelInt : ServiceLevelInt.values()) {
                ServiceLevel.valueOf(serviceLevelInt.name());
            }
        });
    }

    @Test
    void checkInternalServiceLevel(){
        assertDoesNotThrow( ()  ->{
            for (ServiceLevel serviceLevel : ServiceLevel.values()) {
                ServiceLevelInt.valueOf(serviceLevel.name());
            }
        });
    }

    @Test
    void checkExternalIoSendMessageResult(){
        assertDoesNotThrow( ()  ->{
            for (IoSendMessageResultInt ioSendMessageResultInt : IoSendMessageResultInt.values()) {
                IoSendMessageResult.valueOf(ioSendMessageResultInt.name());
            }
        });
    }

    @Test
    void checkInternalIoSendMessageResult(){
        assertDoesNotThrow( ()  ->{
            for (IoSendMessageResult ioSendMessageResult : IoSendMessageResult.values()) {
                IoSendMessageResultInt.valueOf(ioSendMessageResult.name());
            }
        });
    }

    @Test
    void checkExternalEndWorkflowStatus(){
        assertDoesNotThrow( ()  ->{
            for (EndWorkflowStatus endWorkflowStatus : EndWorkflowStatus.values()) {
                it.pagopa.pn.timelineservice.generated.openapi.server.v1.dto.EndWorkflowStatus.valueOf(endWorkflowStatus.name());
            }
        });
    }

    @Test
    void checkInternalEndWorkflowStatus(){
        assertDoesNotThrow( ()  ->{
            for (it.pagopa.pn.timelineservice.generated.openapi.server.v1.dto.EndWorkflowStatus endWorkflowStatus : it.pagopa.pn.timelineservice.generated.openapi.server.v1.dto.EndWorkflowStatus.values()) {
                EndWorkflowStatus.valueOf(endWorkflowStatus.name());
            }
        });
    }


    @Test
    void checkExternalAarTemplateType(){
        assertDoesNotThrow( ()  ->{
            for (AarTemplateType aarTemplateType : AarTemplateType.values()) {
                AarCreationRequestDetails.AarTemplateTypeEnum.valueOf(aarTemplateType.name());
            }
        });
    }

    @Test
    void checkInternalAarTemplateType(){
        assertDoesNotThrow( ()  ->{
            for (AarCreationRequestDetails.AarTemplateTypeEnum aarTemplateTypeEnum : AarCreationRequestDetails.AarTemplateTypeEnum.values()) {
                AarTemplateType.valueOf(aarTemplateTypeEnum.name());
            }
        });
    }

    @Test
    void checkExternalResultFilter(){
        assertDoesNotThrow( ()  ->{
            for (ResultFilterEnum resultFilterInt : ResultFilterEnum.values()) {
                ResultFilter.ResultEnum.valueOf(resultFilterInt.name());
            }
        });
    }

    @Test
    void checkInternalResultFilter(){
        assertDoesNotThrow( ()  ->{
            for (ResultFilter.ResultEnum resultFilter : ResultFilter.ResultEnum.values()) {
                ResultFilterEnum.valueOf(resultFilter.name());
            }
        });
    }

    @Test
    void checkExternalDigitalAddressSource(){
        assertDoesNotThrow( ()  ->{
            for (DigitalAddressSourceInt digitalAddressSourceInt : DigitalAddressSourceInt.values()) {
                DigitalAddressSource.valueOf(digitalAddressSourceInt.name());
            }
        });
    }

    @Test
    void checkInternalDigitalAddressSource(){
        assertDoesNotThrow( ()  ->{
            for (DigitalAddressSource digitalAddressSource : DigitalAddressSource.values()) {
                DigitalAddressSourceInt.valueOf(digitalAddressSource.name());
            }
        });
    }
}
