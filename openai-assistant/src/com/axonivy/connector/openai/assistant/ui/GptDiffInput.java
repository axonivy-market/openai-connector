package com.axonivy.connector.openai.assistant.ui;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.compare.CompareConfiguration;
import org.eclipse.compare.CompareEditorInput;
import org.eclipse.compare.IStreamContentAccessor;
import org.eclipse.compare.ITypedElement;
import org.eclipse.compare.ResourceNode;
import org.eclipse.compare.structuremergeviewer.Differencer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.swt.graphics.Image;

public class GptDiffInput extends CompareEditorInput {

  private final IResource original;
  private final String response;
  private ResourceNode left;

  public GptDiffInput(IResource original, String response) {
    super(config());
    this.original = original;
    this.response = response;
    this.setTitle("accept GPT changes?");
    this.left = new ResourceNode(original);
  }

  @SuppressWarnings("restriction")
  private static CompareConfiguration config() {
    CompareConfiguration cc = new CompareConfiguration();
    cc.setProperty(org.eclipse.compare.internal.CompareEditor.CONFIRM_SAVE_PROPERTY, Boolean.FALSE);
    cc.setLeftLabel("original");
    cc.setRightLabel("Chat GPT's");
    return cc;
  }

  @Override
  protected Object prepareInput(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
    try {
      var right = new StringNode(response);
      String message = "Comparing " + left.getName() + " with " + right.getName();
      monitor.subTask(message);
      return new Differencer().findDifferences(false, monitor, null, null, left, right);
    } catch (OperationCanceledException e) {
      throw new InterruptedException(e.getMessage());
    } finally {
      monitor.done();
    }
  }

  @Override
  public void save(IProgressMonitor pm) {
    // idea: support structuredEditorDocs
    // https://github.com/iloveeclipse/anyedittools/blob/master/AnyEditTools/src/de/loskutov/anyedit/compare/FileStreamContent.java#L59
    ByteArrayInputStream bytes = new ByteArrayInputStream(left.getContent());
    try {
      var file = (IFile)original;
      file.setContents(bytes, IResource.FORCE, pm);
    } catch (CoreException ex) {
      throw new RuntimeException("failed to store file "+original, ex);
    }
  }

  private static class StringNode implements ITypedElement, IStreamContentAccessor {

    private final String response;

    public StringNode(String response) {
      this.response = response;
    }

    @Override
    public String getName() {
      return "gpt-response";
    }

    @Override
    public Image getImage() {
      return null;
    }

    @Override
    public String getType() {
      return ITypedElement.TEXT_TYPE;
    }

    @Override
    public InputStream getContents() throws CoreException {
      return new ByteArrayInputStream(response.getBytes());
    }
  }
}
