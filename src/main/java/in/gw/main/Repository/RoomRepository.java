package in.gw.main.Repository;

import in.gw.main.Entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * ROOM REPOSITORY
 * ----------------
 * Provides database operations for Room entity.
 * Spring Data JPA automatically creates the SQL queries from method names.
 *
 * Examples:
 *   findByActiveTrue() → SELECT * FROM rooms WHERE active = true
 */
@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {

    // Find all active (available) rooms
    List<Room> findByActiveTrue();

    // Find active rooms on a specific floor
    List<Room> findByFloorAndActiveTrue(int floor);

    // Get all distinct floor numbers (sorted)
    @Query("SELECT DISTINCT r.floor FROM Room r WHERE r.active = true ORDER BY r.floor")
    List<Integer> findDistinctFloors();
}
