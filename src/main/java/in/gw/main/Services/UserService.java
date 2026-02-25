package in.gw.main.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import in.gw.main.Entity.User;
import in.gw.main.Repository.UserRepository;

@Service
public class UserService {

	@Autowired
	private UserRepository userRepository;

	public void registerUser(User user) {

		user.setPassword(user.getPassword());
		user.setRole("USER"); // simple string
		userRepository.save(user);

	}

	public boolean validateUser(String email, String password) {
		User user = userRepository.findByEmail(email);
		if (user != null && user.getPassword().equals(password)) {
			return true;
		}
		return false;
	}
}
