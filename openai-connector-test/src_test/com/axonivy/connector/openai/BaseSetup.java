package com.axonivy.connector.openai;

import static com.axonivy.connector.openai.mock.MockAI.URI;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ExtensionContext;

import com.axonivy.connector.openai.constants.OpenAiTestConstants;
import com.axonivy.utils.e2etest.context.MultiEnvironmentContextProvider;
import com.axonivy.utils.e2etest.utils.E2ETestUtils;

import ch.ivyteam.ivy.application.IApplication;
import ch.ivyteam.ivy.bpm.exec.client.IvyProcessTest;
import ch.ivyteam.ivy.environment.AppFixture;
import ch.ivyteam.ivy.rest.client.RestClient;
import ch.ivyteam.ivy.rest.client.RestClients;
import ch.ivyteam.ivy.rest.client.security.CsrfHeaderFeature;

@IvyProcessTest(enableWebServer = true)
@ExtendWith(MultiEnvironmentContextProvider.class)
public class BaseSetup {

  @BeforeEach
  void beforeEach(ExtensionContext context, AppFixture fixture, IApplication app) {
    E2ETestUtils.determineConfigForContext(context.getDisplayName(), runRealEnv(fixture), runMockEnv(fixture, app));
  }

  @AfterEach
  void afterEach(AppFixture fixture, IApplication app) {
    RestClients clients = RestClients.of(app);
    clients.remove(OpenAiTestConstants.OPEN_AI);
  }

  private Runnable runRealEnv(AppFixture fixture) {
    return () -> {
      String openApiKey = System.getProperty(OpenAiTestConstants.OPENAI_KEY);
      fixture.var("openaiConnector.apiKey", openApiKey);
    };
  }

  private Runnable runMockEnv(AppFixture fixture, IApplication app) {
    return () -> {
      fixture.config("RestClients.openai.Url", URI);
      fixture.var("openai.apiKey", OpenAiTestConstants.FAKE_KEY);
      RestClients clients = RestClients.of(app);
      RestClient openAi = clients.find(OpenAiTestConstants.OPEN_AI);
      var testClient = openAi.toBuilder().feature(CsrfHeaderFeature.class.getName())
          .property("AUTH.openaiKey", OpenAiTestConstants.FAKE_KEY).toRestClient();
      clients.set(testClient);
    };
  }
}
