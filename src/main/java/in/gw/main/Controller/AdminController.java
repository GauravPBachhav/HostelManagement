package in.gw.main.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
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

    @Autowired
    private NoticeService noticeService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private StudentArchiveService archiveService;

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

        // Notices
        model.addAttribute("notices", noticeService.getAllNotices());

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

        // Send approval email with room details
        StudentProfile profile = studentProfileService.findById(id);
        if (profile != null && profile.getUser() != null) {
            String roomNum = profile.getRoom() != null ? profile.getRoom().getRoomNumber() : "TBD";
            int rent = profile.getRoom() != null ? (int) profile.getRoom().getMonthlyRent() : 0;
            emailService.sendApprovalEmail(profile.getUser().getEmail(), profile.getUser().getName(), roomNum, rent);
        }

        redirectAttributes.addFlashAttribute("success", "Student approved & email sent!");
        return "redirect:/admin/dashboard";
    }

    /** REJECT a student's admission with optional reason */
    @PostMapping("/reject/{id}")
    public String reject(@PathVariable Long id,
                         RedirectAttributes redirectAttributes) {
        // Send rejection email before changing status
        StudentProfile profile = studentProfileService.findById(id);
        if (profile != null && profile.getUser() != null) {
            emailService.sendRejectionEmail(profile.getUser().getEmail(), profile.getUser().getName());
        }

        // Archive rejected student
        if (profile != null) {
            archiveService.archiveFromProfile(profile, ArchiveStatus.REJECTED, "Application rejected by admin");
        }

        studentProfileService.updateStatus(id, ProfileStatus.REJECTED);
        redirectAttributes.addFlashAttribute("success", "Student rejected, archived & email sent.");
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

    // =============================================
    // NOTICE MANAGEMENT
    // =============================================

    /** Add a new notice */
    @PostMapping("/notice/add")
    public String addNotice(@RequestParam String title,
                            @RequestParam String message,
                            RedirectAttributes redirectAttributes) {
        noticeService.addNotice(title, message);
        redirectAttributes.addFlashAttribute("success", "Notice posted!");
        return "redirect:/admin/dashboard#tab-notices";
    }

    /** Delete a notice */
    @PostMapping("/notice/delete/{id}")
    public String deleteNotice(@PathVariable Long id) {
        noticeService.deleteNotice(id);
        return "redirect:/admin/dashboard#tab-notices";
    }

    // =============================================
    // ROOM EDIT
    // =============================================

    /** Update room details (rent, capacity, type) */
    @PostMapping("/rooms/edit/{id}")
    public String editRoom(@PathVariable Long id,
                           @RequestParam int capacity,
                           @RequestParam int monthlyRent,
                           @RequestParam String roomType,
                           RedirectAttributes redirectAttributes) {
        roomService.updateRoom(id, capacity, monthlyRent, roomType);
        redirectAttributes.addFlashAttribute("success", "Room updated!");
        return "redirect:/admin/dashboard#tab-rooms";
    }

    // =============================================
    // ROOM VACATE / CHECKOUT
    // =============================================

    /**
     * Vacate a student from their room.
     * Sets status to VACATED, frees the bed, unassigns room.
     */
    @PostMapping("/vacate/{id}")
    public String vacateStudent(@PathVariable Long id,
                                RedirectAttributes redirectAttributes) {
        // Archive before vacating
        StudentProfile profile = studentProfileService.findById(id);
        if (profile != null) {
            archiveService.archiveFromProfile(profile, ArchiveStatus.COMPLETED, "Vacated from hostel");
        }

        studentProfileService.vacateStudent(id);
        redirectAttributes.addFlashAttribute("success", "Student vacated, archived & room freed.");
        return "redirect:/admin/dashboard#tab-approved";
    }

    // =============================================
    // STUDENT RECORDS / ARCHIVE
    // =============================================

    /**
     * View student records — Active, Alumni (completed), Rejected.
     * Like a CRM history page.
     */
    @GetMapping("/records")
    public String studentRecords(Model model) {
        // Active students (currently approved & staying)
        model.addAttribute("activeStudents",
            studentProfileService.findByStatus(ProfileStatus.APPROVED));

        // Alumni (completed their stay)
        model.addAttribute("alumni",
            archiveService.findByStatus(ArchiveStatus.COMPLETED));

        // Rejected
        model.addAttribute("rejected",
            archiveService.findByStatus(ArchiveStatus.REJECTED));

        // Counts for badges
        model.addAttribute("activeCount",
            studentProfileService.findByStatus(ProfileStatus.APPROVED).size());
        model.addAttribute("alumniCount",
            archiveService.countByStatus(ArchiveStatus.COMPLETED));
        model.addAttribute("rejectedCount",
            archiveService.countByStatus(ArchiveStatus.REJECTED));

        return "admin-records";
    }

    /**
     * Delete alumni/rejected record — frees email for re-registration.
     * Deletes: archive record + student profile + user account (by email).
     */
    @PostMapping("/records/delete/{id}")
    @org.springframework.transaction.annotation.Transactional
    public String deleteAlumniRecord(@PathVariable Long id, RedirectAttributes ra) {
        StudentArchive archive = archiveService.findById(id);
        if (archive == null) {
            ra.addFlashAttribute("success", "Record not found.");
            return "redirect:/admin/records";
        }

        String email = archive.getEmail();

        // 1. Delete student profile if exists (FK to user)
        if (email != null) {
            User user = userService.findByEmail(email);
            if (user != null) {
                studentProfileService.deleteByUser(user);
            }
        }

        // 2. Delete user account (frees email)
        if (email != null) {
            userService.deleteByEmail(email);
        }

        // 3. Delete archive record
        archiveService.deleteById(id);

        ra.addFlashAttribute("success", "Record deleted. Email '" + email + "' is now free for re-registration.");
        return "redirect:/admin/records";
    }

    // =============================================
    // FACILITY MANAGEMENT (Homepage Cards)
    // =============================================

    @Autowired
    private FacilityService facilityService;

    @Autowired
    private FileStorageService fileStorageService;

    /** Facility management page */
    @GetMapping("/facilities")
    public String facilitiesPage(Model model) {
        model.addAttribute("facilities", facilityService.getAllFacilities());
        model.addAttribute("newFacility", new Facility());
        return "admin-facilities";
    }

    /** Add new facility */
    @PostMapping("/facilities/add")
    public String addFacility(@ModelAttribute Facility facility,
                              @RequestParam(value = "photo", required = false) MultipartFile photo,
                              RedirectAttributes redirectAttributes) {
        try {
            if (photo != null && !photo.isEmpty()) {
                String path = fileStorageService.saveProfilePhoto(photo);
                facility.setImagePath(path);
            }
            facilityService.save(facility);
            redirectAttributes.addFlashAttribute("success", "Facility added!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/facilities";
    }

    /** Update facility photo */
    @PostMapping("/facilities/updatePhoto/{id}")
    public String updateFacilityPhoto(@PathVariable Long id,
                                      @RequestParam("photo") MultipartFile photo,
                                      RedirectAttributes redirectAttributes) {
        try {
            Facility f = facilityService.findById(id);
            if (f != null && photo != null && !photo.isEmpty()) {
                String path = fileStorageService.saveProfilePhoto(photo);
                f.setImagePath(path);
                facilityService.save(f);
                redirectAttributes.addFlashAttribute("success", "Photo updated for " + f.getTitle());
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/facilities";
    }

    /** Toggle facility active/inactive */
    @PostMapping("/facilities/toggle/{id}")
    public String toggleFacility(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        facilityService.toggleActive(id);
        redirectAttributes.addFlashAttribute("success", "Facility visibility toggled!");
        return "redirect:/admin/facilities";
    }

    /** Delete facility */
    @PostMapping("/facilities/delete/{id}")
    public String deleteFacility(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        facilityService.delete(id);
        redirectAttributes.addFlashAttribute("success", "Facility deleted!");
        return "redirect:/admin/facilities";
    }
}
