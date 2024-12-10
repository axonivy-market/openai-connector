package com.axonivy.connector.openai.assistant;

import java.util.function.Supplier;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status.Family;

import com.axonivy.connector.openai.assistant.OpenAiConfig.Key;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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
  
  private final ObjectMapper mapper = new ObjectMapper();

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
    return sendRequest(context, question, false);
  }

  public String insert(String context, String question) {
    return sendRequest(context, question, true);
  }

  private String sendRequest(String context, String question, boolean includeSystemPrompt) {
    WebTarget chat = client.get().path("chat/completions");
    ArrayNode arrayNode = JsonNodeFactory.instance.arrayNode();
    if (includeSystemPrompt) {
      arrayNode.add(message("system", SYSTEM_PROMPT));
    }
    arrayNode.add(message("user", String.format("%s \n\n %s", context, question)));
    ObjectNode request = completion().set("messages", arrayNode);
    if (includeSystemPrompt) {
        try {
			request.put("response_format", format());
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
      }
    var payload = Entity.entity(request, MediaType.APPLICATION_JSON);
    Response resp = chat.request().post(payload);
    return read(resp);
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
    String results = result.toPrettyString();
    return results;
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
  
  private JsonNode format() throws JsonProcessingException {
		String re = "{\n" +
				"    \"type\": \"json_schema\",\n" +
				"    \"json_schema\": {\n" +
				"      \"name\": \"axon_ivy_process_schema\",\n" +
				"      \"strict\": true,\n" +
				"      \"schema\": {\n" +
				"        \"$schema\" : \"https://json-schema.axonivy.com/process/12.0.0/process.json\",\n" +
				"        \"type\": \"object\",\n" +
				"        \"properties\": {\n" +
				"          \"format\": {\n" +
				"            \"type\": \"string\",\n" +
				"            \"description\": \"Version of the Axon Ivy process format.\",\n" +
				"            \"enum\": [\"10.0.0\"]\n" +
				"          },\n" +
				"          \"id\": {\n" +
				"            \"type\": \"string\",\n" +
				"            \"description\": \"Unique identifier for the process.\"\n" +
				"          },\n" +
				"          \"config\": {\n" +
				"            \"type\": \"object\",\n" +
				"            \"properties\": {\n" +
				"              \"data\": {\n" +
				"                \"type\": \"string\",\n" +
				"                \"description\": \"Qualified name of the data class for the process.\"\n" +
				"              }\n" +
				"            },\n" +
				"            \"required\": [\"data\"],\n" +
				"            \"additionalProperties\": false\n" +
				"          },\n" +
				"          \"elements\": {\n" +
				"            \"type\": \"array\",\n" +
				"            \"items\": {\n" +
				"              \"type\": \"object\",\n" +
				"              \"properties\": {\n" +
				"                \"id\": {\n" +
				"                  \"type\": \"string\",\n" +
				"                  \"description\": \"Unique identifier for the process element.\"\n" +
				"                },\n" +
				"                \"type\": {\n" +
				"                  \"type\": \"string\",\n" +
				"                  \"description\": \"Type of the Ivy process element.\",\n" +
				"                  \"enum\": [ \"Alternative\", \"CallSubEnd\", \"CallSubStart\", \"CallableSubProcess\", \"Database\", \"DialogCall\", \"EMail\", \"EmbeddedEnd\", \"EmbeddedProcessElement\", \"EmbeddedStart\", \"ErrorBoundaryEvent\", \"ErrorEnd\", \"ErrorStartEvent\", \"GenericActivity\", \"GenericBpmnElement\", \"HtmlDialogEnd\", \"HtmlDialogEventStart\", \"HtmlDialogExit\", \"HtmlDialogMethodStart\", \"HtmlDialogProcess\", \"HtmlDialogStart\", \"Join\", \"ManualBpmnElement\", \"Process\", \"ProcessAnnotation\", \"ProgramInterface\", \"ProgramStart\", \"ReceiveBpmnElement\", \"RequestStart\", \"RestClientCall\", \"RuleBpmnElement\", \"Script\", \"ScriptBpmnElement\", \"SendBpmnElement\", \"ServiceBpmnElement\", \"SignalBoundaryEvent\", \"SignalStartEvent\", \"Split\", \"SubProcessCall\", \"TaskEnd\", \"TaskEndPage\", \"TaskSwitchEvent\", \"TaskSwitchGateway\", \"ThirdPartyProgramInterface\", \"ThirdPartyProgramStart\", \"ThirdPartyWaitEvent\", \"TriggerCall\", \"UserBpmnElement\", \"UserTask\", \"WaitEvent\", \"WebServiceCall\", \"WebserviceEnd\", \"WebserviceProcess\", \"WebserviceStart\" ]\n" +
				"                },\n" +
				"                \"name\": {\n" +
				"                  \"type\": \"string\",\n" +
				"                  \"description\": \"Name of the process element.\"\n" +
				"                },\n" +
				"                \"visual\": {\n" +
				"                  \"type\": \"object\",\n" +
				"                  \"properties\": {\n" +
				"                    \"at\": {\n" +
				"                      \"type\": \"object\",\n" +
				"                      \"properties\": {\n" +
				"                        \"x\": {\n" +
				"                          \"type\": \"integer\",\n" +
				"                          \"description\": \"X-coordinate of the element in the visual editor.\"\n" +
				"                        },\n" +
				"                        \"y\": {\n" +
				"                          \"type\": \"integer\",\n" +
				"                          \"description\": \"Y-coordinate of the element in the visual editor.\"\n" +
				"                        }\n" +
				"                      },\n" +
				"                      \"required\": [\"x\", \"y\"],\n" +
				"                      \"additionalProperties\": false\n" +
				"                    }\n" +
				"                  },\n" +
				"                  \"required\": [\"at\"],\n" +
				"                  \"additionalProperties\": false\n" +
				"                },\n" +
				"                \"connect\": {\n" +
				"                    \"type\": \"array\",\n" +
				"                    \"description\": \"An array of connections to other tasks.\",\n" +
				"                    \"items\": {\n" +
				"                        \"type\": \"object\",\n" +
				"                        \"properties\": {\n" +
				"                        \"id\": {\n" +
				"                            \"type\": \"string\",\n" +
				"                            \"description\": \"The unique identifier for the connection.\"\n" +
				"                        },\n" +
				"                        \"to\": {\n" +
				"                            \"type\": \"string\",\n" +
				"                            \"description\": \"The identifier of the task to connect to.\"\n" +
				"                        }\n" +
				"                        },\n" +
				"                        \"required\": [\"id\", \"to\"],\n" +
				"                        \"additionalProperties\": false\n" +
				"                    }\n" +
				"                    }\n" +
				"              },\n" +
				"              \"required\": [\"id\", \"type\", \"name\", \"visual\", \"connect\"],\n" +
				"              \"additionalProperties\": false\n" +
				"            }\n" +
				"          }\n" +
				"        },\n" +
				"        \"required\": [\"format\", \"id\", \"config\", \"elements\"],\n" +
				"        \"additionalProperties\": false\n" +
				"      }\n" +
				"    }\n" +
				"    \n" +
				"  }";
		return mapper.readValue(re, JsonNode.class);
	}

}
