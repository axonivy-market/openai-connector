package com.axonivy.connector.openai.test;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static com.axonivy.utils.e2etest.enums.E2EEnvironment.REAL_SERVER;
import java.math.BigDecimal;

import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtensionContext;

import com.axonivy.connector.openai.BaseSetup;
import com.axonivy.connector.openai.constants.OpenAiTestConstants;
import com.openai.api.v1.client.CreateChatCompletionRequest.ModelEnum;
import com.openai.connector.openaiData;

import ch.ivyteam.ivy.bpm.engine.client.BpmClient;
import ch.ivyteam.ivy.bpm.engine.client.ExecutionResult;
import ch.ivyteam.ivy.bpm.engine.client.element.BpmElement;
import ch.ivyteam.ivy.bpm.engine.client.element.BpmProcess;

public class OpenAiTest extends BaseSetup {

  @TestTemplate
  public void chatCompletions(BpmClient bpmClient, ExtensionContext context) {
    BpmElement CHAT = BpmProcess.path(OpenAiTestConstants.OPEN_AI).elementName("chatGpt(String,ModelEnum,BigDecimal)");

    var start = bpmClient.start().subProcess(CHAT).withParam("what", "1 + 1 = ?")
        .withParam("model", ModelEnum.GPT_3_5_TURBO).withParam("temperature", BigDecimal.ONE);

    if (context.getDisplayName().equals(REAL_SERVER.getDisplayName())) {
      start = start.as().everybody();
    }

    ExecutionResult result = start.execute();
    openaiData data = result.data().last();
    assertTrue(data.getAnswer().contains("2"));
  }
}
