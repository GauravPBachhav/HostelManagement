package in.gw.main.Entity;

import jakarta.persistence.*;
import java.time.LocalDate;

/**
 * RENT PAYMENT ENTITY
 * --------------------
 * Records each rent payment made by a student.
 *
 * Each payment records:
 *   - Which student paid (linked to StudentProfile)
 *   - How much was paid
 *   - For which month and year
 *   - Payment mode (CASH, UPI, ONLINE)
 *   - Payment date
 *   - Status (PAID or PENDING)
 *
 * One student can have MANY payments (one per month).
 * This is a @ManyToOne relationship with StudentProfile.
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

    private LocalDate paymentDate;      // when payment was made

    @Enumerated(EnumType.STRING)
    private PaymentMode paymentMode;    // CASH, UPI, ONLINE

    @Enumerated(EnumType.STRING)
    private PaymentStatus status;       // PAID, PENDING

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
}
