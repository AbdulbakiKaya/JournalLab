package journalLabb.service;

import journalLabb.dto.ConditionDto;
import journalLabb.dto.CreateConditionDto;
import journalLabb.model.Condition;
import journalLabb.model.Patient;
import journalLabb.model.Practitioner;
import journalLabb.repository.ConditionRepository;
import journalLabb.repository.PatientRepository;
import journalLabb.repository.PractitionerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ConditionService {

    private final ConditionRepository conditionRepository;
    private final PatientRepository patientRepository;
    private final PractitionerRepository practitionerRepository;

    public ConditionDto createCondition(Long patientId, Long practitionerId, CreateConditionDto dto) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new RuntimeException("Patient not found"));

        Practitioner practitioner = practitionerRepository.findById(practitionerId)
                .orElseThrow(() -> new RuntimeException("Practitioner not found"));

        Condition condition = new Condition();
        condition.setText(dto.getText());
        condition.setSeverity(dto.getSeverity());
        condition.setPatient(patient);
        condition.setPractitioner(practitioner);

        Condition saved = conditionRepository.save(condition);
        return toDto(saved);
    }

    public List<ConditionDto> getConditionsForPatient(Long patientId) {
        return conditionRepository.findAll().stream()
                .filter(c -> c.getPatient().getId().equals(patientId))
                .map(this::toDto)
                .toList();
    }

    private ConditionDto toDto(Condition condition) {
        ConditionDto dto = new ConditionDto();
        dto.setId(condition.getId());
        dto.setText(condition.getText());
        dto.setSeverity(condition.getSeverity());
        dto.setPractitionerName(
                condition.getPractitioner().getFirstName() + " " + condition.getPractitioner().getLastName()
        );
        return dto;
    }
}