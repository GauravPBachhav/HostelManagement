package in.gw.main.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import in.gw.main.Entity.StudentProfile;
import in.gw.main.Entity.User;
import in.gw.main.Services.StudentProfileService;
import in.gw.main.Services.UserService;
import jakarta.servlet.http.HttpSession;
import in.gw.main.Entity.ProfileStatus;

@Controller
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private StudentProfileService studentProfileService;

    // HOME
    @GetMapping({"/", "/logout"})
    public String home(HttpSession session) {
        session.invalidate();
        return "index";
    }

    // LOGIN GET
    @GetMapping("/login")
    public String showLoginForm(Model model) {
        model.addAttribute("user", new User());
        return "login";
    }

    // LOGIN POST
    @PostMapping("/loginForm")
    public String loginUser(@ModelAttribute User user, Model model, HttpSession session) {
        User validUser = userService.checkLogin(user.getEmail(), user.getPassword());

        if (validUser != null) {
            session.setAttribute("loggedUser", validUser);

            if ("ADMIN".equals(validUser.getRole())) {
                return "redirect:/admin/dashboard";
            }

            // ✅ FIX: Check if profile already submitted → go to dashboard directly
            StudentProfile existingProfile = studentProfileService.findByUser(validUser);
            if (existingProfile != null) {
                return "redirect:/dashboard";
            }

            // No profile yet → go fill admission form
            return "redirect:/admission";
        }

        model.addAttribute("error", "Invalid email or password!");
        return "login";
    }

    // REGISTER GET
    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    // REGISTER POST
    @PostMapping("/regForm")
    public String registerUser(@ModelAttribute User user, Model model) {
        try {
            userService.registerUser(user);
            model.addAttribute("success", "Registration successful! Please login.");
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("user", new User());
            return "register";
        }
        model.addAttribute("user", new User());
        return "login";
    }

    // ADMISSION GET
    // ✅ FIX: If profile already exists → redirect to dashboard (don't show form again)
    @GetMapping("/admission")
    public String admissionForm(HttpSession session, Model model) {
        User user = (User) session.getAttribute("loggedUser");
        if (user == null) return "redirect:/login";

        StudentProfile existingProfile = studentProfileService.findByUser(user);
        if (existingProfile != null) {
            // Profile already submitted → go to dashboard, don't show form
            return "redirect:/dashboard";
        }

        model.addAttribute("profile", new StudentProfile());
        return "admission";
    }

    // ADMISSION POST
    @PostMapping("/admission")
    public String saveAdmission(@ModelAttribute StudentProfile profile, HttpSession session) {
        User user = (User) session.getAttribute("loggedUser");
        if (user == null) return "redirect:/login";

        profile.setUser(user);
        profile.setStatus(ProfileStatus.PENDING);
        studentProfileService.saveProfile(profile);

        // Refresh user in session
        user.setProfileCompleted(true);
        userService.updateUser(user);
        session.setAttribute("loggedUser", user);

        return "redirect:/dashboard";
    }

    // DASHBOARD GET
    // ✅ FIX: Always pass profile to model
    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        User user = (User) session.getAttribute("loggedUser");
        if (user == null) return "redirect:/login";

        // Fetch fresh user from DB so profileCompleted is accurate
        User freshUser = userService.findById(user.getId());
        if (freshUser != null) {
            session.setAttribute("loggedUser", freshUser);
            user = freshUser;
        }

        StudentProfile profile = studentProfileService.findByUser(user);
        model.addAttribute("profile", profile);
        return "dashboard";
    }

    // ADMIN LOGIN page (separate entry from index)
    @GetMapping("/admin-login")
    public String adminLoginPage(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("isAdminLogin", true);
        return "login";
    }
}