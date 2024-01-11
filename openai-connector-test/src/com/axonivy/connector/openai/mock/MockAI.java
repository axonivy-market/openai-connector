package com.axonivy.connector.openai.mock;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Objects;

import javax.annotation.security.PermitAll;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.io.IOUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import ch.ivyteam.ivy.environment.Ivy;
import ch.ivyteam.ivy.rest.client.config.IvyDefaultJaxRsTemplates;
import io.swagger.v3.oas.annotations.Hidden;

@Path(MockAI.PATH_SUFFIX)
@PermitAll // allow unauthenticated calls
@Hidden // do not show me on swagger-ui or openapi3 resources.
@SuppressWarnings("all")
public class MockAI {

  private static final ObjectMapper MAPPER = new ObjectMapper();

  static final String PATH_SUFFIX = "aiMock";

  // URI where this mock can be reached: to be referenced in tests that use it!
  public static final String URI = "{"+IvyDefaultJaxRsTemplates.APP_URL+"}/api/"+PATH_SUFFIX;

  private final Map<String, JsonNode> examples = Map.of(
    "assist-selection-explain", json(load("assist-selection-explain.json")),
    "assist-selection-explain-reponse", json(load("assist-selection-explain-response.json")),
    "assist-fix", json(load("assist-fix.json")),
    "assist-fix-reponse", json(load("assist-fix-response.json")),
    "assist-insert", json(load("assist-insert.json")),
    "assist-insert-reponse", json(load("assist-insert-response.json")),
    "assist-chat", json(load("assist-chat.json")),
    "assist-chat-reponse", json(load("assist-chat-response.json")),

    // codex beta
    "assist-edit", json(load("assist-edit.json")),
    "assist-edit-reponse", json(load("assist-edit-response.json"))
  );

  private final Map<String, JsonNode> openAIExamples = Map.of(
      "completions", json(load("completions.json")),
      "completions-response", json(load("completions-response.json")),
      "mail-generator", json(load("mail-generator.json")),
      "mail-generator-response", json(load("mail-generator-response.json"))
  );

  @POST
  @Path("completions")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public Response assist(JsonNode request) {
    var in = input(request, examples);
    Ivy.log().info("in="+in+" /from="+request);
    var node= examples.get(in+"-reponse");
    return Response.ok()
      .entity(node)
      .build();
  }

  @POST
  @Path("edits")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public Response edit(JsonNode request) {
    var in = input(request, examples);
    Ivy.log().info("in="+in+" /from="+request);
    var node= examples.get(in+"-reponse");
    return Response.ok()
      .entity(node)
      .build();
  }

  private String input(JsonNode request, Map<String, JsonNode> examples) {
    for(var entry : examples.entrySet()) {
      if (Objects.equals(entry.getValue(), request)) {
        return entry.getKey();
      }
    }
    return null;
  }

   @POST
  @Path("chat/completions")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public Response chat(JsonNode request) {
    var in = input(request, openAIExamples);
    var node = openAIExamples.get(in+"-response");
    return Response.ok()
      .entity(node)
      .build();
  }

  public static String load(String json) {
    try (var is = MockAI.class.getResourceAsStream("json/"+json)) {
      if (is == null) {
        throw new RuntimeException("The json file '"+json+"' does not exist.");
      }
      return IOUtils.toString(is, StandardCharsets.UTF_8);
    } catch (IOException ex) {
      throw new RuntimeException("Failed to read json "+json, ex);
    }
  }

  public static JsonNode json(String raw) {
    try {
      return MAPPER.readTree(raw);
    } catch (JsonProcessingException ex) {
      throw new RuntimeException("Failed to parse JSON from string: "+raw, ex);
    }
  }
}
