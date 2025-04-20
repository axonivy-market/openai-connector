package com.axonivy.connector.openai.rest.json.mixin;

import com.axonivy.connector.openai.rest.json.deserializer.AssistantsNamedToolChoiceDeserializer;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(using = AssistantsNamedToolChoiceDeserializer.class)
@JsonTypeInfo(use = JsonTypeInfo.Id.NONE)
public interface AssistantsNamedToolChoiceMixin {
}
