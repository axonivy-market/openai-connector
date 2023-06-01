package com.axonivy.connector.openai.assistant;


import java.util.concurrent.TimeUnit;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;

import com.axonivy.connector.openai.assistant.OpenAiConfig.Key;

import ch.ivyteam.ivy.jersey.client.JerseyClientBuilder;

public class ChatGptClientFactory {

  private static final int TIMEOUT_DEFAULT = 30;

  private static Client client = create();

  public WebTarget chatGptClient() {
    return client.target("https://api.openai.com/v1");
  }

  private static Client create() {
    int timeout = new OpenAiConfig()
      .getIntValue(Key.TIMEOUT_SECONDS)
      .orElse(TIMEOUT_DEFAULT);
    var clt = JerseyClientBuilder
      .create("Chat GPT Assistant")
      .readTimeoutInMillis((int)TimeUnit.SECONDS.toMillis(timeout))
      .useJacksonJson()
      .toClient();
    clt.register(new OpenAIAuthFeature());
    return clt;
  }

}
