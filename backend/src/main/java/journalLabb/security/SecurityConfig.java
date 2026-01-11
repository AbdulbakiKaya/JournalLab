package journalLabb.security;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.http.HttpMethod;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final UserDetailsServiceImpl userDetailsService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        return http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> {})

                .sessionManagement(sess ->
                        sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                .authorizeHttpRequests(auth -> auth
                        // ✅ CORS preflight måste vara öppen, annars får du 401 på OPTIONS
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // LOGIN & REGISTER OPEN
                        .requestMatchers(HttpMethod.POST, "/api/auth/register").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/auth/me").authenticated()

                        // Doctors-lista kräver inlogg (och @PreAuthorize tar rollerna)
                        .requestMatchers(HttpMethod.GET, "/api/practitioners/doctors", "/api/practitioners/doctors/**").permitAll()

                        // List + skapa patient: bara DOCTOR/STAFF
                        .requestMatchers(HttpMethod.GET, "/api/patients").hasAnyRole("DOCTOR", "STAFF")
                        .requestMatchers(HttpMethod.POST, "/api/patients").hasAnyRole("DOCTOR", "STAFF")

                        // Details
                        .requestMatchers(HttpMethod.GET, "/api/patients/*").hasAnyRole("PATIENT", "DOCTOR", "STAFF")

                        // CONDITIONS
                        .requestMatchers(HttpMethod.POST, "/api/conditions/**").hasAnyRole("DOCTOR", "STAFF")
                        .requestMatchers(HttpMethod.PUT,  "/api/conditions/**").hasAnyRole("DOCTOR", "STAFF")
                        .requestMatchers(HttpMethod.DELETE,"/api/conditions/**").hasRole("DOCTOR")
                        .requestMatchers(HttpMethod.GET, "/api/conditions/**").hasAnyRole("PATIENT", "DOCTOR", "STAFF")

                        // ENCOUNTERS
                        .requestMatchers(HttpMethod.POST, "/api/encounters/**").hasAnyRole("DOCTOR", "STAFF")
                        .requestMatchers(HttpMethod.PUT,  "/api/encounters/**").hasAnyRole("DOCTOR", "STAFF")
                        .requestMatchers(HttpMethod.DELETE,"/api/encounters/**").hasAnyRole("DOCTOR", "STAFF")
                        .requestMatchers(HttpMethod.GET, "/api/encounters/**").hasAnyRole("PATIENT", "DOCTOR", "STAFF")

                        .requestMatchers("/api/patient/me/**").hasRole("PATIENT")

                        // all messages requires user login
                        .requestMatchers("/api/messages/**").authenticated()

                        .anyRequest().authenticated()
                )

                .httpBasic(h -> h
                        .authenticationEntryPoint((req, res, ex) -> {
                            res.addHeader("X-Role", "NONE");
                            res.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
                        })
                )

                .authenticationProvider(authenticationProvider())
                .build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
            throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
