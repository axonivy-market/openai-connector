package com.axonivy.connector.openai.rest.json.deserializer;

import java.io.IOException;
import java.util.HashMap;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.openai.api.v1.client.ChatCompletionRequestMessageContentPart;

public class ChatCompletionRequestMessageContentPartDeserializer
    extends JsonDeserializer<ChatCompletionRequestMessageContentPart> {

  @Override
  public ChatCompletionRequestMessageContentPart deserialize(JsonParser jp, DeserializationContext ctxt)
      throws IOException {
    JsonNode node = jp.readValueAsTree();
    if (node.isTextual()) {
      return new ChatCompletionRequestMessageContentPart() {
        @Override
        public String toString() {
          return node.textValue();
        }
      };
    }
    ObjectMapper mapper = ((ObjectMapper) jp.getCodec()).copy();
    mapper.setMixIns(new HashMap<>());
    return mapper.treeToValue(node, ChatCompletionRequestMessageContentPart.class);
  }
}
