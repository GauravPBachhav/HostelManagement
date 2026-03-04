package in.gw.main.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import in.gw.main.Entity.*;
import in.gw.main.Services.*;

/**
 * ADMIN CONTROLLER
 * =================
 * Handles all /admin/... pages.
 *
 * Features:
 *   - View and manage student admissions (approve/reject)
 *   - Detailed student application view (full profile + photo)
 *   - Room management (add/delete rooms)
 *   - Rent payment overview
 *   - Reply to student queries (with image preview)
 *   - Password reset for students
 */
@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private StudentProfileService studentProfileService;

    @Autowired
    private RoomService roomService;

    @Autowired
    private RentPaymentService rentPaymentService;

    @Autowired
    private SupportQueryService supportQueryService;

    @Autowired
    private UserService userService;

    /**
     * ADMIN DASHBOARD - main page
     * Loads ALL data needed for admin tabs
     */
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        // Student profiles grouped by status
        model.addAttribute("pendingProfiles", studentProfileService.getPendingProfiles());
        model.addAttribute("approvedProfiles", studentProfileService.getApprovedProfiles());
        model.addAttribute("rejectedProfiles", studentProfileService.getRejectedProfiles());

        // Room management data
        model.addAttribute("rooms", roomService.getAllRooms());
        model.addAttribute("availableRooms", roomService.getAvailableRooms());
        model.addAttribute("totalBeds", roomService.getTotalCapacity());
        model.addAttribute("availableBeds", roomService.getAvailableBeds());
        model.addAttribute("newRoom", new Room());

        // Rent overview
        model.addAttribute("recentPayments", rentPaymentService.getRecentPayments());

        // Student queries (open + all)
        model.addAttribute("openQueries", supportQueryService.getOpenQueries());
        model.addAttribute("allQueries", supportQueryService.getAllQueries());

        return "admin-dashboard";
    }

    /**
     * VIEW STUDENT DETAIL PAGE
     * Shows full admission application with photo, all details in a nice format.
     * Admin opens this to review a student before approving/rejecting.
     */
    @GetMapping("/student/{id}")
    public String viewStudentDetail(@PathVariable Long id, Model model) {
        StudentProfile profile = studentProfileService.findById(id);
        if (profile == null) {
            return "redirect:/admin/dashboard";
        }
        model.addAttribute("profile", profile);
        model.addAttribute("availableRooms", roomService.getAvailableRooms());
        return "student-detail";
    }

    /**
     * APPROVE a student's admission.
     * Admin selects a room → student gets approved + room assigned.
     */
    @PostMapping("/approve/{id}")
    public String approve(@PathVariable Long id,
                          @RequestParam(required = false) Long roomId,
                          RedirectAttributes redirectAttributes) {
        studentProfileService.approveAndAssignRoom(id, roomId);
        redirectAttributes.addFlashAttribute("success", "Student approved successfully!");
        return "redirect:/admin/dashboard";
    }

    /** REJECT a student's admission with optional reason */
    @PostMapping("/reject/{id}")
    public String reject(@PathVariable Long id,
                         RedirectAttributes redirectAttributes) {
        studentProfileService.updateStatus(id, ProfileStatus.REJECTED);
        redirectAttributes.addFlashAttribute("success", "Student rejected.");
        return "redirect:/admin/dashboard";
    }

    /** ADD a new room */
    @PostMapping("/rooms/add")
    public String addRoom(@ModelAttribute Room room) {
        roomService.addRoom(room);
        return "redirect:/admin/dashboard#tab-rooms";
    }

    /** DELETE a room */
    @PostMapping("/rooms/delete/{id}")
    public String deleteRoom(@PathVariable Long id) {
        roomService.deleteRoom(id);
        return "redirect:/admin/dashboard#tab-rooms";
    }

    /** REPLY to a student query → marks it as RESOLVED */
    @PostMapping("/query/reply/{id}")
    public String replyQuery(@PathVariable Long id,
                             @RequestParam String adminReply) {
        supportQueryService.replyToQuery(id, adminReply);
        return "redirect:/admin/dashboard#tab-queries";
    }

    /**
     * RESET PASSWORD for a student
     * Resets to default "hostel123" - student must change after login.
     */
    @PostMapping("/reset-password/{userId}")
    public String resetPassword(@PathVariable Long userId,
                                RedirectAttributes redirectAttributes) {
        String newPassword = userService.resetPassword(userId);
        redirectAttributes.addFlashAttribute("success",
                "Password reset to: " + newPassword);
        return "redirect:/admin/dashboard#tab-approved";
    }
}
