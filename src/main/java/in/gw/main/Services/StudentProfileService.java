package in.gw.main.Services;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import in.gw.main.Entity.ProfileStatus;
import in.gw.main.Entity.StudentProfile;
import in.gw.main.Entity.User;
import in.gw.main.Repository.StudentProfileRepository;

@Service
public class StudentProfileService {

    @Autowired
    private StudentProfileRepository studentProfileRepository;

    @Transactional
    public List<StudentProfile> getPendingProfiles() {
        return studentProfileRepository.findByStatus(ProfileStatus.PENDING);
    }

    @Transactional
    public List<StudentProfile> getApprovedProfiles() {
        return studentProfileRepository.findByStatus(ProfileStatus.APPROVED);
    }

    @Transactional
    public List<StudentProfile> getRejectedProfiles() {
        return studentProfileRepository.findByStatus(ProfileStatus.REJECTED);
    }

    // Month-wise approved profiles
    @Transactional
    public List<StudentProfile> getApprovedByMonth(int month, int year) {
        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end   = start.withDayOfMonth(start.lengthOfMonth());
        return studentProfileRepository.findByStatusAndSubmittedAtBetween(
                ProfileStatus.APPROVED, start, end);
    }

    // Profiles with queries
    @Transactional
    public List<StudentProfile> getProfilesWithQueries() {
        return studentProfileRepository.findByQueryTextNotNull();
    }

    public void updateStatus(Long id, ProfileStatus status) {
        StudentProfile profile = studentProfileRepository
                .findById(id)
                .orElseThrow(() -> new RuntimeException("Profile not found"));
        profile.setStatus(status);
        studentProfileRepository.save(profile);
    }

    // Mark rent as paid
    public void markRentPaid(Long id) {
        StudentProfile profile = studentProfileRepository
                .findById(id)
                .orElseThrow(() -> new RuntimeException("Profile not found"));
        profile.setRentPaid(true);
        studentProfileRepository.save(profile);
    }

    public void saveProfile(StudentProfile profile) {
        StudentProfile existing = studentProfileRepository.findByUser(profile.getUser());
        if (existing != null) {
            throw new RuntimeException("Profile already submitted!");
        }
        profile.setStatus(ProfileStatus.PENDING);
        profile.setSubmittedAt(LocalDate.now()); // ✅ Set date on save
        studentProfileRepository.save(profile);
    }

    public StudentProfile findByUser(User user) {
        return studentProfileRepository.findByUser(user);
    }

    public StudentProfile findById(Long id) {
        return studentProfileRepository.findById(id).orElse(null);
    }
}