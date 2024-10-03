package com.axonivy.connector.openai.assistant;


import java.util.concurrent.TimeUnit;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;

import com.axonivy.connector.openai.assistant.OpenAiConfig.Key;

import ch.ivyteam.ivy.jersey.client.JerseyClientBuilder;

public class ChatGptClientFactory {

  private interface Defaults {
    String OPENAI_V1 = "http://127.0.0.1:5000";
    int TIMEOUT = 30;
  }

  private static Client client = create();

  public WebTarget chatGptClient() {
    return client.target(getPlatformUri());
  }

  private static Client create() {
    int readTimeout = getTimeoutSeconds(Key.READ_TIMEOUT_SECONDS);
    int connectTimeout = getTimeoutSeconds(Key.CONNECT_TIMEOUT_SECONDS);
    var clt = JerseyClientBuilder
      .create("Chat GPT Assistant")
      .readTimeoutInMillis(readTimeout)
      .connectTimeoutInMillis(connectTimeout)
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

  private static int getTimeoutSeconds(String timeout) {
    var secs = new OpenAiConfig()
      .getIntValue(timeout)
      .orElse(Defaults.TIMEOUT);
    return (int) TimeUnit.SECONDS.toMillis(secs);
  }

}
