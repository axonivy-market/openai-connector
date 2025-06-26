package com.axonivy.connector.openai.test;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ExtensionContext;

import com.axonivy.connector.openai.constants.OpenAiCommonConstants;
import com.axonivy.connector.openai.context.MultiEnvironmentContextProvider;
import com.openai.connector.openaiData;

import ch.ivyteam.ivy.application.IApplication;
import ch.ivyteam.ivy.bpm.engine.client.BpmClient;
import ch.ivyteam.ivy.bpm.engine.client.ExecutionResult;
import ch.ivyteam.ivy.bpm.engine.client.element.BpmElement;
import ch.ivyteam.ivy.bpm.engine.client.element.BpmProcess;
import ch.ivyteam.ivy.bpm.exec.client.IvyProcessTest;
import ch.ivyteam.ivy.environment.AppFixture;
import ch.ivyteam.ivy.rest.client.RestClients;
import utils.OpenAiUtils;

@IvyProcessTest(enableWebServer = true)
@ExtendWith(MultiEnvironmentContextProvider.class)
public class OpenAiTest {

  private static final String OPEN_AI = "openai";

//  private interface Start {
////    BpmElement CHAT = BpmProcess.path(OPEN_AI).elementName("chatGpt(String)");
////    BpmElement ASSISTANT = BpmProcess.path(OPEN_AI).elementName("getAssistants()");
//  }

  @BeforeEach
  void beforeEach(ExtensionContext context, AppFixture fixture, IApplication app) {
    OpenAiUtils.setUpConfigForContext(context.getDisplayName(), fixture, app);
    // Example: for tests with "everybody" in name, set a flag in the context store
    boolean useEverybody = context.getDisplayName().equals(OpenAiCommonConstants.REAL_CALL_CONTEXT_DISPLAY_NAME);
    context.getStore(ExtensionContext.Namespace.GLOBAL).put("useEverybody", useEverybody);
  }

  @AfterEach
  void afterEach(IApplication app) {
    RestClients clients = RestClients.of(app);
    clients.remove(OPEN_AI);
  }

  @TestTemplate
  public void chatCompletions(BpmClient bpmClient, ExtensionContext context, IApplication app) {
    BpmElement CHAT = BpmProcess.path(OPEN_AI).elementName("chatGpt(String)");
    boolean useEverybody =
        context.getStore(ExtensionContext.Namespace.GLOBAL).getOrDefault("useEverybody", Boolean.class, false);

    var start = bpmClient.start().subProcess(CHAT).withParam("what", "1 + 1 = ?");

    if (useEverybody) {
      start = start.as().everybody();
    }

    ExecutionResult result = start.execute();
    openaiData data = result.data().last();
    assertTrue(data.getAnswer().contains("2"));
  }

}
