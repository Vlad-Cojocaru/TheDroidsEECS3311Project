# EECS 3311: Service Booking & Consulting Platform

Phase 2 - full frontend, Docker deployment, AI chatbot.

## Architecture Overview

Standard Spring Boot MVC layout. The Phase 1 CLI and in-memory repos are gone — everything now goes through REST endpoints backed by Postgres.

- `controller/` — REST endpoints. One per feature area (auth, bookings, payments, etc).
- `service/` — Business logic for each use case. Same logic from Phase 1, just wired up with Spring `@Service` instead of manual constructor injection.
- `model/` — JPA entities. Same domain classes from Phase 1 with `@Entity`/`@Table` annotations added for persistence.
- `repository/` — Spring Data JPA interfaces. Replaced the old in-memory repos and the hexagonal port/adapter layer (which was just pass-through delegation anyway).
- `config/` — Startup seed data, EventManager bean setup.

Frontend is plain HTML/CSS/JS served by nginx, which also proxies `/api/` requests to the backend.

## Design Patterns

- **State** — Booking lifecycle is managed by `BookingState` implementations. Each state knows what transitions are valid so we don't have if/else chains everywhere. There's 7 states total (Requested, Confirmed, PendingPayment, Paid, Rejected, Cancelled, Completed).
- **Observer** — `EventManager` fires events when things happen (booking confirmed, payment processed, etc). Listeners like `EmailNotificationListener` pick them up without the services needing to know about notifications directly.
- **Strategy** — Each payment type (credit card, debit, PayPal, bank transfer) has its own validation class implementing `PaymentValidationStrategy`.
- **Factory** — `PaymentValidationFactory` returns the right strategy for a given payment type so `PaymentService` doesn't have to figure it out itself.
- **Singleton** — `PolicyManager` is a singleton for the global system policy config.

## How to Run (Docker)

You need Docker installed.

```
docker-compose up --build
```

That starts 3 containers — Postgres, the Spring Boot backend, and the nginx frontend. First build takes a few minutes because it pulls dependencies.

Once it's up:
- Frontend: http://localhost:3000
- Backend API: http://localhost:8080/api/services (if you want to poke at it directly)

To stop everything: `docker-compose down`

To wipe the database too: `docker-compose down -v`

### Demo accounts (seeded on startup)

- admin@thedroids.com / admin123 (admin)
- dave@example.com / pass123 (client)
- eve@example.com / pass123 (client)
- alice@thedroids.com / pass123 (consultant)
- bob@thedroids.com / pass123 (consultant)
- carol@thedroids.com / pass123 (consultant)

## Environment Variables

Edit the `.env` file in the project root before running. The database stuff has defaults that work out of the box, but you need to add an OpenAI key if you want real AI chatbot responses.

- `POSTGRES_DB` — database name, defaults to `booking_platform`
- `POSTGRES_USER` — defaults to `postgres`
- `POSTGRES_PASSWORD` — defaults to `changeme`
- `OPENAI_API_KEY` — put your OpenAI key here. If left empty the chatbot runs in fallback mode (keyword matching instead of GPT)
- `OPENAI_MODEL` — defaults to `gpt-3.5-turbo`

## AI Chatbot

The chatbot shows up as a floating button in the bottom-right corner when you're logged in as a Client. Click it, type a question, and it responds with info about the platform (booking process, payment methods, cancellation policies, available services, etc).

It goes through the backend, the frontend sends your message to `POST /api/chat`, `ChatService` builds a prompt with platform context + the live service list, sends it to OpenAI, and returns the response. The AI never gets any personal data or private booking info, just general platform stuff.

See `AI_CHATBOT_DOCUMENTATION.md` for more details.

## GitHub Repository URL

https://github.com/Vlad-Cojocaru/TheDroidsEECS3311Project

## Team Member Contributions

### Phase 1

**Vlad** — Booking & state management. `Booking`, `BookingStatus`, all 7 state classes, `ConsultingService`, `TimeSlot`, `BookingService`, `AvailabilityService`, and the repos/in-memory implementations for those.

**Dennis** — Payment subsystem. `PaymentValidationFactory`, all 4 validation strategies (credit card, debit, PayPal, bank transfer), `Payment`, `PaymentMethod`, `PaymentMethodType`, `PaymentStatus`, `PaymentService`, and the payment/payment-method repos.

**ChunYen** — Users, notifications, admin. User hierarchy (`User`, `Client`, `Consultant`, `Admin`), the observer system (`EventType`, `EventListener`, `EventManager`, `EmailNotificationListener`, `SMSNotificationListener`), policy stuff (`SystemPolicy`, `PolicyManager`), `ConsultantService`, `AdminService`, `UserRepository`, and the `ConsoleApp` CLI.

### Phase 2

**Vlad** — Backend REST API + database layer. Converted the whole backend from CLI to Spring Boot MVC with Postgres. Created `BookingApplication.java`, `application.properties`, `EventManagerConfig`, `DataInitializer` (seeds the DB on startup), all 7 Spring Data JPA repository interfaces, and all 8 REST controllers (`AuthController`, `ServiceController`, `BookingController`, `PaymentController`, `PaymentMethodController`, `AvailabilityController`, `AdminController`). Modified every entity class to add JPA annotations (`@Entity`, `@Table`, etc) and updated every service class to use `@Service` + direct JPA repo injection. Deleted the old in-memory repos, the hexagonal port interfaces, the adapter classes, and the CLI entry point (19 files removed total — all replaced by Spring Data JPA or Spring Boot).

**ChunYen** — Full frontend. Built the entire single-page app from scratch — `index.html`, `styles.css`, all the JS modules: `api.js` (fetch wrappers), `router.js` (session management, role-based routing), `auth.js` (login/register forms), `client.js` (browse services, request bookings, cancel, pay, manage payment methods, payment history), `consultant.js` (accept/reject requests, complete bookings, manage availability, schedule view), `admin.js` (approve/reject consultants, edit system policies). Also wrote `nginx.conf` for proxying API requests and the frontend `Dockerfile`.

**Dennis** - AI chatbot + Docker/deployment. Created `ChatService.java` (builds the system prompt with platform context, calls OpenAI API, has keyword-based fallback), `ChatController.java`, `chatbot.js` (the floating chat widget in the frontend). Set up the backend `Dockerfile` (multi-stage Maven build), `docker-compose.yml` (Postgres + backend + frontend), `.env`, `AI_CHATBOT_DOCUMENTATION.md`. Updated `.gitignore` for Phase 2.
