package it.pagopa.pn.timelineservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.annotation.Generated;
import javax.validation.constraints.NotNull;
import java.util.Objects;

/**
 * Indirizzo di invio della notifica
 */
@lombok.Builder(toBuilder = true)
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-04-23T15:16:39.518831600+02:00[Europe/Rome]")
@lombok.ToString
public class DigitalAddress {

  @JsonProperty("type")
  @lombok.ToString.Exclude
  private String type;

  @JsonProperty("address")
  @lombok.ToString.Exclude
  private String address;

  public DigitalAddress type(String type) {
    this.type = type;
    return this;
  }

  /**
   * tipo di indirizzo PEC, REM, SERCQ, SMS, EMAIL, APPIO ...
   * @return type
  */
  @NotNull 
  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public DigitalAddress address(String address) {
    this.address = address;
    return this;
  }

  /**
   * account@domain
   * @return address
  */
  @NotNull 
  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    DigitalAddress digitalAddress = (DigitalAddress) o;
    return Objects.equals(this.type, digitalAddress.type) &&
        Objects.equals(this.address, digitalAddress.address);
  }

  @Override
  public int hashCode() {
    return Objects.hash(type, address);
  }
}
