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
public class GetAssistantTest {

  @BeforeEach
  void beforeEach(ExtensionContext context, AppFixture fixture, IApplication app) {
    OpenAiUtils.setUpConfigForContext(context.getDisplayName(), fixture, app);
  }

  @AfterEach
  void afterEach(IApplication app) {
    RestClients clients = RestClients.of(app);
    clients.remove(OpenAiCommonConstants.OPEN_AI);
  }

  @TestTemplate
  public void getAssisstants(BpmClient bpmClient) {
    BpmElement ASSISTANT = BpmProcess.path(OpenAiCommonConstants.OPEN_AI).elementName("getAssistants()");

    var start = bpmClient.start().subProcess(ASSISTANT);
    ExecutionResult result = start.execute();
    openaiData data = result.data().last();
    assertTrue(data.getAssistants().size() >= 2);
  }
}
