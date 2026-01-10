package journalLabb.controller;


import journalLabb.dto.ConditionDto;
import journalLabb.dto.CreateConditionDto;
import journalLabb.dto.ObservationDto;
import journalLabb.model.Observation;
import journalLabb.security.UserPrincipal;
import journalLabb.service.ConditionService;
import journalLabb.service.ObservationService;
import journalLabb.service.PatientAccessService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping("/api/notes")
@RequiredArgsConstructor
public class MedicalNoteController {


    private final ConditionService conditionService;
    private final ObservationService observationService;
    private final PatientAccessService patientAccessService;


    @PreAuthorize("hasAnyRole('DOCTOR','STAFF')")
    @PostMapping("/condition/{patientId}")
    public ConditionDto createCondition(
            @PathVariable Long patientId,
            @RequestBody CreateConditionDto dto,
            Authentication authentication
    ) {
        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();

        patientAccessService.assertDoctorCanWrite(principal, patientId);

        Long practitionerId = principal.getPractitionerId();

        return conditionService.createCondition(patientId, practitionerId, dto);
    }



    @PreAuthorize("hasAnyRole('DOCTOR','STAFF')")
    @PostMapping("/observation")
    public Observation createObservation(
            @RequestBody ObservationDto dto,
            Authentication authentication
    ) {
        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();

        // Kräver att ObservationDto har getPatientId()
        patientAccessService.assertDoctorCanWrite(principal, dto.getPatientId());

        Long practitionerId = principal.getPractitionerId();

        return observationService.create(practitionerId, dto);
    }
}