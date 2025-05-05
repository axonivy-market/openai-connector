package com.axonivy.connector.openai.test;

import static com.axonivy.connector.openai.mock.MockAI.URI;
import static com.axonivy.connector.openai.mock.MockAI.json;
import static com.axonivy.connector.openai.mock.MockAI.load;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.axonivy.connector.openai.mock.utils.AiAssistanceUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.openai.api.v1.client.ListAssistantsResponse;

import ch.ivyteam.ivy.application.IApplication;
import ch.ivyteam.ivy.environment.AppFixture;
import ch.ivyteam.ivy.environment.Ivy;
import ch.ivyteam.ivy.environment.IvyTest;
import ch.ivyteam.ivy.rest.client.RestClient;
import ch.ivyteam.ivy.rest.client.RestClients;
import ch.ivyteam.ivy.rest.client.security.CsrfHeaderFeature;

@IvyTest(enableWebServer = true)
public class AiAssistanceTest {

  // use a valid JWT key; generated from https://www.javainuse.com/jwtgenerator
  static final String FAKE_KEY = "eyJhbGciOiJIUzI1NiJ9.eyJSb2xlIjoiQWRtaW4iLCJJc3N1ZXIiOiJJc3N1ZXIiLCJVc2VybmFtZSI6IkphdmFJblVzZSIsImV4cCI6MTc0MTA3NTYyMSwiaWF0IjoxNzQxMDc1NjIxfQ.8C5qVVQdEArCJmHgeO9pr87rTaZqb5d9fcSa3shecAI";
  
  private static final UUID OPEN_AI = UUID.fromString("6840e778-eb27-42a0-afdc-87588ffae871");

  @BeforeEach
  void setup(AppFixture fixture, IApplication app) {
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
    assertThat(result.toPrettyString()).isNotEmpty();
    assertThat(result.toPrettyString()).as("Provide combobox with generated message")
        .contains("Sure, here is a combobox for you to pick a brand out of Mercedes, BMW, or Tesla");
  }
  
  @Test
  void insertWithSystemPromt() {
    JsonNode result = assistWithQuestion("insert a combobox to pick a brand out of: Mercedes, BMW or Tesla", true);
    assertThat(result.toPrettyString()).isNotEmpty();
    assertThat(result.toPrettyString()).as("Provide combobox with system promt")
        .contains("<select id=\\\"brand-select-without-system-promt\\\"");
  }

  @Test
  void mappingResponseOfAssistants() {
    // Use DeserializationFeature.FAIL_ON_INVALID_SUBTYPE = false to ignore of properties with UNKNOWN source type
    boolean failOnInvalidSubtype = false;
    ListAssistantsResponse result = getAssistantsWithFailOnInvalidSubtypeConfiguration(false);
    assertThat(result).isNotNull();

    // If we use DeserializationFeature.FAIL_ON_INVALID_SUBTYPE = true, objectMapper cannot parse properties properties with UNKNOWN source type
    failOnInvalidSubtype = true;
    result = getAssistantsWithFailOnInvalidSubtypeConfiguration(failOnInvalidSubtype);
    assertThat(result).isNull();
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
    Entity<JsonNode> request = AiAssistanceUtils.buildPayloadFromQuestion(question, includeSystemPrompt);
    JsonNode result = client.path("chat/completions").request()
        .post(request).readEntity(JsonNode.class);
    return result;
  }

  private static ListAssistantsResponse getAssistantsWithFailOnInvalidSubtypeConfiguration(boolean failOnInvalidSubtype) {
    WebTarget client = Ivy.rest().client(OPEN_AI);
    return readListAssistantsResponse(
        client.path("assistants").queryParam("failOnInvalidSubtype", failOnInvalidSubtype).request().get());
  }

  private static ListAssistantsResponse readListAssistantsResponse(Response response) {
    if (response.getStatus() == 200) {
      return response.readEntity(ListAssistantsResponse.class);
    } else {
      return null;
    }
  }
}
