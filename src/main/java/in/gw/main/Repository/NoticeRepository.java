package in.gw.main.Repository;

import in.gw.main.Entity.Notice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * NOTICE REPOSITORY
 * ------------------
 * JPA repository for Notice entity.
 * Provides methods to find active notices ordered by date.
 */
public interface NoticeRepository extends JpaRepository<Notice, Long> {

    /** Get all active notices, newest first (shown to students) */
    List<Notice> findByActiveTrueOrderByPostedAtDesc();

    /** Get ALL notices regardless of status (for admin management) */
    List<Notice> findAllByOrderByPostedAtDesc();
}
