package in.gw.main.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

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


    // =========================
    // COMMON ADMIN CHECK
    // =========================
    private boolean isAdmin(HttpSession session) {
        User user = (User) session.getAttribute("loggedUser");
        return user != null && "ADMIN".equals(user.getRole());
    }


    // =========================
    // ADMIN DASHBOARD
    // =========================
    @GetMapping("/dashboard")
    public String adminDashboard(HttpSession session, Model model) {

        if (!isAdmin(session)) {
            return "redirect:/login";
        }

        List<StudentProfile> pendingProfiles =
                studentProfileService.getPendingProfiles();

        model.addAttribute("profiles", pendingProfiles);

        return "admin-dashboard";
    }


    // =========================
    // APPROVE
    // =========================
    @PostMapping("/approve/{id}")
    public String approveProfile(@PathVariable Long id,
                                 HttpSession session) {

        if (!isAdmin(session)) {
            return "redirect:/login";
        }

        studentProfileService.updateStatus(id, ProfileStatus.APPROVED);

        return "redirect:/admin/dashboard";
    }


    // =========================
    // REJECT
    // =========================
    @PostMapping("/reject/{id}")
    public String rejectProfile(@PathVariable Long id,
                                HttpSession session) {

        if (!isAdmin(session)) { 
            return "redirect:/login";
        }

        studentProfileService.updateStatus(id, ProfileStatus.REJECTED);

        return "redirect:/admin/dashboard";
    }
}