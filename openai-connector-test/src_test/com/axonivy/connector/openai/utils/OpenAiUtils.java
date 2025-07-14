package com.axonivy.connector.openai.utils;

import static com.axonivy.connector.openai.mock.MockAI.URI;

import com.axonivy.connector.openai.constants.OpenAiCommonConstants;

import ch.ivyteam.ivy.application.IApplication;
import ch.ivyteam.ivy.environment.AppFixture;
import ch.ivyteam.ivy.rest.client.RestClient;
import ch.ivyteam.ivy.rest.client.RestClients;
import ch.ivyteam.ivy.rest.client.security.CsrfHeaderFeature;

public class OpenAiUtils {

  public static void setUpConfigForMockServer(AppFixture fixture, IApplication app) {
    fixture.config("RestClients.openai.Url", URI);
    fixture.var("openai.apiKey", OpenAiCommonConstants.FAKE_KEY);
    RestClients clients = RestClients.of(app);
    RestClient openAi = clients.find(OpenAiCommonConstants.OPEN_AI);
    var testClient = openAi.toBuilder()
        .feature(CsrfHeaderFeature.class.getName())
        .property("AUTH.openaiKey", OpenAiCommonConstants.FAKE_KEY)
        .toRestClient();
    clients.set(testClient);
  }

  public static void setUpConfigForContext(String contextName, AppFixture fixture, IApplication app) {
    switch (contextName) {
      case OpenAiCommonConstants.REAL_CALL_CONTEXT_DISPLAY_NAME:
        setUpConfigForApiTest(fixture);
        break;
      case OpenAiCommonConstants.MOCK_SERVER_CONTEXT_DISPLAY_NAME:
        setUpConfigForMockServer(fixture, app);
        break;
      default:
        break;
    }
  }

  public static void setUpConfigForApiTest(AppFixture fixture) {
    String openApiKey = System.getProperty(OpenAiCommonConstants.OPENAI_KEY);
    fixture.var("openai-connector.apiKey", openApiKey);
  }

}
