package com.axonivy.connector.openai.assistant;

import java.util.Optional;

import ch.ivyteam.ivy.application.IApplication;
import ch.ivyteam.ivy.application.app.IApplicationRepository;
import ch.ivyteam.ivy.application.config.Config;
import ch.ivyteam.ivy.application.config.Property;

public class OpenAiConfig {

  public interface Key {
    String OPENAI_PREFIX = "Variables.openai-connector.";

    String API_KEY = OPENAI_PREFIX + "apiKey";
    String TIMEOUT_SECONDS = OPENAI_PREFIX + "timeoutSeconds";
    String MAX_TOKENS = OPENAI_PREFIX + "maxTokens";
  }

  private final Config config;

  public OpenAiConfig() {
    this.config = designerConfig();
  }

  private static Config designerConfig() {
    IApplication app = IApplicationRepository.instance()
       .findByName(IApplication.DESIGNER_APPLICATION_NAME).get();
    return Config.of(app);
  }

  public void storeSecret(String key, String secret) {
    config.set(key, secret);
  }

  public Optional<String> getValue(String key) {
    return Optional.ofNullable(config.get(key))
      .map(Property::value)
      .map(String.class::cast);
  }

  public Optional<Integer> getIntValue(String key) {
    return getValue(key).map(Integer::parseInt);
  }

}