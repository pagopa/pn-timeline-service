package it.pagopa.pn.timelineservice.config;

import org.graalvm.nativeimage.hosted.Feature;
import org.graalvm.nativeimage.hosted.RuntimeReflection;
import software.amazon.awssdk.enhanced.dynamodb.DefaultAttributeConverterProvider;
import software.amazon.awssdk.enhanced.dynamodb.internal.mapper.BeanTableSchemaAttributeTags;

public class DynamodbEnhancedFeature implements Feature {
  @Override
  public void beforeAnalysis(BeforeAnalysisAccess access) {
    try {
      RuntimeReflection.register(DefaultAttributeConverterProvider.class.getConstructor());
      RuntimeReflection.register(BeanTableSchemaAttributeTags.class);
      RuntimeReflection.register(BeanTableSchemaAttributeTags.class.getMethods());
    } catch (NoSuchMethodException ex) {
      throw new IllegalStateException(
          "SVM Substitution: Unable to register method for reflection", ex);
    }
  }
}
