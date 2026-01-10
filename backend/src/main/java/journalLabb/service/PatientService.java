package journalLabb.service;

import journalLabb.dto.MessageDto;
import journalLabb.dto.PatientDetailsDto;
import journalLabb.dto.PatientDto;
import journalLabb.model.Patient;
import journalLabb.repository.MessageRepository;
import journalLabb.repository.PatientRepository;
import journalLabb.repository.UserRepository;
import journalLabb.repository.PractitionerRepository;
import journalLabb.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PatientService {

    private final PatientRepository patientRepository;
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final PractitionerRepository practitionerRepository;

    public List<PatientDto> getAll() {
        return patientRepository.findAll()
                .stream()
                .map(this::toPatientDto)
                .toList();
    }

    public PatientDetailsDto getDetails(Long id) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Patient not found"));

        PatientDetailsDto dto = toPatientDetailsDto(patient);

        // Load messages
        dto.setMessages(
                messageRepository.findByPatient_Id(id)
                        .stream()
                        .map(m -> {
                            MessageDto md = new MessageDto();
                            md.setId(m.getId());
                            md.setSenderId(m.getSenderId());
                            md.setReceiverId(m.getReceiverId());
                            md.setText(m.getText());
                            md.setTimestamp(m.getTimestamp());
                            User sender = safeFindUser(m.getSenderId());
                            User receiver = safeFindUser(m.getReceiverId());
                            md.setSenderName(getUserName(sender));
                            md.setReceiverName(getUserName(receiver));
                            return md;
                        })
                        .toList()
        );

        return dto;
    }

    public PatientDto create(PatientDto dto) {
        Patient patient = new Patient();
        patient.setFirstName(dto.getFirstName());
        patient.setLastName(dto.getLastName());
        Patient saved = patientRepository.save(patient);
        return toPatientDto(saved);
    }
    public void changeAssignedDoctor(Long patientId, Long doctorId) {
        var patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new RuntimeException("Patient not found"));

        var doctor = practitionerRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Practitioner not found"));

        if (doctor.getType() == null || !doctor.getType().name().equals("DOCTOR")) {
            throw new RuntimeException("Selected practitioner is not a doctor");
        }

        patient.setAssignedDoctor(doctor);
        patientRepository.save(patient);
    }

    private String getUserName(User user) {
        if (user == null) return "Okänd";

        if (user.getPractitioner() != null) {
            return user.getPractitioner().getFirstName() + " " + user.getPractitioner().getLastName();
        }

        if (user.getPatient() != null) {
            return user.getPatient().getFirstName() + " " + user.getPatient().getLastName();
        }

        return user.getUsername();
    }

    private PatientDto toPatientDto(Patient p) {
        PatientDto dto = new PatientDto();
        dto.setId(p.getId());
        dto.setFirstName(p.getFirstName());
        dto.setLastName(p.getLastName());
        return dto;
    }

    private User safeFindUser(Long id) {
        if (id == null) return null;
        return userRepository.findById(id).orElse(null);
    }

    private PatientDetailsDto toPatientDetailsDto(Patient p) {
        PatientDetailsDto dto = new PatientDetailsDto();
        dto.setId(p.getId());
        dto.setUserId(p.getUser() != null ? p.getUser().getId() : null);
        dto.setFirstName(p.getFirstName());
        dto.setLastName(p.getLastName());

        // ✅ ASSIGNED DOCTOR
        var ad = p.getAssignedDoctor();
        dto.setAssignedDoctorId(ad != null ? ad.getId() : null);
        dto.setAssignedDoctorName(ad != null ? (ad.getFirstName() + " " + ad.getLastName()) : "Ej tilldelad");

        // CONDITIONS
        dto.setConditions(
                p.getConditions() == null ? List.of() :
                        p.getConditions().stream()
                                .map(c -> {
                                    Map<String, Object> map = new HashMap<>();
                                    map.put("id", c.getId());
                                    map.put("text", c.getText());
                                    map.put("severity", c.getSeverity());

                                    if (c.getPractitioner() != null) {
                                        map.put("practitionerName",
                                                c.getPractitioner().getFirstName() + " " + c.getPractitioner().getLastName());

                                        map.put("practitionerType",
                                                c.getPractitioner().getType() != null ? c.getPractitioner().getType().name() : "UNKNOWN");
                                    } else {
                                        map.put("practitionerName", "Okänd");
                                        map.put("practitionerType", "UNKNOWN");
                                    }
                                    return map;
                                })
                                .toList()
        );

        // ENCOUNTERS
        dto.setEncounters(
                p.getEncounters() == null ? List.of() :
                        p.getEncounters().stream()
                                .map(e -> {
                                    Map<String, Object> map = new HashMap<>();
                                    map.put("id", e.getId());
                                    map.put("startTime", e.getStartTime());
                                    map.put("note", e.getNote());

                                    if (e.getPractitioner() != null) {
                                        map.put("practitionerName",
                                                e.getPractitioner().getFirstName() + " " + e.getPractitioner().getLastName());

                                        map.put("practitionerType",
                                                e.getPractitioner().getType() != null ? e.getPractitioner().getType().name() : "UNKNOWN");
                                    } else {
                                        map.put("practitionerName", "Okänd");
                                        map.put("practitionerType", "UNKNOWN");
                                    }
                                    return map;
                                })
                                .toList()
        );
        return dto;
    }
}