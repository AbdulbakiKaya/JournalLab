package journalLabb.controller;

import journalLabb.dto.EncounterDto;
import journalLabb.service.EncounterService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/encounters")
@RequiredArgsConstructor
public class EncounterController {

    private final EncounterService encounterService;

    @PreAuthorize("hasAnyRole('DOCTOR','STAFF')")
    @GetMapping("/patient/{patientId}")
    public List<EncounterDto> byPatient(@PathVariable Long patientId) {
        return encounterService.getEncountersForPatient(patientId);
    }

    // KRAV: “vilka encounters de haft under en dag”
    @PreAuthorize("hasRole('DOCTOR')")
    @GetMapping("/doctor/{doctorUserId}/date/{date}")
    public List<EncounterDto> byDoctorAndDate(@PathVariable Long doctorUserId, @PathVariable String date) {
        return encounterService.getEncountersForDoctorOnDate(doctorUserId, LocalDate.parse(date));
    }

    @PreAuthorize("hasRole('DOCTOR')")
    @PostMapping
    public EncounterDto create(@RequestBody EncounterDto dto) {
        return encounterService.createEncounter(dto);
    }
}