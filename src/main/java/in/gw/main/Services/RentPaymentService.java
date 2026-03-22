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
 *   - Submit payment request (with screenshot proof)
 *   - Admin approve / reject
 *   - Payment history
 */
@Service
public class RentPaymentService {

    @Autowired
    private RentPaymentRepository rentPaymentRepository;

    /**
     * Submit a new payment request with UPI screenshot proof.
     * Status is set to VERIFICATION_PENDING until admin approves.
     */
    public void submitPaymentRequest(StudentProfile profile, String month, int year,
                                      String screenshotPath, String upiTransactionId) {
        // --- Duplicate check ---
        if (rentPaymentRepository.existsByStudentProfileAndMonthAndYear(profile, month, year)) {
            throw new RuntimeException("Payment for " + month + " " + year + " is already recorded or pending!");
        }

        RentPayment payment = new RentPayment();
        payment.setStudentProfile(profile);

        // Get rent amount from the assigned room
        if (profile.getRoom() != null) {
            payment.setAmount(profile.getRoom().getMonthlyRent());
        } else {
            payment.setAmount(0);
        }

        payment.setMonth(month);
        payment.setYear(year);
        payment.setPaymentDate(LocalDate.now());
        payment.setPaymentMode(PaymentMode.UPI);
        payment.setStatus(PaymentStatus.VERIFICATION_PENDING);
        payment.setScreenshotPath(screenshotPath);
        payment.setUpiTransactionId(upiTransactionId);

        rentPaymentRepository.save(payment);
    }

    /**
     * Legacy: Record a payment directly (for CASH payments by admin).
     */
    public void recordPayment(StudentProfile profile, String month, int year, String paymentMode) {
        if (rentPaymentRepository.existsByStudentProfileAndMonthAndYear(profile, month, year)) {
            throw new RuntimeException("Payment for " + month + " " + year + " is already recorded!");
        }

        RentPayment payment = new RentPayment();
        payment.setStudentProfile(profile);

        if (profile.getRoom() != null) {
            payment.setAmount(profile.getRoom().getMonthlyRent());
        } else {
            payment.setAmount(0);
        }

        payment.setMonth(month);
        payment.setYear(year);
        payment.setPaymentDate(LocalDate.now());
        payment.setPaymentMode(PaymentMode.valueOf(paymentMode));
        payment.setStatus(PaymentStatus.PAID);

        rentPaymentRepository.save(payment);
    }

    /**
     * Admin approves a payment request.
     * Sets status to PAID and records verification date.
     */
    public void approvePayment(Long paymentId) {
        RentPayment payment = rentPaymentRepository.findById(paymentId).orElse(null);
        if (payment != null) {
            payment.setStatus(PaymentStatus.PAID);
            payment.setVerifiedAt(LocalDate.now());
            rentPaymentRepository.save(payment);
        }
    }

    /**
     * Admin rejects a payment request.
     * Sets status to REJECTED with remarks.
     */
    public void rejectPayment(Long paymentId, String remarks) {
        RentPayment payment = rentPaymentRepository.findById(paymentId).orElse(null);
        if (payment != null) {
            payment.setStatus(PaymentStatus.REJECTED);
            payment.setAdminRemarks(remarks);
            payment.setVerifiedAt(LocalDate.now());
            rentPaymentRepository.save(payment);
        }
    }

    /** Get all payments pending admin verification */
    public List<RentPayment> getPendingVerifications() {
        return rentPaymentRepository.findByStatusOrderByPaymentDateDesc(PaymentStatus.VERIFICATION_PENDING);
    }

    /** Count pending verifications (for admin dashboard badge) */
    public long countPendingVerifications() {
        return rentPaymentRepository.countByStatus(PaymentStatus.VERIFICATION_PENDING);
    }

    /** Get all payments for a specific student (for student dashboard) */
    public List<RentPayment> findByProfile(StudentProfile profile) {
        return rentPaymentRepository.findByStudentProfileOrderByYearDescMonthDesc(profile);
    }

    /** Find a single payment by ID */
    public RentPayment findById(Long id) {
        return rentPaymentRepository.findById(id).orElse(null);
    }

    /** Get 20 most recent payments across all students (for admin overview) */
    public List<RentPayment> getRecentPayments() {
        return rentPaymentRepository.findTop20ByOrderByPaymentDateDesc();
    }
}
