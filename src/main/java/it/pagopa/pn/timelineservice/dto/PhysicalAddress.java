package it.pagopa.pn.timelineservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.annotation.Generated;
import javax.validation.constraints.NotNull;
import java.util.Objects;

/**
 * Indirizzo fisico scoperto durante fase di consegna
 */
@lombok.Builder(toBuilder = true)
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-04-23T15:16:39.518831600+02:00[Europe/Rome]")
@lombok.ToString
public class PhysicalAddress {

  @JsonProperty("at")
  @lombok.ToString.Exclude
  private String at;

  @JsonProperty("address")
  @lombok.ToString.Exclude
  private String address;

  @JsonProperty("addressDetails")
  @lombok.ToString.Exclude
  private String addressDetails;

  @JsonProperty("zip")
  @lombok.ToString.Exclude
  private String zip;

  @JsonProperty("municipality")
  @lombok.ToString.Exclude
  private String municipality;

  @JsonProperty("municipalityDetails")
  @lombok.ToString.Exclude
  private String municipalityDetails;

  @JsonProperty("province")
  @lombok.ToString.Exclude
  private String province;

  @JsonProperty("foreignState")
  @lombok.ToString.Exclude
  private String foreignState;

  public PhysicalAddress at(String at) {
    this.at = at;
    return this;
  }

  /**
   * Campo \"presso\" dell'indirizzo
   * @return at
  */
  
  public String getAt() {
    return at;
  }

  public void setAt(String at) {
    this.at = at;
  }

  public PhysicalAddress address(String address) {
    this.address = address;
    return this;
  }

  /**
   * Indirizzo del domicilio fisico
   * @return address
  */
  @NotNull 
  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  public PhysicalAddress addressDetails(String addressDetails) {
    this.addressDetails = addressDetails;
    return this;
  }

  /**
   * Seconda riga dell'indirizzo fisico
   * @return addressDetails
  */
  
  public String getAddressDetails() {
    return addressDetails;
  }

  public void setAddressDetails(String addressDetails) {
    this.addressDetails = addressDetails;
  }

  public PhysicalAddress zip(String zip) {
    this.zip = zip;
    return this;
  }

  /**
   * Codice di avviamento postale. In caso di invio estero diventa facoltativo
   * @return zip
  */
  
  public String getZip() {
    return zip;
  }

  public void setZip(String zip) {
    this.zip = zip;
  }

  public PhysicalAddress municipality(String municipality) {
    this.municipality = municipality;
    return this;
  }

  /**
   * Comune in cui l'indirizzo si trova
   * @return municipality
  */
  @NotNull 
  public String getMunicipality() {
    return municipality;
  }

  public void setMunicipality(String municipality) {
    this.municipality = municipality;
  }

  public PhysicalAddress municipalityDetails(String municipalityDetails) {
    this.municipalityDetails = municipalityDetails;
    return this;
  }

  /**
   * Frazione o località
   * @return municipalityDetails
  */
  
  public String getMunicipalityDetails() {
    return municipalityDetails;
  }

  public void setMunicipalityDetails(String municipalityDetails) {
    this.municipalityDetails = municipalityDetails;
  }

  public PhysicalAddress province(String province) {
    this.province = province;
    return this;
  }

  /**
   * Provincia in cui si trova l'indirizzo
   * @return province
  */
  
  public String getProvince() {
    return province;
  }

  public void setProvince(String province) {
    this.province = province;
  }

  public PhysicalAddress foreignState(String foreignState) {
    this.foreignState = foreignState;
    return this;
  }

  /**
   * Denominazione paese estero
   * @return foreignState
  */
  
  public String getForeignState() {
    return foreignState;
  }

  public void setForeignState(String foreignState) {
    this.foreignState = foreignState;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    PhysicalAddress physicalAddress = (PhysicalAddress) o;
    return Objects.equals(this.at, physicalAddress.at) &&
        Objects.equals(this.address, physicalAddress.address) &&
        Objects.equals(this.addressDetails, physicalAddress.addressDetails) &&
        Objects.equals(this.zip, physicalAddress.zip) &&
        Objects.equals(this.municipality, physicalAddress.municipality) &&
        Objects.equals(this.municipalityDetails, physicalAddress.municipalityDetails) &&
        Objects.equals(this.province, physicalAddress.province) &&
        Objects.equals(this.foreignState, physicalAddress.foreignState);
  }

  @Override
  public int hashCode() {
    return Objects.hash(at, address, addressDetails, zip, municipality, municipalityDetails, province, foreignState);
  }
}
