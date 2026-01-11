package journalLabb.controller;

import journalLabb.dto.PractitionerDto;
import journalLabb.model.PractitionerType;
import journalLabb.repository.PractitionerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/practitioners")
@RequiredArgsConstructor
public class PractitionerController {

    private final PractitionerRepository practitionerRepository;

    @GetMapping("/doctors")
    public List<PractitionerDto> getDoctors() {
        return practitionerRepository.findByType(PractitionerType.DOCTOR)
                .stream()
                .map(p -> {
                    PractitionerDto dto = new PractitionerDto();
                    dto.setId(p.getId());
                    dto.setFirstName(p.getFirstName());
                    dto.setLastName(p.getLastName());
                    dto.setUserId(p.getUserId());
                    dto.setType(p.getType().name());
                    return dto;
                })
                .toList();
    }
}