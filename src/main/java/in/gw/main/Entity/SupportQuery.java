package in.gw.main.Entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * SUPPORT QUERY ENTITY
 * ---------------------
 * Stores queries/complaints submitted by students.
 * Admin can view and reply to these queries.
 *
 * FLOW:
 *   1. Student submits a query (subject + message)
 *   2. Status is set to OPEN
 *   3. Admin sees the query in their dashboard
 *   4. Admin types a reply
 *   5. Status changes to RESOLVED
 *
 * Named "SupportQuery" (not "Query") to avoid conflict with JPA's Query class.
 */
@Entity
@Table(name = "support_queries")
public class SupportQuery {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Which student submitted this query
    @ManyToOne
    @JoinColumn(name = "profile_id", nullable = false)
    private StudentProfile studentProfile;

    @Column(nullable = false)
    private String subject;             // short title, e.g. "Water issue in room"

    @Column(length = 1000, nullable = false)
    private String message;             // detailed description

    @Column(length = 1000)
    private String adminReply;          // admin's response (null if not yet replied)

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private QueryStatus status = QueryStatus.OPEN;

    private LocalDateTime createdAt;    // when student submitted

    private LocalDateTime resolvedAt;   // when admin replied

    // Optional image attached to the query
    // e.g., "uploads/queries/abc-123.jpg"
    private String imageUrl;

    // =====================
    // GETTERS & SETTERS
    // =====================

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public StudentProfile getStudentProfile() { return studentProfile; }
    public void setStudentProfile(StudentProfile s) { this.studentProfile = s; }

    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getAdminReply() { return adminReply; }
    public void setAdminReply(String adminReply) { this.adminReply = adminReply; }

    public QueryStatus getStatus() { return status; }
    public void setStatus(QueryStatus status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getResolvedAt() { return resolvedAt; }
    public void setResolvedAt(LocalDateTime resolvedAt) { this.resolvedAt = resolvedAt; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
}
