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

    @PreAuthorize("hasAnyRole('DOCTOR','STAFF')")
    @GetMapping("/patient/{patientId}")
    public List<ConditionDto> forPatient(@PathVariable Long patientId) {
        return conditionService.getConditionsForPatient(patientId);
    }

    @PreAuthorize("hasRole('DOCTOR')")
    @PostMapping
    public ConditionDto create(@RequestBody CreateConditionDto req, Authentication auth) {
        UserPrincipal principal = (UserPrincipal) auth.getPrincipal();
        return conditionService.createCondition(req, principal.getUserId());
    }
}