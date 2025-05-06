package it.pagopa.pn.timelineservice.dto;

import lombok.*;
import java.util.Objects;

/**
 * Probabile data di inizio del workflow analogico
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder(toBuilder = true)
@EqualsAndHashCode
@ToString
public class ProbableSchedulingAnalogDateDto {
  
  private String iun;
  
  private Integer recIndex;
  
  private java.time.Instant schedulingAnalogDate;

  public ProbableSchedulingAnalogDateDto iun(String iun) {
    this.iun = iun;
    return this;
  }

  /**
   * iun della notifica
   * @return iun
  */
  public String getIun() {
    return iun;
  }

  public void setIun(String iun) {
    this.iun = iun;
  }

  public ProbableSchedulingAnalogDateDto recIndex(Integer recIndex) {
    this.recIndex = recIndex;
    return this;
  }

  /**
   * indice del destinatario
   * @return recIndex
  */
  public Integer getRecIndex() {
    return recIndex;
  }

  public void setRecIndex(Integer recIndex) {
    this.recIndex = recIndex;
  }

  public ProbableSchedulingAnalogDateDto schedulingAnalogDate(java.time.Instant schedulingAnalogDate) {
    this.schedulingAnalogDate = schedulingAnalogDate;
    return this;
  }

  /**
   * data di probabile inizio del workflow analogico
   * @return schedulingAnalogDate
  */
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
    ProbableSchedulingAnalogDateDto ProbableSchedulingAnalogDateDto = (ProbableSchedulingAnalogDateDto) o;
    return Objects.equals(this.iun, ProbableSchedulingAnalogDateDto.iun) &&
        Objects.equals(this.recIndex, ProbableSchedulingAnalogDateDto.recIndex) &&
        Objects.equals(this.schedulingAnalogDate, ProbableSchedulingAnalogDateDto.schedulingAnalogDate);
  }

  @Override
  public int hashCode() {
    return Objects.hash(iun, recIndex, schedulingAnalogDate);
  }
}
