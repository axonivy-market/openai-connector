package com.axonivy.connector.langchain.test;

import static dev.langchain4j.model.chat.Capability.RESPONSE_FORMAT_JSON_SCHEMA;
import static dev.langchain4j.model.openai.internal.chat.ResponseFormatType.JSON_SCHEMA;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;

import org.junit.jupiter.api.Test;

import com.axonivy.connector.langchain.AiBrain;
import com.axonivy.connector.langchain.schema.SchemaLoader;

import ch.ivyteam.ivy.environment.IvyTest;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.request.ChatRequest;
import dev.langchain4j.model.openai.OpenAiChatModelName;
import dev.langchain4j.model.openai.OpenAiChatRequestParameters;
import dev.langchain4j.model.openai.internal.OpenAiUtils;
import dev.langchain4j.model.openai.internal.chat.ChatCompletionRequest;
import dev.langchain4j.model.openai.internal.chat.ChatCompletionRequest.Builder;

@IvyTest
public class LangChainDirectTest {

  @Test
  void json() {
    var model = new AiBrain().buildModel()
        .modelName(OpenAiChatModelName.GPT_4_O_MINI)
        .supportedCapabilities(RESPONSE_FORMAT_JSON_SCHEMA)
        .strictJsonSchema(true)
        .logRequests(true)
        .logResponses(true)
        .build();

    // ResponseFormat responseFormat = ResponseFormat.builder()
    // .type(ResponseFormatType.JSON)
    // .jsonSchema(JsonSchema.builder()
    // .name("Process") // OpenAI requires specifying the name for the schema
    // .rootElement(JsonObjectSchema.builder() // see [1] below
    // .addStringProperty("name")
    // .addIntegerProperty("age")
    // .addNumberProperty("height")
    // .addBooleanProperty("married")
    // .required("name", "age", "height", "married") // see [2] below
    // .build())
    // .build())
    // .build();

    var jsonSchema = SchemaLoader.readSchema("process.json");
    var lc4jSchema = dev.langchain4j.model.openai.internal.chat.JsonSchema.builder()
        .name("Process")
        .strict(true)
        .schema(Map.of("process", jsonSchema))
        .build();

    var lc4jResponse = dev.langchain4j.model.openai.internal.chat.ResponseFormat.builder()
        .type(JSON_SCHEMA)
        .jsonSchema(lc4jSchema)
        .build();

    ChatRequest chatRequest = ChatRequest.builder()
        // .responseFormat(lc4jResponse)
        .messages(new UserMessage("add an email element, telling rolf@axonivy.com that we got the lead!"))
        .build();

    // var asi = AiServices.create(ChatAssistant.class, model);
    // var response = asi.jsonChat("ready?");

    var request = ai(chatRequest, null, true, lc4jResponse).build();

    var response = model.chat(chatRequest);

    assertThat(response).isNotNull();

    // Response what = new AiBrain().jsonChat("hey");
    // assertThat(what.readEntity(JsonNode.class).toPrettyString())
    // .isEqualTo("bla");
  }

  private Builder ai(
      ChatRequest chatRequest,
      OpenAiChatRequestParameters parameters,
      Boolean strictTools,
      dev.langchain4j.model.openai.internal.chat.ResponseFormat lc4jResponse) {
    return ChatCompletionRequest.builder()
        .messages(OpenAiUtils.toOpenAiMessages(chatRequest.messages()))
        // common parameters
        .model(parameters.modelName())
        .temperature(parameters.temperature())
        .topP(parameters.topP())
        .frequencyPenalty(parameters.frequencyPenalty())
        .presencePenalty(parameters.presencePenalty())
        .maxTokens(parameters.maxOutputTokens())
        .stop(parameters.stopSequences())
        .tools(OpenAiUtils.toTools(parameters.toolSpecifications(), strictTools))
        .toolChoice(OpenAiUtils.toOpenAiToolChoice(parameters.toolChoice()))

        .responseFormat(lc4jResponse)
        // .responseFormat(OpenAiUtils.toOpenAiResponseFormat(parameters.responseFormat(), strictJsonSchema))

        // OpenAI-specific parameters
        .maxCompletionTokens(parameters.maxCompletionTokens())
        .logitBias(parameters.logitBias())
        .parallelToolCalls(parameters.parallelToolCalls())
        .seed(parameters.seed())
        .user(parameters.user())
        .store(parameters.store())
        .metadata(parameters.metadata())
        .serviceTier(parameters.serviceTier())
        .reasoningEffort(parameters.reasoningEffort());
  }

}
