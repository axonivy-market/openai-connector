package com.axonivy.connector.openai.assistant;


import java.util.concurrent.TimeUnit;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;

import ch.ivyteam.ivy.jersey.client.JerseyClientBuilder;

public class ChatGptClientFactory {

  private static Client client = create();

  public WebTarget chatGptClient() {
    return client.target("https://api.openai.com/v1");
  }

  private static Client create() {
    var clt = JerseyClientBuilder
      .create("Chat GPT Assistant")
      .readTimeoutInMillis((int)TimeUnit.SECONDS.toMillis(30))
      .useJacksonJson()
      .toClient();
    clt.register(new OpenAIAuthFeature());
    return clt;
  }

}
