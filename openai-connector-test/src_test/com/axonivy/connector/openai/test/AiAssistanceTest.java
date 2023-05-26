package com.axonivy.connector.openai.test;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.axonivy.connector.openai.mock.MockAI;
import com.fasterxml.jackson.databind.JsonNode;

import ch.ivyteam.ivy.application.IApplication;
import ch.ivyteam.ivy.environment.AppFixture;
import ch.ivyteam.ivy.environment.Ivy;
import ch.ivyteam.ivy.environment.IvyTest;
import ch.ivyteam.ivy.rest.client.RestClient;
import ch.ivyteam.ivy.rest.client.RestClients;
import ch.ivyteam.ivy.rest.client.security.CsrfHeaderFeature;

@IvyTest
public class AiAssistanceTest {

  private static final UUID OPEN_AI = UUID.fromString("6840e778-eb27-42a0-afdc-87588ffae871");

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
  void explain() {
    JsonNode explain = MockAI.json(MockAI.load("assist-selection-explain.json"));
    WebTarget client = Ivy.rest().client(OPEN_AI);
    Entity<JsonNode> request = Entity.entity(explain, MediaType.APPLICATION_JSON);
    JsonNode result = client.path("completions").request()
      .post(request).readEntity(JsonNode.class);
    assertThat(result.toPrettyString())
      .contains("Primefaces");
  }

}
