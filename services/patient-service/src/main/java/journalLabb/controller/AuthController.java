package journalLabb.controller;

import jakarta.servlet.http.HttpServletResponse;
import journalLabb.dto.RegisterDto;
import journalLabb.repository.PatientRepository;
import journalLabb.repository.PractitionerRepository;
import journalLabb.security.UserPrincipal;
import journalLabb.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final PatientRepository patientRepository;
    private final PractitionerRepository practitionerRepository;

    @GetMapping("/me")
    public Map<String, Object> getMe(
            @AuthenticationPrincipal UserPrincipal principal,
            HttpServletResponse response
    ) {
        response.addHeader("X-Role", principal.getRole().name());
        response.addHeader("X-UserId", principal.getUserId().toString());

        Long patientId = patientRepository.findByUserId(principal.getUserId())
                .map(p -> p.getId())
                .orElse(null);

        if (patientId != null) {
            response.addHeader("X-PatientId", patientId.toString());
        }

        Long practitionerId = practitionerRepository.findByUserId(principal.getUserId())
                .map(p -> p.getId())
                .orElse(null);

        if (practitionerId != null) {
            response.addHeader("X-PractitionerId", practitionerId.toString());
        }

        Map<String, Object> map = new HashMap<>();
        map.put("username", principal.getUsername());
        map.put("role", principal.getRole().name());
        map.put("userId", principal.getUserId());
        map.put("patientId", patientId);
        map.put("practitionerId", practitionerId);
        return map;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterDto dto) {
        var created = authService.register(dto);
        return ResponseEntity.ok(Map.of(
                "id", created.getId(),
                "username", created.getUsername(),
                "role", created.getRole().name()
        ));
    }
}