package in.gw.main.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import in.gw.main.Entity.User;
import in.gw.main.Services.UserService;

@Controller
public class UserController {

    @Autowired
    private UserService userService;

    // Home page
    @GetMapping({"/","/logout"})
    public String home() {
        return "index";
    }
    
    
    
    
    
    
    
    
    

    // Show login form
    @GetMapping("/login")
    public String showLoginForm(Model model) {
        model.addAttribute("user", new User());  // Add empty user object for Thymeleaf
        return "login";
    }
    
    
    
    @PostMapping("/loginForm")
    public String loginUser(@ModelAttribute User user, Model model) {
        // Validate user using service (implement this in UserService)
        boolean isValid = userService.validateUser(user.getEmail(), user.getPassword());

        if (isValid) {
            return "dashboard";  // Go to dashboard if login successful
        } else {
            model.addAttribute("error", "Invalid email or password");
            return "login";       // Return to login page with error
        }
    }
    
    
    
    
    
    
    
    
    
    
    
    

    // Show registration form
    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("user", new User());  // Add empty user object for Thymeleaf
        return "register";
    }
    
    
    
    
    @PostMapping("/regForm")
    public String registerUser(@ModelAttribute User user, Model model) {
        // Save the user using service
        userService.registerUser(user);
        model.addAttribute("success", "Registration successful. Please login.");
        return "login";  // Redirect to login page after successful registration
    }
    
    
    
    
    

    // Handle login form submission
   

    // Handle registration form submission
   

    // Dashboard page
    @GetMapping("/dashboard")
    public String dashboard() {
        return "dashboard";
    }
}