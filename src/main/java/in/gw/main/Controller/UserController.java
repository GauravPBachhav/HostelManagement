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

            // 🔥 ADMIN LOGIN CHECK
            if (validUser.getRole().equals("ADMIN")) {
                return "redirect:/admin/dashboard";
            }

            // 🔥 USER LOGIN FLOW
            if (!validUser.isProfileCompleted()) {
                return "redirect:/admission";
            }

            return "redirect:/dashboard";
        }

        model.addAttribute("error", "Invalid email or password");
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
                               Model model) {

        userService.registerUser(user);

        model.addAttribute("success",
                "Registration successful. Please login.");

        return "login";
    }


    // =========================
    // STUDENT PROFILE (ADMISSION)
    // =========================
    @GetMapping("/admission")
    public String admissionForm(HttpSession session, Model model) {

        User user = (User) session.getAttribute("loggedUser");

        if (user == null) {
            return "redirect:/login";
        }

        StudentProfile existingProfile =
                studentProfileService.findByUser(user);

        if (existingProfile != null) {
            model.addAttribute("profile", existingProfile);
        } else {
            model.addAttribute("profile", new StudentProfile());
        }

        return "admission";
    }
    @PostMapping("/admission")
    public String saveAdmission(@ModelAttribute StudentProfile profile,
                                HttpSession session) {

        User user = (User) session.getAttribute("loggedUser");

        if (user == null) {
            return "redirect:/login";
        }

        profile.setUser(user);
        profile.setStatus(ProfileStatus.PENDING);

        studentProfileService.saveProfile(profile);

        user.setProfileCompleted(true);
        userService.updateUser(user);

        return "redirect:/dashboard";
    }


    // =========================
    // DASHBOARD
    // =========================
    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {

        User user = (User) session.getAttribute("loggedUser");

        if (user == null) {
            return "redirect:/login";
        }

        StudentProfile profile =
                studentProfileService.findByUser(user);

        model.addAttribute("profile", profile);

        return "dashboard";
    }


    // =========================
    // VIEW PROFILE
    // =========================
    @GetMapping("/profile")
    public String viewProfile(HttpSession session, Model model) {

        User user = (User) session.getAttribute("loggedUser");

        if (user == null) {
            return "redirect:/login";
        }

        StudentProfile profile =
                studentProfileService.findByUser(user);

        model.addAttribute("profile", profile);

        return "profile";
    }
}







//package in.gw.main.Controller;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.ModelAttribute;
//import org.springframework.web.bind.annotation.PostMapping;
//
//import in.gw.main.Entity.StudentProfile;
//import in.gw.main.Entity.User;
//import in.gw.main.Services.StudentProfileService;
//import in.gw.main.Services.UserService;
//import jakarta.servlet.http.HttpSession;
//
//@Controller
//public class UserController {
//
//    @Autowired
//    private UserService userService;
//    @Autowired
//    private StudentProfileService studentProfileService;
//    
//    // Home page
//    @GetMapping({"/","/logout"})
//    public String home() {
//        return "index";
//    }
//    
//    
//    
//    
//    
//    
//    
//    
//
//    // Show login form
//    @GetMapping("/login")
//    public String showLoginForm(Model model) {
//        model.addAttribute("user", new User());  // Add empty user object for Thymeleaf
//        return "login";
//    }
//    
//    
//    
//    @PostMapping("/loginForm")
//    public String loginUser(@ModelAttribute User user, Model model) {
//        // Validate user using service (implement this in UserService)
//        boolean isValid = userService.validateUser(user.getEmail(), user.getPassword());
//
//        if (isValid) {
//            return "dashboard";  // Go to dashboard if login successful
//        } else {
//            model.addAttribute("error", "Invalid email or password");
//            return "login";       // Return to login page with error
//        }
//    }
//    
//    
//    
//    
//    
//    
//    
//    
//    
//    
//    
//    
//
//    // Show registration form
//    @GetMapping("/register")
//    public String showRegisterForm(Model model) {
//        model.addAttribute("user", new User());  // Add empty user object for Thymeleaf
//        return "register";
//    }
//    
//    @PostMapping("/regForm")
//    public String registerUser(@ModelAttribute User user, Model model) {
//        // Save the user using service
//        userService.registerUser(user);
//        model.addAttribute("success", "Registration successful. Please login.");
//        return "index";  // Redirect to login page after successful registration
//    }
//    
//    
//    
//    
//    
//    
//    
//    
//    
//
//    @GetMapping("/admission")
//    public String admissionForm(Model model) {
//        model.addAttribute("profile", new StudentProfile());
//        return "admission";
//    }
//
////    @PostMapping("/admission")
////    public String saveAdmission(@ModelAttribute StudentProfile profile) {
////        studentProfileService.saveProfile(profile);
////        return "redirect:/dashboard";
////    }
////    
//
//    
//    
//    @PostMapping("/admission")
//    public String saveAdmission(@ModelAttribute StudentProfile profile,
//                                HttpSession session) {
//
//        User user = (User) session.getAttribute("loggedUser");
//
//        profile.setUser(user);        // 🔥 Link with user
//        profile.setStatus("PENDING"); // default
//
//        studentProfileService.saveProfile(profile);
//
//        user.setProfileCompleted(true);
//        userService.updateUser(user); // make sure update method exists
//
//        return "redirect:/dashboard";
//    }
//    
//    
//    
//    
//    
//    @GetMapping("/profile")
//    public String userProfile() {
//        return "profile";
//    }
//    
//    
//    
//    
//    
//    
//    // Handle login form submission
//   
//
//    // Handle registration form submission
//   
//
//    // Dashboard page
//    @GetMapping("/dashboard")
//    public String dashboard() {
//        return "dashboard";
//    }
//}