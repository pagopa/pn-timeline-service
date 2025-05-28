package it.pagopa.pn.timelineservice.service.mapper;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(value = {"elementTimestamp"})
public abstract class IgnoreFieldsMixin {
}
