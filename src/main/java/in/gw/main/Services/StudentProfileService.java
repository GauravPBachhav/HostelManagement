package in.gw.main.Services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import in.gw.main.Entity.ProfileStatus;
import in.gw.main.Entity.StudentProfile;
import in.gw.main.Entity.User;
import in.gw.main.Repository.StudentProfileRepository;
@Service
public class StudentProfileService {

    @Autowired
    private StudentProfileRepository studentProfileRepository;

    public List<StudentProfile> getPendingProfiles() {
        return studentProfileRepository.findByStatus(ProfileStatus.PENDING);
    }

    public void updateStatus(Long id, ProfileStatus status) {

        if (status != ProfileStatus.APPROVED &&
            status != ProfileStatus.REJECTED) {
            throw new IllegalArgumentException("Invalid status");
        }

        StudentProfile profile = studentProfileRepository
                .findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Profile not found"));

        profile.setStatus(status);
        studentProfileRepository.save(profile);
    }

    public void saveProfile(StudentProfile profile) {

        StudentProfile existing =
                studentProfileRepository.findByUser(profile.getUser());

        if (existing != null) {
            throw new RuntimeException("Profile already submitted!");
        }

        profile.setStatus(ProfileStatus.PENDING);

        studentProfileRepository.save(profile);
    }

    public StudentProfile findByUser(User user) {
        return studentProfileRepository.findByUser(user);
    }

    public StudentProfile findById(Long id) {
        return studentProfileRepository.findById(id).orElse(null);
    }
}