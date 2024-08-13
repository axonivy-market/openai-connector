package com.axonivy.connector.openai.assistant.ui;

import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.preference.JFacePreferences;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.ProgressEvent;
import org.eclipse.swt.browser.ProgressListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

public abstract class MarkdownDialog extends Dialog {

  private static final String HEX_COLOR_FORMAT = "#%02x%02x%02x";
  protected String title;
  protected String message;
  protected Browser browser;

  public MarkdownDialog(Shell parentShell, String title, String message) {
    super(parentShell);
    this.title = title;
    this.message = message;
  }

  @Override
  protected void configureShell(Shell newShell) {
    super.configureShell(newShell);
    newShell.setText(title);
    newShell.setMinimumSize(750, 400);
  }

  @Override
  protected Point getInitialSize() {
    return new Point(750, 400);
  }

  @Override
  protected Point getInitialLocation(Point initialSize) {
    Rectangle screenSize = getShell().getDisplay().getPrimaryMonitor().getBounds();

    // Calculate the center position
    int x = (screenSize.width - initialSize.x) / 2;
    int y = (screenSize.height - initialSize.y) / 2;

    return new Point(x, y);
  }

  @Override
  protected boolean isResizable() {
    return true;
  }

  @Override
  protected Control createDialogArea(Composite parent) {
    Composite container = (Composite) super.createDialogArea(parent);
    container.setLayout(new GridLayout(1, false));

    browser = new Browser(container, SWT.BORDER);
    browser.setLayoutData(new GridData(GridData.FILL_BOTH));

    Parser parser = Parser.builder().build();
    HtmlRenderer renderer = HtmlRenderer.builder().build();
    String htmlContent = renderer.render(parser.parse(this.message));

    // Get Eclipse theme colors
    Color bgColor = JFaceResources.getColorRegistry().get(JFacePreferences.CONTENT_ASSIST_BACKGROUND_COLOR);
    Color fgColor = JFaceResources.getColorRegistry().get(JFacePreferences.CONTENT_ASSIST_FOREGROUND_COLOR);

    // Create CSS with Eclipse colors
    String css = String.format("body { background-color: %s; color: %s; font-family: '%s'; }", toHex(bgColor.getRGB()),
        toHex(fgColor.getRGB()), JFaceResources.getTextFont().getFontData()[0].getName());

    // Wrap HTML content with custom CSS
    String htmlWithCss = String.format("<html><head><style>%s</style></head><body>%s</body></html>", css, htmlContent);

    browser.setText(htmlWithCss);

    browser.addProgressListener(new ProgressListener() {
      @Override
      public void completed(ProgressEvent event) {
        getShell().getDisplay().asyncExec(() -> resizeDialog());
      }

      @Override
      public void changed(ProgressEvent event) {
        // Not needed for this implementation
      }
    });

    return container;
  }

  private String toHex(RGB rgb) {
    return String.format(HEX_COLOR_FORMAT, rgb.red, rgb.green, rgb.blue);
  }

  private void resizeDialog() {
    Shell shell = getShell();
    Point preferredSize = shell.computeSize(SWT.DEFAULT, SWT.DEFAULT);
    shell.setSize(preferredSize);
    shell.layout(true, true);
  }
}
