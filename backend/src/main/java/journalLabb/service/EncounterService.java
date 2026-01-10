package journalLabb.service;

import journalLabb.dto.CreateEncounterDto;
import journalLabb.model.*;
import journalLabb.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class EncounterService {

    private final EncounterRepository encounterRepository;
    private final PatientRepository patientRepository;
    private final PractitionerRepository practitionerRepository;
    private final LocationRepository locationRepository;

    public Encounter createEncounter(Long patientId, Long practitionerId, CreateEncounterDto dto) {

        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new RuntimeException("Patient not found"));

        Practitioner practitioner = practitionerRepository.findById(practitionerId)
                .orElseThrow(() -> new RuntimeException("Practitioner not found"));

        Location location = null;
        if (dto.getLocationId() != null) {
            location = locationRepository.findById(dto.getLocationId())
                    .orElseThrow(() -> new RuntimeException("Location not found"));
        }

        Encounter e = new Encounter();
        e.setPatient(patient);
        e.setPractitioner(practitioner);
        e.setLocation(location);
        e.setStartTime(LocalDateTime.now());

        return encounterRepository.save(e);
    }
}