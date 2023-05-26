package com.axonivy.connector.openai.assistant;

import java.util.Optional;

import ch.ivyteam.ivy.application.IApplication;
import ch.ivyteam.ivy.application.app.IApplicationRepository;
import ch.ivyteam.ivy.application.config.Config;
import ch.ivyteam.ivy.application.config.Property;

public class KeyRepo {

  private static final String OPEN_AI = "Variables.openai-connector.apiKey";
  private final Config config;

  public KeyRepo() {
    this.config = designerConfig();
  }

  private static Config designerConfig() {
    IApplication app = IApplicationRepository.instance()
       .findByName(IApplication.DESIGNER_APPLICATION_NAME).get();
    return Config.of(app);
  }

  public String getKey() {
    return getInternal().map(Property::value)
      .orElseThrow(()-> new RuntimeException("The aut-key is not configured: missing "+OPEN_AI));
  }

  public void storeKey(String secret) {
    config.set(OPEN_AI, secret);
  }

  public Optional<Property> getInternal() {
    return Optional.ofNullable(config.get(OPEN_AI));
  }

}