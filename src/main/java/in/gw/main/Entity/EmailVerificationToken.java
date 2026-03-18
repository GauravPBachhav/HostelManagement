package in.gw.main.Entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * EMAIL VERIFICATION TOKEN
 * -------------------------
 * Temporarily stores registration data + OTP BEFORE user is created.
 *
 * Flow:
 *   1. User registers → data saved HERE (not in users table)
 *   2. OTP emailed to user
 *   3. User enters OTP → if valid → User created in users table
 *   4. Token deleted after successful verification
 *
 * This prevents fake/unverified entries in the users table.
 * OTP expires after 10 minutes.
 */
@Entity
@Table(name = "email_verification_tokens")
public class EmailVerificationToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String otp;                 // 6-digit code

    // --- Temporary registration data (stored until verified) ---
    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String hashedPassword;      // BCrypt hashed password

    @Column(nullable = false)
    private LocalDateTime expiryTime;   // OTP valid for 10 minutes

    // =====================
    // GETTERS & SETTERS
    // =====================
    public Long getId() { return id; }

    public String getOtp() { return otp; }
    public void setOtp(String otp) { this.otp = otp; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getHashedPassword() { return hashedPassword; }
    public void setHashedPassword(String hashedPassword) { this.hashedPassword = hashedPassword; }

    public LocalDateTime getExpiryTime() { return expiryTime; }
    public void setExpiryTime(LocalDateTime expiryTime) { this.expiryTime = expiryTime; }

    /** Check if this OTP has expired */
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiryTime);
    }
}
