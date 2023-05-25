package com.axonivy.connector.openai.assistant;
import java.util.Map;

import org.eclipse.core.commands.IParameterValues;

import com.axonivy.connector.openai.assistant.ChatGPTAssistantHandler.Quests;


public class ChatGPTquest implements IParameterValues {

  public static final String PARAMETER_ID = "chatGpt.question";

  @SuppressWarnings("rawtypes")
  @Override
  public Map getParameterValues() {
      return Map.of(
        "Explain", Quests.EXPLAIN,
        "Fix", Quests.FIX,
        "Insert", Quests.INSERT,
        "Chat", Quests.CHAT);
  }
}
