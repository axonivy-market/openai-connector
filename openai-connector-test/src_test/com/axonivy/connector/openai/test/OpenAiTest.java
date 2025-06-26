package com.axonivy.connector.openai.test;

import static com.axonivy.connector.openai.test.AiAssistanceTest.FAKE_KEY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ExtensionContext;

import com.axonivy.connector.openai.constants.OpenAiCommonConstants;
import com.axonivy.connector.openai.context.MultiEnvironmentContextProvider;
import com.axonivy.connector.openai.mock.MockAI;
import com.openai.connector.openaiData;

import ch.ivyteam.ivy.application.IApplication;
import ch.ivyteam.ivy.bpm.engine.client.BpmClient;
import ch.ivyteam.ivy.bpm.engine.client.ExecContext;
import ch.ivyteam.ivy.bpm.engine.client.ExecutionResult;
import ch.ivyteam.ivy.bpm.engine.client.element.BpmElement;
import ch.ivyteam.ivy.bpm.engine.client.element.BpmProcess;
import ch.ivyteam.ivy.bpm.engine.client.start.ByBuilder;
import ch.ivyteam.ivy.bpm.exec.client.IvyProcessTest;
import ch.ivyteam.ivy.environment.AppFixture;
import ch.ivyteam.ivy.environment.Ivy;
import ch.ivyteam.ivy.rest.client.RestClient;
import ch.ivyteam.ivy.rest.client.RestClients;
import ch.ivyteam.ivy.rest.client.security.CsrfHeaderFeature;
import utils.OpenAiUtils;

@IvyProcessTest(enableWebServer = true)
@ExtendWith(MultiEnvironmentContextProvider.class)
public class OpenAiTest {

  private static final BpmProcess OPEN_AI = BpmProcess.path("openai");

  private interface Start {
    BpmElement CHAT = OPEN_AI.elementName("chatGpt(String)");
  }

//   @BeforeEach
//   void setup(AppFixture fixture, IApplication app) {
//   fixture.config("RestClients.openai.Url", MockAI.URI);
//   fixture.var("openai.apiKey", FAKE_KEY);
//   RestClients clients = RestClients.of(app);
//   RestClient openAi = clients.find("openai");
//   var testClient = openAi.toBuilder().feature(CsrfHeaderFeature.class.getName()).property("AUTH.openaiKey", FAKE_KEY)
//   .toRestClient();
//   clients.set(testClient);
//   }
//   
//   @Test
//   public void chatCompletions(BpmClient bpmClient) {
//     ExecutionResult result = bpmClient.start().subProcess(Start.CHAT)
//         // .withParam("what", "1 + 1 = ?")
//         .withParam("what", "the meaning of life")
//         .as().everybody()
//         .execute();
//     
//     openaiData data = result.data().last();
//     assertThat(data.getAnswer()).as("don't overestimate AI answers, at times they answer very poorly")
//         .contains("refers to the purpose, significance, or reason for existence");
//   }

  @BeforeEach
  void beforeEach(ExtensionContext context, AppFixture fixture, IApplication app) {
    OpenAiUtils.setUpConfigForContext(context.getDisplayName(), fixture, app);
 // Example: for tests with "everybody" in name, set a flag in the context store
    boolean useEverybody = context.getDisplayName().equals(OpenAiCommonConstants.REAL_CALL_CONTEXT_DISPLAY_NAME);
    context.getStore(ExtensionContext.Namespace.GLOBAL).put("useEverybody", useEverybody);
  }


   
   
  @TestTemplate
  public void chatCompletions(BpmClient bpmClient, ExtensionContext context, IApplication app) {
//    BpmClient bpmClient2 = new BpmClient(new ExecContext(app));
      boolean useEverybody = context.getStore(ExtensionContext.Namespace.GLOBAL)
          .getOrDefault("useEverybody", Boolean.class, false);
      
      var start = bpmClient.start().subProcess(Start.CHAT);
//          .withParam("what", "the meaning of life");

      if (useEverybody) {
          start = start.as().everybody();
      } 
//      Ivy.log().warn(Ivy.var().get("openai-connector.apiKey"));
//      ExecutionResult result = start.execute();
      openaiData data = start.execute("the meaning of life").data().last();
      assertThat(data.getAnswer())
          .as("don't overestimate AI answers, at times they answer very poorly")
          .contains("refers to the purpose, significance, or reason for existence");
  }

}
