package in.gw.main.Services;

import in.gw.main.Entity.Notice;
import in.gw.main.Repository.NoticeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * NOTICE SERVICE
 * ---------------
 * Handles notice/announcement operations.
 *
 * FLOW:
 *   1. Admin creates a notice (title + message)
 *   2. Notice appears on student dashboard
 *   3. Admin can delete/deactivate notices
 */
@Service
public class NoticeService {

    @Autowired
    private NoticeRepository noticeRepository;

    /** Admin adds a new notice */
    public void addNotice(String title, String message) {
        Notice notice = new Notice();
        notice.setTitle(title);
        notice.setMessage(message);
        notice.setPostedAt(LocalDateTime.now());
        notice.setActive(true);
        noticeRepository.save(notice);
    }

    /** Delete a notice by ID */
    public void deleteNotice(Long id) {
        noticeRepository.deleteById(id);
    }

    /** Get all active notices (for student dashboard) */
    public List<Notice> getActiveNotices() {
        return noticeRepository.findByActiveTrueOrderByPostedAtDesc();
    }

    /** Get ALL notices (for admin management) */
    public List<Notice> getAllNotices() {
        return noticeRepository.findAllByOrderByPostedAtDesc();
    }
}
