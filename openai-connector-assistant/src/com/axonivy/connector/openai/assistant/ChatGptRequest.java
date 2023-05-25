package com.axonivy.connector.openai.assistant;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class ChatGptRequest {

  private final WebTarget client;

  public ChatGptRequest(WebTarget client) {
    this.client = client;
  }

  public String ask(String context, String question) {
    WebTarget chat = client.path("chat/completions");
    ObjectNode request = JsonNodeFactory.instance.objectNode();
    var payload = Entity.entity(request, MediaType.APPLICATION_JSON);
    String response = chat.request().post(payload).readEntity(String.class);
    return "nothing serious yet :=) \n" + response;
  }


}
