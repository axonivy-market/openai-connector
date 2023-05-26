package com.axonivy.connector.openai.assistant.ui;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchSite;
import org.eclipse.ui.handlers.HandlerUtil;

public class ChatGPTAssistantHandler extends AbstractHandler {

  @Override
  public boolean isEnabled() {
    return true;
  }

  @Override
  public Object execute(ExecutionEvent event) throws ExecutionException {
    ISelection selected = HandlerUtil.getCurrentSelection(event);
    IWorkbenchSite site = HandlerUtil.getActiveSite(event);
    String quest = event.getParameter(ChatGPTquest.PARAMETER_ID);
    var flow = new ChatGptUiFlow(site, selected, quest);
    flow.run();
    return null;
  }

  public interface Quests {
    String FIX = "fix";
    String EXPLAIN = "explain";
    String INSERT = "insert";
    String CHAT = "chat";
  }
}
