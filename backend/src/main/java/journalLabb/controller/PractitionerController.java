package journalLabb.controller;

import journalLabb.model.Practitioner;
import journalLabb.model.PractitionerType;
import journalLabb.repository.PractitionerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/practitioners")
@RequiredArgsConstructor
public class PractitionerController {

    private final PractitionerRepository practitionerRepository;

    @GetMapping("/doctors")
    public List<Map<String, Object>> getDoctors() {
        return practitionerRepository.findByType(PractitionerType.DOCTOR)
                .stream()
                .map(this::toDoctorOption)
                .toList();
    }

    private Map<String, Object> toDoctorOption(Practitioner p) {
        Map<String, Object> m = new HashMap<>();
        m.put("id", p.getId());
        m.put("firstName", p.getFirstName());
        m.put("lastName", p.getLastName());
        return m;
    }
}