package journalLabb.config;

import jakarta.annotation.PostConstruct;
import journalLabb.model.Condition;
import journalLabb.model.Encounter;
import journalLabb.model.Role;
import journalLabb.model.User;
import journalLabb.repository.ConditionRepository;
import journalLabb.repository.EncounterRepository;
import journalLabb.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;

@Configuration
@RequiredArgsConstructor
public class DataSeeder {

    private final UserRepository userRepository;
    private final EncounterRepository encounterRepository;
    private final ConditionRepository conditionRepository;
    private final PasswordEncoder passwordEncoder;

    @PostConstruct
    public void seed() {
        if (userRepository.count() > 0) {
            System.out.println("=== SEED ALREADY EXISTS (clinical-service) ===");
            return;
        }

        System.out.println("=== SEEDING DATABASE (clinical-service) ===");

        User doctor1 = createUser("doctor1", "test123", Role.DOCTOR);
        createUser("staff1", "test123", Role.STAFF);

        // (valfritt) patient-users för auth på clinical endpoints om du vill
        createUser("karl.patient", "test123", Role.PATIENT);
        createUser("sara.patient", "test123", Role.PATIENT);
        createUser("omar.patient", "test123", Role.PATIENT);

        // Encounters: patientId 1..3, doctorUserId = doctor1.id (=1)
        createEncounter(1L, doctor1.getId(), "Första kontroll av diabetes.", "101A General Medicine");
        createEncounter(2L, doctor1.getId(), "Undersökning av magont.", "101A General Medicine");
        createEncounter(3L, doctor1.getId(), "Uppföljning av migrän.", "202B Neurology");

        // Conditions
        createCondition(1L, doctor1.getId(), "Diabetes Typ 2", "Måttlig");
        createCondition(2L, doctor1.getId(), "Akut magont", "Allvarlig");
        createCondition(3L, doctor1.getId(), "Kronisk migrän", "Mild");

        System.out.println("=== SEED COMPLETE (clinical-service) ===");
    }

    private User createUser(String username, String rawPassword, Role role) {
        User u = new User();
        u.setUsername(username);
        u.setPasswordHash(passwordEncoder.encode(rawPassword));
        u.setRole(role);
        return userRepository.save(u);
    }

    private void createEncounter(Long patientId, Long doctorUserId, String note, String location) {
        Encounter e = new Encounter();
        e.setPatientId(patientId);
        e.setPractitionerUserId(doctorUserId);
        e.setStartTime(LocalDateTime.now().minusDays(1));
        e.setEndTime(LocalDateTime.now().minusDays(1).plusHours(1));
        e.setNote(note);
        e.setLocation(location);
        encounterRepository.save(e);
    }

    private void createCondition(Long patientId, Long doctorUserId, String text, String severity) {
        Condition c = new Condition();
        c.setPatientId(patientId);
        c.setPractitionerUserId(doctorUserId);
        c.setText(text);
        c.setSeverity(severity);
        c.setCreatedAt(LocalDateTime.now().minusDays(1));
        conditionRepository.save(c);
    }
}