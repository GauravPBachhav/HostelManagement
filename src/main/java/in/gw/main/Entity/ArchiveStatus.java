package in.gw.main.Entity;

/**
 * Archive status for student records.
 *   COMPLETED → Normal checkout (degree done, left voluntarily)
 *   EXPELLED  → Removed by admin (rule violation etc.)
 *   REJECTED  → Application was rejected
 */
public enum ArchiveStatus {
    COMPLETED,
    EXPELLED,
    REJECTED
}
