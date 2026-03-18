package in.gw.main.Config;

import in.gw.main.Entity.User;
import in.gw.main.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.ui.Model;

/**
 * GLOBAL MODEL ATTRIBUTES
 * ========================
 * This runs before EVERY controller method.
 * Injects `loggedUser` into the model so ALL pages (header/footer)
 * can check if user is logged in, their role, and name.
 *
 * Without this, only controllers that manually add `loggedUser`
 * would show the correct header state.
 */
@ControllerAdvice
public class GlobalModelAttributes {

    @Autowired
    private UserRepository userRepository;

    @ModelAttribute
    public void addLoggedUser(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth != null && auth.isAuthenticated()
                && auth.getPrincipal() instanceof CustomUserDetails) {
            CustomUserDetails details = (CustomUserDetails) auth.getPrincipal();
            User user = userRepository.findById(details.getUserId()).orElse(null);
            model.addAttribute("loggedUser", user);
        }
        // If not logged in, loggedUser stays null → header shows Login/Register
    }
}
