# EECS 3311: Project Pt. 1

Backend for a service booking and consulting platform.

## Architecture Overview

The app is split into layers loosely based on hexagonal architecture:

- **Domain** — Core entities and business rules: `Booking`, `User` (Client/Consultant/Admin), `ConsultingService`, `TimeSlot`, `Payment`, `PaymentMethod`, plus the booking state machine, payment validators, notification system, and policy management. No dependencies on anything outside this layer.
- **Application** — Service classes that handle the use cases: `BookingService` (UC1–4), `PaymentService` (UC5–7), `AvailabilityService` (UC8), `ConsultantService` (UC9–10), `AdminService` (UC11–12). They talk to repositories through interfaces and fire events through `EventManager`.
- **Ports** — Repository interfaces (`BookingRepository`, `PaymentRepository`, `UserRepository`, etc.) that define how data gets stored/retrieved. Everything depends on these, not on concrete implementations.
- **Infrastructure** — In-memory implementations of the repository interfaces. Could be swapped for a real database without touching any other layer.
- **CLI** — `ConsoleApp` wires everything together, seeds some test data, and runs the main loop with role-based menus for clients, consultants, and admins.

## Design Patterns

- **State** — `Booking` uses a `BookingState` interface with 7 concrete states (`RequestedState`, `ConfirmedState`, etc.) to manage the booking lifecycle. Each state defines what transitions are allowed, so the booking object doesn't need a bunch of if/else checks.
- **Observer** — `EventManager` lets services fire events (like `BOOKING_CONFIRMED` or `PAYMENT_PROCESSED`) and listeners (`EmailNotificationListener`, `SMSNotificationListener`) react to them without being tightly coupled.
- **Strategy** — Payment validation is handled through `PaymentValidationStrategy` with separate implementations for credit card, debit card, PayPal, and bank transfer. Each one has its own validation logic.
- **Factory** — `PaymentValidationFactory` picks the right validation strategy based on the `PaymentMethodType`, so the calling code doesn't need to know which strategy to use.
- **Singleton** — `PolicyManager` uses a singleton to make sure there's one global set of system policies.
- **Repository** — All data access goes through repository interfaces (e.g. `BookingRepository`, `PaymentRepository`), with in-memory implementations behind them. Makes it easy to swap in a real database later.

## How to Run

- **Java:** 17
- **Build:** `mvn compile`
- **Run:** `mvn compile exec:java`
- **Package:** `mvn package`

## GitHub Repository URL

https://github.com/Vlad-Cojocaru/TheDroidsEECS3311Project

## Team Member Contributions

### Vlad — Booking & State Management

- **Domain:** `Booking`, `BookingStatus`, state classes (`RequestedState`, `ConfirmedState`, `PendingPaymentState`, `PaidState`, `RejectedState`, `CancelledState`, `CompletedState`), `ConsultingService`, `TimeSlot`
- **Application:** `BookingService`, `AvailabilityService`
- **Ports:** `BookingRepository`, `ServiceRepository`, `TimeSlotRepository`
- **Infrastructure:** `InMemoryBookingRepository`, `InMemoryServiceRepository`, `InMemoryTimeSlotRepository`

### Dennis — Payment Subsystem

- **Domain:** `PaymentValidationFactory`, payment validation strategies (`BankTransferValidationStrategy`, `CreditCardValidationStrategy`, `DebitCardValidationStrategy`, `PaypalValidationStrategy`, `PaymentValidationStrategy`), `Payment`, `PaymentMethod`, `PaymentMethodType`, `PaymentStatus`
- **Application:** `PaymentService`
- **Ports:** `PaymentRepository`, `PaymentMethodRepository`
- **Infrastructure:** `InMemoryPaymentMethodRepository`, `InMemoryPaymentRepository`

### ChunYen — Users, Notifications & Admin

- **Domain:** User roles (`User`, `Client`, `Consultant`, `Admin`), notification system (`EventType`, `EventListener`, `EventManager`, `EmailNotificationListener`, `SMSNotificationListener`), system policy (`SystemPolicy`, `PolicyManager`)
- **Application:** `ConsultantService`, `AdminService`
- **Ports:** `UserRepository`
- **Infrastructure:** `InMemoryUserRepository`
- **CLI:** `ConsoleApp`
