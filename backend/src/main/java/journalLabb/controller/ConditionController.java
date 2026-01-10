package journalLabb.controller;

import journalLabb.dto.ConditionDto;
import journalLabb.dto.CreateConditionDto;
import journalLabb.security.UserPrincipal;
import journalLabb.service.ConditionService;
import journalLabb.service.PatientAccessService;
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
    private final PatientAccessService patientAccessService;

    @PreAuthorize("hasAnyRole('DOCTOR','STAFF')")
    @GetMapping("/patient/{patientId}")
    public List<ConditionDto> getForPatient(@PathVariable Long patientId) {
        return conditionService.getConditionsForPatient(patientId);
    }

    @PreAuthorize("hasAnyRole('DOCTOR','STAFF')")
    @PostMapping("/patient/{patientId}")
    public ConditionDto create(
            @PathVariable Long patientId,
            @RequestBody CreateConditionDto dto,
            Authentication authentication
    ) {
        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();

        patientAccessService.assertDoctorCanWrite(principal, patientId);

        Long practitionerId = principal.getPractitionerId();
        return conditionService.createCondition(patientId, practitionerId, dto);
    }
}