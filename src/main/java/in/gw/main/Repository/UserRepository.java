package in.gw.main.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

import in.gw.main.Entity.User;

public interface UserRepository extends JpaRepository<User, Long>{
//    Optional<User> findByEmail(String email);
	User findByEmail(String email);

}
