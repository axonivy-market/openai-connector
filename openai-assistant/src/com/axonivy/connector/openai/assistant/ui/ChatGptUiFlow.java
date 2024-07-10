package com.axonivy.connector.openai.assistant.ui;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.eclipse.compare.CompareUI;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchSite;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;

import com.axonivy.connector.openai.assistant.ChatGptClientFactory;
import com.axonivy.connector.openai.assistant.ChatGptRequest;
import com.axonivy.connector.openai.assistant.OpenAiConfig;
import com.axonivy.connector.openai.assistant.OpenAiConfig.Key;
import com.axonivy.connector.openai.assistant.models.Model;
import com.axonivy.connector.openai.assistant.models.ResponseModel;
import com.axonivy.connector.openai.assistant.ui.ChatGPTAssistantHandler.Quests;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

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
    OpenAiConfig repo = new OpenAiConfig();
    if (quest.equals(Quests.KEY)) {
      updateKey(repo);
      return;
    }

    promptKey(repo);
    String what = getSelectedText()
      .filter(Predicate.not(String::isBlank))
      .or(()->getEditorContent())
      .orElseGet(()->selected.toString());

    var chatGpt = new ChatGptRequest(()->new ChatGptClientFactory().chatGptClient());
    repo.getIntValue(Key.MAX_TOKENS).ifPresent(chatGpt::maxTokens);

    if (quest.equalsIgnoreCase(Quests.EDIT)) {
      String insert = SwtCommonDialogs.openInputDialog(site.getShell(), "any wishes?", "what can Chat GPT do for you?",
        "insert a combobox to pick a brand out of: Mercedes, BMW or Tesla");
      if (insert != null) {
        var response = runWithProgress(()->chatGpt.edit(what, insert));
        diffResult(response);
      }
      return;
    }
    if (quest.equalsIgnoreCase(Quests.INSERT)) {
      String insert = SwtCommonDialogs.openInputDialog(site.getShell(), "any wishes?", "what can Chat GPT do for you?",
        "insert a combobox to pick a brand out of: Mercedes, BMW or Tesla");
      if (insert != null) {
        var response = runWithProgress(()->chatGpt.ask(what, insert));
        diffResult(response);
      }
      return;
    }
    if (quest.equalsIgnoreCase(Quests.CHAT)) {
      String instruction = SwtCommonDialogs.openInputDialog(site.getShell(), "any wishes?",
        "what can Chat GPT do for you?", "");
      if (instruction != null) {
        var response = runWithProgress(()->chatGpt.ask(what, instruction));
        new CopyableInfoDialog(site.getShell(), "Chat GPT says", response).open();
      }
      return;
    }
    
    if (quest.equalsIgnoreCase(Quests.MODEL)) {
      String supportedModels = runWithProgress(() -> chatGpt.getModels());

      ObjectMapper mapper = new ObjectMapper();
      try {
        List<Model> models = mapper.readValue(supportedModels, ResponseModel.class).getData();
        String selectedModel = SelectModelDialog.open(site.getShell(), "OpenAI model",
            "Please select model", repo.getValue(Key.MODEL).orElse(""),
            models.stream().map(Model::getId).collect(Collectors.toList()));
        if (selectedModel != null) {
          repo.storeSecret(Key.MODEL, selectedModel);
        }
        return;
      } catch (JsonProcessingException e) {
        return;
      }
    }

    boolean assist = SwtCommonDialogs.openQuestionDialog(site.getShell(), "need assistance?", """
        ready for asking chat GPT on ?
        """+quest+": \n"+abbrev(what));
    if (assist) {
      var response = runWithProgress(()->chatGpt.ask(what, quest));
      if (quest.equalsIgnoreCase(Quests.FIX)) {
        response = diffResult(response);
        return;
      }
      new CopyableInfoDialog(site.getShell(), "Chat GPT says", response).open();
    }
  }

  private String runWithProgress(Supplier<String> gptAction) {
    var response = new AtomicReference<String>();
    run(pm -> {
      pm.setTaskName("waiting for chatGPT response");
      response.set(gptAction.get());
    });
    return response.get();
  }

  public void run(Consumer<IProgressMonitor> monitorable) {
    try {
      if (!Platform.isRunning()) {
        monitorable.accept(new NullProgressMonitor());
        return;
      }
      PlatformUI.getWorkbench().getProgressService().busyCursorWhile(m -> {
        try {
          monitorable.accept(m);
        } catch (Exception ex) {
          SwtCommonDialogs.openErrorDialog(site.getShell(), "request failed", ex.getLocalizedMessage(), ex);
        }
      });
    } catch (Exception ex) {
    }
  }

  private void promptKey(OpenAiConfig repo) {
    var key = repo.getValue(Key.API_KEY);
    if (key.isEmpty()) {
      updateKey(repo);
    }
  }

  public void updateKey(OpenAiConfig repo) {
    var secret = PasswordDialog.open(site.getShell(),
      "OpenAI auth key required",
      "Please insert your OpenAI key");
    if (secret != null) {
      repo.storeSecret(Key.API_KEY, secret);
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
    int limit = 500;
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