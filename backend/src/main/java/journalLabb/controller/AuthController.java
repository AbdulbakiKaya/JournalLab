package journalLabb.controller;

import jakarta.servlet.http.HttpServletResponse;
import journalLabb.dto.RegisterDto;
import journalLabb.security.UserPrincipal;
import journalLabb.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @GetMapping("/me")
    public java.util.Map<String, Object> getMe(
            @AuthenticationPrincipal UserPrincipal principal,
            HttpServletResponse response
    ) {
        response.addHeader("X-Role", principal.getRole().name());
        response.addHeader("X-UserId", principal.getUserId().toString());

        Long patientId = principal.getPatientId(); // null för DOCTOR/STAFF är OK

        if (patientId != null) {
            response.addHeader("X-PatientId", patientId.toString());
        }

        java.util.Map<String, Object> map = new java.util.HashMap<>();
        map.put("username", principal.getUsername());
        map.put("role", principal.getRole().name());
        map.put("userId", principal.getUserId());
        map.put("patientId", patientId);
        map.put("practitionerId", principal.getPractitionerId());
        return map;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterDto dto) {
        var created = authService.register(dto);

        // Returnera en “safe” payload (inte hela User-objektet)
        return ResponseEntity.ok(Map.of(
                "id", created.getId(),
                "username", created.getUsername(),
                "role", created.getRole().name()
        ));
    }
}
