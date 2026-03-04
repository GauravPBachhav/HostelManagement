package in.gw.main.Services;

import in.gw.main.Entity.Room;
import in.gw.main.Repository.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * ROOM SERVICE
 * -------------
 * Handles all room-related business logic:
 *   - Add / delete rooms
 *   - Get total capacity and available beds
 *   - Find rooms with empty spots
 *
 * This service is used by both AdminController and UserController.
 */
@Service
public class RoomService {

    @Autowired
    private RoomRepository roomRepository;

    /** Get ALL rooms (including inactive) */
    public List<Room> getAllRooms() {
        return roomRepository.findAll();
    }

    /** Get only active rooms */
    public List<Room> getAllActiveRooms() {
        return roomRepository.findByActiveTrue();
    }

    /**
     * Get rooms that still have empty beds.
     * Available = rooms where currentOccupancy < capacity
     */
    public List<Room> getAvailableRooms() {
        return roomRepository.findByActiveTrue().stream()
                .filter(room -> room.getCurrentOccupancy() < room.getCapacity())
                .collect(Collectors.toList());
    }

    /** Total bed capacity across all active rooms */
    public int getTotalCapacity() {
        return roomRepository.findByActiveTrue().stream()
                .mapToInt(Room::getCapacity)
                .sum();
    }

    /** Total AVAILABLE beds (empty spots) across all active rooms */
    public int getAvailableBeds() {
        return roomRepository.findByActiveTrue().stream()
                .mapToInt(room -> room.getCapacity() - room.getCurrentOccupancy())
                .sum();
    }

    /** Add a new room to the hostel */
    public void addRoom(Room room) {
        room.setActive(true);
        room.setCurrentOccupancy(0);
        roomRepository.save(room);
    }

    /** Delete a room */
    public void deleteRoom(Long id) {
        roomRepository.deleteById(id);
    }

    /** Find room by ID */
    public Room findById(Long id) {
        return roomRepository.findById(id).orElse(null);
    }
}
