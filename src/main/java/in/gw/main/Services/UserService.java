package in.gw.main.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import in.gw.main.Entity.EmailVerificationToken;
import in.gw.main.Entity.User;
import in.gw.main.Repository.EmailVerificationRepository;
import in.gw.main.Repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Random;

/**
 * USER SERVICE
 * =============
 * Handles registration, OTP verification, login, and password management.
 *
 * Registration Flow (NO fake entries):
 *   1. registerUser() → saves data in token table (NOT users table)
 *   2. OTP emailed
 *   3. verifyOtp() → OTP correct → User CREATED in users table → token deleted
 *   4. Only verified users exist in users table
 */
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailVerificationRepository otpRepository;

    @Autowired
    private EmailService emailService;

    /**
     * REGISTER — stores data in token table only.
     * User is NOT created yet. Only after OTP verification.
     * Returns the email for redirect to verify page.
     */
    @Transactional
    public String registerUser(String name, String email, String rawPassword) {
        email = email.toLowerCase();

        // Check if already verified user exists
        User existing = userRepository.findByEmail(email);
        if (existing != null) {
            throw new RuntimeException("Email already registered! Please login.");
        }

        // Generate OTP
        String otp = String.format("%06d", new Random().nextInt(999999));

        // Check if token already exists for this email (re-registration attempt)
        EmailVerificationToken token = otpRepository.findByEmail(email);
        if (token == null) {
            token = new EmailVerificationToken();
            token.setEmail(email);
        }

        // Update or set fields (works for both new and existing tokens)
        token.setName(name);
        token.setHashedPassword(passwordEncoder.encode(rawPassword));
        token.setOtp(otp);
        token.setExpiryTime(LocalDateTime.now().plusMinutes(10));
        otpRepository.save(token);

        // Send OTP email
        emailService.sendOtpEmail(email, name, otp);

        return email;
    }

    /**
     * VERIFY OTP — if correct, create the actual User in users table.
     * Only verified users exist in the database.
     */
    @Transactional
    public boolean verifyOtp(String email, String enteredOtp) {
        EmailVerificationToken token = otpRepository.findByEmail(email.toLowerCase());
        if (token == null) return false;

        // Check expiry
        if (token.isExpired()) {
            otpRepository.delete(token);
            return false;
        }

        // Check OTP
        if (!token.getOtp().equals(enteredOtp.trim())) {
            return false;
        }

        // ✅ OTP correct — NOW create the actual User
        User user = new User();
        user.setName(token.getName());
        user.setEmail(token.getEmail());
        user.setPassword(token.getHashedPassword());  // Already hashed
        user.setRole("USER");
        user.setProfileCompleted(false);
        user.setEmailVerified(true);   // Verified!
        userRepository.save(user);

        // Cleanup token
        otpRepository.delete(token);
        return true;
    }

    /**
     * RESEND OTP — generates new OTP for pending registration.
     */
    @Transactional
    public boolean resendOtp(String email) {
        EmailVerificationToken token = otpRepository.findByEmail(email.toLowerCase());
        if (token == null) return false;

        // Generate new OTP
        String otp = String.format("%06d", new Random().nextInt(999999));
        token.setOtp(otp);
        token.setExpiryTime(LocalDateTime.now().plusMinutes(10));
        otpRepository.save(token);

        emailService.sendOtpEmail(email, token.getName(), otp);
        return true;
    }

    // =====================
    // EXISTING METHODS
    // =====================

    public User checkLogin(String email, String password) {
        if (email == null || password == null) return null;
        User user = userRepository.findByEmail(email.toLowerCase());
        if (user != null && user.getPassword().equals(password)) return user;
        return null;
    }

    public void updateUser(User user) {
        userRepository.save(user);
    }

    public User findById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email.toLowerCase());
    }

    public boolean validateUser(String email, String password) {
        return checkLogin(email, password) != null;
    }

    public String resetPassword(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        String defaultPassword = "hostel123";
        user.setPassword(passwordEncoder.encode(defaultPassword));
        userRepository.save(user);
        return defaultPassword;
    }

    public java.util.List<User> findAll() {
        return userRepository.findAll();
    }
}