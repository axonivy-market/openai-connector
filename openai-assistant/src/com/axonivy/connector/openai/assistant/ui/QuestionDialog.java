package com.axonivy.connector.openai.assistant.ui;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

public class QuestionDialog extends MarkdownDialog {

  private int buttonId;

  public QuestionDialog(Shell parentShell, String title, String message) {
    super(parentShell, title, message);
  }
  @Override
  protected void createButtonsForButtonBar(Composite parent) {
    createButton(parent, IDialogConstants.YES_ID, IDialogConstants.YES_LABEL, true);
    createButton(parent, IDialogConstants.NO_ID, IDialogConstants.NO_LABEL, false);
  }

  @Override
  protected void buttonPressed(int buttonId) {
    this.buttonId = buttonId;
    super.buttonPressed(OK);
  }

  public boolean isAgreed() {
    return buttonId == IDialogConstants.YES_ID;
  }
}
