package com.axonivy.connector.langchain;

import static dev.langchain4j.model.openai.OpenAiChatModelName.GPT_4_1_MINI;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.axonivy.connector.langchain.assistant.ChatAssistant;

import ch.ivyteam.ivy.environment.Ivy;
import dev.langchain4j.model.chat.Capability;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiChatModel.OpenAiChatModelBuilder;
import dev.langchain4j.service.AiServices;
import io.swagger.v3.oas.annotations.tags.Tag;

@Path("ai")
@Tag(name = "demo")
public class AiBrain {

  @GET
  @Produces(MediaType.TEXT_PLAIN)
  @Path("chat")
  public String chat(@QueryParam("prompt") String prompt) {
    var chatGpt = chatAssistant();
    return chatGpt.chat(prompt);
  }

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Path("jsonChat")
  public Response jsonChat(@QueryParam("prompt") String prompt) {

    OpenAiChatModel.builder()
        .supportedCapabilities(Capability.RESPONSE_FORMAT_JSON_SCHEMA)
        .strictJsonSchema(true)
        .build();

    var chatGpt = chatAssistant();

    dev.langchain4j.model.output.Response<?> response = chatGpt.jsonChat(prompt);
    return Response
        .ok(response.content())
        .build();
  }

  public ChatAssistant chatAssistant() {
    return AiServices.create(ChatAssistant.class, openAiModel());
  }

  private OpenAiChatModel openAiModel() {
    return buildModel()
        .modelName(GPT_4_1_MINI)
        .build();
  }

  public OpenAiChatModelBuilder buildModel() {
    return OpenAiChatModel.builder()
        .apiKey(Ivy.var().get("llm.openai.apiKey"));
  }

  public OpenAiChatModelBuilder grokModel() {
    return OpenAiChatModel.builder()
        .apiKey(Ivy.var().get("llm.grok.apiKey"))
        .baseUrl("https://api.x.ai/v1/") // grok!
        .modelName("grok-3-mini");
  }

}
