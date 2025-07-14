package com.axonivy.connector.langchain.test;

import static dev.langchain4j.model.chat.Capability.RESPONSE_FORMAT_JSON_SCHEMA;

import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.axonivy.connector.langchain.AiBrain;
import com.axonivy.connector.langchain.schema.SchemaLoader;

import ch.ivyteam.ivy.environment.IvyTest;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.request.ChatRequest;
import dev.langchain4j.model.openai.OpenAiChatModelName;
import dev.langchain4j.model.openai.internal.chat.JsonSchema;

@IvyTest
public class ProcessSchemaGenTest {

  @BeforeEach
  void logs() {
    var langChain = Logger.getLogger(dev.langchain4j.http.client.log.LoggingHttpClient.class);
    langChain.setLevel(Level.DEBUG);
  }

  @Test
  void askOpenAi() {
    var model = new AiBrain().buildModel()
        .supportedCapabilities(RESPONSE_FORMAT_JSON_SCHEMA)
        .modelName(OpenAiChatModelName.GPT_4_1_MINI)
        .strictJsonSchema(true)
        .logRequests(true)
        .logResponses(true)
        .build();

    var lc4jSchema = processSchema();

    ChatRequest writeMailProcess = processGeneration();
    var ai = new OpenAiSchemaModel(model);
    var generatedProcess = ai.chat(writeMailProcess, lc4jSchema);

    System.out.println(generatedProcess);
  }

  private ChatRequest processGeneration() {
    var processHints = new SystemMessage("""
      omit as many defaults as possible, but at any rate produce the required values.
      Generate the 'data' as java qualified name.
      For element ID's create unique instances, starting from f1.
      Draw elements as graph.""");
    return ChatRequest.builder()
        .messages(processHints, new UserMessage("add an email element, telling rolf@axonivy.com that we got the lead!"))
        .build();
  }

  private JsonSchema processSchema() {
    var jsonSchema = SchemaLoader.readSchema("simple.json");// SchemaLoader.readSchema("process.json");
    var maap = jsonSchema.properties().stream()
        .collect(Collectors.toMap(Entry::getKey, et -> (Object) et.getValue()));
    return dev.langchain4j.model.openai.internal.chat.JsonSchema.builder()
        .name("Process23")
        .strict(false)
        .schema(maap)
        .build();
  }

}
