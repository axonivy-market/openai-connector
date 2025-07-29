package com.axonivy.connector.openai.context;

import java.util.stream.Stream;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestTemplateInvocationContext;
import org.junit.jupiter.api.extension.TestTemplateInvocationContextProvider;

import com.axonivy.connector.openai.constants.OpenAiTestConstants;

public class MultiEnvironmentContextProvider implements TestTemplateInvocationContextProvider {

  @Override
  public Stream<TestTemplateInvocationContext> provideTestTemplateInvocationContexts(ExtensionContext context) {
    String testEnv = System.getProperty(OpenAiTestConstants.END_TO_END_TESTING_ENVIRONMENT_KEY);
    return switch (testEnv) {
      case OpenAiTestConstants.END_TO_END_TESTING_ENVIRONMENT_VALUE ->
        Stream.of(new TestEnironmentInvocationContext(OpenAiTestConstants.REAL_CALL_CONTEXT_DISPLAY_NAME));
      default ->
        Stream.of(new TestEnironmentInvocationContext(OpenAiTestConstants.MOCK_SERVER_CONTEXT_DISPLAY_NAME));
    };
  }

  @Override
  public boolean supportsTestTemplate(ExtensionContext context) {
    return true;
  }
}
