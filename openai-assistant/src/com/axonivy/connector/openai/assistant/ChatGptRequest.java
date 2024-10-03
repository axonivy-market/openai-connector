package com.axonivy.connector.openai.assistant;

import java.util.Map;
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
  private final String SYSTEM_PROMPT = """
        You are an assistant that updates code based on user instructions. Your task is to modify the provided code snippet according to the user's requirements, ensuring that the new elements are correctly integrated into the existing structure. The result should be plain text, not in markdown format.
        Instructions:
        Understand the User's Code: Analyze the provided code to understand its structure and existing elements.
        Apply the User's Request: Make the required changes to the code based on the user's request. Ensure that new components are integrated correctly into the existing layout.
        Provide the Complete Updated Code: Return the entire code snippet, including the updated or newly added components. Ensure that the code is well-formatted and integrates seamlessly with the existing content.
      """;
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

//  public String ask(String context, String question) {
//    WebTarget chat = client.get().path("chat/completions");
//    ArrayNode arrayNode = JsonNodeFactory.instance.arrayNode();
//    arrayNode.add(message("system", SYSTEM_PROMPT));
//    arrayNode.add(message("user", String.format("%s \n\n %s", context, question)));
//    ObjectNode request = completion().set("messages", arrayNode);
//    var payload = Entity.entity(request, MediaType.APPLICATION_JSON);
//    Response resp = chat.request().post(payload);
//    return read(resp);
//  }
  
	public String ask(String context, String question) {
		WebTarget chat = client.get().path("");
		ObjectNode request = JsonNodeFactory.instance.objectNode();
	    request.put("process_json", context);
	    request.put("request", question);
		var payload = Entity.entity(request, MediaType.APPLICATION_JSON);
		Response resp = chat.request().post(payload);
		return resp.readEntity(JsonNode.class).toPrettyString();
	}

  private ObjectNode message(String role, String content) {
    return JsonNodeFactory.instance.objectNode().put("role", role).put("content", content);
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
    ObjectNode request = JsonNodeFactory.instance.objectNode();
    request.put("model", model());
    request.put("max_tokens", maxTokens);
    request.put("temperature", 1);
    request.put("top_p", 1);
    request.put("frequency_penalty", 0);
    request.put("presence_penalty", 0);
    return request;
  }

  private String model() {
    OpenAiConfig repo = new OpenAiConfig();
    return repo.getValue(Key.MODEL).orElse("gpt-3.5-turbo");
  }

}
