package com.axonivy.connector.openai.test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtensionContext;

import com.axonivy.connector.openai.BaseSetup;
import com.axonivy.connector.openai.constants.OpenAiTestConstants;
import com.openai.connector.openaiData;

import ch.ivyteam.ivy.bpm.engine.client.BpmClient;
import ch.ivyteam.ivy.bpm.engine.client.ExecutionResult;
import ch.ivyteam.ivy.bpm.engine.client.element.BpmElement;
import ch.ivyteam.ivy.bpm.engine.client.element.BpmProcess;
import ch.ivyteam.ivy.environment.Ivy;

import static com.axonivy.utils.e2etest.enums.E2EEnvironment.REAL_SERVER;

public class GetAssistantTest extends BaseSetup {

//	@TestTemplate
//	public void getAssisstants(BpmClient bpmClient) {
//		BpmElement ASSISTANT = BpmProcess.path(OpenAiTestConstants.OPEN_AI).elementName("getAssistants()");
//
//		var start = bpmClient.start().subProcess(ASSISTANT);
//		ExecutionResult result = start.execute();
//		openaiData data = result.data().last();
//		assertTrue(data.getAssistants().size() >= 0);
//	}

	@TestTemplate
	public void getAssistants(BpmClient bpmClient, ExtensionContext context) {
		BpmElement ASSISTANT = BpmProcess.path(OpenAiTestConstants.OPEN_AI).elementName("getAssistants()");

		var start = bpmClient.start().subProcess(ASSISTANT);
		ExecutionResult result = start.execute();
		openaiData data = result.data().last();
		int expected = context.getDisplayName().equals(REAL_SERVER.getDisplayName()) ? 0 : 2;
		assertNotNull(data.getAssistants());
	}
}
