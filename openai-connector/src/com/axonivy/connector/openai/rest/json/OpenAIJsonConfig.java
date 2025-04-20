package com.axonivy.connector.openai.rest.json;

import com.axonivy.connector.openai.rest.json.mixin.AssistantsApiResponseFormatOptionMixin;
import com.axonivy.connector.openai.rest.json.mixin.AssistantsNamedToolChoiceMixin;
import com.axonivy.connector.openai.rest.json.mixin.ChatCompletionRequestMessageContentPartMixin;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.openai.api.v1.client.AssistantsApiResponseFormatOption;
import com.openai.api.v1.client.AssistantsNamedToolChoice;
import com.openai.api.v1.client.ChatCompletionRequestMessageContentPart;

public class OpenAIJsonConfig extends SimpleModule {

  private static final long serialVersionUID = 6502878369574276094L;

  public OpenAIJsonConfig() {
    setMixInAnnotation(AssistantsApiResponseFormatOption.class, AssistantsApiResponseFormatOptionMixin.class);
    setMixInAnnotation(AssistantsNamedToolChoice.class, AssistantsNamedToolChoiceMixin.class);
    setMixInAnnotation(ChatCompletionRequestMessageContentPart.class, ChatCompletionRequestMessageContentPartMixin.class);
  }
}
