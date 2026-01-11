package journalLabb.service;

import journalLabb.dto.EncounterDto;
import journalLabb.model.Encounter;
import journalLabb.repository.EncounterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EncounterService {

    private final EncounterRepository encounterRepository;

    public List<EncounterDto> getEncountersForPatient(Long patientId) {
        return encounterRepository.findByPatientId(patientId).stream().map(this::toDto).toList();
    }

    public List<EncounterDto> getEncountersForDoctorOnDate(Long doctorUserId, LocalDate date) {
        LocalDateTime from = date.atStartOfDay();
        LocalDateTime to = date.plusDays(1).atStartOfDay();
        return encounterRepository
                .findByPractitionerUserIdAndStartTimeBetween(doctorUserId, from, to)
                .stream()
                .map(this::toDto)
                .toList();
    }

    public EncounterDto createEncounter(EncounterDto dto) {
        Encounter e = new Encounter();
        e.setPatientId(dto.getPatientId());
        e.setPractitionerUserId(dto.getPractitionerUserId());
        e.setStartTime(dto.getStartTime());
        e.setEndTime(dto.getEndTime());
        e.setNote(dto.getNote());
        e.setLocation(dto.getLocation());
        return toDto(encounterRepository.save(e));
    }

    public EncounterDto setEncounterImage(Long encounterId, String imageId) {
        Encounter encounter = encounterRepository.findById(encounterId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Encounter not found: " + encounterId));

        encounter.setImageId(imageId);
        encounterRepository.save(encounter);
        return toDto(encounter);
    }

    private EncounterDto toDto(Encounter e) {
        EncounterDto dto = new EncounterDto();

        dto.setId(e.getId());
        dto.setPatientId(e.getPatient().getId());

        if (e.getPractitioner() != null) {
            dto.setPractitionerUserId(e.getPractitioner().getUser().getId());

            dto.setPractitionerName(
                    e.getPractitioner().getFirstName() + " " +
                            e.getPractitioner().getLastName()
            );

            dto.setPractitionerType(
                    e.getPractitioner().getType().name()
            );
        }

        dto.setStartTime(e.getStartTime());
        dto.setEndTime(e.getEndTime());
        dto.setNote(e.getNote());
        dto.setLocation(e.getLocation());
        dto.setImageId(e.getImageId());

        return dto;
    }
}