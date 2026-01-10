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


        // PATIENTS
        Patient karl = createPatient("Karl", "Johansson", "20000101-1234");
        Patient sara = createPatient("Sara", "Lindholm", "19951212-5678");
        Patient omar = createPatient("Omar", "Hassan", "19880909-4321");


        // CONDITIONS (DIAGNOSER)
        createCondition("Diabetes Typ 2", "Måttlig" ,karl, doctor);
        createCondition("Akut magont","Allvarlig" , sara, doctor);
        createCondition("Kronisk migrän", "Mild" ,omar, doctor);


        // ENCOUNTERS (BESÖK)
        Encounter enc1 = createEncounter(karl, doctor, loc1, "Första kontroll av diabetes.");
        Encounter enc2 = createEncounter(sara, doctor, loc1, "Undersökning av magont.");
        Encounter enc3 = createEncounter(omar, doctor, loc2, "Uppföljning av migrän.");


        // OBSERVATIONS (MEDICAL NOTES)
        createObservation(karl, doctor, "Patienten har stabilt blodsocker, rekommenderar kostjustering.");
        createObservation(sara, doctor, "Misstänkt IBS. Informerat om kostvanor och vila.");
        createObservation(omar, doctor, "Fortsatt huvudvärk, ska testa ny medicinering.");


        // MESSAGES – patient ↔ doctor
        seedMessages(karl, doctorUser, karl.getUser());
        seedMessages(sara, doctorUser, sara.getUser());
        seedMessages(omar, doctorUser, omar.getUser());

        System.out.println("=== SEED COMPLETE ===");
    }

    // ---------------- Helper Methods ----------------

    private Patient createPatient(String first, String last, String pnr) {
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
        patientRepository.save(p);

        return p;
    }

    private void createCondition(String text, String severity, Patient patient, Practitioner practitioner) {
        Condition c = new Condition();
        c.setText(text);
        c.setSeverity(severity);
        c.setPatient(patient);
        c.setPractitioner(practitioner);
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

    private void seedMessages(Patient patient, User doctorUser, User patientUser) {

        // PATIENT → DOCTOR
        createMessage(patientUser.getId(), doctorUser.getId(), patient,
                "Hej doktor, jag har en fråga angående min senaste behandling.");

        // DOCTOR → PATIENT
        createMessage(doctorUser.getId(), patientUser.getId(), patient,
                "Självklart! Vad undrar du?");

        // PATIENT → DOCTOR
        createMessage(patientUser.getId(), doctorUser.getId(), patient,
                "Behöver jag ta medicinen varje dag eller bara vid behov?");
    }

    private void createMessage(Long sender, Long receiver, Patient patient, String text) {
        Message m = new Message();
        m.setSenderId(sender);
        m.setReceiverId(receiver);
        m.setText(text);
        m.setPatient(patient);
        messageRepository.save(m);
    }
}