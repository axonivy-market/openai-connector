package com.axonivy.connector.openai.assistant;

import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchSite;

import ch.ivyteam.swt.dialogs.SwtCommonDialogs;

public class ChatGptUiFlow {

  private IWorkbenchSite site;
  private ISelection selected;
  private String quest;

  public ChatGptUiFlow(IWorkbenchSite site, ISelection selected, String quest) {
    this.site = site;
    this.selected = selected;
    this.quest = quest;
  }

  public void run() {
    String what = getSelectedText();
    boolean doIt = SwtCommonDialogs.openQuestionDialog(site.getShell(), "need assistance?", """
        ready for asking chat GPT on ?
        """+quest+":"+what);
    if (doIt) {
      var client = DesignerClient.getWorkspaceClient();
      var response = new ChatGptRequest(client).ask(what, quest);
      SwtCommonDialogs.openInformationDialog(site.getShell(), "Chat GPT says", response);
    }
  }

  private String getSelectedText() {
    if (selected instanceof TextSelection text) {
      return text.getText();
    }
    return selected.toString();
  }

}