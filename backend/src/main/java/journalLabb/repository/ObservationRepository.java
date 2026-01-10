package journalLabb.repository;


import journalLabb.model.Observation;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ObservationRepository extends JpaRepository<Observation, Long> {
}