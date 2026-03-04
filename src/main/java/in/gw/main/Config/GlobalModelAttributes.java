package in.gw.main.Config;

import in.gw.main.Entity.User;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

/**
 * GLOBAL MODEL ATTRIBUTES
 * ------------------------
 * This class makes the logged-in user's info available in ALL HTML templates.
 *
 * WITHOUT this class:
 *   - You'd have to add model.addAttribute("loggedUser", user) in EVERY controller method
 *   - That's repetitive and easy to forget
 *
 * WITH this class:
 *   - The "loggedUser" variable is AUTOMATICALLY available in every page
 *   - In any HTML template: ${loggedUser.name}, ${loggedUser.role}, etc.
 *   - If no one is logged in, loggedUser is null
 *
 * The @ControllerAdvice annotation means this applies to ALL controllers.
 */
@ControllerAdvice
public class GlobalModelAttributes {

    @ModelAttribute("loggedUser")
    public User getLoggedUser(@AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails != null) {
            return userDetails.getUser();
        }
        return null;  // Not logged in
    }
}
