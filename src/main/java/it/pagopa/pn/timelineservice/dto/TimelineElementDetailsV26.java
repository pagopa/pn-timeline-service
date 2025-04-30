package it.pagopa.pn.timelineservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import it.pagopa.pn.timelineservice.action.utils.EndWorkflowStatus;
import it.pagopa.pn.timelineservice.dto.details.SendingReceipt;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.annotation.Generated;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * The raw event payload that will be different based on the event.
 */
@lombok.Builder(toBuilder = true)
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-04-23T15:16:39.518831600+02:00[Europe/Rome]")
@lombok.ToString
public class TimelineElementDetailsV26 {

  @JsonProperty("legalFactId")
  private String legalFactId;

  @JsonProperty("recIndex")
  private Integer recIndex;

  @JsonProperty("oldAddress")
  private PhysicalAddress oldAddress;

  @JsonProperty("normalizedAddress")
  private PhysicalAddress normalizedAddress;

  @JsonProperty("generatedAarUrl")
  private String generatedAarUrl;

  @JsonProperty("physicalAddress")
  private PhysicalAddress physicalAddress;

  @JsonProperty("legalfactId")
  private String legalfactId;

  @JsonProperty("endWorkflowStatus")
  private EndWorkflowStatus endWorkflowStatus;

  @JsonProperty("completionWorkflowDate")
  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private java.time.Instant completionWorkflowDate;

  @JsonProperty("legalFactGenerationDate")
  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private java.time.Instant legalFactGenerationDate;

  @JsonProperty("digitalAddress")
  private DigitalAddress digitalAddress;

  @JsonProperty("digitalAddressSource")
  private DigitalAddressSource digitalAddressSource;

  @JsonProperty("isAvailable")
  private Boolean isAvailable;

  @JsonProperty("attemptDate")
  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private java.time.Instant attemptDate;

  @JsonProperty("eventTimestamp")
  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private java.time.Instant eventTimestamp;

  @JsonProperty("raddType")
  private String raddType;

  @JsonProperty("raddTransactionId")
  private String raddTransactionId;

  @JsonProperty("delegateInfo")
  private DelegateInfo delegateInfo;

  @JsonProperty("notificationCost")
  private Long notificationCost;

  @JsonProperty("deliveryMode")
  private DeliveryMode deliveryMode;

  @JsonProperty("contactPhase")
  private ContactPhase contactPhase;

  @JsonProperty("sentAttemptMade")
  private Integer sentAttemptMade;

  @JsonProperty("sendDate")
  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private java.time.Instant sendDate;

  @JsonProperty("refusalReasons")
  @Valid
  private List<NotificationRefusedErrorV25> refusalReasons = null;

  @JsonProperty("schedulingDate")
  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private java.time.Instant schedulingDate;

  @JsonProperty("lastAttemptDate")
  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private java.time.Instant lastAttemptDate;

  @JsonProperty("ioSendMessageResult")
  private IoSendMessageResult ioSendMessageResult;

  @JsonProperty("retryNumber")
  private Integer retryNumber;

  @JsonProperty("nextDigitalAddressSource")
  private DigitalAddressSource nextDigitalAddressSource;

  @JsonProperty("nextSourceAttemptsMade")
  private Integer nextSourceAttemptsMade;

  @JsonProperty("nextLastAttemptMadeForSource")
  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private java.time.Instant nextLastAttemptMadeForSource;

  @JsonProperty("responseStatus")
  private ResponseStatus responseStatus;

  @JsonProperty("notificationDate")
  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private java.time.Instant notificationDate;

  @JsonProperty("deliveryFailureCause")
  private String deliveryFailureCause;

  @JsonProperty("deliveryDetailCode")
  private String deliveryDetailCode;

  @JsonProperty("sendingReceipts")
  @Valid
  private List<SendingReceipt> sendingReceipts = null;

  @JsonProperty("shouldRetry")
  private Boolean shouldRetry;

  @JsonProperty("serviceLevel")
  private ServiceLevel serviceLevel;

  @JsonProperty("relatedRequestId")
  private String relatedRequestId;

  @JsonProperty("productType")
  private String productType;

  @JsonProperty("analogCost")
  private Integer analogCost;

  @JsonProperty("numberOfPages")
  private Integer numberOfPages;

  @JsonProperty("envelopeWeight")
  private Integer envelopeWeight;

  @JsonProperty("prepareRequestId")
  private String prepareRequestId;

  @JsonProperty("newAddress")
  private PhysicalAddress newAddress;

  @JsonProperty("attachments")
  @Valid
  private List<AttachmentDetails> attachments = null;

  @JsonProperty("sendRequestId")
  private String sendRequestId;

  @JsonProperty("registeredLetterCode")
  private String registeredLetterCode;

  @JsonProperty("aarKey")
  private String aarKey;

  @JsonProperty("reasonCode")
  private String reasonCode;

  @JsonProperty("reason")
  private String reason;

  @JsonProperty("recipientType")
  private RecipientType recipientType;

  @JsonProperty("amount")
  private Integer amount;

  @JsonProperty("creditorTaxId")
  private String creditorTaxId;

  @JsonProperty("noticeCode")
  private String noticeCode;

  @JsonProperty("idF24")
  private String idF24;

  @JsonProperty("paymentSourceChannel")
  private String paymentSourceChannel;

  @JsonProperty("uncertainPaymentDate")
  private Boolean uncertainPaymentDate;

  @JsonProperty("schedulingAnalogDate")
  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private java.time.Instant schedulingAnalogDate;

  @JsonProperty("cancellationRequestId")
  private String cancellationRequestId;

  @JsonProperty("notRefinedRecipientIndexes")
  @Valid
  private List<Integer> notRefinedRecipientIndexes = new ArrayList<>();

  @JsonProperty("foundAddress")
  private PhysicalAddress foundAddress;

  @JsonProperty("failureCause")
  private String failureCause;

  public TimelineElementDetailsV26 legalFactId(String legalFactId) {
    this.legalFactId = legalFactId;
    return this;
  }

  /**
   * Identificativo dell'atto opponibile a terzi del quale è stata richiesta la creazione
   * @return legalFactId
  */
  @Size(max = 128) 
  public String getLegalFactId() {
    return legalFactId;
  }

  public void setLegalFactId(String legalFactId) {
    this.legalFactId = legalFactId;
  }

  public TimelineElementDetailsV26 recIndex(Integer recIndex) {
    this.recIndex = recIndex;
    return this;
  }

  /**
   * Index destinatario notifica digitale
   * @return recIndex
  */
  @NotNull 
  public Integer getRecIndex() {
    return recIndex;
  }

  public void setRecIndex(Integer recIndex) {
    this.recIndex = recIndex;
  }

  public TimelineElementDetailsV26 oldAddress(PhysicalAddress oldAddress) {
    this.oldAddress = oldAddress;
    return this;
  }

  /**
   * Get oldAddress
   * @return oldAddress
  */
  @NotNull @Valid 
  public PhysicalAddress getOldAddress() {
    return oldAddress;
  }

  public void setOldAddress(PhysicalAddress oldAddress) {
    this.oldAddress = oldAddress;
  }

  public TimelineElementDetailsV26 normalizedAddress(PhysicalAddress normalizedAddress) {
    this.normalizedAddress = normalizedAddress;
    return this;
  }

  /**
   * Get normalizedAddress
   * @return normalizedAddress
  */
  @NotNull @Valid 
  public PhysicalAddress getNormalizedAddress() {
    return normalizedAddress;
  }

  public void setNormalizedAddress(PhysicalAddress normalizedAddress) {
    this.normalizedAddress = normalizedAddress;
  }

  public TimelineElementDetailsV26 generatedAarUrl(String generatedAarUrl) {
    this.generatedAarUrl = generatedAarUrl;
    return this;
  }

  /**
   * Chiave per recupero da safe-storage del documento aar
   * @return generatedAarUrl
  */
  @NotNull @Size(max = 128) 
  public String getGeneratedAarUrl() {
    return generatedAarUrl;
  }

  public void setGeneratedAarUrl(String generatedAarUrl) {
    this.generatedAarUrl = generatedAarUrl;
  }

  public TimelineElementDetailsV26 physicalAddress(PhysicalAddress physicalAddress) {
    this.physicalAddress = physicalAddress;
    return this;
  }

  /**
   * Get physicalAddress
   * @return physicalAddress
  */
  @NotNull @Valid 
  public PhysicalAddress getPhysicalAddress() {
    return physicalAddress;
  }

  public void setPhysicalAddress(PhysicalAddress physicalAddress) {
    this.physicalAddress = physicalAddress;
  }

  public TimelineElementDetailsV26 legalfactId(String legalfactId) {
    this.legalfactId = legalfactId;
    return this;
  }

  /**
   * Identificativo dell'atto opponibile a terzi del quale è stata richiesta la creazione
   * @return legalfactId
  */
  @Size(max = 128) 
  public String getLegalfactId() {
    return legalfactId;
  }

  public void setLegalfactId(String legalfactId) {
    this.legalfactId = legalfactId;
  }

  public TimelineElementDetailsV26 endWorkflowStatus(EndWorkflowStatus endWorkflowStatus) {
    this.endWorkflowStatus = endWorkflowStatus;
    return this;
  }

  /**
   * Get endWorkflowStatus
   * @return endWorkflowStatus
  */
  @Valid 
  public EndWorkflowStatus getEndWorkflowStatus() {
    return endWorkflowStatus;
  }

  public void setEndWorkflowStatus(EndWorkflowStatus endWorkflowStatus) {
    this.endWorkflowStatus = endWorkflowStatus;
  }

  public TimelineElementDetailsV26 completionWorkflowDate(java.time.Instant completionWorkflowDate) {
    this.completionWorkflowDate = completionWorkflowDate;
    return this;
  }

  /**
   * Data chiusura workflow
   * @return completionWorkflowDate
  */
  @Valid 
  public java.time.Instant getCompletionWorkflowDate() {
    return completionWorkflowDate;
  }

  public void setCompletionWorkflowDate(java.time.Instant completionWorkflowDate) {
    this.completionWorkflowDate = completionWorkflowDate;
  }

  public TimelineElementDetailsV26 legalFactGenerationDate(java.time.Instant legalFactGenerationDate) {
    this.legalFactGenerationDate = legalFactGenerationDate;
    return this;
  }

  /**
   * Data generazione atto opponibile a terzi allegato
   * @return legalFactGenerationDate
  */
  @Valid 
  public java.time.Instant getLegalFactGenerationDate() {
    return legalFactGenerationDate;
  }

  public void setLegalFactGenerationDate(java.time.Instant legalFactGenerationDate) {
    this.legalFactGenerationDate = legalFactGenerationDate;
  }

  public TimelineElementDetailsV26 digitalAddress(DigitalAddress digitalAddress) {
    this.digitalAddress = digitalAddress;
    return this;
  }

  /**
   * Get digitalAddress
   * @return digitalAddress
  */
  @NotNull @Valid 
  public DigitalAddress getDigitalAddress() {
    return digitalAddress;
  }

  public void setDigitalAddress(DigitalAddress digitalAddress) {
    this.digitalAddress = digitalAddress;
  }

  public TimelineElementDetailsV26 digitalAddressSource(DigitalAddressSource digitalAddressSource) {
    this.digitalAddressSource = digitalAddressSource;
    return this;
  }

  /**
   * Get digitalAddressSource
   * @return digitalAddressSource
  */
  @NotNull @Valid 
  public DigitalAddressSource getDigitalAddressSource() {
    return digitalAddressSource;
  }

  public void setDigitalAddressSource(DigitalAddressSource digitalAddressSource) {
    this.digitalAddressSource = digitalAddressSource;
  }

  public TimelineElementDetailsV26 isAvailable(Boolean isAvailable) {
    this.isAvailable = isAvailable;
    return this;
  }

  /**
   * Disponibilità indirizzo
   * @return isAvailable
  */
  @NotNull 
  public Boolean getIsAvailable() {
    return isAvailable;
  }

  public void setIsAvailable(Boolean isAvailable) {
    this.isAvailable = isAvailable;
  }

  public TimelineElementDetailsV26 attemptDate(java.time.Instant attemptDate) {
    this.attemptDate = attemptDate;
    return this;
  }

  /**
   * data tentativo precedente
   * @return attemptDate
  */
  @NotNull @Valid 
  public java.time.Instant getAttemptDate() {
    return attemptDate;
  }

  public void setAttemptDate(java.time.Instant attemptDate) {
    this.attemptDate = attemptDate;
  }

  public TimelineElementDetailsV26 eventTimestamp(java.time.Instant eventTimestamp) {
    this.eventTimestamp = eventTimestamp;
    return this;
  }

  /**
   * Data evento pagamento
   * @return eventTimestamp
  */
  @NotNull @Valid 
  public java.time.Instant getEventTimestamp() {
    return eventTimestamp;
  }

  public void setEventTimestamp(java.time.Instant eventTimestamp) {
    this.eventTimestamp = eventTimestamp;
  }

  public TimelineElementDetailsV26 raddType(String raddType) {
    this.raddType = raddType;
    return this;
  }

  /**
   * tipo di Rete Anti Digital Divide <br/> __FSU__: Fornitore Servizio Universale <br/> __ALT__: Fornitore RADD Alternativa <br/> 
   * @return raddType
  */
  @NotNull 
  public String getRaddType() {
    return raddType;
  }

  public void setRaddType(String raddType) {
    this.raddType = raddType;
  }

  public TimelineElementDetailsV26 raddTransactionId(String raddTransactionId) {
    this.raddTransactionId = raddTransactionId;
    return this;
  }

  /**
   * Identificativo della pratica all'interno della rete RADD
   * @return raddTransactionId
  */
  @NotNull @Size(max = 512) 
  public String getRaddTransactionId() {
    return raddTransactionId;
  }

  public void setRaddTransactionId(String raddTransactionId) {
    this.raddTransactionId = raddTransactionId;
  }

  public TimelineElementDetailsV26 delegateInfo(DelegateInfo delegateInfo) {
    this.delegateInfo = delegateInfo;
    return this;
  }

  /**
   * Get delegateInfo
   * @return delegateInfo
  */
  @Valid 
  public DelegateInfo getDelegateInfo() {
    return delegateInfo;
  }

  public void setDelegateInfo(DelegateInfo delegateInfo) {
    this.delegateInfo = delegateInfo;
  }

  public TimelineElementDetailsV26 notificationCost(Long notificationCost) {
    this.notificationCost = notificationCost;
    return this;
  }

  /**
   * costo notifica in euro cents, può essere nullo se la notifica è stata precedentemente visualizzata
   * @return notificationCost
  */
  @NotNull 
  public Long getNotificationCost() {
    return notificationCost;
  }

  public void setNotificationCost(Long notificationCost) {
    this.notificationCost = notificationCost;
  }

  public TimelineElementDetailsV26 deliveryMode(DeliveryMode deliveryMode) {
    this.deliveryMode = deliveryMode;
    return this;
  }

  /**
   * Get deliveryMode
   * @return deliveryMode
  */
  @NotNull @Valid 
  public DeliveryMode getDeliveryMode() {
    return deliveryMode;
  }

  public void setDeliveryMode(DeliveryMode deliveryMode) {
    this.deliveryMode = deliveryMode;
  }

  public TimelineElementDetailsV26 contactPhase(ContactPhase contactPhase) {
    this.contactPhase = contactPhase;
    return this;
  }

  /**
   * Get contactPhase
   * @return contactPhase
  */
  @NotNull @Valid 
  public ContactPhase getContactPhase() {
    return contactPhase;
  }

  public void setContactPhase(ContactPhase contactPhase) {
    this.contactPhase = contactPhase;
  }

  public TimelineElementDetailsV26 sentAttemptMade(Integer sentAttemptMade) {
    this.sentAttemptMade = sentAttemptMade;
    return this;
  }

  /**
   * numero dei tentativi effettuati
   * @return sentAttemptMade
  */
  @NotNull 
  public Integer getSentAttemptMade() {
    return sentAttemptMade;
  }

  public void setSentAttemptMade(Integer sentAttemptMade) {
    this.sentAttemptMade = sentAttemptMade;
  }

  public TimelineElementDetailsV26 sendDate(java.time.Instant sendDate) {
    this.sendDate = sendDate;
    return this;
  }

  /**
   * data invio messaggio di cortesia
   * @return sendDate
  */
  @NotNull @Valid 
  public java.time.Instant getSendDate() {
    return sendDate;
  }

  public void setSendDate(java.time.Instant sendDate) {
    this.sendDate = sendDate;
  }

  public TimelineElementDetailsV26 refusalReasons(List<NotificationRefusedErrorV25> refusalReasons) {
    this.refusalReasons = refusalReasons;
    return this;
  }

  public TimelineElementDetailsV26 addRefusalReasonsItem(NotificationRefusedErrorV25 refusalReasonsItem) {
    if (this.refusalReasons == null) {
      this.refusalReasons = new ArrayList<>();
    }
    this.refusalReasons.add(refusalReasonsItem);
    return this;
  }

  /**
   * Motivazioni che hanno portato al rifiuto della notifica
   * @return refusalReasons
  */
  @Valid 
  public List<NotificationRefusedErrorV25> getRefusalReasons() {
    return refusalReasons;
  }

  public void setRefusalReasons(List<NotificationRefusedErrorV25> refusalReasons) {
    this.refusalReasons = refusalReasons;
  }

  public TimelineElementDetailsV26 schedulingDate(java.time.Instant schedulingDate) {
    this.schedulingDate = schedulingDate;
    return this;
  }

  /**
   * Get schedulingDate
   * @return schedulingDate
  */
  @Valid 
  public java.time.Instant getSchedulingDate() {
    return schedulingDate;
  }

  public void setSchedulingDate(java.time.Instant schedulingDate) {
    this.schedulingDate = schedulingDate;
  }

  public TimelineElementDetailsV26 lastAttemptDate(java.time.Instant lastAttemptDate) {
    this.lastAttemptDate = lastAttemptDate;
    return this;
  }

  /**
   * Get lastAttemptDate
   * @return lastAttemptDate
  */
  @NotNull @Valid 
  public java.time.Instant getLastAttemptDate() {
    return lastAttemptDate;
  }

  public void setLastAttemptDate(java.time.Instant lastAttemptDate) {
    this.lastAttemptDate = lastAttemptDate;
  }

  public TimelineElementDetailsV26 ioSendMessageResult(IoSendMessageResult ioSendMessageResult) {
    this.ioSendMessageResult = ioSendMessageResult;
    return this;
  }

  /**
   * Get ioSendMessageResult
   * @return ioSendMessageResult
  */
  @Valid 
  public IoSendMessageResult getIoSendMessageResult() {
    return ioSendMessageResult;
  }

  public void setIoSendMessageResult(IoSendMessageResult ioSendMessageResult) {
    this.ioSendMessageResult = ioSendMessageResult;
  }

  public TimelineElementDetailsV26 retryNumber(Integer retryNumber) {
    this.retryNumber = retryNumber;
    return this;
  }

  /**
   * numero dei tentativi effettuati
   * @return retryNumber
  */
  @NotNull 
  public Integer getRetryNumber() {
    return retryNumber;
  }

  public void setRetryNumber(Integer retryNumber) {
    this.retryNumber = retryNumber;
  }

  public TimelineElementDetailsV26 nextDigitalAddressSource(DigitalAddressSource nextDigitalAddressSource) {
    this.nextDigitalAddressSource = nextDigitalAddressSource;
    return this;
  }

  /**
   * Get nextDigitalAddressSource
   * @return nextDigitalAddressSource
  */
  @Valid 
  public DigitalAddressSource getNextDigitalAddressSource() {
    return nextDigitalAddressSource;
  }

  public void setNextDigitalAddressSource(DigitalAddressSource nextDigitalAddressSource) {
    this.nextDigitalAddressSource = nextDigitalAddressSource;
  }

  public TimelineElementDetailsV26 nextSourceAttemptsMade(Integer nextSourceAttemptsMade) {
    this.nextSourceAttemptsMade = nextSourceAttemptsMade;
    return this;
  }

  /**
   * numero del prossimo tentativo da effettuare
   * @return nextSourceAttemptsMade
  */
  
  public Integer getNextSourceAttemptsMade() {
    return nextSourceAttemptsMade;
  }

  public void setNextSourceAttemptsMade(Integer nextSourceAttemptsMade) {
    this.nextSourceAttemptsMade = nextSourceAttemptsMade;
  }

  public TimelineElementDetailsV26 nextLastAttemptMadeForSource(java.time.Instant nextLastAttemptMadeForSource) {
    this.nextLastAttemptMadeForSource = nextLastAttemptMadeForSource;
    return this;
  }

  /**
   * data tentativo precedente per prossimo source
   * @return nextLastAttemptMadeForSource
  */
  @Valid 
  public java.time.Instant getNextLastAttemptMadeForSource() {
    return nextLastAttemptMadeForSource;
  }

  public void setNextLastAttemptMadeForSource(java.time.Instant nextLastAttemptMadeForSource) {
    this.nextLastAttemptMadeForSource = nextLastAttemptMadeForSource;
  }

  public TimelineElementDetailsV26 responseStatus(ResponseStatus responseStatus) {
    this.responseStatus = responseStatus;
    return this;
  }

  /**
   * Get responseStatus
   * @return responseStatus
  */
  @NotNull @Valid 
  public ResponseStatus getResponseStatus() {
    return responseStatus;
  }

  public void setResponseStatus(ResponseStatus responseStatus) {
    this.responseStatus = responseStatus;
  }

  public TimelineElementDetailsV26 notificationDate(java.time.Instant notificationDate) {
    this.notificationDate = notificationDate;
    return this;
  }

  /**
   * Get notificationDate
   * @return notificationDate
  */
  @NotNull @Valid 
  public java.time.Instant getNotificationDate() {
    return notificationDate;
  }

  public void setNotificationDate(java.time.Instant notificationDate) {
    this.notificationDate = notificationDate;
  }

  public TimelineElementDetailsV26 deliveryFailureCause(String deliveryFailureCause) {
    this.deliveryFailureCause = deliveryFailureCause;
    return this;
  }

  /**
   * Vedi deliveryFailureCause in SendAnalogFeedbackDetails
   * @return deliveryFailureCause
  */
  @Size(max = 10) 
  public String getDeliveryFailureCause() {
    return deliveryFailureCause;
  }

  public void setDeliveryFailureCause(String deliveryFailureCause) {
    this.deliveryFailureCause = deliveryFailureCause;
  }

  public TimelineElementDetailsV26 deliveryDetailCode(String deliveryDetailCode) {
    this.deliveryDetailCode = deliveryDetailCode;
    return this;
  }

  /**
   * Vedi deliveryDetailCode in SendAnalogFeedbackDetails
   * @return deliveryDetailCode
  */
  @Size(max = 20) 
  public String getDeliveryDetailCode() {
    return deliveryDetailCode;
  }

  public void setDeliveryDetailCode(String deliveryDetailCode) {
    this.deliveryDetailCode = deliveryDetailCode;
  }

  public TimelineElementDetailsV26 sendingReceipts(List<SendingReceipt> sendingReceipts) {
    this.sendingReceipts = sendingReceipts;
    return this;
  }

  public TimelineElementDetailsV26 addSendingReceiptsItem(SendingReceipt sendingReceiptsItem) {
    if (this.sendingReceipts == null) {
      this.sendingReceipts = new ArrayList<>();
    }
    this.sendingReceipts.add(sendingReceiptsItem);
    return this;
  }

  /**
   * Get sendingReceipts
   * @return sendingReceipts
  */
  @Valid 
  public List<SendingReceipt> getSendingReceipts() {
    return sendingReceipts;
  }

  public void setSendingReceipts(List<SendingReceipt> sendingReceipts) {
    this.sendingReceipts = sendingReceipts;
  }

  public TimelineElementDetailsV26 shouldRetry(Boolean shouldRetry) {
    this.shouldRetry = shouldRetry;
    return this;
  }

  /**
   * indica se il progress ha dato luogo ad un ritentativo
   * @return shouldRetry
  */
  
  public Boolean getShouldRetry() {
    return shouldRetry;
  }

  public void setShouldRetry(Boolean shouldRetry) {
    this.shouldRetry = shouldRetry;
  }

  public TimelineElementDetailsV26 serviceLevel(ServiceLevel serviceLevel) {
    this.serviceLevel = serviceLevel;
    return this;
  }

  /**
   * Get serviceLevel
   * @return serviceLevel
  */
  @NotNull @Valid 
  public ServiceLevel getServiceLevel() {
    return serviceLevel;
  }

  public void setServiceLevel(ServiceLevel serviceLevel) {
    this.serviceLevel = serviceLevel;
  }

  public TimelineElementDetailsV26 relatedRequestId(String relatedRequestId) {
    this.relatedRequestId = relatedRequestId;
    return this;
  }

  /**
   * Id relativo alla eventuale precedente richiesta di invio cartaceo
   * @return relatedRequestId
  */
  @Size(max = 512) 
  public String getRelatedRequestId() {
    return relatedRequestId;
  }

  public void setRelatedRequestId(String relatedRequestId) {
    this.relatedRequestId = relatedRequestId;
  }

  public TimelineElementDetailsV26 productType(String productType) {
    this.productType = productType;
    return this;
  }

  /**
   * Tipo di invio cartaceo effettivamente inviato   - __RS__: Raccomandata nazionale Semplice (per Avviso di mancato Recapito)   - __RIS__: Raccomandata internazionale Semplice 
   * @return productType
  */
  @Size(max = 10) 
  public String getProductType() {
    return productType;
  }

  public void setProductType(String productType) {
    this.productType = productType;
  }

  public TimelineElementDetailsV26 analogCost(Integer analogCost) {
    this.analogCost = analogCost;
    return this;
  }

  /**
   * costo in eurocent dell'invio
   * @return analogCost
  */
  
  public Integer getAnalogCost() {
    return analogCost;
  }

  public void setAnalogCost(Integer analogCost) {
    this.analogCost = analogCost;
  }

  public TimelineElementDetailsV26 numberOfPages(Integer numberOfPages) {
    this.numberOfPages = numberOfPages;
    return this;
  }

  /**
   * numero di pagine del PDF generato
   * @return numberOfPages
  */
  @NotNull 
  public Integer getNumberOfPages() {
    return numberOfPages;
  }

  public void setNumberOfPages(Integer numberOfPages) {
    this.numberOfPages = numberOfPages;
  }

  public TimelineElementDetailsV26 envelopeWeight(Integer envelopeWeight) {
    this.envelopeWeight = envelopeWeight;
    return this;
  }

  /**
   * peso in grammi della busta
   * @return envelopeWeight
  */
  
  public Integer getEnvelopeWeight() {
    return envelopeWeight;
  }

  public void setEnvelopeWeight(Integer envelopeWeight) {
    this.envelopeWeight = envelopeWeight;
  }

  public TimelineElementDetailsV26 prepareRequestId(String prepareRequestId) {
    this.prepareRequestId = prepareRequestId;
    return this;
  }

  /**
   * RequestId della richiesta di prepare
   * @return prepareRequestId
  */
  @Size(max = 512) 
  public String getPrepareRequestId() {
    return prepareRequestId;
  }

  public void setPrepareRequestId(String prepareRequestId) {
    this.prepareRequestId = prepareRequestId;
  }

  public TimelineElementDetailsV26 newAddress(PhysicalAddress newAddress) {
    this.newAddress = newAddress;
    return this;
  }

  /**
   * Get newAddress
   * @return newAddress
  */
  @Valid 
  public PhysicalAddress getNewAddress() {
    return newAddress;
  }

  public void setNewAddress(PhysicalAddress newAddress) {
    this.newAddress = newAddress;
  }

  public TimelineElementDetailsV26 attachments(List<AttachmentDetails> attachments) {
    this.attachments = attachments;
    return this;
  }

  public TimelineElementDetailsV26 addAttachmentsItem(AttachmentDetails attachmentsItem) {
    if (this.attachments == null) {
      this.attachments = new ArrayList<>();
    }
    this.attachments.add(attachmentsItem);
    return this;
  }

  /**
   * Get attachments
   * @return attachments
  */
  @Valid 
  public List<AttachmentDetails> getAttachments() {
    return attachments;
  }

  public void setAttachments(List<AttachmentDetails> attachments) {
    this.attachments = attachments;
  }

  public TimelineElementDetailsV26 sendRequestId(String sendRequestId) {
    this.sendRequestId = sendRequestId;
    return this;
  }

  /**
   * RequestId della richiesta d'invio
   * @return sendRequestId
  */
  @Size(max = 512) 
  public String getSendRequestId() {
    return sendRequestId;
  }

  public void setSendRequestId(String sendRequestId) {
    this.sendRequestId = sendRequestId;
  }

  public TimelineElementDetailsV26 registeredLetterCode(String registeredLetterCode) {
    this.registeredLetterCode = registeredLetterCode;
    return this;
  }

  /**
   * Codice della raccomandata
   * @return registeredLetterCode
  */
  @Size(max = 128) 
  public String getRegisteredLetterCode() {
    return registeredLetterCode;
  }

  public void setRegisteredLetterCode(String registeredLetterCode) {
    this.registeredLetterCode = registeredLetterCode;
  }

  public TimelineElementDetailsV26 aarKey(String aarKey) {
    this.aarKey = aarKey;
    return this;
  }

  /**
   * Chiave per recupero da safe-storage del documento aar
   * @return aarKey
  */
  @NotNull @Size(max = 128) 
  public String getAarKey() {
    return aarKey;
  }

  public void setAarKey(String aarKey) {
    this.aarKey = aarKey;
  }

  public TimelineElementDetailsV26 reasonCode(String reasonCode) {
    this.reasonCode = reasonCode;
    return this;
  }

  /**
   * Codice motivazione casistica non gestita
   * @return reasonCode
  */
  @NotNull @Size(max = 10) 
  public String getReasonCode() {
    return reasonCode;
  }

  public void setReasonCode(String reasonCode) {
    this.reasonCode = reasonCode;
  }

  public TimelineElementDetailsV26 reason(String reason) {
    this.reason = reason;
    return this;
  }

  /**
   * Motivazione casistica non gestita
   * @return reason
  */
  @NotNull @Size(max = 2048) 
  public String getReason() {
    return reason;
  }

  public void setReason(String reason) {
    this.reason = reason;
  }

  public TimelineElementDetailsV26 recipientType(RecipientType recipientType) {
    this.recipientType = recipientType;
    return this;
  }

  /**
   * Get recipientType
   * @return recipientType
  */
  @NotNull @Valid 
  public RecipientType getRecipientType() {
    return recipientType;
  }

  public void setRecipientType(RecipientType recipientType) {
    this.recipientType = recipientType;
  }

  public TimelineElementDetailsV26 amount(Integer amount) {
    this.amount = amount;
    return this;
  }

  /**
   * Importo di pagamento in eurocent
   * @return amount
  */
  
  public Integer getAmount() {
    return amount;
  }

  public void setAmount(Integer amount) {
    this.amount = amount;
  }

  public TimelineElementDetailsV26 creditorTaxId(String creditorTaxId) {
    this.creditorTaxId = creditorTaxId;
    return this;
  }

  /**
   * Payment PA fiscal code
   * @return creditorTaxId
  */
  @Pattern(regexp = "^\\d+$") @Size(min = 11, max = 11) 
  public String getCreditorTaxId() {
    return creditorTaxId;
  }

  public void setCreditorTaxId(String creditorTaxId) {
    this.creditorTaxId = creditorTaxId;
  }

  public TimelineElementDetailsV26 noticeCode(String noticeCode) {
    this.noticeCode = noticeCode;
    return this;
  }

  /**
   * Payment notice number  numero avviso
   * @return noticeCode
  */
  @Pattern(regexp = "^\\d+$") @Size(min = 18, max = 18) 
  public String getNoticeCode() {
    return noticeCode;
  }

  public void setNoticeCode(String noticeCode) {
    this.noticeCode = noticeCode;
  }

  public TimelineElementDetailsV26 idF24(String idF24) {
    this.idF24 = idF24;
    return this;
  }

  /**
   * un UUID che identifica un pagamento f24
   * @return idF24
  */
  @Size(max = 512) 
  public String getIdF24() {
    return idF24;
  }

  public void setIdF24(String idF24) {
    this.idF24 = idF24;
  }

  public TimelineElementDetailsV26 paymentSourceChannel(String paymentSourceChannel) {
    this.paymentSourceChannel = paymentSourceChannel;
    return this;
  }

  /**
   * Canale sorgente della richiesta di pagamento
   * @return paymentSourceChannel
  */
  @NotNull @Size(max = 512) 
  public String getPaymentSourceChannel() {
    return paymentSourceChannel;
  }

  public void setPaymentSourceChannel(String paymentSourceChannel) {
    this.paymentSourceChannel = paymentSourceChannel;
  }

  public TimelineElementDetailsV26 uncertainPaymentDate(Boolean uncertainPaymentDate) {
    this.uncertainPaymentDate = uncertainPaymentDate;
    return this;
  }

  /**
   * Indica se la data di pagamento é certa
   * @return uncertainPaymentDate
  */
  
  public Boolean getUncertainPaymentDate() {
    return uncertainPaymentDate;
  }

  public void setUncertainPaymentDate(Boolean uncertainPaymentDate) {
    this.uncertainPaymentDate = uncertainPaymentDate;
  }

  public TimelineElementDetailsV26 schedulingAnalogDate(java.time.Instant schedulingAnalogDate) {
    this.schedulingAnalogDate = schedulingAnalogDate;
    return this;
  }

  /**
   * Data probabile di inizio del flusso analogico
   * @return schedulingAnalogDate
  */
  @NotNull @Valid 
  public java.time.Instant getSchedulingAnalogDate() {
    return schedulingAnalogDate;
  }

  public void setSchedulingAnalogDate(java.time.Instant schedulingAnalogDate) {
    this.schedulingAnalogDate = schedulingAnalogDate;
  }

  public TimelineElementDetailsV26 cancellationRequestId(String cancellationRequestId) {
    this.cancellationRequestId = cancellationRequestId;
    return this;
  }

  /**
   * Id della richiesta
   * @return cancellationRequestId
  */
  @NotNull @Size(max = 512) 
  public String getCancellationRequestId() {
    return cancellationRequestId;
  }

  public void setCancellationRequestId(String cancellationRequestId) {
    this.cancellationRequestId = cancellationRequestId;
  }

  public TimelineElementDetailsV26 notRefinedRecipientIndexes(List<Integer> notRefinedRecipientIndexes) {
    this.notRefinedRecipientIndexes = notRefinedRecipientIndexes;
    return this;
  }

  public TimelineElementDetailsV26 addNotRefinedRecipientIndexesItem(Integer notRefinedRecipientIndexesItem) {
    if (this.notRefinedRecipientIndexes == null) {
      this.notRefinedRecipientIndexes = new ArrayList<>();
    }
    this.notRefinedRecipientIndexes.add(notRefinedRecipientIndexesItem);
    return this;
  }

  /**
   * Get notRefinedRecipientIndexes
   * @return notRefinedRecipientIndexes
  */
  @NotNull 
  public List<Integer> getNotRefinedRecipientIndexes() {
    return notRefinedRecipientIndexes;
  }

  public void setNotRefinedRecipientIndexes(List<Integer> notRefinedRecipientIndexes) {
    this.notRefinedRecipientIndexes = notRefinedRecipientIndexes;
  }

  public TimelineElementDetailsV26 foundAddress(PhysicalAddress foundAddress) {
    this.foundAddress = foundAddress;
    return this;
  }

  /**
   * Get foundAddress
   * @return foundAddress
  */
  @Valid 
  public PhysicalAddress getFoundAddress() {
    return foundAddress;
  }

  public void setFoundAddress(PhysicalAddress foundAddress) {
    this.foundAddress = foundAddress;
  }

  public TimelineElementDetailsV26 failureCause(String failureCause) {
    this.failureCause = failureCause;
    return this;
  }

  /**
   * __Motivazione fallimento prepare   - __D00__ Indirizzo non trovato   - __D01__ Indirizzo non valido   - __D02__ Indirizzo coincidente con quello del primo tentativo 
   * @return failureCause
  */
  @Size(max = 10) 
  public String getFailureCause() {
    return failureCause;
  }

  public void setFailureCause(String failureCause) {
    this.failureCause = failureCause;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    TimelineElementDetailsV26 timelineElementDetailsV26 = (TimelineElementDetailsV26) o;
    return Objects.equals(this.legalFactId, timelineElementDetailsV26.legalFactId) &&
        Objects.equals(this.recIndex, timelineElementDetailsV26.recIndex) &&
        Objects.equals(this.oldAddress, timelineElementDetailsV26.oldAddress) &&
        Objects.equals(this.normalizedAddress, timelineElementDetailsV26.normalizedAddress) &&
        Objects.equals(this.generatedAarUrl, timelineElementDetailsV26.generatedAarUrl) &&
        Objects.equals(this.physicalAddress, timelineElementDetailsV26.physicalAddress) &&
        Objects.equals(this.legalfactId, timelineElementDetailsV26.legalfactId) &&
        Objects.equals(this.endWorkflowStatus, timelineElementDetailsV26.endWorkflowStatus) &&
        Objects.equals(this.completionWorkflowDate, timelineElementDetailsV26.completionWorkflowDate) &&
        Objects.equals(this.legalFactGenerationDate, timelineElementDetailsV26.legalFactGenerationDate) &&
        Objects.equals(this.digitalAddress, timelineElementDetailsV26.digitalAddress) &&
        Objects.equals(this.digitalAddressSource, timelineElementDetailsV26.digitalAddressSource) &&
        Objects.equals(this.isAvailable, timelineElementDetailsV26.isAvailable) &&
        Objects.equals(this.attemptDate, timelineElementDetailsV26.attemptDate) &&
        Objects.equals(this.eventTimestamp, timelineElementDetailsV26.eventTimestamp) &&
        Objects.equals(this.raddType, timelineElementDetailsV26.raddType) &&
        Objects.equals(this.raddTransactionId, timelineElementDetailsV26.raddTransactionId) &&
        Objects.equals(this.delegateInfo, timelineElementDetailsV26.delegateInfo) &&
        Objects.equals(this.notificationCost, timelineElementDetailsV26.notificationCost) &&
        Objects.equals(this.deliveryMode, timelineElementDetailsV26.deliveryMode) &&
        Objects.equals(this.contactPhase, timelineElementDetailsV26.contactPhase) &&
        Objects.equals(this.sentAttemptMade, timelineElementDetailsV26.sentAttemptMade) &&
        Objects.equals(this.sendDate, timelineElementDetailsV26.sendDate) &&
        Objects.equals(this.refusalReasons, timelineElementDetailsV26.refusalReasons) &&
        Objects.equals(this.schedulingDate, timelineElementDetailsV26.schedulingDate) &&
        Objects.equals(this.lastAttemptDate, timelineElementDetailsV26.lastAttemptDate) &&
        Objects.equals(this.ioSendMessageResult, timelineElementDetailsV26.ioSendMessageResult) &&
        Objects.equals(this.retryNumber, timelineElementDetailsV26.retryNumber) &&
        Objects.equals(this.nextDigitalAddressSource, timelineElementDetailsV26.nextDigitalAddressSource) &&
        Objects.equals(this.nextSourceAttemptsMade, timelineElementDetailsV26.nextSourceAttemptsMade) &&
        Objects.equals(this.nextLastAttemptMadeForSource, timelineElementDetailsV26.nextLastAttemptMadeForSource) &&
        Objects.equals(this.responseStatus, timelineElementDetailsV26.responseStatus) &&
        Objects.equals(this.notificationDate, timelineElementDetailsV26.notificationDate) &&
        Objects.equals(this.deliveryFailureCause, timelineElementDetailsV26.deliveryFailureCause) &&
        Objects.equals(this.deliveryDetailCode, timelineElementDetailsV26.deliveryDetailCode) &&
        Objects.equals(this.sendingReceipts, timelineElementDetailsV26.sendingReceipts) &&
        Objects.equals(this.shouldRetry, timelineElementDetailsV26.shouldRetry) &&
        Objects.equals(this.serviceLevel, timelineElementDetailsV26.serviceLevel) &&
        Objects.equals(this.relatedRequestId, timelineElementDetailsV26.relatedRequestId) &&
        Objects.equals(this.productType, timelineElementDetailsV26.productType) &&
        Objects.equals(this.analogCost, timelineElementDetailsV26.analogCost) &&
        Objects.equals(this.numberOfPages, timelineElementDetailsV26.numberOfPages) &&
        Objects.equals(this.envelopeWeight, timelineElementDetailsV26.envelopeWeight) &&
        Objects.equals(this.prepareRequestId, timelineElementDetailsV26.prepareRequestId) &&
        Objects.equals(this.newAddress, timelineElementDetailsV26.newAddress) &&
        Objects.equals(this.attachments, timelineElementDetailsV26.attachments) &&
        Objects.equals(this.sendRequestId, timelineElementDetailsV26.sendRequestId) &&
        Objects.equals(this.registeredLetterCode, timelineElementDetailsV26.registeredLetterCode) &&
        Objects.equals(this.aarKey, timelineElementDetailsV26.aarKey) &&
        Objects.equals(this.reasonCode, timelineElementDetailsV26.reasonCode) &&
        Objects.equals(this.reason, timelineElementDetailsV26.reason) &&
        Objects.equals(this.recipientType, timelineElementDetailsV26.recipientType) &&
        Objects.equals(this.amount, timelineElementDetailsV26.amount) &&
        Objects.equals(this.creditorTaxId, timelineElementDetailsV26.creditorTaxId) &&
        Objects.equals(this.noticeCode, timelineElementDetailsV26.noticeCode) &&
        Objects.equals(this.idF24, timelineElementDetailsV26.idF24) &&
        Objects.equals(this.paymentSourceChannel, timelineElementDetailsV26.paymentSourceChannel) &&
        Objects.equals(this.uncertainPaymentDate, timelineElementDetailsV26.uncertainPaymentDate) &&
        Objects.equals(this.schedulingAnalogDate, timelineElementDetailsV26.schedulingAnalogDate) &&
        Objects.equals(this.cancellationRequestId, timelineElementDetailsV26.cancellationRequestId) &&
        Objects.equals(this.notRefinedRecipientIndexes, timelineElementDetailsV26.notRefinedRecipientIndexes) &&
        Objects.equals(this.foundAddress, timelineElementDetailsV26.foundAddress) &&
        Objects.equals(this.failureCause, timelineElementDetailsV26.failureCause);
  }

  @Override
  public int hashCode() {
    return Objects.hash(legalFactId, recIndex, oldAddress, normalizedAddress, generatedAarUrl, physicalAddress, legalfactId, endWorkflowStatus, completionWorkflowDate, legalFactGenerationDate, digitalAddress, digitalAddressSource, isAvailable, attemptDate, eventTimestamp, raddType, raddTransactionId, delegateInfo, notificationCost, deliveryMode, contactPhase, sentAttemptMade, sendDate, refusalReasons, schedulingDate, lastAttemptDate, ioSendMessageResult, retryNumber, nextDigitalAddressSource, nextSourceAttemptsMade, nextLastAttemptMadeForSource, responseStatus, notificationDate, deliveryFailureCause, deliveryDetailCode, sendingReceipts, shouldRetry, serviceLevel, relatedRequestId, productType, analogCost, numberOfPages, envelopeWeight, prepareRequestId, newAddress, attachments, sendRequestId, registeredLetterCode, aarKey, reasonCode, reason, recipientType, amount, creditorTaxId, noticeCode, idF24, paymentSourceChannel, uncertainPaymentDate, schedulingAnalogDate, cancellationRequestId, notRefinedRecipientIndexes, foundAddress, failureCause);
  }
}
