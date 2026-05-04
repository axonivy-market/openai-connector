# OpenAI ChatGPT API

ChatGPT ist ein KI‑Modell von [OpenAI](https://openai.com/). Es kommuniziert in natürlicher Sprache und unterstützt dich bei verschiedenen sprachbasierten Aufgaben, z. B. beim Beantworten von Fragen, beim Verfassen von Texten oder beim Entwickeln neuer Ideen. Mit dem OpenAI ChatGPT API‑Connector kannst du ChatGPT direkt in deine Axon Ivy Geschäftsanwendungen integrieren.

Dieser Konnektor:

- basiert auf der OpenAI API 'https://platform.openai.com/'
- bietet ein einfaches Chat‑Frontend für die nahtlose Integration in Axon Ivy Anwendungen

### Wichtigste Funktionen

- Generiert konversationelle KI‑Antworten mit dem wiederverwendbaren Callable `openai:chatGpt(String,com.openai.api.v1.client.CreateChatCompletionRequest.ModelEnum,BigDecimal)`, dabei sind Modell und Temperatur konfigurierbar.
- Ermöglicht das Abrufen und Verwalten von Assistants via `openai:getAssistants()` — nützlich, um wiederverwendbare, konfigurierbare AI‑Agenten in deinen Prozessen einzusetzen.
- Integrierter OpenAI‑REST‑Client mit automatischer Authentifizierung (`OpenAIAuthFeature`); das Bearer‑Token wird über `config/rest-clients.yaml` gesetzt.
- Generierte OpenAPI‑Clientmodelle unter `com.openai.api.v1.client` für typisierte Anfrage-/Antwort‑Abbildung.
- Einfache Integration in Axon Ivy: Callables und Daten‑Mapping vereinfachen das Einbetten von KI‑Funktionen in deine Workflows.
