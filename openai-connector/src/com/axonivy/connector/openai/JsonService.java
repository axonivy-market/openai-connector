package com.axonivy.connector.openai;

import com.axonivy.connector.openai.rest.json.mixin.AssistantsApiResponseFormatOptionMixin;
import com.axonivy.connector.openai.rest.json.mixin.AssistantsNamedToolChoiceMixin;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.openai.api.v1.client.AssistantObject;
import com.openai.api.v1.client.AssistantsApiResponseFormatOption;
import com.openai.api.v1.client.AssistantsNamedToolChoice;
import com.openai.api.v1.client.Batch;
import com.openai.api.v1.client.ListRunsResponse;
import com.openai.api.v1.client.MessageObject;
import com.openai.api.v1.client.RunObject;

import ch.ivyteam.ivy.environment.Ivy;

public class JsonService {
  public void de() {
    String json = """
{
  "id": "msg_abc123",
  "object": "thread.message",
  "created_at": 1698983503,
  "thread_id": "thread_abc123",
  "status": "completed",
  "incomplete_details": null,
  "completed_at": 1698983600,
  "incomplete_at": null,
  "role": "assistant",
  "content": [
    {
      "type": "text",
      "text": {
        "value": "Hi! How can I help you today?",
        "annotations": []
      }
    }
  ],
  "assistant_id": "asst_abc123",
  "run_id": "run_abc123",
  "attachments": [
    {
      "file_id": "file_abc123",
      "tools": [
        {
          "type": "code_interpreter"
        }
      ]
    }
  ],
  "metadata": {
    "topic": "greeting",
    "language": "en"
  }
}
      """;
    
    ObjectMapper mapper = new ObjectMapper();
    mapper.setSerializationInclusion(Include.NON_NULL);
    mapper.addMixIn(AssistantsApiResponseFormatOption.class, AssistantsApiResponseFormatOptionMixin.class);
    mapper.addMixIn(AssistantsNamedToolChoice.class, AssistantsNamedToolChoiceMixin.class);
    try {
      MessageObject response = mapper.readValue(json, MessageObject.class);
      Ivy.log().info(response);
    } catch (JsonProcessingException e) {
      Ivy.log().error(e.getMessage());
    }
  }
}
