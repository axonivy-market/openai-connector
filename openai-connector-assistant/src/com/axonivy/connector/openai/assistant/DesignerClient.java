package com.axonivy.connector.openai.assistant;

import java.util.UUID;

import javax.ws.rs.client.WebTarget;

import ch.ivyteam.ivy.application.IApplication;
import ch.ivyteam.ivy.application.IProcessModelVersion;
import ch.ivyteam.ivy.application.app.IApplicationRepository;
import ch.ivyteam.ivy.application.restricted.di.ProcessModelVersionContext;
import ch.ivyteam.ivy.rest.client.IRestClientContext;
import ch.ivyteam.ivy.webservice.restricted.execution.IWebserviceExecutionManager;

class DesignerClient {

  private static final UUID OPEN_AI = UUID.fromString("6840e778-eb27-42a0-afdc-87588ffae871");

  public static WebTarget getWorkspaceClient() {
    IApplication designer = IApplicationRepository.instance().findByName(IApplication.DESIGNER_APPLICATION_NAME).get();
    IRestClientContext ctxt = IWebserviceExecutionManager.instance().getRestClientContext(designer);
    IProcessModelVersion connectorPmv = designer.findProcessModelVersion("openai-connector$1");
    ProcessModelVersionContext.push(connectorPmv);
    try {
      return ctxt.client(OPEN_AI);
    } finally {
      ProcessModelVersionContext.pop();
    }
  }
}