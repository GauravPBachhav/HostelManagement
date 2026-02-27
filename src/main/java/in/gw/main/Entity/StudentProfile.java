package in.gw.main.Entity;

import jakarta.persistence.*;

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

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProfileStatus status = ProfileStatus.PENDING;

    // ✅ FIX: Tell MySQL this column has a default value of 0 (false)
    @Column(name = "rent_paid", nullable = false,
            columnDefinition = "TINYINT(1) DEFAULT 0")
    private boolean rentPaid = false;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    // =====================
    // GETTERS & SETTERS
    // =====================
    public Long getId()                    { return id; }

    public ProfileStatus getStatus()       { return status; }
    public void setStatus(ProfileStatus s) { this.status = s; }

    public String getPhoneNumber()         { return phoneNumber; }
    public void setPhoneNumber(String v)   { this.phoneNumber = v; }

    public String getAddress()             { return address; }
    public void setAddress(String v)       { this.address = v; }

    public String getCollegeName()         { return collegeName; }
    public void setCollegeName(String v)   { this.collegeName = v; }

    public String getCourse()              { return course; }
    public void setCourse(String v)        { this.course = v; }

    public String getYearOfStudy()         { return yearOfStudy; }
    public void setYearOfStudy(String v)   { this.yearOfStudy = v; }

    public String getParentName()          { return parentName; }
    public void setParentName(String v)    { this.parentName = v; }

    public String getParentContact()       { return parentContact; }
    public void setParentContact(String v) { this.parentContact = v; }

    public boolean isRentPaid()            { return rentPaid; }
    public void setRentPaid(boolean v)     { this.rentPaid = v; }

    public User getUser()                  { return user; }
    public void setUser(User user) {
        this.user = user;
        user.setStudentProfile(this);
    }
}