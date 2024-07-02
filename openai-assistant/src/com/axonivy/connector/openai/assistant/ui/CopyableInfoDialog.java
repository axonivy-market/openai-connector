package com.axonivy.connector.openai.assistant.ui;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class CopyableInfoDialog extends Dialog {

  private String title;
  private String message;
  private Text messageText;

  public CopyableInfoDialog(Shell parentShell, String title, String message) {
    super(parentShell);
    this.title = title;
    this.message = message;
  }

  @Override
  protected void configureShell(Shell newShell) {
    super.configureShell(newShell);
    newShell.setText(title);
  }

  @Override
  protected Control createDialogArea(Composite parent) {
    Composite container = (Composite) super.createDialogArea(parent);
    container.setLayout(new GridLayout(1, false));

    messageText = new Text(container, SWT.BORDER | SWT.MULTI | SWT.READ_ONLY | SWT.WRAP | SWT.V_SCROLL);
    messageText.setText(message);
    messageText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

    return container;
  }

  @Override
  protected void createButtonsForButtonBar(Composite parent) {
    createButton(parent, 1, "Copy to Clipboard", false);
    createButton(parent, OK, "OK", true);
  }

  @Override
  protected void buttonPressed(int buttonId) {
    if (buttonId == 1) {
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
