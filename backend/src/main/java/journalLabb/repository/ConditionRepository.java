package journalLabb.repository;


import journalLabb.model.Condition;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ConditionRepository extends JpaRepository<Condition, Long> {
}