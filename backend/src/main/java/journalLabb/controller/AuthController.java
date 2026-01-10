package journalLabb.controller;

import jakarta.servlet.http.HttpServletResponse;
import journalLabb.dto.RegisterDto;
import journalLabb.model.User;
import journalLabb.security.UserPrincipal;
import journalLabb.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @GetMapping("/me")
    public String getMe(
            @AuthenticationPrincipal UserPrincipal principal,
            HttpServletResponse response
    ) {
        response.addHeader("X-Role", principal.getRole().name());

        response.addHeader("X-UserId", principal.getUserId().toString());

        return principal.getUsername();
    }


    @PostMapping("/register")
    public User register(@RequestBody RegisterDto dto) {
        return authService.register(dto);
    }
}
