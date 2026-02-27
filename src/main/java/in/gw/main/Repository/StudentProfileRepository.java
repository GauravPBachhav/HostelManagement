package in.gw.main.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import in.gw.main.Entity.ProfileStatus;
import in.gw.main.Entity.StudentProfile;
import in.gw.main.Entity.User;

@Repository
public interface StudentProfileRepository
        extends JpaRepository<StudentProfile, Long> {

    // Find by user
    StudentProfile findByUser(User user);

    // Find by status (PENDING / APPROVED / REJECTED)
    List<StudentProfile> findByStatus(ProfileStatus status);

    // ✅ REMOVED: findByQueryTextNotNull() — field doesn't exist in entity yet
}