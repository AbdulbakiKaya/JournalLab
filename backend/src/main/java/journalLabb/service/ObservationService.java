package journalLabb.service;


import journalLabb.dto.ObservationDto;
import journalLabb.model.Observation;
import journalLabb.model.Patient;
import journalLabb.model.Practitioner;
import journalLabb.repository.ObservationRepository;
import journalLabb.repository.PatientRepository;
import journalLabb.repository.PractitionerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class ObservationService {
    private final ObservationRepository observationRepository;
    private final PatientRepository patientRepository;
    private final PractitionerRepository practitionerRepository;


    public Observation create(Long userId, ObservationDto dto) {
        Patient patient = patientRepository.findById(dto.getPatientId())
                .orElseThrow(() -> new RuntimeException("Patient not found"));


        Practitioner practitioner = practitionerRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Practitioner not found"));


        Observation o = new Observation();
        o.setText(dto.getText());
        o.setPatient(patient);
        o.setPractitioner(practitioner);
        return observationRepository.save(o);
    }
}