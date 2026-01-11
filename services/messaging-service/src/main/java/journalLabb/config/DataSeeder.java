package journalLabb.config;

import jakarta.annotation.PostConstruct;
import journalLabb.model.Message;
import journalLabb.model.MessageThreadType;
import journalLabb.model.Role;
import journalLabb.model.User;
import journalLabb.repository.MessageRepository;
import journalLabb.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;

@Configuration
@RequiredArgsConstructor
public class DataSeeder {

    private final UserRepository userRepository;
    private final MessageRepository messageRepository;
    private final PasswordEncoder passwordEncoder;

    @PostConstruct
    public void seed() {

        // Seed bara första gången
        if (userRepository.count() > 0) {
            System.out.println("=== SEED ALREADY EXISTS (messaging-service) ===");
            return;
        }

        System.out.println("=== SEEDING DATABASE (messaging-service) ===");

        // ---------------- Users ----------------
        User doctorUser = createUser("doctor1", "test123", Role.DOCTOR);
        User staffUser  = createUser("staff1",  "test123", Role.STAFF);

        // Patient users (som i din gamla seeder)
        User karlUser = createUser("karl.patient", "test123", Role.PATIENT);
        User saraUser = createUser("sara.patient", "test123", Role.PATIENT);
        User omarUser = createUser("omar.patient", "test123", Role.PATIENT);

        // ---------------- Patient IDs ----------------
        // I messaging-service känner vi inte till Patient-entity.
        // Vi använder bara patientId. Dessa är "logiska IDs" som matchar patient-service senare.
        Long karlPatientId = 1L;
        Long saraPatientId = 2L;
        Long omarPatientId = 3L;

        // ---------------- Messages ----------------
        // DOCTOR-trådar
        seedDoctorThread(karlPatientId, doctorUser, karlUser);
        seedDoctorThread(saraPatientId, doctorUser, saraUser);
        seedDoctorThread(omarPatientId, doctorUser, omarUser);

        // STAFF-trådar
        seedStaffThread(karlPatientId, staffUser, karlUser);
        seedStaffThread(saraPatientId, staffUser, saraUser);
        seedStaffThread(omarPatientId, staffUser, omarUser);

        System.out.println("=== SEED COMPLETE (messaging-service) ===");
    }

    // ---------------- Helper Methods ----------------

    private User createUser(String username, String rawPassword, Role role) {
        User u = new User();
        u.setUsername(username);
        u.setPasswordHash(passwordEncoder.encode(rawPassword));
        u.setRole(role);
        return userRepository.save(u);
    }

    private void seedDoctorThread(Long patientId, User doctorUser, User patientUser) {
        // PATIENT -> DOCTOR
        createMessage(
                patientUser.getId(),
                doctorUser.getId(),
                patientId,
                MessageThreadType.DOCTOR,
                "Hej doktor, jag har en fråga angående min senaste behandling.",
                LocalDateTime.now().minusHours(6)
        );

        // DOCTOR -> PATIENT
        createMessage(
                doctorUser.getId(),
                patientUser.getId(),
                patientId,
                MessageThreadType.DOCTOR,
                "Självklart! Vad undrar du?",
                LocalDateTime.now().minusHours(5).minusMinutes(50)
        );

        // PATIENT -> DOCTOR
        createMessage(
                patientUser.getId(),
                doctorUser.getId(),
                patientId,
                MessageThreadType.DOCTOR,
                "Behöver jag ta medicinen varje dag eller bara vid behov?",
                LocalDateTime.now().minusHours(5).minusMinutes(30)
        );
    }

    private void seedStaffThread(Long patientId, User staffUser, User patientUser) {
        // STAFF initierar (policy A)
        createMessage(
                staffUser.getId(),
                patientUser.getId(),
                patientId,
                MessageThreadType.STAFF,
                "Hej! Jag är från vården. Säg till om du behöver hjälp med bokning eller frågor.",
                LocalDateTime.now().minusHours(4)
        );

        // PATIENT -> STAFF
        createMessage(
                patientUser.getId(),
                staffUser.getId(),
                patientId,
                MessageThreadType.STAFF,
                "Tack! Jag undrar hur jag bokar om min tid.",
                LocalDateTime.now().minusHours(3).minusMinutes(45)
        );

        // STAFF -> PATIENT
        createMessage(
                staffUser.getId(),
                patientUser.getId(),
                patientId,
                MessageThreadType.STAFF,
                "Du kan boka om via 1177 eller så hjälper vi dig här. Vilken tid passar dig?",
                LocalDateTime.now().minusHours(3).minusMinutes(30)
        );
    }

    private void createMessage(Long senderId,
                               Long receiverId,
                               Long patientId,
                               MessageThreadType threadType,
                               String text,
                               LocalDateTime timestamp) {

        Message m = new Message();
        m.setSenderId(senderId);
        m.setReceiverId(receiverId);
        m.setPatientId(patientId); // <-- VIKTIGT: patientId, inte Patient-entity
        m.setThreadType(threadType);
        m.setText(text);
        m.setTimestamp(timestamp != null ? timestamp : LocalDateTime.now());

        messageRepository.save(m);
    }
}