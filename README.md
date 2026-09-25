    # VECTRA

VECTRA is a multi-provider AI API platform built around a **BYOK (Bring Your Own Key)** architecture.

The current project is a Java prototype focused on building the core systems required for a provider-agnostic AI platform.

VECTRA currently supports multiple AI providers, encrypted API-key handling, dynamic model discovery, token usage tracking, and token-budget-based conversation context management.

The long-term goal is to evolve this prototype into a Spring Boot backend with persistent storage and a unified API layer.

---

## Current Status

VECTRA is currently in the prototype stage.

The core provider abstraction, API-key encryption, model discovery, token usage tracking, and context-window management are implemented.

<<<<<<< HEAD
Next what i am planning to do is that somehow reduce the token count of every prompt
streak saver commit
another streak saver
=======
The project is being developed incrementally, with each subsystem being tested before moving to the next stage.

---

## Features

- 🔐 AES-256-GCM API-key encryption
- 🔌 Multi-provider architecture
- 🤖 Dynamic model discovery
- 📊 Provider token-usage tracking
- 🧠 Model-aware context budgeting
- ✂️ Token-budget-based conversation compaction
- 📝 Automatic conversation summarization
- 💬 Conversation history preservation
- 🧩 Provider-specific API implementations

---

## Supported Providers

Currently supported:

- OpenAI
- Gemini
- Groq
- Anthropic

Each provider implements the common `Provider` interface, allowing the application to interact with different AI providers through a common abstraction.

---

## Architecture

VECTRA follows a provider-agnostic architecture.

The application separates:

- Application entry point
- Services
- Models
- Security
- Provider abstraction
- Provider implementations
- User interface

### Architecture Diagram

> **Architecture diagram will be added here.**

The architecture diagram will be added after the current codebase architecture is finalized and reviewed against the implementation.

---

## Project Structure

```text
src/main/
│
├── app/
│   └── KeyCheck.java
│
├── models/
│   ├── API.java
│   ├── Message.java
│   ├── ProviderConfig.java
│   ├── TokenUsage.java
│   ├── ProviderResponse.java
│   ├── ModelInfo.java
│   ├── ContextBudget.java
│   ├── ConversationContext.java
│   └── ModelRegistry.java
│
├── providers/
│   ├── Provider.java
│   ├── ProviderRegistry.java
│   ├── OpenAIProvider.java
│   ├── GeminiProvider.java
│   ├── GroqProvider.java
│   └── AnthropicProvider.java
│
├── security/
│   └── Encrypt.java
│
├── service/
│   ├── ChatService.java
│   ├── KeyManager.java
│   ├── ContextManager.java
│   ├── TokenEstimator.java
│   ├── HeuristicTokenEstimator.java
│   ├── CompactionService.java
│   ├── Summarizer.java
│   └── ProviderSummarizer.java
│
└── ui/
    └── ModelSelection.java
```

---

## How VECTRA Works

At a high level, the current prototype follows this flow:

```text
User
  │
  ▼
KeyCheck
  │
  ▼
ChatService
  │
  ├──────────────► Context Management
  │
  ├──────────────► Key Management
  │
  ▼
Provider Interface
  │
  ├── OpenAIProvider
  ├── GeminiProvider
  ├── GroqProvider
  └── AnthropicProvider
  │
  ▼
AI Provider API
```

The `Provider` interface allows `ChatService` to communicate with different providers without directly depending on a specific provider implementation.

---

# API Key Security

The current prototype encrypts provider API keys using **AES-256-GCM** before they are stored inside the application model.

The encryption process uses:

- AES-256
- GCM mode
- 12-byte randomly generated IV
- 128-bit authentication tag

The current prototype keeps the encryption key in memory.

Persistent key management is intentionally left for the backend implementation.

### Current Prototype Flow

```text
Provider API Key
       │
       ▼
   KeyManager
       │
       ▼
   AES-256-GCM
       │
       ▼
Encrypted API Key
       │
       ▼
      API
```

When a provider request is made, the encrypted API key is decrypted before being used to authenticate the request.

> Never commit real API keys or `.env` files to the repository.

---

# Provider Architecture

All providers implement the common `Provider` interface.

The interface currently exposes three major operations:

```java
List<String> getModels(API api);

ModelInfo getModelInfo(
    API api,
    String modelId
);

ProviderResponse sendRequest(
    API api,
    String selectedModel,
    List<Message> messages
);
```

This allows provider-specific HTTP requests and response parsing to remain inside their respective provider classes.

For example:

```text
ChatService
     │
     ▼
 Provider
     │
     ├── OpenAIProvider
     ├── GeminiProvider
     ├── GroqProvider
     └── AnthropicProvider
```

---

# Model Discovery

VECTRA dynamically retrieves available models from supported providers.

The user can select a model from the models returned by the provider.

Model metadata is used by the context-management system to determine:

- Maximum context size
- Maximum output tokens

This allows context management to adapt to the selected model.

---

# Token Usage

Provider responses are converted into a common `ProviderResponse` model containing:

- Response content
- Input tokens
- Output tokens
- Cached input tokens
- Total tokens

The provider's reported token usage is treated as the source of truth for actual usage.

The local token estimator is used only for pre-request context management.

---

# Context Management

VECTRA does not simply keep a fixed number of conversation messages.

Instead, it calculates a token budget based on the selected model's context window.

The process is:

```text
Conversation
     │
     ▼
Token Estimation
     │
     ▼
Context Budget Calculation
     │
     ▼
Threshold Reached?
     │
 ┌───┴────┐
 │        │
 NO      YES
 │        │
 ▼        ▼
Send   Compaction
          │
          ▼
      Summarization
          │
          ▼
    Keep Recent Messages
          │
          ▼
      Re-estimate
          │
          ▼
         Send
```

The current compaction threshold is:

```text
75% of the available input-token budget
```

The available input budget is calculated after reserving tokens for the model's output.

---

# Token Estimation

The current prototype uses a heuristic token estimator.

The estimator approximates token usage based on character count.

```text
Estimated Tokens ≈ Characters / 4
```

This estimation is used only to determine whether the conversation is approaching the context limit.

It is not used for billing.

Actual provider-reported token usage remains the source of truth.

---

# Conversation Compaction

When the estimated context reaches the configured threshold, VECTRA automatically compacts the conversation.

Instead of keeping an arbitrary number of messages, the system uses a token target.

The compaction process:

1. Identifies older messages.
2. Sends those messages to the summarizer.
3. Preserves important information through the generated summary.
4. Removes the compacted messages from the active message list.
5. Keeps recent messages directly.
6. Re-estimates the resulting context.

This allows conversations to grow without indefinitely retaining every previous message.

---

# Conversation Summarization

`ProviderSummarizer` implements the `Summarizer` interface.

The summarizer is instructed to preserve:

- Important facts
- Technical details
- Decisions
- User preferences
- Unresolved tasks

The summarizer is also instructed not to invent information.

The resulting summary becomes part of the conversation context.

---

# Error Handling

Provider errors are currently surfaced as exceptions containing the provider's HTTP status code and response body.

For example, provider rate limits can currently result in HTTP `429` errors.

Dedicated rate-limit handling, retry logic, and backoff are planned for a future iteration.

---

# Technology Stack

### Current Prototype

- Java
- Maven
- Java HTTP Client
- Gson
- AES-256-GCM
- Multiple AI provider REST APIs

### Planned Backend

- Spring Boot
- MongoDB
- REST API
- Persistent credential storage
- VECTRA virtual API keys

---

# Getting Started

## Requirements

- Java
- Maven
- An API key from a supported AI provider

## Clone the Repository

```bash
git clone https://github.com/Anirudhpande/KeyCheck.git
cd KeyCheck
```

## Build

```bash
mvn clean compile
```

## Run

Run the `KeyCheck` application.

The application will ask for:

1. AI provider
2. Provider API key
3. Model
4. Conversation prompts

Type:

```text
exit
```

to end the conversation.

---

# Environment Variables

Local secrets should never be committed to Git.

The repository ignores:

```text
.env
```

A future version will provide an `.env.example` file containing only the required variable names and no real credentials.

---

# Roadmap

## Core Prototype

- [x] Provider abstraction
- [x] Multiple AI providers
- [x] Dynamic model discovery
- [x] API-key encryption
- [x] Token usage tracking
- [x] Context-window budgeting
- [x] Token-based conversation compaction
- [x] Conversation summarization

## Reliability

- [ ] Provider rate-limit handling
- [ ] Retry and exponential backoff
- [ ] Provider-specific error handling
- [ ] Request timeout handling

## Backend

- [ ] Spring Boot backend
- [ ] MongoDB persistence
- [ ] User authentication
- [ ] Persistent conversations
- [ ] Persistent provider credentials

## VECTRA Platform

- [ ] VECTRA virtual API keys
- [ ] Unified API gateway
- [ ] Usage tracking
- [ ] Provider routing
- [ ] Provider fallback
- [ ] API marketplace

---

# Project Philosophy

VECTRA is being developed incrementally.

The goal is not to immediately build a large abstraction over multiple AI providers.

Instead, each subsystem is implemented, tested, understood, and then extended.

Current development focuses on understanding the underlying architecture before moving the prototype into a persistent Spring Boot backend.

---

# Status

🚧 **Active Development**

VECTRA is currently a Java prototype.

The provider abstraction, encrypted API-key handling, token usage tracking, and context-window management are currently implemented.

The project will continue evolving toward a persistent, provider-agnostic AI API platform.
>>>>>>> 1ff3173 (updated README.md)
