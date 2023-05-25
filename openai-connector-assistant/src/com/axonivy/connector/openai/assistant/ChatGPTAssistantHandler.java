package com.axonivy.connector.openai.assistant;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchSite;
import org.eclipse.ui.handlers.HandlerUtil;

import ch.ivyteam.swt.dialogs.SwtCommonDialogs;

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
    var flow = new ChatGptFlow(site, selected, quest);
    flow.run();
    return null;
  }

  public static class ChatGptFlow {

    private IWorkbenchSite site;
    private ISelection selected;
    private String quest;

    public ChatGptFlow(IWorkbenchSite site, ISelection selected, String quest) {
      this.site = site;
      this.selected = selected;
      this.quest = quest;
    }

    public void run() {
      String what = getSelectedText();
      SwtCommonDialogs.openInformationDialog(site.getShell(), "need assistance?", """
          ready for asking chat GPT on ?
          """+quest+":"+what);
    }

    private String getSelectedText() {
      if (selected instanceof TextSelection text) {
        return text.getText();
      }
      return selected.toString();
    }

  }

  public interface Quests {
    String FIX = "fix";
    String EXPLAIN = "explain";
    String INSERT = "insert";
    String CHAT = "chat";
  }
}
