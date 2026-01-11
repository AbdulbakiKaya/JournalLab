package journalLabb.service;

import journalLabb.dto.PatientDetailsDto;
import journalLabb.dto.PatientDto;
import journalLabb.model.Patient;
import journalLabb.model.Role;
import journalLabb.security.UserPrincipal;
import journalLabb.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PatientService {

    private final PatientRepository patientRepository;

    public List<PatientDto> getAllPatients() {
        return patientRepository.findAll().stream().map(this::toDto).toList();
    }

    public PatientDetailsDto getPatientById(Long patientId, UserPrincipal principal) {
        Patient p = patientRepository.findById(patientId)
                .orElseThrow(() -> new RuntimeException("Patient not found: " + patientId));

        assertCanReadPatient(principal, p);
        return toDetailsDto(p);
    }

    public PatientDetailsDto getMyPatient(UserPrincipal principal) {
        if (principal.getRole() != Role.PATIENT) {
            throw new RuntimeException("Only PATIENT can call /me");
        }

        Patient p = patientRepository.findByUserId(principal.getUserId())
                .orElseThrow(() -> new RuntimeException("Patient not found for userId: " + principal.getUserId()));

        return toDetailsDto(p);
    }

    public List<PatientDto> getPatientsForDoctor(Long doctorUserId) {
        return patientRepository.findByAssignedDoctorId(doctorUserId).stream().map(this::toDto).toList();
    }

    public void changeAssignedDoctor(Long patientId, Long newDoctorUserId, UserPrincipal principal) {
        if (principal.getRole() != Role.DOCTOR) {
            throw new RuntimeException("Only DOCTOR can change assigned doctor");
        }

        Patient p = patientRepository.findById(patientId)
                .orElseThrow(() -> new RuntimeException("Patient not found: " + patientId));

        // Enkel policy för nu: doktor får bara ändra patienter som är kopplade till honom
        if (!p.getAssignedDoctorId().equals(principal.getUserId())) {
            throw new RuntimeException("Doctor is not assigned to this patient");
        }

        p.setAssignedDoctorId(newDoctorUserId);
        patientRepository.save(p);
    }

    private void assertCanReadPatient(UserPrincipal principal, Patient p) {
        if (principal.getRole() == Role.DOCTOR || principal.getRole() == Role.STAFF) {
            return;
        }
        if (principal.getRole() == Role.PATIENT && p.getUserId().equals(principal.getUserId())) {
            return;
        }
        throw new RuntimeException("Forbidden");
    }

    private PatientDto toDto(Patient p) {
        PatientDto dto = new PatientDto();
        dto.setId(p.getId());
        dto.setFirstName(p.getFirstName());
        dto.setLastName(p.getLastName());
        dto.setPersonalNumber(p.getPersonalNumber());
        return dto;
    }

    private PatientDetailsDto toDetailsDto(Patient p) {
        PatientDetailsDto dto = new PatientDetailsDto();
        dto.setId(p.getId());
        dto.setFirstName(p.getFirstName());
        dto.setLastName(p.getLastName());
        dto.setPersonalNumber(p.getPersonalNumber());
        dto.setUserId(p.getUserId());
        dto.setAssignedDoctorId(p.getAssignedDoctorId());
        return dto;
    }
}