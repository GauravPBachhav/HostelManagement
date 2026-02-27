package in.gw.main.Repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import in.gw.main.Entity.ProfileStatus;
import in.gw.main.Entity.StudentProfile;
import in.gw.main.Entity.User;

@Repository
public interface StudentProfileRepository extends JpaRepository<StudentProfile, Long> {

    StudentProfile findByUser(User user);

    List<StudentProfile> findByStatus(ProfileStatus status);

    // For month-wise filtering
    List<StudentProfile> findByStatusAndSubmittedAtBetween(
            ProfileStatus status, LocalDate start, LocalDate end);

    // For query/support section
    List<StudentProfile> findByQueryTextNotNull();
}