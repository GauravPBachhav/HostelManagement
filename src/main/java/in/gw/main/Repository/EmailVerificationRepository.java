package in.gw.main.Repository;

import in.gw.main.Entity.EmailVerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmailVerificationRepository extends JpaRepository<EmailVerificationToken, Long> {

    /** Find token by email */
    EmailVerificationToken findByEmail(String email);

    /** Delete all tokens for an email (cleanup) */
    void deleteByEmail(String email);
}
