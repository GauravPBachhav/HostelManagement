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
        user.setEmail(user.getEmail().toLowerCase());
        if (userRepository.findByEmail(user.getEmail()) != null) {
            throw new RuntimeException("Email already registered!");
        }
        user.setRole("USER");
        user.setProfileCompleted(false);
        userRepository.save(user);
    }

    public User checkLogin(String email, String password) {
        if (email == null || password == null) return null;
        User user = userRepository.findByEmail(email.toLowerCase());
        if (user != null && user.getPassword().equals(password)) return user;
        return null;
    }

    public void updateUser(User user) {
        userRepository.save(user);
    }

    // ✅ NEW: needed to refresh session user from DB
    public User findById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    public boolean validateUser(String email, String password) {
        return checkLogin(email, password) != null;
    }
}