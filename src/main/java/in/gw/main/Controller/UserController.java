package in.gw.main.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import in.gw.main.Config.CustomUserDetails;
import in.gw.main.Entity.*;
import in.gw.main.Services.*;

/**
 * USER CONTROLLER
 * ================
 * Handles all pages for:
 *   - Public visitors (home, login, register)
 *   - Logged-in students (dashboard, admission, rent, queries)
 *
 * FEATURES:
 *   - Profile photo upload during admission
 *   - Query image upload for support tickets
 *   - All form submissions use multipart/form-data for file uploads
 */
@Controller
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private StudentProfileService studentProfileService;

    @Autowired
    private RoomService roomService;

    @Autowired
    private RentPaymentService rentPaymentService;

    @Autowired
    private SupportQueryService supportQueryService;

    @Autowired
    private FileStorageService fileStorageService;

    // ===================================================================
    //  PUBLIC PAGES (no login needed - configured in SecurityConfig)
    // ===================================================================

    /**
     * HOME PAGE - shows hostel info + live vacancy count
     */
    @GetMapping("/")
    public String homePage(Model model) {
        model.addAttribute("totalBeds", roomService.getTotalCapacity());
        model.addAttribute("availableBeds", roomService.getAvailableBeds());
        model.addAttribute("rooms", roomService.getAllActiveRooms());
        return "index";
    }

    /**
     * LOGIN PAGE
     */
    @GetMapping("/login")
    public String loginPage(@RequestParam(value = "error", required = false) String error,
                            @RequestParam(value = "logout", required = false) String logout,
                            Model model) {
        if (error != null) {
            model.addAttribute("error", "Invalid email or password!");
        }
        if (logout != null) {
            model.addAttribute("success", "Logged out successfully!");
        }
        return "login";
    }

    /** ADMIN LOGIN PAGE */
    @GetMapping("/admin-login")
    public String adminLoginPage(Model model) {
        model.addAttribute("isAdminLogin", true);
        return "login";
    }

    /** REGISTER PAGE */
    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    /**
     * REGISTER FORM SUBMIT
     */
    @PostMapping("/regForm")
    public String registerUser(@ModelAttribute User user, Model model) {
        try {
            userService.registerUser(user);
            model.addAttribute("success", "Registration successful! Please login.");
            return "login";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("user", new User());
            return "register";
        }
    }

    // ===================================================================
    //  STUDENT PAGES (login required - enforced by Spring Security)
    // ===================================================================

    /**
     * ADMISSION FORM PAGE
     */
    @GetMapping("/admission")
    public String admissionPage(@AuthenticationPrincipal CustomUserDetails userDetails,
                                Model model) {
        User user = userService.findById(userDetails.getUserId());

        // If student already submitted admission form, go to dashboard
        StudentProfile existing = studentProfileService.findByUser(user);
        if (existing != null) {
            return "redirect:/dashboard";
        }

        model.addAttribute("profile", new StudentProfile());
        return "admission";
    }

    /**
     * ADMISSION FORM SUBMIT - with profile photo upload
     * The form uses enctype="multipart/form-data" to support file upload.
     */
    @PostMapping("/admission")
    public String submitAdmission(@ModelAttribute StudentProfile profile,
                                  @RequestParam(value = "photo", required = false) MultipartFile photo,
                                  @AuthenticationPrincipal CustomUserDetails userDetails,
                                  RedirectAttributes redirectAttributes) {
        User user = userService.findById(userDetails.getUserId());

        profile.setUser(user);
        profile.setStatus(ProfileStatus.PENDING);

        // Save profile photo if uploaded
        if (photo != null && !photo.isEmpty()) {
            String photoPath = fileStorageService.saveFile(photo, "profiles");
            profile.setProfilePhoto(photoPath);
        }

        try {
            studentProfileService.saveProfile(profile);
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/admission";
        }

        // Mark that user has completed their profile
        user.setProfileCompleted(true);
        userService.updateUser(user);

        return "redirect:/dashboard";
    }

    /**
     * STUDENT DASHBOARD
     */
    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal CustomUserDetails userDetails,
                            Model model) {
        User user = userService.findById(userDetails.getUserId());
        StudentProfile profile = studentProfileService.findByUser(user);
        model.addAttribute("profile", profile);

        // If student is approved, load their rent payments and queries
        if (profile != null && profile.getStatus() == ProfileStatus.APPROVED) {
            model.addAttribute("rentPayments", rentPaymentService.findByProfile(profile));
            model.addAttribute("queries", supportQueryService.findByProfile(profile));
        }

        return "dashboard";
    }

    /** PAY RENT */
    @PostMapping("/dashboard/rent/pay")
    public String payRent(@RequestParam String month,
                          @RequestParam int year,
                          @RequestParam String paymentMode,
                          @AuthenticationPrincipal CustomUserDetails userDetails) {
        User user = userService.findById(userDetails.getUserId());
        StudentProfile profile = studentProfileService.findByUser(user);
        rentPaymentService.recordPayment(profile, month, year, paymentMode);
        return "redirect:/dashboard#tab-rent";
    }

    /**
     * SUBMIT QUERY - with optional image upload
     * Student can attach a photo (e.g., water leakage, broken furniture)
     */
    @PostMapping("/dashboard/query/submit")
    public String submitQuery(@RequestParam String subject,
                              @RequestParam String message,
                              @RequestParam(value = "queryImage", required = false) MultipartFile queryImage,
                              @AuthenticationPrincipal CustomUserDetails userDetails) {
        User user = userService.findById(userDetails.getUserId());
        StudentProfile profile = studentProfileService.findByUser(user);

        // Save query image if uploaded
        String imageUrl = null;
        if (queryImage != null && !queryImage.isEmpty()) {
            imageUrl = fileStorageService.saveFile(queryImage, "queries");
        }

        supportQueryService.submitQuery(profile, subject, message, imageUrl);
        return "redirect:/dashboard#tab-query";
    }
}
