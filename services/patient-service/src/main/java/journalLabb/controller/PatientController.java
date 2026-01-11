package journalLabb.controller;

import journalLabb.dto.PatientDetailsDto;
import journalLabb.dto.PatientDto;
import journalLabb.security.UserPrincipal;
import journalLabb.service.PatientService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/patients")
@RequiredArgsConstructor
public class PatientController {

    private final PatientService patientService;

    @PreAuthorize("hasAnyRole('DOCTOR','STAFF')")
    @GetMapping
    public List<PatientDto> getAll() {
        return patientService.getAllPatients();
    }

    @PreAuthorize("hasAnyRole('DOCTOR','STAFF','PATIENT')")
    @GetMapping("/{patientId}")
    public PatientDetailsDto getById(@PathVariable Long patientId, Authentication authentication) {
        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        return patientService.getPatientById(patientId, principal);
    }

    @PreAuthorize("hasRole('PATIENT')")
    @GetMapping("/me")
    public PatientDetailsDto me(Authentication authentication) {
        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        return patientService.getMyPatient(principal);
    }

    @PreAuthorize("hasAnyRole('DOCTOR','STAFF')")
    @GetMapping("/doctor/{doctorUserId}")
    public List<PatientDto> getForDoctor(@PathVariable Long doctorUserId) {
        return patientService.getPatientsForDoctor(doctorUserId);
    }

    @PreAuthorize("hasRole('DOCTOR')")
    @PutMapping("/{patientId}/assigned-doctor/{doctorUserId}")
    public void changeAssignedDoctor(
            @PathVariable Long patientId,
            @PathVariable Long doctorUserId,
            Authentication authentication
    ) {
        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        patientService.changeAssignedDoctor(patientId, doctorUserId, principal);
    }
}