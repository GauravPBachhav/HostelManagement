package in.gw.main.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * EMAIL SERVICE
 * ==============
 * Sends notification emails to students.
 *
 * Used for:
 *   - Admission approved → congratulation email
 *   - Admission rejected → info email
 *
 * Uses Spring's JavaMailSender (configured in application.properties).
 * If mail server is unavailable, errors are caught and logged
 * (won't crash the application).
 */
@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    /**
     * Send email to student when admission APPROVED.
     * Includes room number and rent info.
     */
    public void sendApprovalEmail(String toEmail, String studentName, String roomNumber, int monthlyRent) {
        try {
            SimpleMailMessage msg = new SimpleMailMessage();
            msg.setTo(toEmail);
            msg.setSubject("🎉 Admission Approved - Shivtirth Hostel");
            msg.setText(
                "Dear " + studentName + ",\n\n"
                + "Congratulations! Your hostel admission has been APPROVED.\n\n"
                + "Room Details:\n"
                + "  Room Number: " + roomNumber + "\n"
                + "  Monthly Rent: ₹" + monthlyRent + "\n\n"
                + "Please login to your dashboard to view complete details and pay rent.\n\n"
                + "Website: http://localhost:8080/login\n\n"
                + "Best regards,\n"
                + "Shivtirth Hostel Management"
            );
            mailSender.send(msg);
        } catch (Exception e) {
            // Log error but don't crash the app
            System.err.println("⚠ EMAIL SEND FAILED: " + e.getMessage());
        }
    }

    /**
     * Send email to student when admission REJECTED.
     */
    public void sendRejectionEmail(String toEmail, String studentName) {
        try {
            SimpleMailMessage msg = new SimpleMailMessage();
            msg.setTo(toEmail);
            msg.setSubject("Admission Update - Shivtirth Hostel");
            msg.setText(
                "Dear " + studentName + ",\n\n"
                + "We regret to inform you that your hostel admission application has been REJECTED.\n\n"
                + "You can re-apply with updated details by logging into your dashboard.\n\n"
                + "Website: http://localhost:8080/login\n\n"
                + "If you have questions, please contact the hostel office.\n\n"
                + "Best regards,\n"
                + "Shivtirth Hostel Management"
            );
            mailSender.send(msg);
        } catch (Exception e) {
            System.err.println("⚠ EMAIL SEND FAILED: " + e.getMessage());
        }
    }

    /**
     * Send 6-digit OTP for email verification during registration.
     */
    public void sendOtpEmail(String toEmail, String studentName, String otp) {
        try {
            SimpleMailMessage msg = new SimpleMailMessage();
            msg.setTo(toEmail);
            msg.setSubject("Email Verification - Shivtirth Hostel");
            msg.setText(
                "Dear " + studentName + ",\n\n"
                + "Your OTP for email verification is:\n\n"
                + "    " + otp + "\n\n"
                + "This code is valid for 10 minutes.\n"
                + "If you did not register, please ignore this email.\n\n"
                + "Best regards,\n"
                + "Shivtirth Hostel Management"
            );
            mailSender.send(msg);
        } catch (Exception e) {
            System.err.println("⚠ OTP EMAIL SEND FAILED: " + e.getMessage());
        }
    }

    /**
     * Send 6-digit OTP for password reset (forgot password).
     */
    public void sendPasswordResetOtp(String toEmail, String otp) {
        try {
            SimpleMailMessage msg = new SimpleMailMessage();
            msg.setTo(toEmail);
            msg.setSubject("Password Reset OTP - Shivtirth Hostel");
            msg.setText(
                "Hello,\n\n"
                + "You requested a password reset for your Shivtirth Hostel account.\n\n"
                + "Your OTP is:\n\n"
                + "    " + otp + "\n\n"
                + "This code is valid for 10 minutes.\n"
                + "If you did not request this, please ignore this email.\n\n"
                + "Best regards,\n"
                + "Shivtirth Hostel Management"
            );
            mailSender.send(msg);
        } catch (Exception e) {
            System.err.println("⚠ PASSWORD RESET OTP EMAIL SEND FAILED: " + e.getMessage());
        }
    }
}
