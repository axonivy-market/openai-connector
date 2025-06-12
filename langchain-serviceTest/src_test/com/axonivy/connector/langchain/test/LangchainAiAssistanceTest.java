package com.axonivy.connector.langchain.test;

import static org.assertj.core.api.Assertions.assertThat;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import org.junit.jupiter.api.Test;

import ch.ivyteam.ivy.environment.Ivy;
import ch.ivyteam.ivy.environment.IvyTest;

@IvyTest(enableWebServer = true)
public class LangchainAiAssistanceTest {

  @Test
  void chat() {
    WebTarget client = Ivy.rest().client("langchain-service");
    Response response = client.path("chat")
        .queryParam("prompt", "Is Java fit to interact with major LLM's?")
        .request()
        .get();
    String answer = response.readEntity(String.class);
    assertThat(answer)
        .containsIgnoringCase("yes");
  }

}
