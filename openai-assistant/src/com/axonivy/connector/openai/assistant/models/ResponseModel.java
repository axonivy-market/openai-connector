package com.axonivy.connector.openai.assistant.models;

import java.util.List;

public class ResponseModel {
  private String object;
  private List<Model> data;

  public String getObject() {
    return object;
  }

  public void setObject(String object) {
    this.object = object;
  }

  public List<Model> getData() {
    return data;
  }

  public void setData(List<Model> data) {
    this.data = data;
  }

}
