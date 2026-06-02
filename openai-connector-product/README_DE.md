# OpenAI ChatGPT 4 AxonIvy

OpenAI ChatGPT 4 AxonIvy verbindet deine Axon-Ivy-Prozesse mit OpenAI-Diensten, damit du Chat-Antworten erzeugen, Assistenten anzeigen und passgenaue E-Mails aus Geschäftsdaten erstellen kannst.

Der Connector spricht über die OpenAI API mit [api.openai.com/v1](https://api.openai.com/v1) und nutzt eine dedizierte REST-Client-Konfiguration für sicheren, wiederverwendbaren Zugriff.

![OpenAI connector process overview](images/demo0.png)

**Wichtige Funktionen**

- Stelle Fragen und führe Unterhaltungen in Axon Ivy mit OpenAI-gestützten Antworten fort.
- Erstelle personalisierte Kandidaten-E-Mails aus Prozessdaten und reduziere so manuelle Schreibarbeit.
- Liste OpenAI-Assistenten in einer einfachen Oberfläche auf, damit du verfügbare Agenten schnell prüfen kannst.
- Steuere Modell und Temperatur direkt im Dialog und gib Nutzern damit einfache Feineinstellungen ohne Codeänderungen.
- Nutze die Callable Subprocesses des Connectors aus anderen Prozessen weiter und halte die KI-Integration so zentral.
- Konfiguriere sicheren REST-Zugriff mit einer dedizierten Authentifizierungsfunktion und OpenAPI-Endpunktmetadaten.

## Demo

Probiere die mitgelieferten Demos aus, um den Connector bei Chat, Assistentenabfrage und E-Mail-Generierung in Aktion zu sehen.

### Demo Workflows

#### openai-connector-demo (openai-connector-demo)

##### KI-Chat

1. Starte die KI-Chat-Demo über die Aufgaben- oder Startliste.
2. Öffne den Konfigurationsdialog und wähle das Modell sowie die Temperatur passend zu deinem Anwendungsfall.

![AI Chat configuration](images/demo1.png)

3. Gib deine Frage ein und sende sie an die KI.
4. Prüfe den Gesprächsverlauf und führe die Unterhaltung bei Bedarf fort.

![AI Chat conversation](images/demo3.png)

##### Assistentenliste anzeigen

1. Starte die Assistentenlisten-Demo.
2. Warte, bis die App die OpenAI Assistants API aufruft und die Ergebnisse lädt.

![Assistants list](images/demo4.png)

3. Prüfe ID, Name und Modell in der Tabelle.
4. Schließe die Ansicht, wenn du fertig bist.

##### KI-Mail-Generator

1. Starte die KI-Mail-Generator-Demo.
2. Fülle die Kandidaten- und Interviewdaten aus und wähle dann Jobposition und Entscheidung.

![AI Mail Generator](images/demo2.png)

3. Erzeuge den E-Mail-Entwurf und prüfe Betreff und Inhalt.
4. Sende die Mail an die Kandidatin oder den Kandidaten, wenn du mit dem Ergebnis zufrieden bist.

## Setup

- **Rollen:** Everybody (konfiguriert in `config/roles.xml`)
- **OpenAPI:** https://raw.githubusercontent.com/axonivy-market/openai-openapi/refs/heads/master/openapi.yaml

1. Hinterlege deinen OpenAI API-Schlüssel in `config/variables.yaml` unter `openaiConnector.apiKey`.
2. Lass den REST-Client mit der OpenAI-Basis-URL `https://api.openai.com/v1` konfiguriert.
3. Belass `OpenAIAuthFeature` im Projekt, damit der Connector den API-Schlüssel als `Authorization: Bearer ...` sendet.
4. Prüfe, dass die OpenAPI-Spec-URL auf das verwendete OpenAI-Schema zeigt.

### Variablen

```
@variables.yaml@
```

## Komponenten

### Aufrufbare Teilprozesse

#### openai.p.json

- **Signature**: chatGpt(String what, com.openai.api.v1.client.CreateChatCompletionRequest.ModelEnum model, BigDecimal temperature) -> answer: String
  - **Beschreibung**: Der Client-Request setzt alle erforderlichen und optionalen Variablen, um eine Anfrage zu senden und vom KI-Modell generierten Inhalt zu erhalten.
  - **Eingabe:**
    - `what` (String) - Die Nachricht oder Eingabe, die an das Modell gesendet wird.
    - `model` (com.openai.api.v1.client.CreateChatCompletionRequest.ModelEnum) - Das Modell für die Antwort.
    - `temperature` (BigDecimal) - Die Temperatur, die die Kreativität der Antwort steuert.
  - **Ergebnis:**
    - `answer` (String) - Die von OpenAI zurückgegebene generierte Antwort.

- **Signature**: getAssistants() -> assistants: List<com.openai.api.v1.client.AssistantObject>
  - **Beschreibung**: Ruft die aktuellen Assistenten aus der OpenAI Assistants API ab und gibt sie an den aufrufenden Prozess zurück.
  - **Eingabe:** (none)
  - **Ergebnis:**
    - `assistants` (List<com.openai.api.v1.client.AssistantObject>) - Die von der OpenAI API zurückgegebenen Assistenten.

### Dialog-Komponenten

#### ChatWithAi — Über einen einfachen Dialog mit dem KI-Assistenten chatten

- **Namensraum:** com.openai.connector.demo.ChatWithAi
- **Komponententyp:** UI-Dialog
- **Felder:**
  - `message` (String) — Die Eingabe, die du vor dem Senden einer Chat-Nachricht erfasst.
  - `conversation` (java.util.List<com.openai.connector.demo.Message>) — Der im Dialog angezeigte Gesprächsverlauf.
  - `model` (com.openai.api.v1.client.CreateChatCompletionRequest.ModelEnum) — Das ausgewählte Modell für die KI-Antwort.
  - `temperature` (java.math.BigDecimal) — Die Antworttemperatur, mit der du die Kreativität steuerst.
- **Zweck:** Damit kannst du mit OpenAI chatten, die Modellkonfiguration anpassen und die gesamte Unterhaltung an einer Stelle nachverfolgen.

#### GetAssistants — Die in OpenAI verfügbaren Assistenten anzeigen

- **Namensraum:** com.openai.connector.demo.GetAssistants
- **Komponententyp:** UI-Dialog
- **Felder:**
  - `assistants` (List<com.openai.api.v1.client.AssistantObject>) — Die aus der OpenAI API geladenen Assistenten.
- **Zweck:** Zeigt die Assistentenliste an, damit du IDs, Namen und Modelle vor der Auswahl prüfen kannst.

#### MailGenerator — Eine personalisierte E-Mail für eine Kandidatin oder einen Kandidaten generieren

- **Namensraum:** com.openai.connector.demo.MailGenerator
- **Komponententyp:** UI-Dialog
- **Felder:**
  - `mail` (com.openai.connector.demo.AIGeneratedMail) — Der generierte E-Mail-Inhalt, der angezeigt und gesendet wird.
  - `message` (String) — Der Prompt-Text, der an das KI-Modell gesendet wird.
  - `candidateInformation` (com.openai.connector.demo.CandidateInformation) — Die Kandidateninformationen, aus denen der E-Mail-Entwurf erstellt wird.
- **Zweck:** Hilft dir, auf Basis von Kandidaten- und Interviewdaten eine passende E-Mail zu entwerfen und zu senden.

### Maven-Artefakte

1. openai-connector

```xml
<dependency>
  <groupId>com.axonivy.connector.openai</groupId>
  <artifactId>openai-connector</artifactId>
  <type>iar</type>
</dependency>
```

2. openai-connector-demo

```xml
<dependency>
  <groupId>com.axonivy.connector.openai</groupId>
  <artifactId>openai-connector-demo</artifactId>
  <type>iar</type>
</dependency>
```
