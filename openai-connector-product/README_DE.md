# OpenAI ChatGPT API

ChatGPT ist ein von [OpenAI](https://openai.com/) entwickeltes KI-Modell. Es
kann mit Ihnen in natürlicher Sprache kommunizieren und Sie bei verschiedenen
sprachbasierten Aufgaben unterstützen, z. B. beim Beantworten von Fragen, beim
Verfassen von Texten oder beim Entwickeln neuer Ideen. Mit dem OpenAI ChatGPT
API-Konnektor können Sie ChatGPT in Ihre Axon Ivy-Geschäftsanwendungen
integrieren.

Dieser Konnektor:

- basiert auf der OpenAI-API „https://platform.openai.com/”
- bietet eine einfache Chat-Oberfläche für die nahtlose Integration in Axon
  Ivy-Anwendungen.

## Demo

### ChatGPT-Demo
Erleben Sie mit dieser Demo nahtlos intelligente Unterhaltungen mit ChatGPT.
Stellen Sie Fragen in jeder beliebigen Sprache und erhalten Sie sofort präzise,
natürliche und aufschlussreiche Antworten – direkt in Axon Ivy. Genießen Sie
eine übersichtliche, intuitive Benutzeroberfläche, sofortiges Feedback und volle
Flexibilität für jede Situation.

Sie können Ihre KI-Erfahrung weiter personalisieren, indem Sie verschiedene
Modelle auswählen und den Temperaturparameter – von 0,0 bis 2,0 – konfigurieren,
um das Gleichgewicht zwischen Genauigkeit und Kreativität zu steuern.

- Niedrigere Werte (z. B. 0,0–0,3) → Deterministische, präzise und
  faktenorientierte Antworten.

- Mittlere Werte (z. B. 0,4–0,7) → Ausgewogene Antworten mit einer Mischung aus
  Genauigkeit und Vielfalt.

- Höhere Werte (z. B. 0,8–1,5) → Kreativere, vielfältigere und explorativere
  Antworten, jedoch mit etwas geringerer Vorhersagbarkeit.

- Sehr hohe Werte (>1,5) → Sehr kreative, experimentelle Ergebnisse, oft eher
  für Brainstorming als für sachliche Genauigkeit geeignet.

![demo-dialog](images/demo1.png)

![demo-dialog](images/demo3.png)

Dies ist ein Prozess der ChatGPT-Demo.

![demo-dialog](images/demo0.png)

## E-Mail-Generator Demo

Erleben Sie die Leistungsfähigkeit der KI bei der Automatisierung Ihres
E-Mail-Prozesses für die Personalbeschaffung direkt in dieser Demo. Geben Sie
einfach die Daten des Bewerbers, die Stelle, die Schlüsselqualifikationen, die
Ergebnisse des Vorstellungsgesprächs und die Informationen zum Interviewer ein –
das System generiert automatisch personalisierte E-Mails mit professionellen,
natürlichen und kontextbezogenen Inhalten (entweder zur Annahme oder Ablehnung).

Schnell – Genau – Bequem:

- Intuitive Benutzeroberfläche: Geben Sie alle erforderlichen Informationen in
  nur wenigen Schritten ein.

- Intelligente Personalisierung: Der Inhalt der E-Mail wird auf Grundlage der
  von Ihnen bereitgestellten Daten individuell angepasst.

- Nahtlose Integration: Senden Sie die E-Mail direkt nach der Erstellung an den
  Kandidaten, ohne zwischen Anwendungen wechseln zu müssen.

Mit der Unterstützung von OpenAI Assistant sparen Sie bei jeder E-Mail, die Sie
im Rahmen der Personalbeschaffung versenden, Zeit, ohne dabei an
Professionalität einzubüßen, und zeigen gleichzeitig echtes Interesse an den
Bewerbern.

![demo-dialog](images/demo2.png)

### GetAssistant-Demo
Mit dieser Demo können Sie schnell verfügbare Assistenten auflisten und
identifizieren, wodurch es einfacher wird, den richtigen für Gespräche, die
Erstellung von Inhalten oder spezielle Aufgaben (wie RFI-Unterstützung oder
BPMN-Erstellung) auszuwählen.

Zeigt eine Liste der in Ihrem OpenAI-Konto erstellten Assistenten an. Jeder
Assistent ist ein konfigurierbarer KI-Agent mit folgenden Eigenschaften:

- ID – eine eindeutige Kennung zum Aufrufen oder Verwalten des Assistenten.

- Name – eine beschreibende Bezeichnung, die die Funktion oder Rolle angibt (z.
  B. Axon Ivy RFI Helper, BPMN Modeling Generator).

- Modell – die verwendete KI-Modellversion (z. B. gpt-4o-mini,
  gpt-4o-2024-08-06).

![demo-dialog](images/demo4.png)

## Setup

Chat-GPT-Anfragen sind nicht kostenlos. Wenn Sie jedoch ein neues Konto
registrieren, werden diesem automatisch 5 $ gutgeschrieben. Dies ist ideal, um
Ihre Chat-GPT-Integration kostenlos zu entwickeln.

1. Registrieren Sie ein Konto auf
   [platform.openai.com](https://platform.openai.com/overview).
2. Sobald Sie angemeldet sind, klicken Sie auf Ihr Benutzersymbol in der oberen
   rechten Ecke.
3. Verwenden Sie im Menü die Option „API-Schlüssel anzeigen”.
4. Generieren Sie einen neuen API-Schlüssel und speichern Sie ihn in Ihrer
   variables.yaml unter `Variables.openai-connector.apiKey`

```
@variables.yaml@
```
