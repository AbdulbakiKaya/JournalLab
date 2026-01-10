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
                        // LOGIN & REGISTER OPEN
                        .requestMatchers("/api/auth/**").permitAll()

                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/practitioners/doctors").permitAll()

                        // List + skapa patient: bara DOCTOR/STAFF
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/patients").hasAnyRole("DOCTOR", "STAFF")
                        .requestMatchers(org.springframework.http.HttpMethod.POST, "/api/patients").hasAnyRole("DOCTOR", "STAFF")

                        // Details: PATIENT får (men controller blockerar andra än sig själv)
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/patients/*").hasAnyRole("PATIENT", "DOCTOR", "STAFF")
                        .requestMatchers("/api/conditions/**").hasAnyRole("DOCTOR", "STAFF")
                        .requestMatchers("/api/encounters/**").hasAnyRole("DOCTOR", "STAFF")

                        // patient own data
                        .requestMatchers("/api/patient/me/**").hasRole("PATIENT")

                        // all messages requires user login
                        .requestMatchers("/api/messages/**").authenticated()

                        // everything else requires auth
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
