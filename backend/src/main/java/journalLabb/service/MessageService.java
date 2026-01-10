package journalLabb.service;

import journalLabb.dto.MessageDto;
import journalLabb.dto.SendMessageDto;
import journalLabb.model.*;
import journalLabb.repository.MessageRepository;
import journalLabb.repository.PatientRepository;
import journalLabb.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;
    private final PatientRepository patientRepository;
    private final UserRepository userRepository;


    public MessageDto sendMessage(Long senderId, SendMessageDto dto) {
        if (dto.getPatientId() == null) throw new IllegalArgumentException("patientId must not be null");
        if (dto.getText() == null || dto.getText().isBlank()) throw new IllegalArgumentException("text must not be empty");
        if (dto.getThreadType() == null) throw new IllegalArgumentException("threadType must not be null");

        Patient patient = patientRepository.findById(dto.getPatientId())
                .orElseThrow(() -> new RuntimeException("Patient not found"));

        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new RuntimeException("Sender user not found"));

        MessageThreadType threadType = MessageThreadType.valueOf(dto.getThreadType().toUpperCase());

        // -------- Access control --------
        if (sender.getRole() == Role.PATIENT) {
            if (sender.getPatient() == null || !sender.getPatient().getId().equals(patient.getId())) {
                throw new RuntimeException("Forbidden");
            }
        } else if (sender.getRole() == Role.DOCTOR) {
            if (threadType != MessageThreadType.DOCTOR) throw new RuntimeException("Forbidden");
        } else if (sender.getRole() == Role.STAFF) {
            if (threadType != MessageThreadType.STAFF) throw new RuntimeException("Forbidden");
        }

        Long receiverId;

        // -------- Receiver resolution --------
        if (sender.getRole() == Role.PATIENT) {

            if (threadType == MessageThreadType.DOCTOR) {
                // STEG 6: Assigned doctor
                if (patient.getAssignedDoctor() == null) {
                    throw new IllegalStateException("Patient has no assigned doctor");
                }
                if (patient.getAssignedDoctor().getUser() == null) {
                    throw new IllegalStateException("Assigned doctor has no user account");
                }
                receiverId = patient.getAssignedDoctor().getUser().getId();

            } else {
                // STEG 7: STAFF-tråd -> svara senaste STAFF som skrev
                receiverId = resolveLastStaffUserId(patient.getId());

                // Policy: om ingen staff har skrivit än
                if (receiverId == null) {
                    // POLICY A (strikt): staff måste initiera
                    throw new IllegalStateException("No staff conversation exists yet. Staff must message first.");

                    // POLICY B (UX): skicka till en default triage staff-user
                    // receiverId = 123L; // <-- sätt din default staff userId här
                }
            }

        } else {
            // DOCTOR/STAFF -> alltid till patientens user
            if (patient.getUser() == null) throw new IllegalArgumentException("patient has no user account");
            receiverId = patient.getUser().getId();
        }

        // -------- Save --------
        Message m = new Message();
        m.setSenderId(senderId);
        m.setReceiverId(receiverId);
        m.setText(dto.getText());
        m.setPatient(patient);
        m.setThreadType(threadType);

        if (m.getTimestamp() == null) {
            m.setTimestamp(java.time.LocalDateTime.now());
        }

        return toDto(messageRepository.save(m));
    }

    /**
     * Hittar userId för den STAFF som senast skickade ett meddelande i STAFF-tråden.
     * Returnerar null om ingen staff hittas.
     */
    private Long resolveLastStaffUserId(Long patientId) {
        List<Message> thread = messageRepository
                .findByPatient_IdAndThreadTypeOrderByTimestampAsc(patientId, MessageThreadType.STAFF);

        for (int i = thread.size() - 1; i >= 0; i--) {
            Message msg = thread.get(i);
            User u = safeFindUser(msg.getSenderId());
            if (u != null && u.getRole() == Role.STAFF) {
                return u.getId();
            }
        }
        return null;
    }


    public List<MessageDto> getMessagesForPatientByThread(Long requesterUserId, Long patientId, String threadTypeStr) {
        User requester = userRepository.findById(requesterUserId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new RuntimeException("Patient not found"));

        MessageThreadType threadType = MessageThreadType.valueOf(threadTypeStr.toUpperCase());

        // Access rules:
        // - PATIENT: får bara läsa sin egen patient
        // - DOCTOR: får bara läsa DOCTOR-tråden
        // - STAFF: får bara läsa STAFF-tråden
        if (requester.getRole() == Role.PATIENT) {
            if (requester.getPatient() == null || !requester.getPatient().getId().equals(patientId)) {
                throw new RuntimeException("Forbidden");
            }
        } else if (requester.getRole() == Role.DOCTOR) {
            if (threadType != MessageThreadType.DOCTOR) throw new RuntimeException("Forbidden");
        } else if (requester.getRole() == Role.STAFF) {
            if (threadType != MessageThreadType.STAFF) throw new RuntimeException("Forbidden");
        }

        return messageRepository.findByPatient_IdAndThreadTypeOrderByTimestampAsc(patientId, threadType)
                .stream()
                .map(this::toDto)
                .toList();
    }

    public List<MessageDto> getMessagesForUser(Long userId) {
        return messageRepository.findByReceiverId(userId)
                .stream()
                .map(this::toDto)
                .toList();
    }

    public List<MessageDto> getMessagesForPatient(Long patientId) {
        return messageRepository.findByPatient_Id(patientId)
                .stream()
                .map(this::toDto)
                .toList();
    }

    private MessageDto toDto(Message m) {
        MessageDto dto = new MessageDto();
        dto.setId(m.getId());
        dto.setSenderId(m.getSenderId());
        dto.setReceiverId(m.getReceiverId());
        dto.setText(m.getText());
        dto.setTimestamp(m.getTimestamp());

        User sender = safeFindUser(m.getSenderId());
        User receiver = safeFindUser(m.getReceiverId());

        dto.setSenderName(getUserName(sender));
        dto.setReceiverName(getUserName(receiver));

        return dto;
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

    private User safeFindUser(Long id) {
        if (id == null) return null;
        return userRepository.findById(id).orElse(null);
    }

}