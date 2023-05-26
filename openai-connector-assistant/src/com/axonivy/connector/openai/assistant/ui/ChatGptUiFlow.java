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

import com.axonivy.connector.openai.assistant.ChatGptClientFactory;
import com.axonivy.connector.openai.assistant.ChatGptRequest;
import com.axonivy.connector.openai.assistant.KeyRepo;
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
    KeyRepo repo = new KeyRepo();
    if (quest.equals(Quests.KEY)) {
      updateKey(repo);
      return;
    }

    promptKey(repo);
    String what = getSelectedText()
      .filter(Predicate.not(String::isBlank))
      .or(()->getEditorContent())
      .orElse(selected.toString());

    var chatGpt = new ChatGptRequest(()->new ChatGptClientFactory().chatGptClient());
    if (quest.equalsIgnoreCase(Quests.EDIT)) {
      String insert = SwtCommonDialogs.openInputDialog(site.getShell(), "any wishes?", "what can Chat GPT do for you?",
        "insert a combobox to pick a brand out of: Mercedes, BMW or Tesla");
      if (insert != null) {
        var response = chatGpt.edit(what, insert);
        diffResult(response);
      }
      return;
    }
    if (quest.equalsIgnoreCase(Quests.INSERT)) {
      String insert = SwtCommonDialogs.openInputDialog(site.getShell(), "any wishes?", "what can Chat GPT do for you?",
        "insert a combobox to pick a brand out of: Mercedes, BMW or Tesla");
      if (insert != null) {
        var response = chatGpt.ask(what, insert);
        diffResult(response);
      }
      return;
    }
    if (quest.equalsIgnoreCase(Quests.CHAT)) {
      String instruction = SwtCommonDialogs.openInputDialog(site.getShell(), "any wishes?",
        "what can Chat GPT do for you?", "");
      if (instruction != null) {
        var response = chatGpt.ask(what, instruction);
        SwtCommonDialogs.openInformationDialog(site.getShell(), "Chat GPT says", abbrev(response));
      }
      return;
    }

    boolean assist = SwtCommonDialogs.openQuestionDialog(site.getShell(), "need assistance?", """
        ready for asking chat GPT on ?
        """+quest+": \n"+abbrev(what));
    if (assist) {
      var response = chatGpt.ask(what, quest);
      if (quest.equalsIgnoreCase(Quests.FIX)) {
        response = diffResult(response);
        return;
      }
      SwtCommonDialogs.openInformationDialog(site.getShell(), "Chat GPT says", abbrev(response));
    }
  }

  private void promptKey(KeyRepo repo) {
    var key = repo.getInternal();
    if (key.isEmpty()) {
      updateKey(repo);
    }
  }

  public void updateKey(KeyRepo repo) {
    var secret = PasswordDialog.open(site.getShell(),
      "OpenAI auth key required",
      "Please insert your OpenAI key");
    if (secret != null) {
      repo.storeKey(secret);
    }
  }

  private String diffResult(String response) {
    if (selected instanceof TextSelection text && !text.getText().isBlank()) {
      response = toFullResponse(response, text);
    }
    var input = new GptDiffInput(getEditorResource(), response);
    CompareUI.openCompareEditorOnPage(input, site.getPage());
    return response;
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