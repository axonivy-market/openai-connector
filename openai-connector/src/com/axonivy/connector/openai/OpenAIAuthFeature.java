package com.axonivy.connector.openai;

import java.io.IOException;

import javax.ws.rs.Priorities;
import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.core.Feature;
import javax.ws.rs.core.FeatureContext;

import ch.ivyteam.ivy.rest.client.FeatureConfig;

public class OpenAIAuthFeature implements Feature {

  @Override
  public boolean configure(FeatureContext context) {
    context.register(new AuthFilter(), Priorities.AUTHENTICATION);
    return true;
  }

  private static class AuthFilter implements ClientRequestFilter {

    @Override
    public void filter(ClientRequestContext ctxt) throws IOException {
      var config = new FeatureConfig(ctxt.getConfiguration(), OpenAIAuthFeature.class);
      String key = config.readMandatory("AUTH.openaiKey");
      ctxt.getHeaders().putSingle("Authorization", "Bearer "+key);
    }

  }

}
