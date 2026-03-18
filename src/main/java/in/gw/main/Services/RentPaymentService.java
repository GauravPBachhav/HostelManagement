package in.gw.main.Services;

import in.gw.main.Entity.*;
import in.gw.main.Repository.RentPaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

/**
 * RENT PAYMENT SERVICE
 * ---------------------
 * Handles all rent payment operations:
 *   - Record a new payment
 *   - Get payment history for a student
 *   - Get recent payments for admin overview
 */
@Service
public class RentPaymentService {

    @Autowired
    private RentPaymentRepository rentPaymentRepository;

    /**
     * Record a new rent payment.
     * Throws exception if payment already recorded for same month/year.
     */
    public void recordPayment(StudentProfile profile, String month, int year, String paymentMode) {
        // --- Duplicate check ---
        if (rentPaymentRepository.existsByStudentProfileAndMonthAndYear(profile, month, year)) {
            throw new RuntimeException("Payment for " + month + " " + year + " is already recorded!");
        }

        RentPayment payment = new RentPayment();
        payment.setStudentProfile(profile);

        // Get rent amount from the assigned room
        if (profile.getRoom() != null) {
            payment.setAmount(profile.getRoom().getMonthlyRent());
        } else {
            payment.setAmount(0);  // No room assigned yet
        }

        payment.setMonth(month);
        payment.setYear(year);
        payment.setPaymentDate(LocalDate.now());
        payment.setPaymentMode(PaymentMode.valueOf(paymentMode));
        payment.setStatus(PaymentStatus.PAID);

        rentPaymentRepository.save(payment);
    }

    /** Get all payments for a specific student (for student dashboard) */
    public List<RentPayment> findByProfile(StudentProfile profile) {
        return rentPaymentRepository.findByStudentProfileOrderByYearDescMonthDesc(profile);
    }

    /** Find a single payment by ID (for PDF receipt download) */
    public RentPayment findById(Long id) {
        return rentPaymentRepository.findById(id).orElse(null);
    }

    /** Get 20 most recent payments across all students (for admin overview) */
    public List<RentPayment> getRecentPayments() {
        return rentPaymentRepository.findTop20ByOrderByPaymentDateDesc();
    }
}
