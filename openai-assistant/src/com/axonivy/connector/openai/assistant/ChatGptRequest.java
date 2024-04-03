package com.axonivy.connector.openai.assistant;

import java.util.function.Supplier;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status.Family;

import com.axonivy.connector.openai.assistant.OpenAiConfig.Key;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class ChatGptRequest {

  private final Supplier<WebTarget> client;
  private int maxTokens = 1024;

  public ChatGptRequest(Supplier<WebTarget> client) {
    this.client = client;
  }

  public ChatGptRequest maxTokens(int tokens) {
    this.maxTokens = tokens;
    return this;
  }
  
  public String getModels() {
    WebTarget chat = client.get().path("models");
    Response resp = chat.request().get();
    return read(resp);
  }

  public String ask(String context, String question) {
    WebTarget chat = client.get().path("chat/completions");
    ObjectNode message = JsonNodeFactory.instance.objectNode();
    ArrayNode arrayNode = JsonNodeFactory.instance.arrayNode();
    message.put("role", "user");
    message.put("content", context + "\n\n"+question);
    arrayNode.add(message);
    ObjectNode request = completion().set("messages", arrayNode);
    var payload = Entity.entity(request, MediaType.APPLICATION_JSON);
    Response resp = chat.request().post(payload);
    return read(resp);
  }

  private String read(Response resp) {
    JsonNode result = resp.readEntity(JsonNode.class);
    if (resp.getStatusInfo().getFamily() == Family.SUCCESSFUL) {
      if (result.get("choices") instanceof ArrayNode choices) {
        if (choices.get(0).get("message") instanceof ObjectNode message) {
          return message.get("content").asText();
        }
        return choices.get(0).get("text").asText();
      }
    }
    return result.toPrettyString();
  }

  private ObjectNode completion() {
    OpenAiConfig repo = new OpenAiConfig();
    ObjectNode request = JsonNodeFactory.instance.objectNode();
    request.put("model", repo.getValue(Key.MODEL).orElse("gpt-4"));
    request.put("max_tokens", maxTokens);
    request.put("temperature", 1);
    request.put("top_p", 1);
    request.put("frequency_penalty", 0);
    request.put("presence_penalty", 0);
    return request;
  }

  public String edit(String code, String instruction) {
    WebTarget edits = client.get().path("edits");
    ObjectNode request = edit()
      .put("input", code)
      .put("instruction", instruction);
    var payload = Entity.entity(request, MediaType.APPLICATION_JSON);
    Response resp = edits.request().post(payload);
    return read(resp);
  }

  private ObjectNode edit() {
    ObjectNode request = JsonNodeFactory.instance.objectNode();
    request.put("model", "code-davinci-edit-001");
    request.put("temperature", 1);
    request.put("top_p", 1);
    return request;
  }

}
