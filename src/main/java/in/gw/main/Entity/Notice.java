package in.gw.main.Entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

/**
 * NOTICE ENTITY
 * --------------
 * Represents a hostel notice/announcement posted by admin.
 *
 * Fields:
 *   - title    : Short title of the notice (e.g. "Water Supply Timing Change")
 *   - message  : Detailed message
 *   - postedAt : When it was posted
 *   - active   : If false, notice is hidden from students
 */
@Entity
@Data
public class Notice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(length = 1000)
    private String message;

    private LocalDateTime postedAt;

    private boolean active = true;
}
