package it.pagopa.pn.timelineservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.annotation.Generated;
import javax.validation.Valid;
import javax.validation.constraints.Size;
import java.util.Objects;

/**
 * DelegateInfo
 */
@lombok.Builder(toBuilder = true)
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-04-23T15:16:39.518831600+02:00[Europe/Rome]")
@lombok.ToString
public class DelegateInfo {

  @JsonProperty("internalId")
  private String internalId;

  @JsonProperty("taxId")
  @lombok.ToString.Exclude
  private String taxId;

  @JsonProperty("operatorUuid")
  private String operatorUuid;

  @JsonProperty("mandateId")
  private String mandateId;

  @JsonProperty("denomination")
  @lombok.ToString.Exclude
  private String denomination;

  @JsonProperty("delegateType")
  private RecipientType delegateType;

  public DelegateInfo internalId(String internalId) {
    this.internalId = internalId;
    return this;
  }

  /**
   * Get internalId
   * @return internalId
  */
  @Size(max = 128) 
  public String getInternalId() {
    return internalId;
  }

  public void setInternalId(String internalId) {
    this.internalId = internalId;
  }

  public DelegateInfo taxId(String taxId) {
    this.taxId = taxId;
    return this;
  }

  /**
   * Get taxId
   * @return taxId
  */
  @Size(max = 128) 
  public String getTaxId() {
    return taxId;
  }

  public void setTaxId(String taxId) {
    this.taxId = taxId;
  }

  public DelegateInfo operatorUuid(String operatorUuid) {
    this.operatorUuid = operatorUuid;
    return this;
  }

  /**
   * Get operatorUuid
   * @return operatorUuid
  */
  @Size(max = 128) 
  public String getOperatorUuid() {
    return operatorUuid;
  }

  public void setOperatorUuid(String operatorUuid) {
    this.operatorUuid = operatorUuid;
  }

  public DelegateInfo mandateId(String mandateId) {
    this.mandateId = mandateId;
    return this;
  }

  /**
   * Get mandateId
   * @return mandateId
  */
  @Size(max = 128) 
  public String getMandateId() {
    return mandateId;
  }

  public void setMandateId(String mandateId) {
    this.mandateId = mandateId;
  }

  public DelegateInfo denomination(String denomination) {
    this.denomination = denomination;
    return this;
  }

  /**
   * Get denomination
   * @return denomination
  */
  @Size(max = 128) 
  public String getDenomination() {
    return denomination;
  }

  public void setDenomination(String denomination) {
    this.denomination = denomination;
  }

  public DelegateInfo delegateType(RecipientType delegateType) {
    this.delegateType = delegateType;
    return this;
  }

  /**
   * Get delegateType
   * @return delegateType
  */
  @Valid 
  public RecipientType getDelegateType() {
    return delegateType;
  }

  public void setDelegateType(RecipientType delegateType) {
    this.delegateType = delegateType;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    DelegateInfo delegateInfo = (DelegateInfo) o;
    return Objects.equals(this.internalId, delegateInfo.internalId) &&
        Objects.equals(this.taxId, delegateInfo.taxId) &&
        Objects.equals(this.operatorUuid, delegateInfo.operatorUuid) &&
        Objects.equals(this.mandateId, delegateInfo.mandateId) &&
        Objects.equals(this.denomination, delegateInfo.denomination) &&
        Objects.equals(this.delegateType, delegateInfo.delegateType);
  }

  @Override
  public int hashCode() {
    return Objects.hash(internalId, taxId, operatorUuid, mandateId, denomination, delegateType);
  }
}
