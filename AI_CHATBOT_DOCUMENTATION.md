# AI Customer Assistant

## What it is

There's a chatbot built into the platform that clients can use to ask questions about how things work. It shows up as a floating button in the bottom-right when you're logged in as a Client. It doesn't do anything on its own — it just answers questions about the platform.

## What it can answer

- How to book a session (the whole flow from browsing to payment)
- What payment methods are available (credit card, debit, PayPal, bank transfer)
- Cancellation and refund policy info
- What consulting services are currently listed
- General stuff like how to register or navigate the app

## Example questions

- "How do I book a consulting session?" — walks you through browsing services, picking a time slot, submitting the request, waiting for the consultant to accept, and paying
- "What payment methods are accepted?" — lists the 4 types, mentions they're simulated
- "Can I cancel my booking?" — explains the cancellation window (24h default) and refund (90% default)
- "What services are available?" — pulls the current list from the database and shows names/prices
- "How does the refund policy work?" — explains the fee and refund percentages

## How it works

The frontend sends the user's message to `POST /api/chat`. On the backend, `ChatService` builds a system prompt that includes some context about the platform (how booking works, payment types, cancellation rules) plus the list of services currently in the database. It sends that along with the user's question to the OpenAI Chat Completions API and returns whatever GPT says.

If there's no API key set, or the API call fails for whatever reason, it falls back to a simple keyword matcher — it checks if the message mentions "book", "pay", "cancel", or "service" and returns a canned response for that topic. So the chatbot still works for demos even without a key.

## What the AI gets (and doesn't get)

The prompt only includes general platform info and the public service list. It never gets:
- User names, emails, or passwords
- Booking IDs or who booked what
- Payment details like card numbers or transaction IDs

The AI also can't do anything — it can't make bookings, process payments, or modify data. It just responds with text.

## Config

Set these in the `.env` file:
- `OPENAI_API_KEY` — your OpenAI key. Leave it empty and the chatbot runs in fallback mode.
- `OPENAI_MODEL` — which model to use, defaults to `gpt-3.5-turbo`
