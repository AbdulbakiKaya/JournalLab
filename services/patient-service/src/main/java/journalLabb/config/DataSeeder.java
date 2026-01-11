package journalLabb.config;

import jakarta.annotation.PostConstruct;
import journalLabb.model.Patient;
import journalLabb.model.Role;
import journalLabb.model.User;
import journalLabb.repository.PatientRepository;
import journalLabb.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class DataSeeder {

    private final UserRepository userRepository;
    private final PatientRepository patientRepository;
    private final PasswordEncoder passwordEncoder;

    @PostConstruct
    public void seed() {
        if (userRepository.count() > 0) {
            System.out.println("=== SEED ALREADY EXISTS (patient-service) ===");
            return;
        }

        System.out.println("=== SEEDING DATABASE (patient-service) ===");

        User doctor1 = createUser("doctor1", "test123", Role.DOCTOR);
        createUser("staff1", "test123", Role.STAFF);

        User karlU = createUser("karl.patient", "test123", Role.PATIENT);
        User saraU = createUser("sara.patient", "test123", Role.PATIENT);
        User omarU = createUser("omar.patient", "test123", Role.PATIENT);

        createPatient("Karl", "Johansson", "20000101-1234", karlU.getId(), doctor1.getId());
        createPatient("Sara", "Lindholm", "19951212-5678", saraU.getId(), doctor1.getId());
        createPatient("Omar", "Hassan", "19880909-4321", omarU.getId(), doctor1.getId());

        System.out.println("=== SEED COMPLETE (patient-service) ===");
    }

    private User createUser(String username, String rawPassword, Role role) {
        User u = new User();
        u.setUsername(username);
        u.setPasswordHash(passwordEncoder.encode(rawPassword));
        u.setRole(role);
        return userRepository.save(u);
    }

    private void createPatient(String first, String last, String pnr, Long userId, Long assignedDoctorId) {
        Patient p = new Patient();
        p.setFirstName(first);
        p.setLastName(last);
        p.setPersonalNumber(pnr);
        p.setUserId(userId);
        p.setAssignedDoctorId(assignedDoctorId);
        patientRepository.save(p);
    }
}