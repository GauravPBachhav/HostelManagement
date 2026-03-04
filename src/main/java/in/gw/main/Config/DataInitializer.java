package in.gw.main.Config;

import in.gw.main.Entity.User;
import in.gw.main.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * DATA INITIALIZER
 * -----------------
 * This class runs AUTOMATICALLY when the app starts.
 * It creates a default ADMIN account if one doesn't exist.
 *
 * ╔══════════════════════════════════════╗
 * ║  DEFAULT ADMIN LOGIN:               ║
 * ║    Email:    admin@hostel.com        ║
 * ║    Password: admin123               ║
 * ╚══════════════════════════════════════╝
 *
 * NOTE: After adding BCrypt, old plain-text passwords won't work.
 * If you had existing users, they need to re-register.
 * The admin account above is created with BCrypt hashed password.
 */
@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        // Only create admin if it doesn't already exist
        if (userRepository.findByEmail("admin@hostel.com") == null) {
            User admin = new User();
            admin.setName("Hostel Admin");
            admin.setEmail("admin@hostel.com");
            admin.setPassword(passwordEncoder.encode("admin123"));  // BCrypt hashed!
            admin.setRole("ADMIN");
            admin.setProfileCompleted(true);
            userRepository.save(admin);

            System.out.println("╔══════════════════════════════════════╗");
            System.out.println("║  DEFAULT ADMIN CREATED               ║");
            System.out.println("║  Email:    admin@hostel.com          ║");
            System.out.println("║  Password: admin123                  ║");
            System.out.println("╚══════════════════════════════════════╝");
        }
    }
}
