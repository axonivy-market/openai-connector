package com.axonivy.connector.openai.rest.json.mixin;

import com.axonivy.connector.openai.rest.json.deserializer.AssistantsNamedToolChoiceDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(using = AssistantsNamedToolChoiceDeserializer.class)
public interface AssistantsNamedToolChoiceMixin {
}
