# OpenAI ChatGPT API

Mit dem OpenAI ChatGPT API Konnektor integriest Du ChatGPT in Deine Geschäftsanwendungen. ChatGPT ist ein KI-Modell, das von OpenAI entwickelt wurde. Es kann mit dir in natürlicher Sprache kommunizieren und dich bei verschiedenen Sprachbasierten Aufgaben unterstützen, wie zum Beispiel beim Beantworten von Fragen, beim Schreiben von Texten oder bei der Entwicklung neuer Ideen.

Dieser Konnektor:
- Basiert auf der OpenAI API 'https://platform.openai.com/'
- Bietet ein einfaches Chat-Frontend für die nahtlose Integration in Axon Ivy Anwendungen

## Demo

This demo showcases a simple prompt window to chat directly with ChatGPT. Enter in any language a question that is of interest, and you will immediately get ChatGPT’s answer within the Axon Ivy business process.

![demo-dialog](images/demo1.png)
![demo-dialog](images/demo0.png)

This is the demo showcase for generating the email subject and content base on the information which is entered by the user.

![demo-dialog](images/demo2.png)

## Setup

Chat GPT requests do not come for free. However, when you register a new account,
 5$ are automatically added to it. This is perfect to develop your Chat GPT integration free of charge.

1. Register an account on [platform.openai.com](https://platform.openai.com/overview).
2. Once logged in, click on your user icon on the upper right corner.
3. In the menu, use the "View API keys" option.
4. Generate a new API key and store it in your variables.yaml under `Variables.openai-connector.apiKey`

```
@variables.yaml@
```

