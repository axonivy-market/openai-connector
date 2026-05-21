# OpenAI ChatGPT API

Der OpenAI ChatGPT API-Connector integriert ChatGPT in Axon Ivy-Anwendungen und ermöglicht es deinen Prozessen und Dialogen, Nachrichten zu senden und KI-generierte Antworten direkt auf der Plattform zu erhalten. Er bietet ein einsatzbereites Chat-Frontend, einen vorkonfigurierten REST-Client für die OpenAI-API sowie Demo-Workflows für ChatGPT, Assistenz-Listen und E-Mail-Generierung. Mehr Informationen: https://platform.openai.com/.

## Wichtigste Funktionen

- Sende Nachrichten an ChatGPT und erhalte kontextbezogene KI-Antworten direkt in deinen Axon Ivy-Prozessen und -Dialogen.
- Konfiguriere Modell und Temperatur, um die Kreativität und Präzision der Antworten zu steuern.
- Rufe Assistants von OpenAI ab und verwalte sie für spezialisierte agentengesteuerte Workflows.
- Teste die Integration schnell mit den bereitgestellten Demo-Workflows: ChatGPT, Assistant-Liste und E-Mail-Generator.
- Nahtlose Verbindung zur OpenAI-API mit einem vorkonfigurierten REST-Client und OpenAPI-Unterstützung.
- Erzeuge personalisierte E-Mails aus Vorlagen, um Kommunikationsaufwand zu reduzieren.

## Demo

Probiere die Demo-Implementierungen, um die Funktionen und Benutzeroberflächen des Connectors kennenzulernen. Die Demos zeigen typische Benutzerabläufe wie das Starten eines Chats, das Auflisten verfügbarer Assistants und das Erzeugen personalisierter E-Mails.

### Demo-Workflows

#### OpenAI Connector Demo (openai-connector-demo)

##### ChatGPT Demo
1. Starte die ChatGPT-Demo über das Demo-Menü.
2. Du siehst einen Chat-Dialog, in den du eine Frage oder Eingabe schreiben kannst.
3. Gib deine Anfrage ein und sende sie ab.
4. Das System zeigt eine KI-generierte Antwort des ChatGPT-Modells an.
5. Optional: Passe Modell und Temperatur an, um Kreativität und Präzision zu verändern.

![ChatGPT demo](images/demo1.png)

##### GetAssistant Demo
1. Starte die Get Assistant-Demo über das Demo-Menü.
2. Es wird eine Liste der Assistants in deinem OpenAI-Konto angezeigt.
3. Wähle einen Assistant aus, um Details wie Name, ID und Modell zu sehen.
4. Verwende den ausgewählten Assistant in nachfolgenden Chat-Workflows.

![GetAssistant demo](images/demo4.png)

##### EmailGenerator Demo
1. Starte die Email Generator-Demo über das Demo-Menü.
2. Fülle die Felder für Kandidatendaten, Position und relevante Metadaten im Formular aus.
3. Erzeuge eine personalisierte E-Mail-Vorschau basierend auf den Eingaben.
4. Prüfe und versende die generierte E-Mail oder kopiere sie zur weiteren Bearbeitung.

![EmailGenerator demo](images/demo2.png)

## Einrichtung

- **Rollen:** Everybody (configured in config/roles.xml)
- **OpenAPI:** Spec URL: https://raw.githubusercontent.com/axonivy-market/openai-openapi/refs/heads/master/openapi.yaml — Namespace: com.openai.api.v1.client

### Variablen
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

1. Registriere ein Konto auf https://platform.openai.com/overview.
2. Erzeuge einen API-Schlüssel in den Kontoeinstellungen.
3. Lege den API-Schlüssel in den Projektvariablen ab: füge ihn in die Datei `config/variables.yaml` unter `Variables.openaiConnector.apiKey` ein.
4. Starte die Anwendung oder die Konfiguration neu, falls erforderlich.

## Komponenten

### Connector-Prozesse

#### openai.p.json

- **chatGpt(String what, com.openai.api.v1.client.CreateChatCompletionRequest.ModelEnum model, BigDecimal temperature) -> answer: String**
		- Eingabe:
			- `what` (String) — 
			- `model` (com.openai.api.v1.client.CreateChatCompletionRequest.ModelEnum) — 
			- `temperature` (BigDecimal) — 
		- Ergebnis:
			- `answer` (String) — 

- **getAssistants() -> assistants: List<com.openai.api.v1.client.AssistantObject>**
		- Eingabe: (keine)
		- Ergebnis:
			- `assistants` (List<com.openai.api.v1.client.AssistantObject>) — Liste verfügbarer Assistants

### Formular-Komponenten

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

### Maven-Artefakte

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