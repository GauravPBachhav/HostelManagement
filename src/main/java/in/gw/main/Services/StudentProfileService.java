package in.gw.main.Services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import in.gw.main.Entity.ProfileStatus;
import in.gw.main.Entity.Room;
import in.gw.main.Entity.StudentProfile;
import in.gw.main.Entity.User;
import in.gw.main.Repository.RoomRepository;
import in.gw.main.Repository.StudentProfileRepository;

@Service
public class StudentProfileService {

    @Autowired
    private StudentProfileRepository studentProfileRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Transactional
    public List<StudentProfile> getPendingProfiles() {
        return studentProfileRepository.findByStatus(ProfileStatus.PENDING);
    }

    @Transactional
    public List<StudentProfile> getApprovedProfiles() {
        return studentProfileRepository.findByStatus(ProfileStatus.APPROVED);
    }

    @Transactional
    public List<StudentProfile> getRejectedProfiles() {
        return studentProfileRepository.findByStatus(ProfileStatus.REJECTED);
    }

    /** Generic find by any status */
    public List<StudentProfile> findByStatus(ProfileStatus status) {
        return studentProfileRepository.findByStatus(status);
    }

    public void updateStatus(Long id, ProfileStatus status) {
        StudentProfile profile = studentProfileRepository
                .findById(id)
                .orElseThrow(() -> new RuntimeException("Profile not found"));
        profile.setStatus(status);
        studentProfileRepository.save(profile);
    }

    public void saveProfile(StudentProfile profile) {
        StudentProfile existing = studentProfileRepository.findByUser(profile.getUser());
        if (existing != null) {
            throw new RuntimeException("Profile already submitted!");
        }
        profile.setStatus(ProfileStatus.PENDING);
        studentProfileRepository.save(profile);
    }

    @Transactional
    public StudentProfile findByUser(User user) {
        return studentProfileRepository.findByUser(user);
    }

    public StudentProfile findById(Long id) {
        return studentProfileRepository.findById(id).orElse(null);
    }

    /** Save/update an existing profile (used by profile edit feature) */
    public void save(StudentProfile profile) {
        studentProfileRepository.save(profile);
    }

    /**
     * Approve a student's admission AND assign them a room.
     * This is called when admin clicks "Approve" with a room selected.
     * 
     * Steps:
     *   1. Set profile status to APPROVED
     *   2. If a room was selected, assign it to the student
     *   3. Increase the room's current occupancy by 1
     */
    @Transactional
    public void approveAndAssignRoom(Long profileId, Long roomId) {
        StudentProfile profile = studentProfileRepository.findById(profileId)
                .orElseThrow(() -> new RuntimeException("Profile not found"));

        profile.setStatus(ProfileStatus.APPROVED);

        // Assign room if selected
        if (roomId != null) {
            Room room = roomRepository.findById(roomId)
                    .orElseThrow(() -> new RuntimeException("Room not found"));
            profile.setRoom(room);
            room.setCurrentOccupancy(room.getCurrentOccupancy() + 1);
            roomRepository.save(room);
        }

        studentProfileRepository.save(profile);
    }

    /**
     * VACATE a student from their room.
     * Steps:
     *   1. Set status to VACATED
     *   2. Decrease room occupancy by 1
     *   3. Remove room assignment from profile
     */
    @Transactional
    public void vacateStudent(Long profileId) {
        StudentProfile profile = studentProfileRepository.findById(profileId)
                .orElseThrow(() -> new RuntimeException("Profile not found"));

        // Free the bed in the room
        if (profile.getRoom() != null) {
            Room room = profile.getRoom();
            room.setCurrentOccupancy(Math.max(0, room.getCurrentOccupancy() - 1));
            roomRepository.save(room);
            profile.setRoom(null);
        }

        profile.setStatus(ProfileStatus.VACATED);
        studentProfileRepository.save(profile);
    }

    /** Delete a student's profile (used for re-apply on rejection) */
    public void deleteByUser(User user) {
        StudentProfile profile = studentProfileRepository.findByUser(user);
        if (profile != null) {
            studentProfileRepository.delete(profile);
        }
    }
}