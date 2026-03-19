package in.gw.main.Services;

import in.gw.main.Entity.Room;
import in.gw.main.Entity.RoomType;
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

    /** Add a new room to the hostel (auto-detects floor from room number) */
    public void addRoom(Room room) {
        room.setActive(true);
        room.setCurrentOccupancy(0);

        // Auto-detect floor from room number (e.g., "F11" → floor 1, "F24" → floor 2)
        String rn = room.getRoomNumber();
        if (rn != null && rn.length() >= 2 && (rn.charAt(0) == 'F' || rn.charAt(0) == 'f')) {
            try {
                room.setFloor(Character.getNumericValue(rn.charAt(1)));
            } catch (Exception e) {
                // keep whatever floor was set
            }
        }

        // Default room type if not set
        if (room.getRoomType() == null) {
            room.setRoomType(RoomType.SINGLE);
        }

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

    /** Update an existing room's details (capacity, rent, type) */
    public void updateRoom(Long id, int capacity, int monthlyRent, String roomType) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Room not found"));
        room.setCapacity(capacity);
        room.setMonthlyRent(monthlyRent);
        room.setRoomType(RoomType.valueOf(roomType));  // Convert String → RoomType enum
        roomRepository.save(room);
    }

    /** Get active rooms on a specific floor */
    public List<Room> getActiveRoomsByFloor(int floor) {
        return roomRepository.findByFloorAndActiveTrue(floor);
    }

    /** Get all distinct floor numbers that have active rooms */
    public List<Integer> getDistinctFloors() {
        return roomRepository.findDistinctFloors();
    }
}

