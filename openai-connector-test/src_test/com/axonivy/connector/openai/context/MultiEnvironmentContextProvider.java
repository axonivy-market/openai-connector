package com.axonivy.connector.openai.context;

import java.util.stream.Stream;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestTemplateInvocationContext;
import org.junit.jupiter.api.extension.TestTemplateInvocationContextProvider;

import com.axonivy.connector.openai.constants.OpenAiCommonConstants;

public class MultiEnvironmentContextProvider implements TestTemplateInvocationContextProvider {

  @Override
  public Stream<TestTemplateInvocationContext> provideTestTemplateInvocationContexts(ExtensionContext context) {
    return Stream.of(new TestEnironmentInvocationContext(OpenAiCommonConstants.MOCK_SERVER_CONTEXT_DISPLAY_NAME),
        new TestEnironmentInvocationContext(OpenAiCommonConstants.REAL_CALL_CONTEXT_DISPLAY_NAME));
  }

  @Override
  public boolean supportsTestTemplate(ExtensionContext context) {
    return true;
  }
}
