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

    @GetMapping
    @PreAuthorize("hasAnyRole('DOCTOR','STAFF')")
    public List<PatientDto> getAllPatients() {
        return patientService.getAll();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('PATIENT','DOCTOR','STAFF')")
    public PatientDetailsDto getPatient(
            @PathVariable Long id,
            Authentication authentication) {

        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();

        if (principal.getRole().name().equals("PATIENT")
                && principal.getPatientId() != null
                && !principal.getPatientId().equals(id)) {
            throw new RuntimeException("Not allowed to see other patients");
        }

        return patientService.getDetails(id);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('DOCTOR','STAFF')")
    public PatientDto createPatient(@RequestBody PatientDto patientDto) {
        return patientService.create(patientDto);
    }
}
