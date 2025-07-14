package com.axonivy.connector.langchain.schema;

import static dev.langchain4j.model.openai.internal.OpenAiUtils.aiMessageFrom;
import static dev.langchain4j.model.openai.internal.OpenAiUtils.finishReasonFrom;
import static dev.langchain4j.model.openai.internal.OpenAiUtils.tokenUsageFrom;
import static dev.langchain4j.model.openai.internal.chat.ResponseFormatType.JSON_SCHEMA;

import org.apache.commons.lang3.reflect.FieldUtils;

import com.fasterxml.jackson.databind.JsonNode;

import dev.langchain4j.internal.Json;
import dev.langchain4j.internal.RetryUtils;
import dev.langchain4j.model.chat.request.ChatRequest;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiChatRequestParameters;
import dev.langchain4j.model.openai.OpenAiChatResponseMetadata;
import dev.langchain4j.model.openai.internal.OpenAiClient;
import dev.langchain4j.model.openai.internal.OpenAiUtils;
import dev.langchain4j.model.openai.internal.chat.ChatCompletionRequest;
import dev.langchain4j.model.openai.internal.chat.ChatCompletionRequest.Builder;
import dev.langchain4j.model.openai.internal.chat.ChatCompletionResponse;
import dev.langchain4j.model.openai.internal.chat.JsonSchema;

/**
 * Another wrapper around {@link OpenAiChatModel},
 * required to gain the flexibility of providing our custom complex JSON-Schema response format.
 */
public class OpenAiSchemaModel {

  private final OpenAiChatModel model;

  public OpenAiSchemaModel(OpenAiChatModel model) {
    this.model = model;
  }

  public JsonNode chat(ChatRequest chatRequest, JsonSchema lc4jSchema) {
    var lc4jResponse = dev.langchain4j.model.openai.internal.chat.ResponseFormat.builder()
        .type(JSON_SCHEMA)
        .jsonSchema(lc4jSchema)
        .build();

    var request = request(chatRequest, model.defaultRequestParameters(), true, lc4jResponse).build();
    var out = chat(model, request);
    String rawJson = out.aiMessage().text();
    return Json.fromJson(rawJson, JsonNode.class);
  }

  private ChatResponse chat(OpenAiChatModel theModel, ChatCompletionRequest request2) {

    var client = client(theModel);

    ChatCompletionResponse openAiResponse = RetryUtils
        .withRetryMappingExceptions(() -> client.chatCompletion(request2).execute(), 3);

    OpenAiChatResponseMetadata responseMetadata = responseMeta(openAiResponse);

    return ChatResponse.builder()
        .aiMessage(aiMessageFrom(openAiResponse))
        .metadata(responseMetadata)
        .build();
  }

  private static Builder request(
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

  private static OpenAiChatResponseMetadata responseMeta(ChatCompletionResponse openAiResponse) {
    return OpenAiChatResponseMetadata.builder()
        .id(openAiResponse.id())
        .modelName(openAiResponse.model())
        .tokenUsage(tokenUsageFrom(openAiResponse.usage()))
        .finishReason(finishReasonFrom(openAiResponse.choices().get(0).finishReason()))
        .created(openAiResponse.created())
        .serviceTier(openAiResponse.serviceTier())
        .systemFingerprint(openAiResponse.systemFingerprint())
        .build();
  }

  private static OpenAiClient client(OpenAiChatModel model) {
    try {
      return (OpenAiClient) FieldUtils.readDeclaredField(model, "client", true);
    } catch (IllegalAccessException ex) {
      throw new RuntimeException("Failed to extract http client", ex);
    }
  }
}
