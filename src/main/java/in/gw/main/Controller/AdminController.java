package in.gw.main.Controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import in.gw.main.Entity.ProfileStatus;
import in.gw.main.Entity.StudentProfile;
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

    // ADMIN DASHBOARD — passes all 3 lists separately
    @GetMapping("/dashboard")
    public String adminDashboard(HttpSession session, Model model) {
        if (!isAdmin(session)) return "redirect:/login";

        // ✅ FIX: Pass all profile lists — admin sees full details
        List<StudentProfile> pendingProfiles  = studentProfileService.getPendingProfiles();
        List<StudentProfile> approvedProfiles = studentProfileService.getApprovedProfiles();
        List<StudentProfile> rejectedProfiles = studentProfileService.getRejectedProfiles();

        model.addAttribute("pendingProfiles",  pendingProfiles);
        model.addAttribute("approvedProfiles", approvedProfiles);
        model.addAttribute("rejectedProfiles", rejectedProfiles);

        return "admin-dashboard";
    }

    @PostMapping("/approve/{id}")
    public String approveProfile(@PathVariable Long id, HttpSession session) {
        if (!isAdmin(session)) return "redirect:/login";
        studentProfileService.updateStatus(id, ProfileStatus.APPROVED);
        return "redirect:/admin/dashboard";
    }

    @PostMapping("/reject/{id}")
    public String rejectProfile(@PathVariable Long id, HttpSession session) {
        if (!isAdmin(session)) return "redirect:/login";
        studentProfileService.updateStatus(id, ProfileStatus.REJECTED);
        return "redirect:/admin/dashboard";
    }
}