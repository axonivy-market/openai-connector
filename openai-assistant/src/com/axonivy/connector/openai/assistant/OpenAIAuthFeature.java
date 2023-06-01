package com.axonivy.connector.openai.assistant;

import java.io.IOException;

import javax.ws.rs.Priorities;
import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.core.Feature;
import javax.ws.rs.core.FeatureContext;

import com.axonivy.connector.openai.assistant.OpenAiConfig.Key;

public class OpenAIAuthFeature implements Feature {

  @Override
  public boolean configure(FeatureContext context) {
    context.register(new AuthFilter(), Priorities.AUTHENTICATION);
    return true;
  }

  private static class AuthFilter implements ClientRequestFilter {
    @Override
    public void filter(ClientRequestContext ctxt) throws IOException {
      String key = new OpenAiConfig().getValue(Key.API_KEY)
        .orElseThrow(()-> new RuntimeException("The aut-key is not configured: missing "+Key.API_KEY));
      ctxt.getHeaders().putSingle("Authorization", "Bearer "+key);
    }
  }

}
