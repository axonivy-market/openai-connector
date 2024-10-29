package com.axonivy.connector.openai;


public enum Model {
  GPT_35("gpt-3.5-turbo"), // deprecated
  GPT_35_0301("gpt-3.5-turbo-0301"), // deprecated
  GPT_4O_MINI("gpt-4o-mini");

  private final String model;

  private Model(String model) {
    this.model = model;
  }

  public String getValue() {
    return model;
  }

}
