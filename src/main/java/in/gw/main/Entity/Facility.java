package in.gw.main.Entity;

import jakarta.persistence.*;

/**
 * FACILITY
 * =========
 * Represents a hostel facility/feature shown on the homepage.
 * Admin can add/edit/delete these from the dashboard.
 *
 * Examples: Modern Building, Furnished Rooms, CCTV Security, Purified Water, etc.
 *
 * If imagePath is set → shows photo as background.
 * If imagePath is null → shows gradient background with SVG icon.
 */
@Entity
@Table(name = "facilities")
public class Facility {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;            // e.g. "Modern Building"

    @Column(nullable = false)
    private String description;      // e.g. "Well-maintained 3-floor hostel"

    private String imagePath;        // Photo path (optional, stored via FileStorageService)

    @Column(nullable = false)
    private String gradientColors;   // e.g. "#1a1a2e,#4361ee" — fallback if no photo

    @Column(nullable = false)
    private int displayOrder = 0;    // Order on homepage (lower = first)

    private boolean active = true;   // Can be hidden without deleting

    // =====================
    // GETTERS & SETTERS
    // =====================
    public Long getId() { return id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getImagePath() { return imagePath; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }

    public String getGradientColors() { return gradientColors; }
    public void setGradientColors(String gradientColors) { this.gradientColors = gradientColors; }

    public int getDisplayOrder() { return displayOrder; }
    public void setDisplayOrder(int displayOrder) { this.displayOrder = displayOrder; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}
