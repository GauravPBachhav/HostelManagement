package in.gw.main.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import in.gw.main.Config.CustomUserDetails;
import in.gw.main.Entity.ProfileStatus;
import in.gw.main.Entity.StudentProfile;
import in.gw.main.Entity.User;
import in.gw.main.Services.FileStorageService;
import in.gw.main.Services.NoticeService;
import in.gw.main.Services.PdfService;
import in.gw.main.Services.RentPaymentService;
import in.gw.main.Services.RoomService;
import in.gw.main.Services.StudentProfileService;
import in.gw.main.Services.SupportQueryService;
import in.gw.main.Services.UserService;
import in.gw.main.Services.FacilityService;

/**
 * USER CONTROLLER
 * ================
 * Handles all pages for:
 *   - Public visitors (home, login, register)
 *   - Logged-in students (dashboard, admission, rent, queries)
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

    @Autowired
    private NoticeService noticeService;

    @Autowired
    private PdfService pdfService;

    @Autowired
    private FacilityService facilityService;

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
        model.addAttribute("facilities", facilityService.getActiveFacilities());
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
     * Data saved to token table only — NOT users table.
     * User created only after OTP verification.
     */
    @PostMapping("/regForm")
    public String registerUser(@RequestParam String name,
                               @RequestParam String email,
                               @RequestParam String password,
                               Model model) {
        try {
            String savedEmail = userService.registerUser(name, email, password);
            return "redirect:/verify-email?email=" + savedEmail;
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("user", new User());
            return "register";
        }
    }

    /**
     * VERIFY EMAIL — Show OTP entry form
     */
    @GetMapping("/verify-email")
    public String showVerifyEmail(@RequestParam String email, Model model) {
        model.addAttribute("email", email);
        return "verify-email";
    }

    /**
     * VERIFY EMAIL — Check OTP
     */
    @PostMapping("/verify-email")
    public String verifyEmail(@RequestParam String email,
                              @RequestParam String otp,
                              Model model) {
        boolean success = userService.verifyOtp(email, otp);
        if (success) {
            model.addAttribute("success", "Email verified! You can now login.");
            return "login";
        } else {
            model.addAttribute("email", email);
            model.addAttribute("error", "Invalid or expired OTP. Please try again.");
            return "verify-email";
        }
    }

    /**
     * RESEND OTP
     */
    @GetMapping("/resend-otp")
    public String resendOtp(@RequestParam String email, Model model) {
        boolean sent = userService.resendOtp(email);
        model.addAttribute("email", email);
        if (sent) {
            model.addAttribute("success", "New OTP sent to " + email);
        } else {
            model.addAttribute("error", "Could not resend OTP.");
        }
        return "verify-email";
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

        StudentProfile existing = studentProfileService.findByUser(user);

        // RE-APPLY: If profile was REJECTED, delete old one and let student re-apply
        if (existing != null && existing.getStatus() == ProfileStatus.REJECTED) {
            studentProfileService.deleteByUser(user);
            user.setProfileCompleted(false);
            userService.updateUser(user);
            existing = null;
        }

        // If student already has an active profile, go to dashboard
        if (existing != null) {
            return "redirect:/dashboard";
        }

        model.addAttribute("profile", new StudentProfile());
        return "admission";
    }

    /**
     * ADMISSION FORM SUBMIT
     * Photo is uploaded separately via AJAX to /admission/uploadPhoto.
     * The photo path is passed as a hidden form field (profilePhotoPath).
     */
    @PostMapping("/admission")
    public String submitAdmission(@ModelAttribute StudentProfile profile,
                                  @AuthenticationPrincipal CustomUserDetails userDetails,
                                  RedirectAttributes redirectAttributes) {
        User user = userService.findById(userDetails.getUserId());

        profile.setUser(user);
        profile.setStatus(ProfileStatus.PENDING);

        // profilePhotoPath is already set via the hidden field (uploaded by AJAX)

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
     * PHOTO UPLOAD (AJAX endpoint)
     * Handles profile photo upload separately from the main form.
     * Returns the saved file path as plain text.
     */
    @PostMapping("/admission/uploadPhoto")
    @ResponseBody
    public ResponseEntity<String> uploadProfilePhoto(
            @RequestParam("profilePhoto") MultipartFile profilePhoto) {
        try {
            String photoPath = fileStorageService.saveProfilePhoto(profilePhoto);
            return ResponseEntity.ok(photoPath);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Upload failed: " + e.getMessage());
        }
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

        // Notices — visible to ALL logged-in students
        model.addAttribute("notices", noticeService.getActiveNotices());

        return "dashboard";
    }

    /** PAY RENT */
    @PostMapping("/dashboard/rent/pay")
    public String payRent(@RequestParam String month,
                          @RequestParam int year,
                          @RequestParam String paymentMode,
                          @AuthenticationPrincipal CustomUserDetails userDetails,
                          RedirectAttributes redirectAttributes) {
        User user = userService.findById(userDetails.getUserId());
        StudentProfile profile = studentProfileService.findByUser(user);
        try {
            rentPaymentService.recordPayment(profile, month, year, paymentMode);
            redirectAttributes.addFlashAttribute("success", "Rent paid for " + month + " " + year + "!");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/dashboard#tab-rent";
    }

    /**
     * SUBMIT QUERY (with optional photo attachment)
     */
    @PostMapping("/dashboard/query/submit")
    public String submitQuery(@RequestParam String subject,
                              @RequestParam String message,
                              @RequestParam(value = "queryPhoto", required = false) MultipartFile queryPhoto,
                              @AuthenticationPrincipal CustomUserDetails userDetails) {
        User user = userService.findById(userDetails.getUserId());
        StudentProfile profile = studentProfileService.findByUser(user);

        String photoPath = null;
        if (queryPhoto != null && !queryPhoto.isEmpty()) {
            try {
                photoPath = fileStorageService.saveQueryPhoto(queryPhoto);
            } catch (Exception e) {
                // If photo upload fails, still submit the query without the photo
                photoPath = null;
            }
        }

        supportQueryService.submitQuery(profile, subject, message, photoPath);
        return "redirect:/dashboard#tab-query";
    }

    // =============================================
    // CHANGE PASSWORD
    // =============================================

    /** Show the change password form */
    @GetMapping("/change-password")
    public String showChangePasswordForm() {
        return "change-password";
    }

    /**
     * Process change password request.
     * Steps:
     *   1. Check if current password is correct
     *   2. Check if new password matches confirm password
     *   3. Encode new password with BCrypt and save
     */
    @PostMapping("/change-password")
    public String changePassword(@RequestParam String currentPassword,
                                  @RequestParam String newPassword,
                                  @RequestParam String confirmPassword,
                                  @AuthenticationPrincipal CustomUserDetails userDetails,
                                  RedirectAttributes redirectAttributes) {

        User user = userService.findById(userDetails.getUserId());
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        // Step 1: Verify current password
        if (!encoder.matches(currentPassword, user.getPassword())) {
            redirectAttributes.addFlashAttribute("error", "Current password is incorrect!");
            return "redirect:/change-password";
        }

        // Step 2: Check new passwords match (also validated client-side)
        if (!newPassword.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("error", "New passwords do not match!");
            return "redirect:/change-password";
        }

        // Step 3: Encode and save new password
        user.setPassword(encoder.encode(newPassword));
        userService.updateUser(user);

        redirectAttributes.addFlashAttribute("success", "Password changed successfully!");
        return "redirect:/change-password";
    }

    // ====================================
    // RENT RECEIPT PDF DOWNLOAD
    // ====================================
    /**
     * Download a rent receipt as PDF.
     * Student clicks the download button next to a payment.
     */
    @GetMapping("/rent-receipt/{paymentId}")
    @ResponseBody
    public ResponseEntity<byte[]> downloadRentReceipt(
            @org.springframework.web.bind.annotation.PathVariable Long paymentId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        in.gw.main.Entity.RentPayment payment = rentPaymentService.findById(paymentId);
        if (payment == null) {
            return ResponseEntity.notFound().build();
        }

        // Security check: only allow the student to download their own receipt
        User user = userDetails.getUser();
        if (!payment.getStudentProfile().getUser().getId().equals(user.getId())) {
            return ResponseEntity.status(403).build();
        }

        byte[] pdfBytes = pdfService.generateRentReceipt(payment);

        return ResponseEntity.ok()
                .header("Content-Type", "application/pdf")
                .header("Content-Disposition",
                        "attachment; filename=receipt_" + payment.getMonth() + "_" + payment.getYear() + ".pdf")
                .body(pdfBytes);
    }

    // ====================================
    // STUDENT PROFILE EDIT
    // ====================================
    /** Show profile edit form (pre-filled with current data) */
    @GetMapping("/edit-profile")
    public String editProfilePage(@AuthenticationPrincipal CustomUserDetails userDetails,
                                  Model model) {
        User user = userDetails.getUser();
        model.addAttribute("loggedUser", user);

        StudentProfile profile = studentProfileService.findByUser(user);
        if (profile == null) {
            return "redirect:/dashboard";
        }

        model.addAttribute("profile", profile);
        return "edit-profile";
    }

    /** Save updated profile fields */
    @PostMapping("/edit-profile")
    public String saveProfile(@AuthenticationPrincipal CustomUserDetails userDetails,
                              @RequestParam String phoneNumber,
                              @RequestParam String address,
                              @RequestParam String parentContact,
                              RedirectAttributes redirectAttributes) {

        User user = userDetails.getUser();
        StudentProfile profile = studentProfileService.findByUser(user);
        if (profile == null) {
            return "redirect:/dashboard";
        }

        profile.setPhoneNumber(phoneNumber);
        profile.setAddress(address);
        profile.setParentContact(parentContact);
        studentProfileService.save(profile);

        redirectAttributes.addFlashAttribute("success", "Profile updated successfully!");
        return "redirect:/edit-profile";
    }

    /**
     * HANDLE FILE SIZE EXCEEDED
     * If uploaded file is too large, redirect back with error message
     * instead of showing ugly HTTP 413 error page.
     */
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public String handleMaxSizeException(MaxUploadSizeExceededException ex,
                                         RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("error",
                "File too large! Maximum allowed size is 10MB. Please upload a smaller file.");
        return "redirect:/admission";
    }
}
