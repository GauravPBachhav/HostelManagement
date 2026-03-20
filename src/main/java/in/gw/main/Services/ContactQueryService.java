package in.gw.main.Services;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import in.gw.main.Entity.ContactQuery;
import in.gw.main.Entity.QueryStatus;
import in.gw.main.Repository.ContactQueryRepository;

@Service
public class ContactQueryService {

    @Autowired
    private ContactQueryRepository repo;

    /** Save a new contact query from public visitor */
    public ContactQuery save(String name, String mobile, String email, String message) {
        ContactQuery q = new ContactQuery();
        q.setName(name);
        q.setMobile(mobile);
        q.setEmail(email);
        q.setMessage(message);
        q.setStatus(QueryStatus.OPEN);
        q.setCreatedAt(LocalDateTime.now());
        return repo.save(q);
    }

    /** Get all queries (newest first) */
    public List<ContactQuery> findAll() {
        return repo.findAllByOrderByCreatedAtDesc();
    }

    /** Toggle solved/unsolved */
    public void toggleStatus(Long id) {
        ContactQuery q = repo.findById(id).orElse(null);
        if (q != null) {
            if (q.getStatus() == QueryStatus.OPEN) {
                q.setStatus(QueryStatus.RESOLVED);
                q.setResolvedAt(LocalDateTime.now());
            } else {
                q.setStatus(QueryStatus.OPEN);
                q.setResolvedAt(null);
            }
            repo.save(q);
        }
    }

    /** Delete a query */
    public void delete(Long id) {
        repo.deleteById(id);
    }

    public long countOpen() {
        return repo.countByStatus(QueryStatus.OPEN);
    }
}
