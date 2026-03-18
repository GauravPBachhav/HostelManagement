package in.gw.main.Repository;

import in.gw.main.Entity.Facility;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FacilityRepository extends JpaRepository<Facility, Long> {

    /** Get all active facilities ordered by displayOrder */
    List<Facility> findByActiveTrueOrderByDisplayOrderAsc();

    /** Get all facilities (including inactive) for admin */
    List<Facility> findAllByOrderByDisplayOrderAsc();
}
