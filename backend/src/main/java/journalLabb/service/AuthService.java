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

        Role role = Role.valueOf(dto.getRole().toUpperCase());

        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPasswordHash(passwordEncoder.encode(dto.getPassword()));
        user.setRole(role);

        userRepository.save(user);

        // CREATE PATIENT
        if (role == Role.PATIENT) {

            if (dto.getAssignedDoctorPractitionerId() == null) {
                throw new IllegalArgumentException("assignedDoctorPractitionerId is required for PATIENT");
            }

            Practitioner doctor = practitionerRepository.findById(dto.getAssignedDoctorPractitionerId())
                    .orElseThrow(() -> new RuntimeException("Assigned doctor not found"));

            if (doctor.getType() != PractitionerType.DOCTOR) {
                throw new IllegalArgumentException("Assigned practitioner must be a DOCTOR");
            }

            Patient p = new Patient();
            p.setFirstName(dto.getFirstName());
            p.setLastName(dto.getLastName());
            p.setPersonalNumber(dto.getPersonalNumber());
            p.setUser(user);

            p.setAssignedDoctor(doctor);

            patientRepository.save(p);
        }

        if (role == Role.DOCTOR || role == Role.STAFF) {

            Practitioner pr = new Practitioner();
            pr.setFirstName(dto.getPractitionerFirstName());
            pr.setLastName(dto.getPractitionerLastName());
            pr.setLicenseNumber(dto.getLicenseNumber());
            pr.setUser(user);
            pr.setType(role == Role.DOCTOR ? PractitionerType.DOCTOR : PractitionerType.STAFF);

            practitionerRepository.save(pr);
        }

        return user;
    }
}