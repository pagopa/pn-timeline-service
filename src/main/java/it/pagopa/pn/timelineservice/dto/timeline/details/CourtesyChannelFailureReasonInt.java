package it.pagopa.pn.timelineservice.dto.timeline.details;

import lombok.Getter;

@Getter
public enum CourtesyChannelFailureReasonInt {

  EXPECTED_FAILURE("EXPECTED_FAILURE"),

  RETRIES_EXHAUSTED("RETRIES_EXHAUSTED");

  private final String value;

  CourtesyChannelFailureReasonInt(String value) {
    this.value = value;
  }

  @Override
  public String toString() {
    return String.valueOf(value);
  }

}
