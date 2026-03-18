package in.gw.main.Services;

import in.gw.main.Entity.Facility;
import in.gw.main.Repository.FacilityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * FACILITY SERVICE
 * =================
 * Manages hostel facilities displayed on the homepage.
 * Admin can add, edit, delete, toggle visibility.
 */
@Service
public class FacilityService {

    @Autowired
    private FacilityRepository facilityRepository;

    /** Get active facilities for homepage display */
    public List<Facility> getActiveFacilities() {
        return facilityRepository.findByActiveTrueOrderByDisplayOrderAsc();
    }

    /** Get all facilities for admin management */
    public List<Facility> getAllFacilities() {
        return facilityRepository.findAllByOrderByDisplayOrderAsc();
    }

    /** Find by ID */
    public Facility findById(Long id) {
        return facilityRepository.findById(id).orElse(null);
    }

    /** Save (create or update) */
    public Facility save(Facility facility) {
        return facilityRepository.save(facility);
    }

    /** Delete */
    public void delete(Long id) {
        facilityRepository.deleteById(id);
    }

    /** Toggle active/inactive */
    public void toggleActive(Long id) {
        Facility f = facilityRepository.findById(id).orElse(null);
        if (f != null) {
            f.setActive(!f.isActive());
            facilityRepository.save(f);
        }
    }
}
