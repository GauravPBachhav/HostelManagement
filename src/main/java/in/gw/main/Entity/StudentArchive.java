package in.gw.main.Entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * STUDENT ARCHIVE
 * ================
 * Stores a permanent copy of student data when they leave the hostel.
 *
 * This is like a "history table" — data is NEVER deleted.
 * Used for:
 *   - Alumni records (students who completed their stay)
 *   - Rejected application records
 *   - Audit trail for admin
 *
 * Statuses:
 *   COMPLETED  → Stayed full term, vacated normally
 *   EXPELLED   → Removed by admin mid-term
 *   REJECTED   → Application rejected (never stayed)
 */
@Entity
@Table(name = "student_archives")
public class StudentArchive {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // --- Student Details (copied from User + StudentProfile) ---
    private String studentName;
    private String email;
    private String phoneNumber;
    private String gender;
    private String address;
    private String aadharNumber;

    // --- Academic ---
    private String collegeName;
    private String course;
    private String yearOfStudy;
    private String academicYear;

    // --- Parent ---
    private String parentName;
    private String parentContact;

    // --- Room/Stay Details ---
    private String roomNumber;       // Room they stayed in (null for rejected)
    private String roomType;         // SINGLE/DOUBLE/TRIPLE
    private double rentPaid;         // Total rent paid during stay

    // --- Dates ---
    private LocalDate checkInDate;   // When they moved in
    private LocalDate checkOutDate;  // When they left

    // --- Archive Meta ---
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ArchiveStatus archiveStatus;   // COMPLETED, EXPELLED, REJECTED

    @Column(nullable = false)
    private LocalDateTime archivedAt;      // When this record was created

    private String remarks;                // Admin notes (optional)

    // =====================
    // GETTERS & SETTERS
    // =====================
    public Long getId() { return id; }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getAadharNumber() { return aadharNumber; }
    public void setAadharNumber(String aadharNumber) { this.aadharNumber = aadharNumber; }

    public String getCollegeName() { return collegeName; }
    public void setCollegeName(String collegeName) { this.collegeName = collegeName; }

    public String getCourse() { return course; }
    public void setCourse(String course) { this.course = course; }

    public String getYearOfStudy() { return yearOfStudy; }
    public void setYearOfStudy(String yearOfStudy) { this.yearOfStudy = yearOfStudy; }

    public String getAcademicYear() { return academicYear; }
    public void setAcademicYear(String academicYear) { this.academicYear = academicYear; }

    public String getParentName() { return parentName; }
    public void setParentName(String parentName) { this.parentName = parentName; }

    public String getParentContact() { return parentContact; }
    public void setParentContact(String parentContact) { this.parentContact = parentContact; }

    public String getRoomNumber() { return roomNumber; }
    public void setRoomNumber(String roomNumber) { this.roomNumber = roomNumber; }

    public String getRoomType() { return roomType; }
    public void setRoomType(String roomType) { this.roomType = roomType; }

    public double getRentPaid() { return rentPaid; }
    public void setRentPaid(double rentPaid) { this.rentPaid = rentPaid; }

    public LocalDate getCheckInDate() { return checkInDate; }
    public void setCheckInDate(LocalDate checkInDate) { this.checkInDate = checkInDate; }

    public LocalDate getCheckOutDate() { return checkOutDate; }
    public void setCheckOutDate(LocalDate checkOutDate) { this.checkOutDate = checkOutDate; }

    public ArchiveStatus getArchiveStatus() { return archiveStatus; }
    public void setArchiveStatus(ArchiveStatus archiveStatus) { this.archiveStatus = archiveStatus; }

    public LocalDateTime getArchivedAt() { return archivedAt; }
    public void setArchivedAt(LocalDateTime archivedAt) { this.archivedAt = archivedAt; }

    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }
}
