package com.axonivy.connector.langchain;

import static dev.langchain4j.model.openai.OpenAiChatModelName.GPT_4_1_MINI;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.axonivy.connector.langchain.assistant.ChatAssistant;

import ch.ivyteam.ivy.environment.Ivy;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.service.AiServices;
import io.swagger.v3.oas.annotations.tags.Tag;

@Path("ai")
@Tag(name = "demo")
public class AiBrain {

  @GET
  @Produces(MediaType.TEXT_PLAIN)
  @Path("chat")
  public String chat(@QueryParam("prompt") String prompt) {
    ChatModel model = OpenAiChatModel.builder()
        .apiKey(Ivy.var().get("llm.openai.apiKey"))
        .modelName(GPT_4_1_MINI)
        .build();
    var chatGpt = AiServices.create(ChatAssistant.class, model);

    return chatGpt.chat(prompt);
  }

}
