package journalLabb.controller;


import journalLabb.dto.ConditionDto;
import journalLabb.dto.CreateConditionDto;
import journalLabb.dto.ObservationDto;
import journalLabb.model.Observation;
import journalLabb.security.UserPrincipal;
import journalLabb.service.ConditionService;
import journalLabb.service.ObservationService;
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


    @PostMapping("/condition/{patientId}")
    @PreAuthorize("hasAnyRole('DOCTOR','STAFF')")
    public ConditionDto createCondition(@PathVariable Long patientId,
                                        @RequestBody CreateConditionDto dto,
                                        Authentication authentication) {

        return conditionService.createCondition(patientId, dto);
    }



    @PostMapping("/observation")
    @PreAuthorize("hasAnyRole('DOCTOR','STAFF')")
    public Observation createObservation(@RequestBody ObservationDto dto,
                                         Authentication authentication) {
        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        return observationService.create(principal.getUserId(), dto);
    }
}