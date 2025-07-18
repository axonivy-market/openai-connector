package com.axonivy.connector.openai.test;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ExtensionContext;

import com.axonivy.connector.openai.constants.OpenAiTestConstants;
import com.axonivy.connector.openai.context.MultiEnvironmentContextProvider;
import com.axonivy.connector.openai.utils.OpenAiTestUtils;
import com.openai.connector.openaiData;

import ch.ivyteam.ivy.application.IApplication;
import ch.ivyteam.ivy.bpm.engine.client.BpmClient;
import ch.ivyteam.ivy.bpm.engine.client.ExecutionResult;
import ch.ivyteam.ivy.bpm.engine.client.element.BpmElement;
import ch.ivyteam.ivy.bpm.engine.client.element.BpmProcess;
import ch.ivyteam.ivy.bpm.exec.client.IvyProcessTest;
import ch.ivyteam.ivy.environment.AppFixture;
import ch.ivyteam.ivy.rest.client.RestClients;

@IvyProcessTest(enableWebServer = true)
@ExtendWith(MultiEnvironmentContextProvider.class)
public class OpenAiTest {

  @BeforeEach
  void beforeEach(ExtensionContext context, AppFixture fixture, IApplication app) {
    OpenAiTestUtils.setUpConfigForContext(context.getDisplayName(), fixture, app);
  }

  @AfterEach
  void afterEach(AppFixture fixture, IApplication app) {
    RestClients clients = RestClients.of(app);
    clients.remove(OpenAiTestConstants.OPEN_AI);
  }

  @TestTemplate
  public void chatCompletions(BpmClient bpmClient, ExtensionContext context) {
    BpmElement CHAT = BpmProcess.path(OpenAiTestConstants.OPEN_AI).elementName("chatGpt(String)");

    var start = bpmClient.start().subProcess(CHAT).withParam("what", "1 + 1 = ?");

    if (context.getDisplayName().equals(OpenAiTestConstants.REAL_CALL_CONTEXT_DISPLAY_NAME)) {
      start = start.as().everybody();
    }

    ExecutionResult result = start.execute();
    openaiData data = result.data().last();
    assertTrue(data.getAnswer().contains("2"));
  }
}
