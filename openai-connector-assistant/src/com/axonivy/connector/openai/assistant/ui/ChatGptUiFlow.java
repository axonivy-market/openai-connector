package com.axonivy.connector.openai.assistant.ui;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.function.Predicate;

import org.eclipse.compare.CompareUI;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchSite;
import org.eclipse.ui.part.FileEditorInput;

import com.axonivy.connector.openai.assistant.ChatGptRequest;
import com.axonivy.connector.openai.assistant.DesignerClient;
import com.axonivy.connector.openai.assistant.ui.ChatGPTAssistantHandler.Quests;

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
    boolean assist = SwtCommonDialogs.openQuestionDialog(site.getShell(), "need assistance?", """
        ready for asking chat GPT on ?
        """+quest+": \n"+abbrev(what));
    if (assist) {
      var client = DesignerClient.get();
      var response = new ChatGptRequest(client).ask(what, quest);
      if (quest.equalsIgnoreCase(Quests.FIX)) {
        if (selected instanceof TextSelection text) {
          response = toFullResponse(response, text);
        }
        var input = new GptDiffInput(getEditorResource(), response);
        CompareUI.openCompareEditorOnPage(input, site.getPage());
        return;
      }
      SwtCommonDialogs.openInformationDialog(site.getShell(), "Chat GPT says", abbrev(response));
    }
  }

  private String toFullResponse(String response, TextSelection text) {
    String full = getEditorContent().get();
    String start = full.substring(0, text.getOffset());
    String end = full.substring(text.getLength()+text.getOffset());
    var patched = start+response+end;
    return patched;
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
    var input = getEditorIn().map(FileEditorInput::getStorage);
    if (input.isPresent()) {
      try(InputStream contents = input.get().getContents()) {
        var content = new String(contents.readAllBytes(), StandardCharsets.UTF_8);
        return Optional.of(content);
      } catch (Exception ex) {
      }
    }
    return Optional.empty();
  }

  private IResource getEditorResource() {
    return getEditorIn().map(FileEditorInput::getFile).orElse(null);
  }

  private Optional<FileEditorInput> getEditorIn() {
    return Optional.ofNullable(site.getPage())
      .map(IWorkbenchPage::getActiveEditor)
      .map(IEditorPart::getEditorInput)
      .filter(FileEditorInput.class::isInstance)
      .map(FileEditorInput.class::cast);
  }

}