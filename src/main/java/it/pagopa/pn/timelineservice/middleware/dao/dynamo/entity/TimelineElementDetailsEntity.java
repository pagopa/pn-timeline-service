package it.pagopa.pn.timelineservice.middleware.dao.dynamo.entity;

import lombok.*;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;

import java.time.Instant;
import java.util.List;

@Builder( toBuilder = true )
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
@DynamoDbBean
public class TimelineElementDetailsEntity {

    @Getter(onMethod=@__({@DynamoDbAttribute("recIndex")}))  private Integer recIndex;
    @Getter(onMethod=@__({@DynamoDbAttribute("physicalAddress")}))  private PhysicalAddressEntity physicalAddress;
    @Getter(onMethod=@__({@DynamoDbAttribute("digitalAddress")}))  private DigitalAddressEntity digitalAddress;
    @Getter(onMethod=@__({@DynamoDbAttribute("digitalAddressSource")}))  private DigitalAddressSourceEntity digitalAddressSource;
    @Getter(onMethod=@__({@DynamoDbAttribute("isAvailable")})) private Boolean isAvailable;
    @Getter(onMethod=@__({@DynamoDbAttribute("attemptDate")})) private Instant attemptDate;
    @Getter(onMethod=@__({@DynamoDbAttribute("deliveryMode")})) private DeliveryModeEntity deliveryMode;
    @Getter(onMethod=@__({@DynamoDbAttribute("contactPhase")})) private ContactPhaseEntity contactPhase;
    @Getter(onMethod=@__({@DynamoDbAttribute("sentAttemptMade")})) private Integer sentAttemptMade;
    @Getter(onMethod=@__({@DynamoDbAttribute("sendDate")})) private Instant sendDate;
    @Getter(onMethod=@__({@DynamoDbAttribute("errors")})) private List<String> errors;
    @Getter(onMethod=@__({@DynamoDbAttribute("numberOfRecipients")})) private Integer numberOfRecipients;
    @Getter(onMethod=@__({@DynamoDbAttribute("lastAttemptDate")})) private Instant lastAttemptDate;
    @Getter(onMethod=@__({@DynamoDbAttribute("retryNumber")})) private Integer retryNumber;
    @Getter(onMethod=@__({@DynamoDbAttribute("downstreamId")})) private DownstreamIdEntity downstreamId;
    @Getter(onMethod=@__({@DynamoDbAttribute("responseStatus")})) private ResponseStatusEntity responseStatus;
    @Getter(onMethod=@__({@DynamoDbAttribute("notificationDate")})) private Instant notificationDate;
    @Getter(onMethod=@__({@DynamoDbAttribute("serviceLevel")})) private ServiceLevelEntity serviceLevel;
    @Getter(onMethod=@__({@DynamoDbAttribute("investigation")})) private Boolean investigation;
    @Getter(onMethod=@__({@DynamoDbAttribute("relatedRequestId")})) private String relatedRequestId;
    @Getter(onMethod=@__({@DynamoDbAttribute("newAddress")})) private PhysicalAddressEntity newAddress;
    @Getter(onMethod=@__({@DynamoDbAttribute("generatedAarUrl")})) private String generatedAarUrl;
    @Getter(onMethod=@__({@DynamoDbAttribute("numberOfPages")})) private Integer numberOfPages;
    @Getter(onMethod=@__({@DynamoDbAttribute("reasonCode")})) private String reasonCode;
    @Getter(onMethod=@__({@DynamoDbAttribute("reason")})) private String reason;
    @Getter(onMethod=@__({@DynamoDbAttribute("notificationCost")})) private Integer notificationCost;
    @Getter(onMethod=@__({@DynamoDbAttribute("analogCost")})) private Integer analogCost;
    @Getter(onMethod=@__({@DynamoDbAttribute("sendingReceipts")})) private List<SendingReceiptEntity> sendingReceipts;
    @Getter(onMethod=@__({@DynamoDbAttribute("eventCode")})) private String eventCode;
    @Getter(onMethod=@__({@DynamoDbAttribute("shouldRetry")})) private Boolean shouldRetry;
    @Getter(onMethod=@__({@DynamoDbAttribute("raddType")})) private String raddType;
    @Getter(onMethod=@__({@DynamoDbAttribute("raddTransactionId")})) private String raddTransactionId;
    @Getter(onMethod=@__({@DynamoDbAttribute("productType")})) private String productType;
    @Getter(onMethod=@__({@DynamoDbAttribute("requestTimelineId")})) private String requestTimelineId;
    @Getter(onMethod=@__({@DynamoDbAttribute("delegateInfo")})) private DelegateInfoEntity delegateInfo;
    @Getter(onMethod=@__({@DynamoDbAttribute("legalFactId")})) private String legalFactId;
    @Getter(onMethod=@__({@DynamoDbAttribute("aarKey")})) private String aarKey;
    @Getter(onMethod=@__({@DynamoDbAttribute("eventTimestamp")})) private Instant eventTimestamp;
    @Getter(onMethod=@__({@DynamoDbAttribute("completionWorkflowDate")})) private Instant completionWorkflowDate;
    @Getter(onMethod=@__({@DynamoDbAttribute("status")})) private String endWorkflowStatus;
    @Getter(onMethod=@__({@DynamoDbAttribute("recipientType")})) private String recipientType;
    @Getter(onMethod=@__({@DynamoDbAttribute("amount")})) private Integer amount;
    @Getter(onMethod=@__({@DynamoDbAttribute("creditorTaxId")})) private String creditorTaxId;
    @Getter(onMethod=@__({@DynamoDbAttribute("noticeCode")})) private String noticeCode;
    @Getter(onMethod=@__({@DynamoDbAttribute("paymentSourceChannel")})) private String paymentSourceChannel;
    @Getter(onMethod=@__({@DynamoDbAttribute("schedulingDate")})) private Instant schedulingDate;
    @Getter(onMethod=@__({@DynamoDbAttribute("ioSendMessageResult")})) private IoSendMessageResultEntity ioSendMessageResult;
    @Getter(onMethod=@__({@DynamoDbAttribute("envelopeWeight")})) private Integer envelopeWeight;
    @Getter(onMethod=@__({@DynamoDbAttribute("f24Attachments")})) private List<String> f24Attachments;
    @Getter(onMethod=@__({@DynamoDbAttribute("categorizedAttachmentsResult")})) private CategorizedAttachmentsResultEntity categorizedAttachmentsResult;
    @Getter(onMethod=@__({@DynamoDbAttribute("deliveryDetailCode")})) private String deliveryDetailCode;
    @Getter(onMethod=@__({@DynamoDbAttribute("deliveryFailureCause")})) private String deliveryFailureCause;
    @Getter(onMethod=@__({@DynamoDbAttribute("attachments")})) private List<AttachmentDetailsEntity> attachments;
    @Getter(onMethod=@__({@DynamoDbAttribute("prepareRequestId")})) String prepareRequestId;
    @Getter(onMethod=@__({@DynamoDbAttribute("sendRequestId")})) String sendRequestId;
    @Getter(onMethod=@__({@DynamoDbAttribute("isFirstSendRetry")})) Boolean isFirstSendRetry;
    @Getter(onMethod=@__({@DynamoDbAttribute("relatedFeedbackTimelineId")})) String relatedFeedbackTimelineId;
    @Getter(onMethod=@__({@DynamoDbAttribute("nextDigitalAddressSource")}))  DigitalAddressSourceEntity nextDigitalAddressSource;
    @Getter(onMethod=@__({@DynamoDbAttribute("nextSourceAttemptsMade")}))  int nextSourceAttemptsMade;
    @Getter(onMethod=@__({@DynamoDbAttribute("nextLastAttemptMadeForSource")}))  Instant nextLastAttemptMadeForSource;
    @Getter(onMethod=@__({@DynamoDbAttribute("refusalReasons")})) private List<NotificationRefusedErrorEntity> refusalReasons;
    @Getter(onMethod=@__({@DynamoDbAttribute("uncertainPaymentDate")})) private Boolean uncertainPaymentDate;
    @Getter(onMethod=@__({@DynamoDbAttribute("legalFactGenerationDate")})) private Instant legalFactGenerationDate;
    @Getter(onMethod=@__({@DynamoDbAttribute("registeredLetterCode")})) String registeredLetterCode;
    @Getter(onMethod=@__({@DynamoDbAttribute("schedulingAnalogDate")})) private Instant schedulingAnalogDate;
    @Getter(onMethod=@__({@DynamoDbAttribute("cancellationRequestId")})) private String cancellationRequestId;
    @Getter(onMethod=@__({@DynamoDbAttribute("notRefinedRecipientIndexes")})) private List<Integer> notRefinedRecipientIndexes;
    @Getter(onMethod=@__({@DynamoDbAttribute("failureCause")})) private String failureCause;
    @Getter(onMethod=@__({@DynamoDbAttribute("vat")})) private Integer vat;
    @Getter(onMethod=@__({@DynamoDbAttribute("aarTemplateType")})) private AarTemplateTypeEntity aarTemplateType;
    @Getter(onMethod=@__({@DynamoDbAttribute("sourceChannel")})) private String sourceChannel;
    @Getter(onMethod=@__({@DynamoDbAttribute("sourceChannelDetails")})) private String sourceChannelDetails;
    @Getter(onMethod=@__({@DynamoDbAttribute("recIndexes")}))  private List<Integer> recIndexes;
    @Getter(onMethod=@__({@DynamoDbAttribute("registry")})) private String registry;
    @Getter(onMethod=@__({@DynamoDbAttribute("notificationRequestId")})) private String notificationRequestId;
    @Getter(onMethod=@__({@DynamoDbAttribute("paProtocolNumber")})) private String paProtocolNumber;
    @Getter(onMethod=@__({@DynamoDbAttribute("idempotenceToken")})) private String idempotenceToken;
}
