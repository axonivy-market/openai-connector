package com.axonivy.connector.openai.assistant;

import java.util.UUID;
import java.util.function.UnaryOperator;

import javax.ws.rs.client.WebTarget;

import ch.ivyteam.ivy.application.IApplication;
import ch.ivyteam.ivy.application.IProcessModelVersion;
import ch.ivyteam.ivy.application.app.IApplicationRepository;
import ch.ivyteam.ivy.application.restricted.di.ProcessModelVersionContext;
import ch.ivyteam.ivy.rest.client.RestClient;
import ch.ivyteam.ivy.rest.client.RestClients;
import ch.ivyteam.ivy.rest.client.security.CsrfHeaderFeature;

public class DesignerClient {

  private static final UUID OPEN_AI = UUID.fromString("6840e778-eb27-42a0-afdc-87588ffae871");

  public static WebTarget get() {
    return getWorkspaceClient();
  }

  public static WebTarget getWorkspaceClient() {
    return getDesignerWsClient(client -> client);
  }

  public static WebTarget getTestWorkspaceClient() {
    var target = getDesignerWsClient(c -> toTestClient(c));
    return target.register(CsrfHeaderFeature.class);
  }

  private static RestClient toTestClient(RestClient config) {
    return config.toBuilder()
      .uri("{ivy.app.baseurl}/api/aiMock")
      .toRestClient();
  }

  @SuppressWarnings("restriction")
  private static WebTarget getDesignerWsClient(UnaryOperator<RestClient> configModifier) {
    IApplication app = IApplicationRepository.instance().findByName(IApplication.DESIGNER_APPLICATION_NAME).get();
    IProcessModelVersion connectorPmv = app.findProcessModelVersion("openai-connector$1");
    ProcessModelVersionContext.push(connectorPmv);
    try {
      RestClient config = RestClients.of(app).find(OPEN_AI);
      RestClient custom = configModifier.apply(config);
      var rest = new ch.ivyteam.ivy.rest.client.internal.ExternalRestWebService(app, null, custom);
      WebTarget target = rest.createCall().getWebTarget();
      return target;
    } finally {
      ProcessModelVersionContext.pop();
    }
  }
}