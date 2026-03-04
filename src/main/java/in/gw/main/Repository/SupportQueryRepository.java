package in.gw.main.Repository;

import in.gw.main.Entity.QueryStatus;
import in.gw.main.Entity.StudentProfile;
import in.gw.main.Entity.SupportQuery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * SUPPORT QUERY REPOSITORY
 * -------------------------
 * Provides database operations for SupportQuery entity.
 */
@Repository
public interface SupportQueryRepository extends JpaRepository<SupportQuery, Long> {

    // Get all queries for a student (newest first)
    List<SupportQuery> findByStudentProfileOrderByCreatedAtDesc(StudentProfile profile);

    // Get queries by status (OPEN or RESOLVED), newest first
    List<SupportQuery> findByStatusOrderByCreatedAtDesc(QueryStatus status);
}
