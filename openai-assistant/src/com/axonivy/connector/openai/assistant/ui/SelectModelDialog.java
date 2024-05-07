package com.axonivy.connector.openai.assistant.ui;

import java.util.List;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

public class SelectModelDialog extends MessageDialog {

  private String value;
  private Combo comboField;
  private List<String> models;

  public SelectModelDialog(Shell parent, String message, String title, String value, List<String> models) {
    super(parent, title, null, message, MessageDialog.CONFIRM,
        new String[] { IDialogConstants.OK_LABEL, IDialogConstants.CANCEL_LABEL }, 0);
    this.value = value;
    this.models = models;
  }

  @Override
  protected Control createCustomArea(Composite parent) {
    comboField = new Combo(parent, SWT.READ_ONLY);
    String items[] = models.toArray(new String[0]);
    comboField.setItems(items);
    int defaultModelIndex = models.indexOf(value);
    comboField.select(defaultModelIndex);
    return comboField;
  }

  @Override
  protected void buttonPressed(int buttonId) {
    if (buttonId == 0) {
      value = comboField.getText();
    } else {
      value = null;
    }
    super.buttonPressed(buttonId);
  }

  public static String open(Shell parent, String title, String message, String value, List<String> models) {
    var modelDialog = new SelectModelDialog(parent, message, title, value, models);
    String[] result = new String[1];
    parent.getDisplay().syncExec(() -> {
      modelDialog.open();
      result[0] = modelDialog.getValue();
    });
    return result[0];
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

}
