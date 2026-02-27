package in.gw.main.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import in.gw.main.Entity.ProfileStatus;
import in.gw.main.Entity.StudentProfile;
import in.gw.main.Entity.User;
import in.gw.main.Services.StudentProfileService;
import in.gw.main.Services.UserService;
import jakarta.servlet.http.HttpSession;

@Controller
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private StudentProfileService studentProfileService;


    // =========================
    // HOME / LOGOUT
    // =========================
    @GetMapping({"/", "/logout"})
    public String home(HttpSession session) {
        session.invalidate();
        return "index";
    }


    // =========================
    // LOGIN
    // =========================
    @GetMapping("/login")
    public String showLoginForm(Model model) {
        model.addAttribute("user", new User());
        return "login";
    }

    @PostMapping("/loginForm")
    public String loginUser(@ModelAttribute User user,
                            Model model,
                            HttpSession session) {

        User validUser = userService.checkLogin(
                user.getEmail(),
                user.getPassword()
        );

        if (validUser != null) {
            session.setAttribute("loggedUser", validUser);

            // ADMIN LOGIN
            if ("ADMIN".equals(validUser.getRole())) {
                return "redirect:/admin/dashboard";
            }

            // USER: profile not filled yet → go to admission
            if (!validUser.isProfileCompleted()) {
                return "redirect:/admission";
            }

            // USER: profile already filled → go to dashboard
            return "redirect:/dashboard";
        }

        model.addAttribute("error", "Invalid email or password!");
        return "login";
    }


    // =========================
    // REGISTER
    // =========================
    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/regForm")
    public String registerUser(@ModelAttribute User user,
                               RedirectAttributes redirectAttributes) {
        try {
            userService.registerUser(user);
            // ✅ FIX: Use RedirectAttributes so message survives redirect
            redirectAttributes.addFlashAttribute("success",
                    "Registration successful! Please login.");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/register";
        }
        // ✅ FIX: Redirect to login (not forward) so form is clean
        return "redirect:/login";
    }


    // =========================
    // STUDENT PROFILE (ADMISSION)
    // =========================
    @GetMapping("/admission")
    public String admissionForm(HttpSession session, Model model) {

        User user = (User) session.getAttribute("loggedUser");
        if (user == null) return "redirect:/login";

        StudentProfile existingProfile = studentProfileService.findByUser(user);

        if (existingProfile != null) {
            // Profile already submitted → show it (read-only view)
            model.addAttribute("profile", existingProfile);
            model.addAttribute("alreadySubmitted", true);
        } else {
            model.addAttribute("profile", new StudentProfile());
            model.addAttribute("alreadySubmitted", false);
        }

        return "admission";
    }

    @PostMapping("/admission")
    public String saveAdmission(@ModelAttribute StudentProfile profile,
                                HttpSession session,
                                RedirectAttributes redirectAttributes) {

        User user = (User) session.getAttribute("loggedUser");
        if (user == null) return "redirect:/login";

        try {
            profile.setUser(user);
            profile.setStatus(ProfileStatus.PENDING);
            studentProfileService.saveProfile(profile);

            // ✅ FIX: Mark profile completed and REFRESH session user
            user.setProfileCompleted(true);
            userService.updateUser(user);
            session.setAttribute("loggedUser", user); // refresh session

            redirectAttributes.addFlashAttribute("success",
                    "Admission form submitted! Waiting for admin approval.");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/dashboard";
    }


    // Add this route so "Login as Admin" button on index works:
@GetMapping("/admin-login")
public String showAdminLoginForm(Model model) {
    model.addAttribute("user", new User());
    model.addAttribute("adminLogin", true); // hint for the page title
    return "login"; // reuses same login.html
}   





    // =========================
    // DASHBOARD
    // =========================
    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {

        User user = (User) session.getAttribute("loggedUser");
        if (user == null) return "redirect:/login";

        // ✅ FIX: Fetch latest profile and pass to model
        StudentProfile profile = studentProfileService.findByUser(user);
        model.addAttribute("profile", profile);

        return "dashboard";
    }
}