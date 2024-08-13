package com.axonivy.connector.openai.assistant.ui;

import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

public class CopyableInfoDialog extends MarkdownDialog {

  private static final int COPY_TO_CLIPBOARD_ID = 1;

  public CopyableInfoDialog(Shell parentShell, String title, String message) {
    super(parentShell, title, message);
  }

  @Override
  protected void createButtonsForButtonBar(Composite parent) {
    createButton(parent, COPY_TO_CLIPBOARD_ID, "Copy to Clipboard", false);
    createButton(parent, OK, "OK", true);
  }

  @Override
  protected void buttonPressed(int buttonId) {
    if (buttonId == COPY_TO_CLIPBOARD_ID) {
      copyToClipboard();
    } else {
      super.buttonPressed(buttonId);
    }
  }

  private void copyToClipboard() {
    Clipboard clipboard = new Clipboard(getShell().getDisplay());
    TextTransfer textTransfer = TextTransfer.getInstance();
    clipboard.setContents(new Object[] { message }, new Transfer[] { textTransfer });
    clipboard.dispose();
  }
}
