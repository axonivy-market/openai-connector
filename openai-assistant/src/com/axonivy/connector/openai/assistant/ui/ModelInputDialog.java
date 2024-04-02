package com.axonivy.connector.openai.assistant.ui;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;

public class ModelInputDialog extends MessageDialog {

  public ModelInputDialog(Shell parent, String message, String title) {
    super(parent, title, null, message, MessageDialog.CONFIRM,
        new String[] { IDialogConstants.OK_LABEL, IDialogConstants.CANCEL_LABEL }, 0);
  }

}
