package com.axonivy.connector.openai.assistant;

import java.util.function.Supplier;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status.Family;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class ChatGptRequest {

  private final Supplier<WebTarget> client;

  public ChatGptRequest(Supplier<WebTarget> client) {
    this.client = client;
  }

  public String ask(String context, String question) {
    WebTarget chat = client.get().path("completions");
    ObjectNode request = completion()
      .put("prompt", context + "\n\n"+question);
    var payload = Entity.entity(request, MediaType.APPLICATION_JSON);
    Response resp = chat.request().post(payload);
    return read(resp);
  }

  private String read(Response resp) {
    JsonNode result = resp.readEntity(JsonNode.class);
    if (resp.getStatusInfo().getFamily() == Family.SUCCESSFUL) {
      if (result.get("choices") instanceof ArrayNode choices) {
        return choices.get(0).get("text").asText();
      }
    }
    return result.toPrettyString();
  }

  private ObjectNode completion() {
    ObjectNode request = JsonNodeFactory.instance.objectNode();
    request.put("model", "text-davinci-003");
    request.put("max_tokens", 1024);// raise
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
