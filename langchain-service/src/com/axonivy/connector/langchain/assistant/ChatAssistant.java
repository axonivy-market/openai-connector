package com.axonivy.connector.langchain.assistant;

public interface ChatAssistant {

  String chat(String prompt);

  dev.langchain4j.model.output.Response jsonChat(String what);

}
