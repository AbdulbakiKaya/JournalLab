package journalLabb.controller;

import journalLabb.dto.CreateEncounterDto;
import journalLabb.model.Encounter;
import journalLabb.security.UserPrincipal;
import journalLabb.service.EncounterService;
import journalLabb.service.PatientAccessService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/encounters")
@RequiredArgsConstructor
public class EncounterController {

    private final EncounterService encounterService;
    private final PatientAccessService patientAccessService;

    @PreAuthorize("hasAnyRole('DOCTOR','STAFF')")
    @PostMapping("/patient/{patientId}")
    public Encounter createEncounter(
            @PathVariable Long patientId,
            @RequestBody CreateEncounterDto dto,
            Authentication authentication) {

        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();

        patientAccessService.assertDoctorCanWrite(principal, patientId);

        Long practitionerId = principal.getPractitionerId();

        return encounterService.createEncounter(patientId, practitionerId, dto);
    }
}