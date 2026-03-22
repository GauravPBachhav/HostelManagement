package in.gw.main.Repository;

import in.gw.main.Entity.PaymentStatus;
import in.gw.main.Entity.RentPayment;
import in.gw.main.Entity.StudentProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * RENT PAYMENT REPOSITORY
 * ------------------------
 * Provides database operations for RentPayment entity.
 */
@Repository
public interface RentPaymentRepository extends JpaRepository<RentPayment, Long> {

    // Get all payments for a specific student (newest first)
    List<RentPayment> findByStudentProfileOrderByYearDescMonthDesc(StudentProfile profile);

    // Get 20 most recent payments (for admin overview)
    List<RentPayment> findTop20ByOrderByPaymentDateDesc();

    // Check if payment already exists for a student for a given month/year
    boolean existsByStudentProfileAndMonthAndYear(StudentProfile profile, String month, int year);

    // ===== NEW: For UPI verification flow =====

    // Get all payments with a specific status (e.g., VERIFICATION_PENDING)
    List<RentPayment> findByStatusOrderByPaymentDateDesc(PaymentStatus status);

    // Count payments by status (for admin dashboard badge)
    long countByStatus(PaymentStatus status);
}
