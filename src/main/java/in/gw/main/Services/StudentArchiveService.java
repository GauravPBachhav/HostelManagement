package in.gw.main.Services;

import in.gw.main.Entity.*;
import in.gw.main.Repository.StudentArchiveRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * STUDENT ARCHIVE SERVICE
 * ========================
 * Creates permanent records when students leave the hostel.
 *
 * Called from:
 *   - vacateStudent() → archives with COMPLETED status
 *   - rejectStudent() → archives with REJECTED status
 *
 * Data is NEVER deleted — acts as hostel history/audit log.
 */
@Service
public class StudentArchiveService {

    @Autowired
    private StudentArchiveRepository archiveRepository;

    /**
     * Archive a student who is being vacated (completed their stay).
     * Copies all data from User + StudentProfile into a permanent record.
     */
    public void archiveFromProfile(StudentProfile profile, ArchiveStatus status, String remarks) {
        StudentArchive archive = new StudentArchive();

        // Copy user info
        if (profile.getUser() != null) {
            archive.setStudentName(profile.getUser().getName());
            archive.setEmail(profile.getUser().getEmail());
        }

        // Copy profile info
        archive.setPhoneNumber(profile.getPhoneNumber());
        archive.setGender(profile.getGender());
        archive.setAddress(profile.getAddress());
        archive.setAadharNumber(profile.getAadharNumber());
        archive.setCollegeName(profile.getCollegeName());
        archive.setCourse(profile.getCourse());
        archive.setYearOfStudy(profile.getYearOfStudy());
        archive.setAcademicYear(profile.getAcademicYear());
        archive.setParentName(profile.getParentName());
        archive.setParentContact(profile.getParentContact());

        // Copy room info
        if (profile.getRoom() != null) {
            archive.setRoomNumber(profile.getRoom().getRoomNumber());
            archive.setRoomType(profile.getRoom().getRoomType().name());
        }

        // Set dates
        archive.setCheckOutDate(LocalDate.now());
        archive.setArchiveStatus(status);
        archive.setArchivedAt(LocalDateTime.now());
        archive.setRemarks(remarks);

        archiveRepository.save(archive);
    }

    // =====================
    // QUERY METHODS
    // =====================

    /** Get all archived records */
    public List<StudentArchive> findAll() {
        return archiveRepository.findAll();
    }

    /** Get by status: COMPLETED, EXPELLED, REJECTED */
    public List<StudentArchive> findByStatus(ArchiveStatus status) {
        return archiveRepository.findByArchiveStatusOrderByArchivedAtDesc(status);
    }

    /** Count by status */
    public long countByStatus(ArchiveStatus status) {
        return archiveRepository.countByArchiveStatus(status);
    }

    /** Search by name or email */
    public List<StudentArchive> search(String query) {
        return archiveRepository.searchByNameOrEmail(query);
    }

    /** Find a single archive record by ID */
    public StudentArchive findById(Long id) {
        return archiveRepository.findById(id).orElse(null);
    }

    /** Delete an archive record (frees email for re-registration) */
    public void deleteById(Long id) {
        archiveRepository.deleteById(id);
    }
}
