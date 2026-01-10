package journalLabb.service;

import journalLabb.dto.MessageDto;
import journalLabb.dto.SendMessageDto;
import journalLabb.model.Message;
import journalLabb.model.Patient;
import journalLabb.model.User;
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

        if (dto.getPatientId() == null) {
            throw new IllegalArgumentException("patientId must not be null");
        }
        if (dto.getText() == null || dto.getText().isBlank()) {
            throw new IllegalArgumentException("text must not be empty");
        }

        Patient patient = patientRepository.findById(dto.getPatientId())
                .orElseThrow(() -> new RuntimeException("Patient not found"));

        Long receiverId = dto.getReceiverId();
        if (receiverId == null) {
            if (patient.getUser() == null) {
                throw new IllegalArgumentException("patient has no user account");
            }
            receiverId = patient.getUser().getId();
        }

        Message m = new Message();
        m.setSenderId(senderId);
        m.setReceiverId(receiverId);
        m.setText(dto.getText());
        m.setPatient(patient);

        return toDto(messageRepository.save(m));
    }

    public List<MessageDto> getMessagesForUser(Long userId) {
        return messageRepository.findByReceiverId(userId)
                .stream()
                .map(this::toDto)
                .toList();
    }

    public List<MessageDto> getMessagesForPatient(Long patientId) {
        return messageRepository.findByPatientId(patientId)
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