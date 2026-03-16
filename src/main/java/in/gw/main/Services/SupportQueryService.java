package in.gw.main.Services;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import in.gw.main.Entity.QueryStatus;
import in.gw.main.Entity.StudentProfile;
import in.gw.main.Entity.SupportQuery;
import in.gw.main.Repository.SupportQueryRepository;

/**
 * SUPPORT QUERY SERVICE
 * ----------------------
 * Handles student queries/complaints and admin replies.
 *
 * FLOW:
 *   1. Student submits query → status = OPEN
 *   2. Admin sees open queries in dashboard
 *   3. Admin replies → status = RESOLVED
 *   4. Student sees the reply in their dashboard
 */
@Service
public class SupportQueryService {

    @Autowired
    private SupportQueryRepository supportQueryRepository;

    /** Student submits a new query (without photo) */
    public void submitQuery(StudentProfile profile, String subject, String message) {
        submitQuery(profile, subject, message, null);
    }

    /** Student submits a new query (with optional photo) */
    public void submitQuery(StudentProfile profile, String subject, String message, String photoPath) {
        SupportQuery query = new SupportQuery();
        query.setStudentProfile(profile);
        query.setSubject(subject);
        query.setMessage(message);
        query.setStatus(QueryStatus.OPEN);
        query.setCreatedAt(LocalDateTime.now());
        if (photoPath != null && !photoPath.isEmpty()) {
            query.setPhotoPath(photoPath);
        }
        supportQueryRepository.save(query);
    }

    /** Admin replies to a query → marks it as RESOLVED */
    public void replyToQuery(Long queryId, String adminReply) {
        SupportQuery query = supportQueryRepository.findById(queryId)
                .orElseThrow(() -> new RuntimeException("Query not found"));
        query.setAdminReply(adminReply);
        query.setStatus(QueryStatus.RESOLVED);
        query.setResolvedAt(LocalDateTime.now());
        supportQueryRepository.save(query);
    }

    /** Get all queries for a specific student */
    public List<SupportQuery> findByProfile(StudentProfile profile) {
        return supportQueryRepository.findByStudentProfileOrderByCreatedAtDesc(profile);
    }

    /** Get all OPEN queries (for admin to see) */
    public List<SupportQuery> getOpenQueries() {
        return supportQueryRepository.findByStatusOrderByCreatedAtDesc(QueryStatus.OPEN);
    }

    /** Get ALL queries regardless of status */
    public List<SupportQuery> getAllQueries() {
        return supportQueryRepository.findAll();
    }
}
