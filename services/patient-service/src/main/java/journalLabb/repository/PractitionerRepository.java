package journalLabb.repository;

import journalLabb.model.Practitioner;
import journalLabb.model.PractitionerType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PractitionerRepository extends JpaRepository<Practitioner, Long> {
    List<Practitioner> findByType(PractitionerType type);
    Optional<Practitioner> findByUserId(Long userId);
}