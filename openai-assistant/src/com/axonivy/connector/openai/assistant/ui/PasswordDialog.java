package com.axonivy.connector.openai.assistant.ui;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class PasswordDialog extends MessageDialog {

  private String value;
  private Text textField;

  public PasswordDialog(Shell parent, String message, String title) {
    super(parent, title, null, message, MessageDialog.CONFIRM, new String[]{IDialogConstants.OK_LABEL, IDialogConstants.CANCEL_LABEL}, 0);
    value = "";
  }

  public String getValue() {
    if (getReturnCode() == Window.OK) {
      return value;
    }
    return null;
  }

  @Override
  protected Control createCustomArea(Composite parent) {
    textField = new Text(parent, SWT.SINGLE | SWT.BORDER | SWT.PASSWORD);
    textField.setText(value);
    textField.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
    return textField;
  }

  @Override
  protected void buttonPressed(int buttonId) {
    if (buttonId == 0) {
      value = textField.getText();
    } else {
      value = null;
    }
    super.buttonPressed(buttonId);
  }

  public static String open(Shell parent, String title, String message) {
    var inputDialog = new PasswordDialog(parent, message, title);
    var result = new String[1];
    parent.getDisplay().syncExec(() -> {
      inputDialog.open();
      result[0] = inputDialog.getValue();
    });
    return result[0];
  }

}
