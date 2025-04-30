package it.pagopa.pn.timelineservice.dto.ext.notification;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.format.annotation.DateTimeFormat;

import javax.annotation.Generated;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Objects;

/**
 * Probabile data di inizio del workflow analogico
 */
@lombok.Builder(toBuilder = true)
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-04-23T15:16:39.518831600+02:00[Europe/Rome]")
@lombok.ToString
public class ProbableSchedulingAnalogDateResponse {

  @JsonProperty("iun")
  private String iun;

  @JsonProperty("recIndex")
  private Integer recIndex;

  @JsonProperty("schedulingAnalogDate")
  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private java.time.Instant schedulingAnalogDate;

  public ProbableSchedulingAnalogDateResponse iun(String iun) {
    this.iun = iun;
    return this;
  }

  /**
   * iun della notifica
   * @return iun
  */
  @NotNull 
  public String getIun() {
    return iun;
  }

  public void setIun(String iun) {
    this.iun = iun;
  }

  public ProbableSchedulingAnalogDateResponse recIndex(Integer recIndex) {
    this.recIndex = recIndex;
    return this;
  }

  /**
   * indice del destinatario
   * @return recIndex
  */
  @NotNull 
  public Integer getRecIndex() {
    return recIndex;
  }

  public void setRecIndex(Integer recIndex) {
    this.recIndex = recIndex;
  }

  public ProbableSchedulingAnalogDateResponse schedulingAnalogDate(java.time.Instant schedulingAnalogDate) {
    this.schedulingAnalogDate = schedulingAnalogDate;
    return this;
  }

  /**
   * data di probabile inizio del workflow analogico
   * @return schedulingAnalogDate
  */
  @NotNull @Valid 
  public java.time.Instant getSchedulingAnalogDate() {
    return schedulingAnalogDate;
  }

  public void setSchedulingAnalogDate(java.time.Instant schedulingAnalogDate) {
    this.schedulingAnalogDate = schedulingAnalogDate;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ProbableSchedulingAnalogDateResponse probableSchedulingAnalogDateResponse = (ProbableSchedulingAnalogDateResponse) o;
    return Objects.equals(this.iun, probableSchedulingAnalogDateResponse.iun) &&
        Objects.equals(this.recIndex, probableSchedulingAnalogDateResponse.recIndex) &&
        Objects.equals(this.schedulingAnalogDate, probableSchedulingAnalogDateResponse.schedulingAnalogDate);
  }

  @Override
  public int hashCode() {
    return Objects.hash(iun, recIndex, schedulingAnalogDate);
  }
}
