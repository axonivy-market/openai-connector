package com.axonivy.connector.openai.mock.utils;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class AiAssistanceUtils {
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
  
  private static ObjectNode message(String role, String content) {
    return JsonNodeFactory.instance.objectNode().put("role", role).put("content", content);
  }
  
  public static Entity<JsonNode> buildPayloadFromQuestion(String question, boolean includeSystemPrompt) {
    ArrayNode arrayNode = JsonNodeFactory.instance.arrayNode();
    if (includeSystemPrompt) {
      arrayNode.add(message("system", "SYSTEM_PROMT"));
    }
    arrayNode.add(message("user", question));
    ObjectNode request = completion().set("messages", arrayNode);
    return Entity.entity(request, MediaType.APPLICATION_JSON);
  }
}
