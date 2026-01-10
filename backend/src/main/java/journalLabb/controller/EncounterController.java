package journalLabb.controller;

import journalLabb.dto.CreateEncounterDto;
import journalLabb.model.Encounter;
import journalLabb.security.UserPrincipal;
import journalLabb.service.EncounterService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/encounters")
@RequiredArgsConstructor
public class EncounterController {

    private final EncounterService encounterService;

    @PostMapping("/patient/{patientId}")
    @PreAuthorize("hasAnyRole('DOCTOR','STAFF')")
    public Encounter createEncounter(
            @PathVariable Long patientId,
            @RequestBody CreateEncounterDto dto,
            Authentication authentication) {

        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        Long practitionerId = principal.getUserId(); // om du kopplar user → practitioner separat får du ändra detta

        return encounterService.createEncounter(patientId, practitionerId, dto);
    }
}