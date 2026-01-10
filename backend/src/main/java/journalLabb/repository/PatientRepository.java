package journalLabb.repository;

import journalLabb.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PatientRepository extends JpaRepository<Patient, Long> {
    boolean existsByIdAndAssignedDoctor_Id(Long patientId, Long doctorPractitionerId);
}