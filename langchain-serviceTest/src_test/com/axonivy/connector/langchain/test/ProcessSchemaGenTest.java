package com.axonivy.connector.langchain.test;

import static dev.langchain4j.model.chat.Capability.RESPONSE_FORMAT_JSON_SCHEMA;

import java.time.Duration;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import com.axonivy.connector.langchain.AiBrain;
import com.axonivy.connector.langchain.schema.OpenAiSchemaModel;
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
  void askElon() {
    var model = new AiBrain().grokModel()
        .supportedCapabilities(RESPONSE_FORMAT_JSON_SCHEMA)
        .strictJsonSchema(true)
        .logRequests(true)
        .logResponses(true)
        .build();

    var lc4jSchema = processSchema("simple.json");
    var writeMailProcess = processGeneration();
    var ai = new OpenAiSchemaModel(model);
    var generatedProcess = ai.chat(writeMailProcess, lc4jSchema);

    System.out.println(generatedProcess.toPrettyString());
  }

  @Test
  void askElon_grok4remote() {
    var model = new AiBrain().grokModel()
        .modelName("grok-4-0709")
        .timeout(Duration.ofMinutes(4)) // be patient!
        .supportedCapabilities(RESPONSE_FORMAT_JSON_SCHEMA)
        .strictJsonSchema(true)
        .logRequests(true)
        .logResponses(true)
        .build();

    var lc4jSchema = processSchema("proc-remote.json");
    var writeMailProcess = processGeneration();
    var ai = new OpenAiSchemaModel(model);
    var generatedProcess = ai.chat(writeMailProcess, lc4jSchema);

    System.out.println(generatedProcess.toPrettyString());
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

    var lc4jSchema = processSchema("simple.json");
    var writeMailProcess = processGeneration();
    var ai = new OpenAiSchemaModel(model);
    var generatedProcess = ai.chat(writeMailProcess, lc4jSchema);

    System.out.println(generatedProcess.toPrettyString());
  }

  @Test
  @Disabled("explicitly rejected by gpt4.1mini")
  void askOpenAi_remoteRefs() {
    var model = new AiBrain().buildModel()
        .supportedCapabilities(RESPONSE_FORMAT_JSON_SCHEMA)
        .modelName(OpenAiChatModelName.GPT_4_1_MINI)
        .strictJsonSchema(true)
        .logRequests(true)
        .logResponses(true)
        .build();

    var lc4jSchema = processSchema("proc-remote.json");
    var writeMailProcess = processGeneration();
    var ai = new OpenAiSchemaModel(model);
    var generatedProcess = ai.chat(writeMailProcess, lc4jSchema);

    System.out.println(generatedProcess.toPrettyString());
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

  private JsonSchema processSchema(String resource) {
    var jsonSchema = SchemaLoader.readSchema(resource);// SchemaLoader.readSchema("process.json");
    var maap = jsonSchema.properties().stream()
        .collect(Collectors.toMap(Entry::getKey, et -> (Object) et.getValue()));
    return dev.langchain4j.model.openai.internal.chat.JsonSchema.builder()
        .name(StringUtils.substringBefore(resource, "."))
        .strict(false)
        .schema(maap)
        .build();
  }

}
