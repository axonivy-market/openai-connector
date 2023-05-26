package com.axonivy.connector.openai.assistant.ui;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.function.Predicate;

import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchSite;
import org.eclipse.ui.part.FileEditorInput;

import com.axonivy.connector.openai.assistant.ChatGptRequest;
import com.axonivy.connector.openai.assistant.DesignerClient;

import ch.ivyteam.swt.dialogs.SwtCommonDialogs;

public class ChatGptUiFlow {

  private IWorkbenchSite site;
  private ISelection selected;
  private String quest;

  public ChatGptUiFlow(IWorkbenchSite site, ISelection selected, String quest) {
    this.site = site;
    this.selected = selected;
    this.quest = abbrev(quest);
  }

  public void run() {
    String what = getSelectedText()
      .filter(Predicate.not(String::isBlank))
      .or(()->getEditorContent())
      .orElse(selected.toString());
    boolean doIt = SwtCommonDialogs.openQuestionDialog(site.getShell(), "need assistance?", """
        ready for asking chat GPT on ?
        """+quest+": \n"+abbrev(what));
    if (doIt) {
      var client = DesignerClient.getWorkspaceClient();
      var response = new ChatGptRequest(client).ask(abbrev(what), quest);
      SwtCommonDialogs.openInformationDialog(site.getShell(), "Chat GPT says", abbrev(response));
    }
  }

  private static String abbrev(String what) {
    int limit = 1000;
    if (what.length() > limit) {
      return what.substring(0, limit)+"...";
    }
    return what;
  }

  private Optional<String> getSelectedText() {
    if (selected instanceof TextSelection text) {
      return Optional.of(text.getText());
    }
    return Optional.empty();
  }

  private Optional<String> getEditorContent() {
    var input = Optional.ofNullable(site.getPage())
      .map(IWorkbenchPage::getActiveEditor)
      .map(IEditorPart::getEditorInput)
      .orElse(null);
    if (input instanceof FileEditorInput fileIn) {
      var store = fileIn.getStorage();
      try(InputStream contents = store.getContents()) {
        var content = new String(contents.readAllBytes(), StandardCharsets.UTF_8);
        return Optional.of(abbrev(content));
      } catch (Exception ex) {
      }
    }
    return Optional.empty();
  }

}