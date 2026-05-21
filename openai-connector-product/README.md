# OpenAI ChatGPT API

OpenAI ChatGPT API connector integrates ChatGPT into Axon Ivy applications, enabling your processes and dialogs to send messages and receive AI-generated responses directly within the platform. It provides a ready-to-use chat frontend, a preconfigured REST client for the OpenAI API, and demo workflows for chat completion, assistant listing, and email generation. Learn more at https://platform.openai.com/.

## Key features

- Send messages to ChatGPT and receive contextual AI responses directly within Axon Ivy processes and dialogs.
- Configure model and temperature to control response creativity and precision.
- Retrieve and manage Assistants from OpenAI for specialized agent-driven workflows.
- Evaluate integration quickly using built-in demo workflows: ChatGPT, Assistant listing, and Email generation.
- Seamless connectivity to the OpenAI API with a preconfigured REST client and OpenAPI spec support.
- Generate personalized emails from templates to streamline communications.

### Key features

- Generate conversational AI responses with the reusable callable `openai:chatGpt(String,com.openai.api.v1.client.CreateChatCompletionRequest.ModelEnum,BigDecimal)`, supporting configurable model and temperature.
- Retrieve and manage Assistants via the callable `openai:getAssistants()` to list configurable AI agents for reuse in your processes.
- Built-in OpenAI REST client with automatic authentication (`OpenAIAuthFeature`) using the API key configured in `config/rest-clients.yaml`.
- Generated OpenAPI client models under `com.openai.api.v1.client` for typed request/response mapping.
- Simple integration into Axon Ivy: callable subprocesses and data mappings let you embed AI capabilities into existing workflows.

## Demo

Check the demo implementations provided to experience the connector's capabilities and user interfaces. The demos show typical user flows such as starting a chat, listing available Assistants, and generating personalized emails.

### Demo workflows

#### OpenAI Connector Demo (openai-connector-demo)

##### ChatGPT Demo
1. Launch the ChatGPT demo from the demo menu.
2. You'll see a chat dialog where you can type a question or prompt.
3. Enter your prompt and send it.
4. The system displays an AI-generated response produced by the ChatGPT model.
5. Optionally adjust the model and temperature to refine creativity and precision.

![ChatGPT demo](images/demo1.png)

##### GetAssistant Demo
1. Launch the Get Assistant demo from the demo menu.
2. A list of Assistants from your OpenAI account is displayed.
3. Select an Assistant to view details such as name, ID, and model.
4. Use the selected Assistant in subsequent chat workflows as needed.

![GetAssistant demo](images/demo4.png)

##### EmailGenerator Demo
1. Launch the Email Generator demo from the demo menu.
2. Fill in candidate details, job position, and relevant metadata in the form.
3. Generate a personalized email preview based on the provided inputs.
4. Review and send the generated email or copy it for further edits.

![EmailGenerator demo](images/demo2.png)

## Setup

- **Roles:** Everybody (configured in config/roles.xml)
- **OpenAPI:** Spec URL: https://raw.githubusercontent.com/axonivy-market/openai-openapi/refs/heads/master/openapi.yaml — Namespace: com.openai.api.v1.client

### Variables
```yaml
# yaml-language-server: $schema=https://json-schema.axonivy.com/app/13.2.0/variables.json
# == Variables ==
# 
# You can define here your project Variables.
# If you want to define/override a Variable for a specific Environment, 
# add an additional ‘variables.yaml’ file in a subdirectory in the ‘Config’ folder: 
# '<project>/Config/_<environment>/variables.yaml
#
Variables:
	openaiConnector:
		# your openai key
		# [password]
		apiKey: ''
```

1. Register an account on https://platform.openai.com/overview.
2. Create an API key in your account settings.
3. Store the API key in the project variables: add it to `config/variables.yaml` under `Variables.openaiConnector.apiKey`.
4. Restart or reload the application configuration if necessary.

## Components

### Connector Processes

#### openai.p.json

- **chatGpt(String what, com.openai.api.v1.client.CreateChatCompletionRequest.ModelEnum model, BigDecimal temperature) -> answer: String**
		- Input:
				- `what` (String) — 
				- `model` (com.openai.api.v1.client.CreateChatCompletionRequest.ModelEnum) — 
				- `temperature` (BigDecimal) — 
		- Result:
				- `answer` (String) — 

- **getAssistants() -> assistants: List<com.openai.api.v1.client.AssistantObject>**
		- Input: (none)
		- Result:
				- `assistants` (List<com.openai.api.v1.client.AssistantObject>) — List with available Assistants

### Form Components

#### openaiData — Data Class

- **Namespace:** com.openai.connector
- **Component type:** Data Class
- **Fields:**
	 - `message` (com.openai.api.v1.client.ChatCompletionRequestMessage) — 
	 - `logitBias` (java.util.Map) — 
	 - `answer` (String) — 
	 - `assistants` (List<com.openai.api.v1.client.AssistantObject>) — 
	 - `model` (com.openai.api.v1.client.CreateChatCompletionRequest.ModelEnum) — 
	 - `temperature` (java.math.BigDecimal) — 
- **Where used:** openai.p.json (chatGpt, getAssistants)
- **Purpose:** Data container for chat requests and responses

### Maven artifacts

1. openai-connector
```xml
<dependency>
	<groupId>com.axonivy.connector.openai</groupId>
	<artifactId>openai-connector</artifactId>
	<version>@version@</version>
	<type>iar</type>
</dependency>
```

2. openai-connector-demo
```xml
<dependency>
	<groupId>com.axonivy.connector.openai</groupId>
	<artifactId>openai-connector-demo</artifactId>
	<version>@version@</version>
	<type>iar</type>
</dependency>
```
