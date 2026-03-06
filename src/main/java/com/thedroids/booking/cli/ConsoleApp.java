package com.thedroids.booking.cli;

import com.thedroids.booking.application.*;
import com.thedroids.booking.domain.availability.TimeSlot;
import com.thedroids.booking.domain.booking.Booking;
import com.thedroids.booking.domain.booking.BookingStatus;
import com.thedroids.booking.domain.notification.*;
import com.thedroids.booking.domain.payment.*;
import com.thedroids.booking.domain.policy.PolicyManager;
import com.thedroids.booking.domain.policy.SystemPolicy;
import com.thedroids.booking.domain.service.ConsultingService;
import com.thedroids.booking.domain.user.*;
import com.thedroids.booking.infrastructure.persistence.*;
import com.thedroids.booking.ports.repository.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

public class ConsoleApp {

    private final Scanner scanner = new Scanner(System.in);

    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final ServiceRepository serviceRepository;
    private final PaymentRepository paymentRepository;
    private final PaymentMethodRepository paymentMethodRepository;
    private final TimeSlotRepository timeSlotRepository;

    private final BookingService bookingService;
    private final PaymentService paymentService;
    private final AvailabilityService availabilityService;
    private final ConsultantService consultantService;
    private final AdminService adminService;
    private final EventManager eventManager;

    private User currentUser;

    public ConsoleApp() {
        userRepository = new InMemoryUserRepository();
        bookingRepository = new InMemoryBookingRepository();
        serviceRepository = new InMemoryServiceRepository();
        paymentRepository = new InMemoryPaymentRepository();
        paymentMethodRepository = new InMemoryPaymentMethodRepository();
        timeSlotRepository = new InMemoryTimeSlotRepository();

        eventManager = new EventManager();
        for (EventType type : EventType.values()) {
            eventManager.subscribe(type, new EmailNotificationListener("system@thedroids.com"));
        }

        bookingService = new BookingService(bookingRepository, serviceRepository,
                timeSlotRepository, eventManager);
        paymentService = new PaymentService(paymentRepository, paymentMethodRepository,
                bookingRepository, serviceRepository, eventManager);
        availabilityService = new AvailabilityService(timeSlotRepository);
        consultantService = new ConsultantService(bookingRepository, eventManager);
        adminService = new AdminService(userRepository, eventManager);

        seedData();
    }

    public static void main(String[] args) {
        ConsoleApp app = new ConsoleApp();
        app.run();
    }

    private void run() {
        System.out.println("=".repeat(55));
        System.out.println("  Service Booking & Consulting Platform");
        System.out.println("  Team: The Droids | EECS 3311 Phase 1");
        System.out.println("=".repeat(55));

        boolean running = true;
        while (running) {
            System.out.println("\n--- Main Menu ---");
            System.out.println("1. Login");
            System.out.println("2. Register");
            System.out.println("0. Exit");
            int choice = readInt("Choose: ");

            switch (choice) {
                case 1 -> login();
                case 2 -> register();
                case 0 -> {
                    running = false;
                    System.out.println("Goodbye!");
                }
                default -> System.out.println("Invalid option.");
            }
        }
        scanner.close();
    }

    // Authentication

    private void login() {
        String email = readLine("Email: ");
        String password = readLine("Password: ");

        Optional<User> found = userRepository.findByEmail(email);
        if (found.isEmpty() || !found.get().getPassword().equals(password)) {
            System.out.println("Invalid email or password.");
            return;
        }

        currentUser = found.get();

        if (currentUser instanceof Consultant c && !c.isApproved()) {
            System.out.println("Your consultant account is pending admin approval.");
            currentUser = null;
            return;
        }

        System.out.println("Welcome, " + currentUser.getName() + " (" + currentUser.getRole() + ")!");

        if (currentUser instanceof Client cl) {
            clientMenu(cl);
        } else if (currentUser instanceof Consultant co) {
            consultantMenu(co);
        } else if (currentUser instanceof Admin ad) {
            adminMenu(ad);
        }
        currentUser = null;
    }

    private void register() {
        System.out.println("Register as: 1. Client  2. Consultant");
        int role = readInt("Choose: ");

        String name = readLine("Name: ");
        String email = readLine("Email: ");
        String password = readLine("Password: ");

        if (userRepository.findByEmail(email).isPresent()) {
            System.out.println("Email already registered.");
            return;
        }

        String id = UUID.randomUUID().toString();
        switch (role) {
            case 1 -> {
                userRepository.save(new Client(id, name, email, password));
                System.out.println("Client account created. You can now log in.");
            }
            case 2 -> {
                String spec = readLine("Specialization: ");
                double rate = readDouble("Hourly rate ($): ");
                userRepository.save(new Consultant(id, name, email, password, spec, rate));
                System.out.println("Consultant account created (pending admin approval).");
            }
            default -> System.out.println("Invalid role.");
        }
    }

    // Client Menu UseCase 1 - 7

    private void clientMenu(Client client) {
        boolean active = true;
        while (active) {
            System.out.println("\n--- Client Menu ---");
            System.out.println("1. Browse Consulting Services  (UC1)");
            System.out.println("2. Request a Booking           (UC2)");
            System.out.println("3. Cancel a Booking            (UC3)");
            System.out.println("4. View Booking History         (UC4)");
            System.out.println("5. Process Payment             (UC5)");
            System.out.println("6. Manage Payment Methods      (UC6)");
            System.out.println("7. View Payment History         (UC7)");
            System.out.println("0. Logout");
            int choice = readInt("Choose: ");

            try {
                switch (choice) {
                    case 1 -> browseServices();
                    case 2 -> requestBooking(client);
                    case 3 -> cancelBooking(client);
                    case 4 -> viewBookingHistory(client);
                    case 5 -> processPayment(client);
                    case 6 -> managePaymentMethods(client);
                    case 7 -> viewPaymentHistory(client);
                    case 0 -> active = false;
                    default -> System.out.println("Invalid option.");
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    private void browseServices() {
        List<ConsultingService> services = bookingService.browseServices();
        if (services.isEmpty()) {
            System.out.println("No services available.");
            return;
        }
        System.out.println("\n  Available Consulting Services:");
        System.out.printf("  %-5s %-20s %-15s %8s %10s%n", "#", "Name", "Type", "Duration", "Price");
        System.out.println("  " + "-".repeat(62));
        for (int i = 0; i < services.size(); i++) {
            ConsultingService s = services.get(i);
            System.out.printf("  %-5d %-20s %-15s %5d min  $%7.2f%n",
                    i + 1, s.getName(), s.getType(), s.getDurationMinutes(), s.getBasePrice());
        }
    }

    private void requestBooking(Client client) {
        List<ConsultingService> services = bookingService.browseServices();
        if (services.isEmpty()) {
            System.out.println("No services available.");
            return;
        }
        browseServices();
        int sIdx = readInt("Select service #: ") - 1;
        if (sIdx < 0 || sIdx >= services.size()) {
            System.out.println("Invalid selection.");
            return;
        }

        ConsultingService service = services.get(sIdx);
        List<TimeSlot> slots = availabilityService.getAvailableSlots(service.getConsultantId());
        if (slots.isEmpty()) {
            System.out.println("No available time slots for this consultant.");
            return;
        }

        System.out.println("\n  Available Time Slots:");
        for (int i = 0; i < slots.size(); i++) {
            System.out.println("  " + (i + 1) + ". " + slots.get(i));
        }
        int tIdx = readInt("Select slot #: ") - 1;
        if (tIdx < 0 || tIdx >= slots.size()) {
            System.out.println("Invalid selection.");
            return;
        }

        Booking booking = bookingService.requestBooking(
                client.getId(), service.getConsultantId(),
                service.getId(), slots.get(tIdx).getId());
        System.out.println("Booking requested! ID: " + booking.getId());
        System.out.println("Status: " + booking.getStatus());
    }

    private void cancelBooking(Client client) {
        List<Booking> bookings = bookingService.getClientBookings(client.getId()).stream()
                .filter(b -> b.getStatus() != BookingStatus.CANCELLED
                        && b.getStatus() != BookingStatus.COMPLETED
                        && b.getStatus() != BookingStatus.REJECTED)
                .toList();

        if (bookings.isEmpty()) {
            System.out.println("No active bookings to cancel.");
            return;
        }
        printBookings(bookings);
        int idx = readInt("Select booking # to cancel: ") - 1;
        if (idx < 0 || idx >= bookings.size()) {
            System.out.println("Invalid selection.");
            return;
        }

        bookingService.cancelBooking(bookings.get(idx).getId());
        System.out.println("Booking cancelled.");
    }

    private void viewBookingHistory(Client client) {
        List<Booking> bookings = bookingService.getClientBookings(client.getId());
        if (bookings.isEmpty()) {
            System.out.println("No bookings found.");
            return;
        }
        printBookings(bookings);
    }

    private void processPayment(Client client) {
        List<Booking> pending = bookingService.getClientBookings(client.getId()).stream()
                .filter(b -> b.getStatus() == BookingStatus.PENDING_PAYMENT)
                .toList();

        if (pending.isEmpty()) {
            System.out.println("No bookings awaiting payment.");
            return;
        }

        System.out.println("\n  Bookings Awaiting Payment:");
        printBookings(pending);
        int bIdx = readInt("Select booking # to pay: ") - 1;
        if (bIdx < 0 || bIdx >= pending.size()) {
            System.out.println("Invalid selection.");
            return;
        }

        List<PaymentMethod> methods = paymentService.getPaymentMethods(client.getId());
        if (methods.isEmpty()) {
            System.out.println("No payment methods on file. Add one first (option 6).");
            return;
        }

        System.out.println("\n  Your Payment Methods:");
        for (int i = 0; i < methods.size(); i++) {
            System.out.println("  " + (i + 1) + ". " + methods.get(i));
        }
        int mIdx = readInt("Select payment method #: ") - 1;
        if (mIdx < 0 || mIdx >= methods.size()) {
            System.out.println("Invalid selection.");
            return;
        }

        Payment payment = paymentService.processPayment(
                pending.get(bIdx).getId(), methods.get(mIdx).getId());
        System.out.println("Payment successful!");
        System.out.println("Transaction ID: " + payment.getTransactionId());
        System.out.println("Amount: $" + String.format("%.2f", payment.getAmount()));
    }

    private void managePaymentMethods(Client client) {
        System.out.println("\n  1. View payment methods");
        System.out.println("  2. Add payment method");
        System.out.println("  3. Remove payment method");
        int choice = readInt("Choose: ");

        switch (choice) {
            case 1 -> {
                List<PaymentMethod> methods = paymentService.getPaymentMethods(client.getId());
                if (methods.isEmpty()) {
                    System.out.println("No payment methods saved.");
                } else {
                    methods.forEach(m -> System.out.println("  - " + m));
                }
            }
            case 2 -> addPaymentMethod(client);
            case 3 -> {
                List<PaymentMethod> methods = paymentService.getPaymentMethods(client.getId());
                if (methods.isEmpty()) {
                    System.out.println("No payment methods to remove.");
                    return;
                }
                for (int i = 0; i < methods.size(); i++) {
                    System.out.println("  " + (i + 1) + ". " + methods.get(i));
                }
                int idx = readInt("Select # to remove: ") - 1;
                if (idx >= 0 && idx < methods.size()) {
                    paymentService.removePaymentMethod(methods.get(idx).getId());
                    System.out.println("Payment method removed.");
                }
            }
            default -> System.out.println("Invalid option.");
        }
    }

    private void addPaymentMethod(Client client) {
        System.out.println("  Payment types: 1. Credit Card  2. Debit Card  3. PayPal  4. Bank Transfer");
        int typeChoice = readInt("  Select type: ");
        PaymentMethodType type = switch (typeChoice) {
            case 1 -> PaymentMethodType.CREDIT_CARD;
            case 2 -> PaymentMethodType.DEBIT_CARD;
            case 3 -> PaymentMethodType.PAYPAL;
            case 4 -> PaymentMethodType.BANK_TRANSFER;
            default -> null;
        };
        if (type == null) {
            System.out.println("Invalid type.");
            return;
        }

        Map<String, String> details = new HashMap<>();
        switch (type) {
            case CREDIT_CARD, DEBIT_CARD -> {
                details.put("cardNumber", readLine("  Card number (16 digits): "));
                details.put("expiryDate", readLine("  Expiry date (MM/YY): "));
                details.put("cvv", readLine("  CVV (3-4 digits): "));
            }
            case PAYPAL -> details.put("email", readLine("  PayPal email: "));
            case BANK_TRANSFER -> {
                details.put("accountNumber", readLine("  Account number (8-12 digits): "));
                details.put("routingNumber", readLine("  Routing number (9 digits): "));
            }
        }

        PaymentMethod method = paymentService.addPaymentMethod(client.getId(), type, details);
        System.out.println("Payment method added: " + method);
    }

    private void viewPaymentHistory(Client client) {
        List<Payment> payments = paymentService.getPaymentHistory(client.getId());
        if (payments.isEmpty()) {
            System.out.println("No payment history.");
            return;
        }
        System.out.println("\n  Payment History:");
        System.out.printf("  %-5s %-12s %10s %-12s %-20s%n", "#", "Status", "Amount", "Transaction", "Date");
        System.out.println("  " + "-".repeat(65));
        for (int i = 0; i < payments.size(); i++) {
            Payment p = payments.get(i);
            System.out.printf("  %-5d %-12s $%9.2f %-12s %s%n",
                    i + 1, p.getStatus(), p.getAmount(),
                    p.getTransactionId() != null ? p.getTransactionId() : "N/A",
                    p.getTimestamp().toLocalDate());
        }
    }

    // Consultant Menu UseCase 8 - 10

    private void consultantMenu(Consultant consultant) {
        boolean active = true;
        while (active) {
            System.out.println("\n--- Consultant Menu ---");
            System.out.println("1. Manage Availability         (UC8)");
            System.out.println("2. View Booking Requests       (UC9)");
            System.out.println("3. Accept/Reject Booking       (UC9)");
            System.out.println("4. Complete a Booking          (UC10)");
            System.out.println("5. View All My Bookings");
            System.out.println("0. Logout");
            int choice = readInt("Choose: ");

            try {
                switch (choice) {
                    case 1 -> manageAvailability(consultant);
                    case 2 -> viewBookingRequests(consultant);
                    case 3 -> acceptRejectBooking(consultant);
                    case 4 -> completeBooking(consultant);
                    case 5 -> viewConsultantBookings(consultant);
                    case 0 -> active = false;
                    default -> System.out.println("Invalid option.");
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    private void manageAvailability(Consultant consultant) {
        System.out.println("\n  1. View my time slots");
        System.out.println("  2. Add a time slot");
        System.out.println("  3. Remove a time slot");
        int choice = readInt("Choose: ");

        switch (choice) {
            case 1 -> {
                List<TimeSlot> slots = availabilityService.getAllSlots(consultant.getId());
                if (slots.isEmpty()) {
                    System.out.println("No time slots configured.");
                } else {
                    for (int i = 0; i < slots.size(); i++) {
                        System.out.println("  " + (i + 1) + ". " + slots.get(i));
                    }
                }
            }
            case 2 -> {
                String dateStr = readLine("  Date (YYYY-MM-DD): ");
                String startStr = readLine("  Start time (HH:MM): ");
                String endStr = readLine("  End time (HH:MM): ");
                TimeSlot slot = availabilityService.addTimeSlot(
                        consultant.getId(),
                        LocalDate.parse(dateStr),
                        LocalTime.parse(startStr),
                        LocalTime.parse(endStr));
                System.out.println("Time slot added: " + slot);
            }
            case 3 -> {
                List<TimeSlot> slots = availabilityService.getAllSlots(consultant.getId());
                if (slots.isEmpty()) {
                    System.out.println("No time slots to remove.");
                    return;
                }
                for (int i = 0; i < slots.size(); i++) {
                    System.out.println("  " + (i + 1) + ". " + slots.get(i));
                }
                int idx = readInt("Select # to remove: ") - 1;
                if (idx >= 0 && idx < slots.size()) {
                    availabilityService.removeTimeSlot(slots.get(idx).getId());
                    System.out.println("Time slot removed.");
                }
            }
            default -> System.out.println("Invalid option.");
        }
    }

    private void viewBookingRequests(Consultant consultant) {
        List<Booking> requests = consultantService.getPendingRequests(consultant.getId());
        if (requests.isEmpty()) {
            System.out.println("No pending booking requests.");
            return;
        }
        System.out.println("\n  Pending Booking Requests:");
        printBookings(requests);
    }

    private void acceptRejectBooking(Consultant consultant) {
        List<Booking> requests = consultantService.getPendingRequests(consultant.getId());
        if (requests.isEmpty()) {
            System.out.println("No pending requests.");
            return;
        }
        printBookings(requests);
        int idx = readInt("Select booking #: ") - 1;
        if (idx < 0 || idx >= requests.size()) {
            System.out.println("Invalid selection.");
            return;
        }

        System.out.println("1. Accept  2. Reject");
        int action = readInt("Choose: ");
        String bookingId = requests.get(idx).getId();
        if (action == 1) {
            consultantService.acceptBooking(bookingId);
            System.out.println("Booking accepted — awaiting client payment.");
        } else if (action == 2) {
            consultantService.rejectBooking(bookingId);
            System.out.println("Booking rejected.");
        }
    }

    private void completeBooking(Consultant consultant) {
        List<Booking> paid = consultantService.getPaidBookings(consultant.getId());
        if (paid.isEmpty()) {
            System.out.println("No paid bookings to complete.");
            return;
        }
        System.out.println("\n  Paid Bookings (ready to complete):");
        printBookings(paid);
        int idx = readInt("Select booking # to complete: ") - 1;
        if (idx >= 0 && idx < paid.size()) {
            consultantService.completeBooking(paid.get(idx).getId());
            System.out.println("Booking marked as completed.");
        }
    }

    private void viewConsultantBookings(Consultant consultant) {
        List<Booking> bookings = bookingService.getConsultantBookings(consultant.getId());
        if (bookings.isEmpty()) {
            System.out.println("No bookings found.");
            return;
        }
        printBookings(bookings);
    }

    //  Admin Menu Use Case 11 - 12
    private void adminMenu(Admin admin) {
        boolean active = true;
        while (active) {
            System.out.println("\n--- Admin Menu ---");
            System.out.println("1. Approve/Reject Consultants  (UC11)");
            System.out.println("2. View System Policies        (UC12)");
            System.out.println("3. Update System Policies      (UC12)");
            System.out.println("4. View All Bookings");
            System.out.println("0. Logout");
            int choice = readInt("Choose: ");

            try {
                switch (choice) {
                    case 1 -> approveConsultants();
                    case 2 -> System.out.println("  Current Policy: " + adminService.getCurrentPolicy());
                    case 3 -> updatePolicies();
                    case 4 -> {
                        List<Booking> all = bookingRepository.findAll();
                        if (all.isEmpty()) {
                            System.out.println("No bookings in the system.");
                        } else {
                            printBookings(all);
                        }
                    }
                    case 0 -> active = false;
                    default -> System.out.println("Invalid option.");
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    private void approveConsultants() {
        List<Consultant> pending = adminService.getPendingConsultants();
        if (pending.isEmpty()) {
            System.out.println("No consultants pending approval.");
            return;
        }

        System.out.println("\n  Pending Consultant Registrations:");
        for (int i = 0; i < pending.size(); i++) {
            Consultant c = pending.get(i);
            System.out.printf("  %d. %s — %s ($%.2f/hr)%n",
                    i + 1, c.getName(), c.getSpecialization(), c.getHourlyRate());
        }
        int idx = readInt("Select consultant #: ") - 1;
        if (idx < 0 || idx >= pending.size()) {
            System.out.println("Invalid selection.");
            return;
        }

        System.out.println("1. Approve  2. Reject");
        int action = readInt("Choose: ");
        if (action == 1) {
            adminService.approveConsultant(pending.get(idx).getId());
            System.out.println("Consultant approved.");
        } else if (action == 2) {
            adminService.rejectConsultant(pending.get(idx).getId());
            System.out.println("Consultant rejected.");
        }
    }

    private void updatePolicies() {
        SystemPolicy policy = adminService.getCurrentPolicy();
        System.out.println("  Current: " + policy);
        policy.setCancellationWindowHours(readInt("  Cancellation window (hours): "));
        policy.setCancellationFeePercent(readDouble("  Cancellation fee (%): "));
        policy.setRefundPercent(readDouble("  Refund (%): "));
        System.out.println("  Enable notifications? (1=yes, 0=no)");
        policy.setNotificationsEnabled(readInt("  ") == 1);
        adminService.updatePolicy(policy);
        System.out.println("  Policies updated: " + policy);
    }

    // Helper Methods 

    private void printBookings(List<Booking> bookings) {
        System.out.printf("  %-5s %-16s %-15s %s%n", "#", "Status", "Service", "Created");
        System.out.println("  " + "-".repeat(55));
        for (int i = 0; i < bookings.size(); i++) {
            Booking b = bookings.get(i);
            String serviceName = serviceRepository.findById(b.getServiceId())
                    .map(ConsultingService::getName).orElse("Unknown");
            System.out.printf("  %-5d %-16s %-15s %s%n",
                    i + 1, b.getStatus(), serviceName, b.getCreatedAt().toLocalDate());
        }
    }

    private int readInt(String prompt) {
        System.out.print(prompt);
        while (!scanner.hasNextInt()) {
            scanner.next();
            System.out.print(prompt);
        }
        int value = scanner.nextInt();
        scanner.nextLine();
        return value;
    }

    private double readDouble(String prompt) {
        System.out.print(prompt);
        while (!scanner.hasNextDouble()) {
            scanner.next();
            System.out.print(prompt);
        }
        double value = scanner.nextDouble();
        scanner.nextLine();
        return value;
    }

    private String readLine(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    // Seed Data

    private void seedData() {
        Admin admin = new Admin("admin-1", "Admin", "admin@platform.com", "admin");
        userRepository.save(admin);

        Consultant c1 = new Consultant("con-1", "Alice Chen", "alice@consult.com", "pass",
                "Software Architecture", 150.0);
        c1.setApproved(true);
        Consultant c2 = new Consultant("con-2", "Bob Martinez", "bob@consult.com", "pass",
                "Career Advising", 100.0);
        c2.setApproved(true);
        userRepository.save(c1);
        userRepository.save(c2);

        serviceRepository.save(new ConsultingService("svc-1",
                "Architecture Review", "Review your system architecture for scalability",
                "Software", 60, 200.0, "con-1"));
        serviceRepository.save(new ConsultingService("svc-2",
                "Code Review", "In-depth review of your codebase",
                "Software", 45, 150.0, "con-1"));
        serviceRepository.save(new ConsultingService("svc-3",
                "Career Strategy", "Plan your next career move",
                "Career", 30, 80.0, "con-2"));
        serviceRepository.save(new ConsultingService("svc-4",
                "Resume Review", "Get your resume reviewed by an expert",
                "Career", 30, 60.0, "con-2"));

        LocalDate tomorrow = LocalDate.now().plusDays(1);
        LocalDate dayAfter = LocalDate.now().plusDays(2);
        timeSlotRepository.save(new TimeSlot("ts-1", "con-1", tomorrow,
                LocalTime.of(9, 0), LocalTime.of(10, 0)));
        timeSlotRepository.save(new TimeSlot("ts-2", "con-1", tomorrow,
                LocalTime.of(10, 0), LocalTime.of(11, 0)));
        timeSlotRepository.save(new TimeSlot("ts-3", "con-1", dayAfter,
                LocalTime.of(14, 0), LocalTime.of(15, 0)));
        timeSlotRepository.save(new TimeSlot("ts-4", "con-2", tomorrow,
                LocalTime.of(11, 0), LocalTime.of(11, 30)));
        timeSlotRepository.save(new TimeSlot("ts-5", "con-2", dayAfter,
                LocalTime.of(13, 0), LocalTime.of(13, 30)));

        Client demo = new Client("cli-1", "Demo User", "demo@test.com", "demo");
        userRepository.save(demo);
        paymentMethodRepository.save(new PaymentMethod("pm-1",
                PaymentMethodType.CREDIT_CARD, "cli-1",
                Map.of("cardNumber", "4111111111111111",
                        "expiryDate", "12/28", "cvv", "123")));
    }
}
