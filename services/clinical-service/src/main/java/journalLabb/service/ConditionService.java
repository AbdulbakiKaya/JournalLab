package journalLabb.service;

import journalLabb.dto.ConditionDto;
import journalLabb.dto.CreateConditionDto;
import journalLabb.model.Condition;
import journalLabb.repository.ConditionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ConditionService {

    private final ConditionRepository conditionRepository;

    public List<ConditionDto> getConditionsForPatient(Long patientId) {
        return conditionRepository.findByPatientId(patientId).stream().map(this::toDto).toList();
    }

    public ConditionDto createCondition(CreateConditionDto req, Long doctorUserId) {
        Condition c = new Condition();
        c.setPatientId(req.getPatientId());
        c.setPractitionerUserId(doctorUserId);
        c.setText(req.getText());
        c.setSeverity(req.getSeverity());
        c.setCreatedAt(LocalDateTime.now());
        return toDto(conditionRepository.save(c));
    }

    private ConditionDto toDto(Condition c) {
        ConditionDto dto = new ConditionDto();
        dto.setId(c.getId());
        dto.setPatientId(c.getPatientId());
        dto.setPractitionerUserId(c.getPractitionerUserId());
        dto.setText(c.getText());
        dto.setSeverity(c.getSeverity());
        dto.setCreatedAt(c.getCreatedAt());
        return dto;
    }
}