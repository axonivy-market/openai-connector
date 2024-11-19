package com.axonivy.connector.openai.test;

import static com.axonivy.connector.openai.mock.MockAI.URI;
import static com.axonivy.connector.openai.mock.MockAI.json;
import static com.axonivy.connector.openai.mock.MockAI.load;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import ch.ivyteam.ivy.application.IApplication;
import ch.ivyteam.ivy.environment.AppFixture;
import ch.ivyteam.ivy.environment.Ivy;
import ch.ivyteam.ivy.environment.IvyTest;
import ch.ivyteam.ivy.rest.client.RestClient;
import ch.ivyteam.ivy.rest.client.RestClients;
import ch.ivyteam.ivy.rest.client.security.CsrfHeaderFeature;

@IvyTest(enableWebServer = true)
public class AiAssistanceTest {

  private static final UUID OPEN_AI = UUID.fromString("6840e778-eb27-42a0-afdc-87588ffae871");

  @BeforeEach
  void setup(AppFixture fixture, IApplication app) {
    fixture.config("RestClients.openai.Url", URI);
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
    JsonNode explain = json(load("assist-selection-explain.json"));
    JsonNode result = assist(explain);
    assertThat(result.toPrettyString())
      .contains("Primefaces");
  }

  @Test
  void fix() {
    JsonNode fix = json(load("assist-fix.json"));
    JsonNode result = assist(fix);
    assertThat(result.toPrettyString())
      .as("returns fixed quoting statements around el-expression")
      .contains("value=\\\"#{mail.subject}\\\" />");
  }

  @Test
  void insert() {
    JsonNode insert = json(load("assist-insert.json"));
    JsonNode result = assist(insert);
    assertThat(result.toPrettyString())
      .as("writes a complex selectOne for the user")
      .contains("<p:selectOneMenu id=\\\"selectBrand\\\"");
  }

  @Test
  void chat() {
    JsonNode chat = json(load("assist-chat.json"));
    JsonNode result = assist(chat);
    assertThat(result.toPrettyString())
      .as("complains about inline CSS")
      .contains("Inline styling should be avoided");
  }

  @Test
  void mailGenerator() {
    JsonNode chat = json(load("mail-generator.json"));
    JsonNode result = chatAssist(chat);
    assertThat(result.toPrettyString())
        .isNotEmpty();
  }

  @Test
  void askWithOutSystemPromt() {
    JsonNode result = assistWithQuestion("insert a combobox to pick a brand out of: Mercedes, BMW or Tesla", false);
    assertThat(result.toPrettyString())
    .isNotEmpty();
  }

  private static JsonNode assist(JsonNode quest) {
    WebTarget client = Ivy.rest().client(OPEN_AI);
    Entity<JsonNode> request = Entity.entity(quest, MediaType.APPLICATION_JSON);
    JsonNode result = client.path("completions").request()
      .post(request).readEntity(JsonNode.class);
    return result;
  }

  private static JsonNode chatAssist(JsonNode quest) {
    WebTarget client = Ivy.rest().client(OPEN_AI);
    Entity<JsonNode> request = Entity.entity(quest, MediaType.APPLICATION_JSON);
    JsonNode result = client.path("chat/completions").request()
      .post(request).readEntity(JsonNode.class);
    return result;
  }
  
  private static JsonNode assistWithQuestion(String question, boolean includeSystemPrompt) {
    WebTarget client = Ivy.rest().client(OPEN_AI);
    Entity<JsonNode> request = buildPayloadFromQuestion(question, includeSystemPrompt);
    Ivy.log().warn("here");
    JsonNode result = client.path("chat/completions").request()
        .post(request).readEntity(JsonNode.class);
    return result;
  }
  
  private static Entity<JsonNode> buildPayloadFromQuestion(String question, boolean includeSystemPrompt) {
    ArrayNode arrayNode = JsonNodeFactory.instance.arrayNode();
    if (includeSystemPrompt) {
      arrayNode.add(message("system", "SYSTEM_PROMT"));
    }
    arrayNode.add(message("user", question));
    ObjectNode request = completion().set("messages", arrayNode);
    return Entity.entity(request, MediaType.APPLICATION_JSON);
  }
  
  private static ObjectNode message(String role, String content) {
    return JsonNodeFactory.instance.objectNode().put("role", role).put("content", content);
  }
  
  private static ObjectNode completion() {
    ObjectNode request = JsonNodeFactory.instance.objectNode();
    request.put("model", "gpt-3.5-turbo");
    request.put("temperature", 1);
    request.put("top_p", 1);
    request.put("frequency_penalty", 0);
    request.put("presence_penalty", 0);
    request.put("max_tokens", 1024);
    return request;
  }

}
