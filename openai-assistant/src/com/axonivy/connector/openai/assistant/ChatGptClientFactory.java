package com.axonivy.connector.openai.assistant;


import java.util.concurrent.TimeUnit;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;

import com.axonivy.connector.openai.assistant.OpenAiConfig.Key;

import ch.ivyteam.ivy.jersey.client.JerseyClientBuilder;

public class ChatGptClientFactory {

  private interface Defaults {
    String OPENAI_V1 = "https://api.openai.com/v1";
    int TIMEOUT = 30;
  }

  private static Client client = create();

  public WebTarget chatGptClient() {
    return client.target(getPlatformUri());
  }

  private static Client create() {
    int timeout = getTimeoutSeconds();
    var clt = JerseyClientBuilder
      .create("Chat GPT Assistant")
      .readTimeoutInMillis((int)TimeUnit.SECONDS.toMillis(timeout))
      .useJacksonJson()
      .toClient();
    clt.register(new OpenAIAuthFeature());
    return clt;
  }

  private static String getPlatformUri() {
    return new OpenAiConfig()
      .getValue(Key.PLATFORM_URI)
      .orElse(Defaults.OPENAI_V1);
  }

  private static Integer getTimeoutSeconds() {
    return new OpenAiConfig()
      .getIntValue(Key.TIMEOUT_SECONDS)
      .orElse(Defaults.TIMEOUT);
  }

}
