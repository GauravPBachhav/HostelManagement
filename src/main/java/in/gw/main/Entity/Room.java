package in.gw.main.Entity;

import jakarta.persistence.*;

/**
 * ROOM ENTITY
 * ------------
 * Represents a physical room in the hostel.
 *
 * Each room has:
 *   - A unique room number (like "101", "202")
 *   - A type (SINGLE, DOUBLE, TRIPLE)
 *   - A capacity (how many students CAN stay)
 *   - Current occupancy (how many ARE staying)
 *   - Monthly rent amount
 *
 * The available beds = capacity - currentOccupancy
 */
@Entity
@Table(name = "rooms")
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String roomNumber;          // e.g., "101", "202", "305"

    private int floor;                  // e.g., 1, 2, 3

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RoomType roomType;          // SINGLE, DOUBLE, TRIPLE

    private int capacity;               // max students allowed in this room

    private int currentOccupancy = 0;   // how many students are currently in this room

    private double monthlyRent;         // rent in rupees per month

    private boolean active = true;      // is this room available for use?

    // =====================
    // GETTERS & SETTERS
    // =====================

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getRoomNumber() { return roomNumber; }
    public void setRoomNumber(String roomNumber) { this.roomNumber = roomNumber; }

    public int getFloor() { return floor; }
    public void setFloor(int floor) { this.floor = floor; }

    public RoomType getRoomType() { return roomType; }
    public void setRoomType(RoomType roomType) { this.roomType = roomType; }

    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) { this.capacity = capacity; }

    public int getCurrentOccupancy() { return currentOccupancy; }
    public void setCurrentOccupancy(int currentOccupancy) { this.currentOccupancy = currentOccupancy; }

    public double getMonthlyRent() { return monthlyRent; }
    public void setMonthlyRent(double monthlyRent) { this.monthlyRent = monthlyRent; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}
