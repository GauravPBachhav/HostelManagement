package in.gw.main.Controller;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import in.gw.main.Entity.ProfileStatus;
import in.gw.main.Entity.User;
import in.gw.main.Services.StudentProfileService;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private StudentProfileService studentProfileService;

    private boolean isAdmin(HttpSession session) {
        User user = (User) session.getAttribute("loggedUser");
        return user != null && "ADMIN".equals(user.getRole());
    }

    // =====================
    // ADMIN DASHBOARD
    // Sections: Pending | Approved | Rejected | Month-wise | Queries
    // =====================
    @GetMapping("/dashboard")
    public String adminDashboard(HttpSession session, Model model,
                                 @RequestParam(defaultValue = "0")  int month,
                                 @RequestParam(defaultValue = "0")  int year) {

        if (!isAdmin(session)) return "redirect:/login";

        // Default to current month/year if not given
        LocalDate now = LocalDate.now();
        if (month == 0) month = now.getMonthValue();
        if (year  == 0) year  = now.getYear();

        // All sections
        model.addAttribute("pendingProfiles",  studentProfileService.getPendingProfiles());
        model.addAttribute("approvedProfiles", studentProfileService.getApprovedProfiles());
        model.addAttribute("rejectedProfiles", studentProfileService.getRejectedProfiles());
        model.addAttribute("monthlyProfiles",  studentProfileService.getApprovedByMonth(month, year));
        model.addAttribute("queryProfiles",    studentProfileService.getProfilesWithQueries());

        // Pass month/year to template for the form
        model.addAttribute("selectedMonth", month);
        model.addAttribute("selectedYear",  year);

        return "admin-dashboard";
    }

    // Approve
    @PostMapping("/approve/{id}")
    public String approve(@PathVariable Long id, HttpSession session,
                          RedirectAttributes ra) {
        if (!isAdmin(session)) return "redirect:/login";
        studentProfileService.updateStatus(id, ProfileStatus.APPROVED);
        ra.addFlashAttribute("success", "✅ Profile approved!");
        return "redirect:/admin/dashboard";
    }

    // Reject
    @PostMapping("/reject/{id}")
    public String reject(@PathVariable Long id, HttpSession session,
                         RedirectAttributes ra) {
        if (!isAdmin(session)) return "redirect:/login";
        studentProfileService.updateStatus(id, ProfileStatus.REJECTED);
        ra.addFlashAttribute("success", "Profile rejected.");
        return "redirect:/admin/dashboard";
    }

    // Mark rent paid
    @PostMapping("/rent-paid/{id}")
    public String markRentPaid(@PathVariable Long id, HttpSession session,
                               RedirectAttributes ra) {
        if (!isAdmin(session)) return "redirect:/login";
        studentProfileService.markRentPaid(id);
        ra.addFlashAttribute("success", "💰 Rent marked as paid!");
        return "redirect:/admin/dashboard";
    }
}