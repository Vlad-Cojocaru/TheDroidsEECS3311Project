EECS 3311: Project Pt.1 

Backend for a service booking and consulting platform. This repo contains my contribution: **Booking & State Management** - the booking lifecycle, state machine, consulting services, time-slot availability, and related application services and repositories.

## My contribution: Booking & State Management

- **Domain:** `Booking`, `BookingStatus`, state classes (`RequestedState`, `ConfirmedState`, `PendingPaymentState`, `PaidState`, `RejectedState`, `CancelledState`, `CompletedState`), `ConsultingService`, `TimeSlot`
- **Application:** `BookingService`, `AvailabilityService`
- **Ports:** `BookingRepository`, `ServiceRepository`, `TimeSlotRepository`
- **Infrastructure:** `InMemoryBookingRepository`, `InMemoryServiceRepository`, `InMemoryTimeSlotRepository`

Other areas (payments, users, admin, notifications, CLI) are owned by other team members and will be integrated as the project progresses.

## Build

- **Java:** 17  
- **Build:** `mvn compile`  
- **Package:** `mvn package` (library JAR; runnable CLI and main class will be provided by the team member responsible for the CLI)

## Diagrams

UML and other diagrams go in `diagrams/`.
