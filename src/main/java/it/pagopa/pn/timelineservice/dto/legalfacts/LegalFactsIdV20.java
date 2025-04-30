package it.pagopa.pn.timelineservice.dto.legalfacts;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.annotation.Generated;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.Objects;

/**
 * Chiavi dei documenti generati durante il processo di consegna cartacea
 */
@lombok.Builder(toBuilder = true)
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-04-23T15:16:43.168944600+02:00[Europe/Rome]")
@lombok.ToString
public class LegalFactsIdV20 {

  @JsonProperty("key")
  private String key;

  @JsonProperty("category")
  private LegalFactCategoryV20 category;

  public LegalFactsIdV20 key(String key) {
    this.key = key;
    return this;
  }

  /**
   * Chiave dell'atto opponibile a terzi generato durante il processo di consegna
   * @return key
  */
  @NotNull @Pattern(regexp = "^(safestorage://)?[A-Za-z0-9._-]+$") @Size(max = 512) 
  public String getKey() {
    return key;
  }

  public void setKey(String key) {
    this.key = key;
  }

  public LegalFactsIdV20 category(LegalFactCategoryV20 category) {
    this.category = category;
    return this;
  }

  /**
   * Get category
   * @return category
  */
  @NotNull @Valid 
  public LegalFactCategoryV20 getCategory() {
    return category;
  }

  public void setCategory(LegalFactCategoryV20 category) {
    this.category = category;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    LegalFactsIdV20 legalFactsIdV20 = (LegalFactsIdV20) o;
    return Objects.equals(this.key, legalFactsIdV20.key) &&
        Objects.equals(this.category, legalFactsIdV20.category);
  }

  @Override
  public int hashCode() {
    return Objects.hash(key, category);
  }
}
