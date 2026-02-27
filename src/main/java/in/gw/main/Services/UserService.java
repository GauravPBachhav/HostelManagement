package in.gw.main.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import in.gw.main.Entity.User;
import in.gw.main.Repository.UserRepository;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;


    // =========================
    // REGISTER
    // =========================
    public void registerUser(User user) {

        // Normalize email
        user.setEmail(user.getEmail().toLowerCase());

        // Check duplicate email
        if (userRepository.findByEmail(user.getEmail()) != null) {
            throw new RuntimeException("Email already registered!");
        }

        // Default values
        user.setRole("USER");
        user.setProfileCompleted(false);

        userRepository.save(user);
    }


    // =========================
    // LOGIN
    // =========================
    public User checkLogin(String email, String password) {

        if (email == null || password == null) {
            return null;
        }

        User user = userRepository.findByEmail(email.toLowerCase());

        if (user != null && user.getPassword().equals(password)) {
            return user;
        }

        return null;
    }


    // =========================
    // UPDATE USER
    // =========================
    public void updateUser(User user) {
        userRepository.save(user);
    }


    public boolean validateUser(String email, String password) {
        return checkLogin(email, password) != null;
    }
}