package journalLabb.controller;

import journalLabb.dto.ConditionDto;
import journalLabb.dto.CreateConditionDto;
import journalLabb.security.UserPrincipal;
import journalLabb.service.ConditionService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/conditions")
@RequiredArgsConstructor
public class ConditionController {

    private final ConditionService conditionService;

    @PostMapping("/patient/{patientId}")
    @PreAuthorize("hasAnyRole('DOCTOR','STAFF')")
    public ConditionDto create(
            @PathVariable Long patientId,
            @RequestBody CreateConditionDto dto,
            Authentication authentication
    ) {
        return conditionService.createCondition(patientId, dto);
    }

    @GetMapping("/patient/{patientId}")
    @PreAuthorize("hasAnyRole('PATIENT','DOCTOR','STAFF')")
    public List<ConditionDto> getByPatient(
            @PathVariable Long patientId,
            Authentication authentication
    ) {
        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();

        // Patienten får bara se sina egna diagnoser
        if (principal.getRole().name().equals("PATIENT") &&
                !principal.getPatientId().equals(patientId)) {
            throw new RuntimeException("Not allowed");
        }

        return conditionService.getConditionsForPatient(patientId);
    }
}