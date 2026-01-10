package journalLabb.config;

import jakarta.annotation.PostConstruct;
import journalLabb.model.*;
import journalLabb.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;

@Configuration
@RequiredArgsConstructor
public class DataSeeder {

    private final UserRepository userRepository;
    private final PatientRepository patientRepository;
    private final PractitionerRepository practitionerRepository;
    private final OrganizationRepository organizationRepository;
    private final ConditionRepository conditionRepository;
    private final EncounterRepository encounterRepository;
    private final ObservationRepository observationRepository;
    private final LocationRepository locationRepository;
    private final MessageRepository messageRepository;
    private final PasswordEncoder passwordEncoder;

    @PostConstruct
    public void seed() {

        if (userRepository.count() > 0) {
            System.out.println("=== SEED ALREADY EXISTS ===");
            return;
        }

        System.out.println("=== SEEDING DATABASE ===");

        // ORGANIZATION
        Organization org = new Organization();
        org.setName("KTH Hospital");
        organizationRepository.save(org);

        // LOCATIONS
        Location loc1 = new Location();
        loc1.setRoom("101A");
        loc1.setDepartment("General Medicine");
        loc1.setOrganization(org);
        locationRepository.save(loc1);

        Location loc2 = new Location();
        loc2.setRoom("202B");
        loc2.setDepartment("Neurology");
        loc2.setOrganization(org);
        locationRepository.save(loc2);

        // DOCTOR USER
        User doctorUser = new User();
        doctorUser.setUsername("doctor1");
        doctorUser.setPasswordHash(passwordEncoder.encode("test123"));
        doctorUser.setRole(Role.DOCTOR);
        userRepository.save(doctorUser);

        // PRACTITIONER DOCTOR
        Practitioner doctor = new Practitioner();
        doctor.setFirstName("John");
        doctor.setLastName("Doe");
        doctor.setLicenseNumber("DOC001");
        doctor.setType(PractitionerType.DOCTOR);
        doctor.setUser(doctorUser);
        doctor.setOrganization(org);
        practitionerRepository.save(doctor);

        // STAFF USER
        User staffUser = new User();
        staffUser.setUsername("staff1");
        staffUser.setPasswordHash(passwordEncoder.encode("test123"));
        staffUser.setRole(Role.STAFF);
        userRepository.save(staffUser);

        // PRACTITIONER STAFF
        Practitioner staff = new Practitioner();
        staff.setFirstName("Anna");
        staff.setLastName("Nurse");
        staff.setLicenseNumber("STF001");
        staff.setType(PractitionerType.STAFF);
        staff.setUser(staffUser);
        staff.setOrganization(org);
        practitionerRepository.save(staff);

        // PATIENTS (alla får assigned doctor)
        Patient karl = createPatient("Karl", "Johansson", "20000101-1234", doctor);
        Patient sara = createPatient("Sara", "Lindholm", "19951212-5678", doctor);
        Patient omar = createPatient("Omar", "Hassan", "19880909-4321", doctor);

        // CONDITIONS (DIAGNOSER) - endast DOCTOR
        createCondition("Diabetes Typ 2", "Måttlig", karl, doctor);
        createCondition("Akut magont", "Allvarlig", sara, doctor);
        createCondition("Kronisk migrän", "Mild", omar, doctor);

        // ENCOUNTERS (BESÖK / JOURNALANTECKNINGAR)
        createEncounter(karl, doctor, loc1, "Första kontroll av diabetes.");
        createEncounter(sara, doctor, loc1, "Undersökning av magont.");
        createEncounter(omar, doctor, loc2, "Uppföljning av migrän.");

        // OBSERVATIONS (MEDICAL NOTES)
        createObservation(karl, doctor, "Patienten har stabilt blodsocker, rekommenderar kostjustering.");
        createObservation(sara, doctor, "Misstänkt IBS. Informerat om kostvanor och vila.");
        createObservation(omar, doctor, "Fortsatt huvudvärk, ska testa ny medicinering.");

        // MESSAGES – seed både DOCTOR- och STAFF-trådar
        seedDoctorThread(karl, doctorUser, karl.getUser());
        seedDoctorThread(sara, doctorUser, sara.getUser());
        seedDoctorThread(omar, doctorUser, omar.getUser());

        // STAFF-tråden: staff initierar så patient kan svara (steg 7 policy A)
        seedStaffThread(karl, staffUser, karl.getUser());
        seedStaffThread(sara, staffUser, sara.getUser());
        seedStaffThread(omar, staffUser, omar.getUser());

        System.out.println("=== SEED COMPLETE ===");
    }

    // ---------------- Helper Methods ----------------

    private Patient createPatient(String first, String last, String pnr, Practitioner assignedDoctor) {
        User u = new User();
        u.setUsername(first.toLowerCase() + ".patient");
        u.setPasswordHash(passwordEncoder.encode("test123"));
        u.setRole(Role.PATIENT);
        userRepository.save(u);

        Patient p = new Patient();
        p.setFirstName(first);
        p.setLastName(last);
        p.setPersonalNumber(pnr);
        p.setUser(u);

        // ✅ NYTT: assigned doctor (viktigt för DOCTOR-tråd)
        p.setAssignedDoctor(assignedDoctor);

        patientRepository.save(p);
        return p;
    }

    private void createCondition(String text, String severity, Patient patient, Practitioner practitionerDoctor) {
        Condition c = new Condition();
        c.setText(text);
        c.setSeverity(severity);
        c.setPatient(patient);
        c.setPractitioner(practitionerDoctor);
        conditionRepository.save(c);
    }

    private Encounter createEncounter(Patient p, Practitioner prac, Location loc, String note) {
        Encounter e = new Encounter();
        e.setPatient(p);
        e.setPractitioner(prac);
        e.setLocation(loc);
        e.setStartTime(LocalDateTime.now().minusDays(2));
        e.setEndTime(LocalDateTime.now().minusDays(2).plusHours(1));
        e.setNote(note);
        return encounterRepository.save(e);
    }

    private void createObservation(Patient p, Practitioner prac, String text) {
        Observation o = new Observation();
        o.setText(text);
        o.setPatient(p);
        o.setPractitioner(prac);
        observationRepository.save(o);
    }

    private void seedDoctorThread(Patient patient, User doctorUser, User patientUser) {
        // PATIENT → DOCTOR (i DOCTOR-tråd ska backend ändå routa till assigned doctor,
        // men i seed sätter vi receiverId till doctorUser för att DB ska se logisk ut)
        createMessage(patientUser.getId(), doctorUser.getId(), patient, MessageThreadType.DOCTOR,
                "Hej doktor, jag har en fråga angående min senaste behandling.", LocalDateTime.now().minusHours(6));

        // DOCTOR → PATIENT
        createMessage(doctorUser.getId(), patientUser.getId(), patient, MessageThreadType.DOCTOR,
                "Självklart! Vad undrar du?", LocalDateTime.now().minusHours(5).minusMinutes(50));

        // PATIENT → DOCTOR
        createMessage(patientUser.getId(), doctorUser.getId(), patient, MessageThreadType.DOCTOR,
                "Behöver jag ta medicinen varje dag eller bara vid behov?", LocalDateTime.now().minusHours(5).minusMinutes(30));
    }

    private void seedStaffThread(Patient patient, User staffUser, User patientUser) {
        // STAFF initierar (så patient kan svara i steg 7 policy A)
        createMessage(staffUser.getId(), patientUser.getId(), patient, MessageThreadType.STAFF,
                "Hej! Jag är från vården. Säg till om du behöver hjälp med bokning eller frågor.", LocalDateTime.now().minusHours(4));

        // PATIENT svarar -> i din backend kommer detta routas till senaste staff automatiskt
        createMessage(patientUser.getId(), staffUser.getId(), patient, MessageThreadType.STAFF,
                "Tack! Jag undrar hur jag bokar om min tid.", LocalDateTime.now().minusHours(3).minusMinutes(45));

        // STAFF svarar
        createMessage(staffUser.getId(), patientUser.getId(), patient, MessageThreadType.STAFF,
                "Du kan boka om via 1177 eller så hjälper vi dig här. Vilken tid passar dig?", LocalDateTime.now().minusHours(3).minusMinutes(30));
    }

    private void createMessage(Long sender, Long receiver, Patient patient, MessageThreadType threadType, String text, LocalDateTime timestamp) {
        Message m = new Message();
        m.setSenderId(sender);
        m.setReceiverId(receiver);
        m.setText(text);
        m.setPatient(patient);
        m.setThreadType(threadType);
        m.setTimestamp(timestamp != null ? timestamp : LocalDateTime.now());
        messageRepository.save(m);
    }
}