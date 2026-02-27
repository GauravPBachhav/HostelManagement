package in.gw.main.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import in.gw.main.Entity.ProfileStatus;
import in.gw.main.Entity.StudentProfile;
import in.gw.main.Entity.User;

@Repository
public interface StudentProfileRepository 
        extends JpaRepository<StudentProfile, Long> {

    StudentProfile findByUser(User user);

    List<StudentProfile> findByStatus(ProfileStatus status);

    @Query("SELECT sp FROM StudentProfile sp JOIN FETCH sp.user WHERE sp.status = :status")
    List<StudentProfile> findByStatusWithUser(ProfileStatus status);
}