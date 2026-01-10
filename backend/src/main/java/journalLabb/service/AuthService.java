package journalLabb.service;

import journalLabb.dto.RegisterDto;
import journalLabb.model.*;
import journalLabb.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PatientRepository patientRepository;
    private final PractitionerRepository practitionerRepository;
    private final PasswordEncoder passwordEncoder;

    public User register(RegisterDto dto) {

        if (userRepository.existsByUsername(dto.getUsername())) {
            throw new RuntimeException("Username already exists");
        }

        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPasswordHash(passwordEncoder.encode(dto.getPassword()));
        user.setRole(Role.valueOf(dto.getRole()));

        userRepository.save(user);

        // CREATE PATIENT
        if (dto.getRole().equals("PATIENT")) {
            Patient p = new Patient();
            p.setFirstName(dto.getFirstName());
            p.setLastName(dto.getLastName());
            p.setPersonalNumber(dto.getPersonalNumber());
            p.setUser(user);
            patientRepository.save(p);
        }

        // CREATE PRACTITIONER (DOCTOR or STAFF)
        if (dto.getRole().equals("DOCTOR") || dto.getRole().equals("STAFF")) {
            Practitioner pr = new Practitioner();
            pr.setFirstName(dto.getPractitionerFirstName());
            pr.setLastName(dto.getPractitionerLastName());
            pr.setLicenseNumber(dto.getLicenseNumber());
            pr.setUser(user);
            pr.setType(
                    dto.getRole().equals("DOCTOR")
                            ? PractitionerType.DOCTOR
                            : PractitionerType.STAFF
            );
            practitionerRepository.save(pr);
        }

        return user;
    }
}
