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

        Patient patient = patientRepository.findById(dto.getPatientId())
                .orElseThrow(() -> new RuntimeException("Patient not found"));

        Message m = new Message();
        m.setSenderId(senderId);
        m.setReceiverId(dto.getReceiverId());
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

        // 🌟 NEW: senderName + receiverName
        dto.setSenderName(
                userRepository.findById(m.getSenderId())
                        .map(u -> {
                            if (u.getPatient() != null)
                                return u.getPatient().getFirstName() + " " + u.getPatient().getLastName();
                            if (u.getPractitioner() != null)
                                return u.getPractitioner().getFirstName() + " " + u.getPractitioner().getLastName();
                            return u.getUsername();
                        })
                        .orElse("Okänd")
        );

        dto.setReceiverName(
                userRepository.findById(m.getReceiverId())
                        .map(u -> {
                            if (u.getPatient() != null)
                                return u.getPatient().getFirstName() + " " + u.getPatient().getLastName();
                            if (u.getPractitioner() != null)
                                return u.getPractitioner().getFirstName() + " " + u.getPractitioner().getLastName();
                            return u.getUsername();
                        })
                        .orElse("Okänd")
        );

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
}