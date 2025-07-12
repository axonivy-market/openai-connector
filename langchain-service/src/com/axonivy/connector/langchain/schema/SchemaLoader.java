package com.axonivy.connector.langchain.schema;

import java.io.IOException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class SchemaLoader {

  private static final ObjectMapper MAPPER = new ObjectMapper();

  public static JsonNode readSchema(String resource) {
    try (var in = SchemaLoader.class.getResourceAsStream(resource)) {
      return MAPPER.readTree(in);
    } catch (IOException ex) {
      throw new RuntimeException("Failed to load schema " + resource, ex);
    }
  }

}
