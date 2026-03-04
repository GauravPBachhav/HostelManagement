package in.gw.main.Repository;

import in.gw.main.Entity.RentPayment;
import in.gw.main.Entity.StudentProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * RENT PAYMENT REPOSITORY
 * ------------------------
 * Provides database operations for RentPayment entity.
 *
 * Method naming convention (Spring Data JPA auto-generates SQL):
 *   findByStudentProfile...  → WHERE profile_id = ?
 *   ...OrderByYearDesc       → ORDER BY year DESC
 *   findTop20By...           → LIMIT 20
 */
@Repository
public interface RentPaymentRepository extends JpaRepository<RentPayment, Long> {

    // Get all payments for a specific student (newest first)
    List<RentPayment> findByStudentProfileOrderByYearDescMonthDesc(StudentProfile profile);

    // Get 20 most recent payments (for admin overview)
    List<RentPayment> findTop20ByOrderByPaymentDateDesc();
}
