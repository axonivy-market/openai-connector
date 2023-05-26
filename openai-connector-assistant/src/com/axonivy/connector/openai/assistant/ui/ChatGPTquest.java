package com.axonivy.connector.openai.assistant.ui;
import java.util.Map;

import org.eclipse.core.commands.IParameterValues;

import com.axonivy.connector.openai.assistant.ui.ChatGPTAssistantHandler.Quests;


public class ChatGPTquest implements IParameterValues {

  public static final String PARAMETER_ID = "chatGpt.question";

  @SuppressWarnings("rawtypes")
  @Override
  public Map getParameterValues() {
      return Map.of(
        "Explain", Quests.EXPLAIN,
        "Fix", Quests.FIX,
        "Insert", Quests.INSERT,
        "Edit (beta)", Quests.EDIT,
        "Chat", Quests.CHAT);
  }
}
