EECS 3311: Project Pt.1 

Backend for a service booking and consulting platform. This repo contains vlad's contribution: **Booking & State Management** - the booking lifecycle, state machine, consulting services, time-slot availability, and related application services and repositories.

## Vlad's Contribution: Booking & State Management

- **Domain:** `Booking`, `BookingStatus`, state classes (`RequestedState`, `ConfirmedState`, `PendingPaymentState`, `PaidState`, `RejectedState`, `CancelledState`, `CompletedState`), `ConsultingService`, `TimeSlot`
- **Application:** `BookingService`, `AvailabilityService`
- **Ports:** `BookingRepository`, `ServiceRepository`, `TimeSlotRepository`
- **Infrastructure:** `InMemoryBookingRepository`, `InMemoryServiceRepository`, `InMemoryTimeSlotRepository`

Other areas (payments, users, admin, notifications, CLI) are owned by other team members and will be integrated as the project progresses.

## Dennis' Contribution: Payment Subsystem

Domain: factory method(`PaymentValidationFactory`), 
        payment validation strategies (`BankTransferValidationStrategy`, `CreditCardValidationStrategy`, `DebidCardValidation`, `PaymentValidationStrategy`, `PaypalValidationStrategy`),
        `Payment`, `PaymentMethod`, `PaymentMethodType`, `PaymentStatus`

Application: `PaymentService`

Ports: `PaymentRepository`, `PaymentMethodRepository`

Infrastructure: `InMemoryPaymentMethodRepository`, `InMemoryPaymentRepository`

## ChunYen's Contribution: Users, Notifications & Admin 

Domain: user roles (`User`, `Client`, `Consultant`, `Admin`), notification system (`EventType`, `EventListener`, `EventManager`,` EmailNotificationListener`, `SMSNotificationListener`), system policy (`SystemPolicy`, `PolicyManager`)

Application:`ConsultantService`, `AdminService`

Ports: `UserRepository`

Infrastructure: `InMemoryUserRepository`

CLI: `ConsoleApp`


## Build

- **Java:** 17  
- **Build:** `mvn compile`  
- **Package:** `mvn package` (library JAR; runnable CLI and main class will be provided by the team member responsible for the CLI)

## Diagrams

UML and other diagrams go in `diagrams/`.

