package in.gw.main.Config;

import in.gw.main.Entity.User;
import in.gw.main.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * CUSTOM USER DETAILS SERVICE
 * ----------------------------
 * Spring Security calls this service automatically when a user tries to login.
 *
 * FLOW:
 *   1. User enters email + password on login page
 *   2. Spring Security calls loadUserByUsername(email)
 *   3. We find the user in database
 *   4. We wrap user in CustomUserDetails and return it
 *   5. Spring Security then compares the entered password with stored hash
 *   6. If match → login success. If not → login failure.
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // Find user by email in our database
        User user = userRepository.findByEmail(email.toLowerCase());

        // If not found, throw exception (Spring Security shows error on login page)
        if (user == null) {
            throw new UsernameNotFoundException("No user found with email: " + email);
        }

        // Wrap our User in CustomUserDetails and give it to Spring Security
        return new CustomUserDetails(user);
    }
}
