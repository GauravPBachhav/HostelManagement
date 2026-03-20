package in.gw.main.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import in.gw.main.Entity.ContactQuery;
import in.gw.main.Entity.QueryStatus;

@Repository
public interface ContactQueryRepository extends JpaRepository<ContactQuery, Long> {
    List<ContactQuery> findAllByOrderByCreatedAtDesc();
    List<ContactQuery> findByStatusOrderByCreatedAtDesc(QueryStatus status);
    long countByStatus(QueryStatus status);
}
