package in.gw.main.Config;

import in.gw.main.Entity.Facility;
import in.gw.main.Entity.User;
import in.gw.main.Repository.FacilityRepository;
import in.gw.main.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * DATA INITIALIZER
 * -----------------
 * Runs on startup:
 *   1. Creates default ADMIN if none exists
 *   2. Seeds default FACILITY cards if none exist
 */
@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private FacilityRepository facilityRepository;

    @Override
    public void run(String... args) {
        // ===== Default Admin =====
        if (userRepository.findByEmail("admin@hostel.com") == null) {
            User admin = new User();
            admin.setName("Hostel Admin");
            admin.setEmail("admin@hostel.com");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRole("ADMIN");
            admin.setProfileCompleted(true);
            admin.setEmailVerified(true);
            userRepository.save(admin);

            System.out.println("╔══════════════════════════════════════╗");
            System.out.println("║  DEFAULT ADMIN CREATED               ║");
            System.out.println("║  Email:    admin@hostel.com          ║");
            System.out.println("║  Password: admin123                  ║");
            System.out.println("╚══════════════════════════════════════╝");
        }

        // ===== Default Facilities (seed only if empty) =====
        if (facilityRepository.count() == 0) {
            Facility f1 = new Facility();
            f1.setTitle("Modern Building");
            f1.setDescription("Well-maintained 3-floor hostel with spacious rooms");
            f1.setGradientColors("#1a1a2e,#4361ee");
            f1.setDisplayOrder(1);
            facilityRepository.save(f1);

            Facility f2 = new Facility();
            f2.setTitle("Furnished Rooms");
            f2.setDescription("Single, Double & Triple sharing options available");
            f2.setGradientColors("#16213e,#06d6a0");
            f2.setDisplayOrder(2);
            facilityRepository.save(f2);

            Facility f3 = new Facility();
            f3.setTitle("Safe & Secure");
            f3.setDescription("24/7 CCTV surveillance and security staff");
            f3.setGradientColors("#1a1a2e,#f72585");
            f3.setDisplayOrder(3);
            facilityRepository.save(f3);

            Facility f4 = new Facility();
            f4.setTitle("Community Living");
            f4.setDescription("Friendly environment for all students");
            f4.setGradientColors("#16213e,#ffd166");
            f4.setDisplayOrder(4);
            facilityRepository.save(f4);

            System.out.println("✅ Default facilities seeded (4 cards).");
        }
    }
}
