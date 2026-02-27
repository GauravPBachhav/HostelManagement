package in.gw.main.Entity;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "student_profiles")
public class StudentProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String phoneNumber;
    private String address;
    private String collegeName;
    private String course;
    private String yearOfStudy;
    private String parentName;
    private String parentContact;

    // ✅ Track when form was submitted (for month-wise admin view)
    private LocalDate submittedAt;

    // ✅ Track rent payment status
    private boolean rentPaid = false;

    // ✅ Query/complaint text from student
    private String queryText;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProfileStatus status = ProfileStatus.PENDING;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    // =====================
    // GETTERS & SETTERS
    // =====================
    public Long getId() { return id; }

    public ProfileStatus getStatus() { return status; }
    public void setStatus(ProfileStatus status) { this.status = status; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String v) { this.phoneNumber = v; }

    public String getAddress() { return address; }
    public void setAddress(String v) { this.address = v; }

    public String getCollegeName() { return collegeName; }
    public void setCollegeName(String v) { this.collegeName = v; }

    public String getCourse() { return course; }
    public void setCourse(String v) { this.course = v; }

    public String getYearOfStudy() { return yearOfStudy; }
    public void setYearOfStudy(String v) { this.yearOfStudy = v; }

    public String getParentName() { return parentName; }
    public void setParentName(String v) { this.parentName = v; }

    public String getParentContact() { return parentContact; }
    public void setParentContact(String v) { this.parentContact = v; }

    public LocalDate getSubmittedAt() { return submittedAt; }
    public void setSubmittedAt(LocalDate v) { this.submittedAt = v; }

    public boolean isRentPaid() { return rentPaid; }
    public void setRentPaid(boolean v) { this.rentPaid = v; }

    public String getQueryText() { return queryText; }
    public void setQueryText(String v) { this.queryText = v; }

    public User getUser() { return user; }
    public void setUser(User user) {
        this.user = user;
        user.setStudentProfile(this);
    }
}