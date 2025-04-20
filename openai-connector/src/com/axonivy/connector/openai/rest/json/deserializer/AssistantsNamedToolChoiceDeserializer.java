package com.axonivy.connector.openai.rest.json.deserializer;

import java.io.IOException;
import java.util.HashMap;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.openai.api.v1.client.AssistantsNamedToolChoice;

public class AssistantsNamedToolChoiceDeserializer extends JsonDeserializer<AssistantsNamedToolChoice> {

  public enum ValueEnum {
    AUTO("auto"), NONE("none"), REQUIRED("required");

    private String value;

    ValueEnum(String value) {
      this.value = value;
    }

    public String getValue() {
      return value;
    }

    public static ValueEnum fromValue(String input) {
      for (ValueEnum b : ValueEnum.values()) {
        if (b.value.equals(input)) {
          return b;
        }
      }
      return null;
    }

  }

  @Override
  public AssistantsNamedToolChoice deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {

    JsonNode node = jp.readValueAsTree();
    if (node.isTextual()) {
      ValueEnum value = ValueEnum.fromValue(node.textValue());
      if (value != null) {
        return new AssistantsNamedToolChoice() {
          @Override
          public String toString() {
            return value.getValue();
          }
        };
      }
    }
    ObjectMapper mapper = ((ObjectMapper) jp.getCodec()).copy();
    mapper.setMixIns(new HashMap<>());
    return mapper.treeToValue(node, AssistantsNamedToolChoice.class);
  }
}
