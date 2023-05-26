package com.axonivy.connector.openai.test;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.axonivy.connector.openai.mock.MockAI;
import com.openai.connector.openaiData;

import ch.ivyteam.ivy.application.IApplication;
import ch.ivyteam.ivy.bpm.engine.client.BpmClient;
import ch.ivyteam.ivy.bpm.engine.client.ExecutionResult;
import ch.ivyteam.ivy.bpm.engine.client.element.BpmElement;
import ch.ivyteam.ivy.bpm.engine.client.element.BpmProcess;
import ch.ivyteam.ivy.bpm.exec.client.IvyProcessTest;
import ch.ivyteam.ivy.environment.AppFixture;
import ch.ivyteam.ivy.rest.client.RestClient;
import ch.ivyteam.ivy.rest.client.RestClients;
import ch.ivyteam.ivy.rest.client.security.CsrfHeaderFeature;

@IvyProcessTest
public class OpenAiTest{

  private static final BpmProcess OPEN_AI = BpmProcess.path("openai");

  private interface Start {
    BpmElement CHAT = OPEN_AI.elementName("chatGpt(String)");
  }

  @BeforeEach
  void setup(AppFixture fixture, IApplication app) {
    fixture.config("RestClients.openai.Url", MockAI.URI);
    fixture.var("openai.apiKey", "notMyKey");
    RestClients clients = RestClients.of(app);
    RestClient openAi = clients.find("openai");
    var testClient = openAi.toBuilder()
      .feature(CsrfHeaderFeature.class.getName())
      .property("AUTH.openaiKey", "notMyKey")
      .toRestClient();
    clients.set(testClient);
  }

  @Test
  public void chatCompletions(BpmClient bpmClient) {
    ExecutionResult result = bpmClient.start()
      .subProcess(Start.CHAT)
      .withParam("what", "the meaning of life")
      .execute();
    openaiData data = result.data().last();
    assertThat(data.getAnswer())
      .as("don't overestimate AI answers, at times they answer very poorly")
      .contains("refers to the purpose, significance, or reason for existence");
  }

}
