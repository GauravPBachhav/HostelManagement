package in.gw.main.Entity;

import jakarta.persistence.*;
import java.time.LocalDate;

/**
 * RENT PAYMENT ENTITY
 * --------------------
 * Records each rent payment made by a student.
 *
 * UPI Payment Flow:
 *   1. Student pays via UPI deep-link
 *   2. Student uploads payment screenshot (mandatory)
 *   3. Status = VERIFICATION_PENDING
 *   4. Admin verifies screenshot → Approve (PAID) or Reject (REJECTED)
 */
@Entity
@Table(name = "rent_payments")
public class RentPayment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Which student made this payment
    @ManyToOne
    @JoinColumn(name = "profile_id", nullable = false)
    private StudentProfile studentProfile;

    private double amount;              // amount paid in rupees

    private String month;               // "January", "February", etc.

    private int year;                   // 2026

    private LocalDate paymentDate;      // when payment was submitted

    @Enumerated(EnumType.STRING)
    @Column(length = 30)
    private PaymentMode paymentMode;    // CASH, UPI, ONLINE

    @Enumerated(EnumType.STRING)
    @Column(length = 30)
    private PaymentStatus status;       // PAID, PENDING, VERIFICATION_PENDING, REJECTED

    // ===== NEW FIELDS FOR UPI VERIFICATION =====

    // Path to uploaded payment screenshot (relative to uploads dir)
    private String screenshotPath;

    // Optional UPI transaction reference ID entered by student
    private String upiTransactionId;

    // Admin remarks (reason for rejection, etc.)
    @Column(length = 500)
    private String adminRemarks;

    // Date when admin approved/rejected
    private LocalDate verifiedAt;

    // =====================
    // GETTERS & SETTERS
    // =====================

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public StudentProfile getStudentProfile() { return studentProfile; }
    public void setStudentProfile(StudentProfile studentProfile) { this.studentProfile = studentProfile; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public String getMonth() { return month; }
    public void setMonth(String month) { this.month = month; }

    public int getYear() { return year; }
    public void setYear(int year) { this.year = year; }

    public LocalDate getPaymentDate() { return paymentDate; }
    public void setPaymentDate(LocalDate paymentDate) { this.paymentDate = paymentDate; }

    public PaymentMode getPaymentMode() { return paymentMode; }
    public void setPaymentMode(PaymentMode paymentMode) { this.paymentMode = paymentMode; }

    public PaymentStatus getStatus() { return status; }
    public void setStatus(PaymentStatus status) { this.status = status; }

    public String getScreenshotPath() { return screenshotPath; }
    public void setScreenshotPath(String screenshotPath) { this.screenshotPath = screenshotPath; }

    public String getUpiTransactionId() { return upiTransactionId; }
    public void setUpiTransactionId(String upiTransactionId) { this.upiTransactionId = upiTransactionId; }

    public String getAdminRemarks() { return adminRemarks; }
    public void setAdminRemarks(String adminRemarks) { this.adminRemarks = adminRemarks; }

    public LocalDate getVerifiedAt() { return verifiedAt; }
    public void setVerifiedAt(LocalDate verifiedAt) { this.verifiedAt = verifiedAt; }
}
