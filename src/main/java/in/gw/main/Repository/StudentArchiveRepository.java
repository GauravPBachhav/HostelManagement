package in.gw.main.Repository;

import in.gw.main.Entity.ArchiveStatus;
import in.gw.main.Entity.StudentArchive;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudentArchiveRepository extends JpaRepository<StudentArchive, Long> {

    /** Find all archives by status (COMPLETED, EXPELLED, REJECTED) */
    List<StudentArchive> findByArchiveStatusOrderByArchivedAtDesc(ArchiveStatus status);

    /** Get total count by status */
    long countByArchiveStatus(ArchiveStatus status);

    /** Find by email (to check if student was previously archived) */
    List<StudentArchive> findByEmailOrderByArchivedAtDesc(String email);

    /** Search archives by name or email */
    @Query("SELECT a FROM StudentArchive a WHERE LOWER(a.studentName) LIKE LOWER(CONCAT('%',:query,'%')) OR LOWER(a.email) LIKE LOWER(CONCAT('%',:query,'%'))")
    List<StudentArchive> searchByNameOrEmail(String query);
}
