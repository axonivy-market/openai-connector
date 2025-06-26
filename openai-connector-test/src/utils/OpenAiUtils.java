package utils;

import static com.axonivy.connector.openai.mock.MockAI.URI;

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;

import com.axonivy.connector.openai.constants.OpenAiCommonConstants;
import java.util.Properties;

import ch.ivyteam.ivy.application.IApplication;
import ch.ivyteam.ivy.environment.AppFixture;
import ch.ivyteam.ivy.environment.Ivy;
import ch.ivyteam.ivy.rest.client.RestClient;
import ch.ivyteam.ivy.rest.client.RestClients;
import ch.ivyteam.ivy.rest.client.security.CsrfHeaderFeature;

public class OpenAiUtils {

  private static final String OPENAI_KEY = "openaiKey";
  public static final String FAKE_KEY = "eyJhbGciOiJIUzI1NiJ9.eyJSb2xlIjoiQWRtaW4iLCJJc3N1ZXIiOiJJc3N1ZXIiLCJVc2VybmFtZSI6IkphdmFJblVzZSIsImV4cCI6MTc0MTA3NTYyMSwiaWF0IjoxNzQxMDc1NjIxfQ.8C5qVVQdEArCJmHgeO9pr87rTaZqb5d9fcSa3shecAI";
  

  public static void setUpConfigForMockServer(AppFixture fixture, IApplication app) {
    fixture.config("RestClients.openai.Url", URI);
    fixture.var("openai.apiKey", FAKE_KEY);
    RestClients clients = RestClients.of(app);
    RestClient openAi = clients.find("openai");
    var testClient = openAi.toBuilder()
      .feature(CsrfHeaderFeature.class.getName())
      .property("AUTH.openaiKey", FAKE_KEY)
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
    String openApiKey = System.getProperty(OPENAI_KEY);
    fixture.var("openai-connector.apiKey", openApiKey);
  }

}
