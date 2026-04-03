package com.thedroids.booking.config;

import com.thedroids.booking.model.availability.TimeSlot;
import com.thedroids.booking.model.policy.SystemPolicy;
import com.thedroids.booking.model.service.ConsultingService;
import com.thedroids.booking.model.user.Admin;
import com.thedroids.booking.model.user.Client;
import com.thedroids.booking.model.user.Consultant;
import com.thedroids.booking.repository.PolicyRepository;
import com.thedroids.booking.repository.ServiceRepository;
import com.thedroids.booking.repository.TimeSlotRepository;
import com.thedroids.booking.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final ServiceRepository serviceRepository;
    private final TimeSlotRepository timeSlotRepository;
    private final PolicyRepository policyRepository;

    public DataInitializer(UserRepository userRepository,
                           ServiceRepository serviceRepository,
                           TimeSlotRepository timeSlotRepository,
                           PolicyRepository policyRepository) {
        this.userRepository = userRepository;
        this.serviceRepository = serviceRepository;
        this.timeSlotRepository = timeSlotRepository;
        this.policyRepository = policyRepository;
    }

    @Override
    public void run(String... args) {
        
        // The first thing run() does is check whether the admin user already exists in the DB
        if (userRepository.findByEmail("admin@thedroids.com").isPresent()) {
            return;
        }

        Admin admin = new Admin("admin-001", "System Admin", "admin@thedroids.com", "admin123");
        userRepository.save(admin);

        Consultant c1 = new Consultant("con-001", "Alice Johnson", "alice@thedroids.com", "pass123",
                "Software Architecture", 150.0);
        c1.setApproved(true);
        userRepository.save(c1);

        Consultant c2 = new Consultant("con-002", "Bob Smith", "bob@thedroids.com", "pass123",
                "Career Advising", 120.0);
        c2.setApproved(true);
        userRepository.save(c2);

        Consultant c3 = new Consultant("con-003", "Carol Davis", "carol@thedroids.com", "pass123",
                "Technical Support", 100.0);
        c3.setApproved(true);
        userRepository.save(c3);

        Client cl1 = new Client("cli-001", "Dave Wilson", "dave@example.com", "pass123");
        userRepository.save(cl1);

        Client cl2 = new Client("cli-002", "Eve Martinez", "eve@example.com", "pass123");
        userRepository.save(cl2);

        serviceRepository.save(new ConsultingService(UUID.randomUUID().toString(),
                "Architecture Review", "Full review of your software architecture",
                "Software", 60, 200.0, "con-001"));
        serviceRepository.save(new ConsultingService(UUID.randomUUID().toString(),
                "Code Review", "In-depth code quality and best practices review",
                "Software", 45, 150.0, "con-001"));
        serviceRepository.save(new ConsultingService(UUID.randomUUID().toString(),
                "Career Strategy Session", "Personalized career planning and advice",
                "Career", 60, 180.0, "con-002"));
        serviceRepository.save(new ConsultingService(UUID.randomUUID().toString(),
                "Resume Review", "Professional resume critique and improvement",
                "Career", 30, 80.0, "con-002"));
        serviceRepository.save(new ConsultingService(UUID.randomUUID().toString(),
                "System Troubleshooting", "Diagnose and resolve technical issues",
                "Support", 45, 120.0, "con-003"));
        serviceRepository.save(new ConsultingService(UUID.randomUUID().toString(),
                "Cloud Migration Consult", "Planning and strategy for cloud migration",
                "Support", 90, 250.0, "con-003"));

        LocalDate today = LocalDate.now();
        for (int d = 1; d <= 5; d++) {
            LocalDate date = today.plusDays(d);
            timeSlotRepository.save(new TimeSlot(UUID.randomUUID().toString(), "con-001",
                    date, LocalTime.of(9, 0), LocalTime.of(10, 0)));
            timeSlotRepository.save(new TimeSlot(UUID.randomUUID().toString(), "con-001",
                    date, LocalTime.of(14, 0), LocalTime.of(15, 0)));
            timeSlotRepository.save(new TimeSlot(UUID.randomUUID().toString(), "con-002",
                    date, LocalTime.of(10, 0), LocalTime.of(11, 0)));
            timeSlotRepository.save(new TimeSlot(UUID.randomUUID().toString(), "con-003",
                    date, LocalTime.of(13, 0), LocalTime.of(14, 0)));
        }

        if (policyRepository.count() == 0) {
            policyRepository.save(new SystemPolicy());
        }

        System.out.println("=== Database seeded with sample data ===");
    }
}
