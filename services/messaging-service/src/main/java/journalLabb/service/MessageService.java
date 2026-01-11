package journalLabb.service;

import journalLabb.dto.MessageDto;
import journalLabb.dto.SendMessageDto;
import journalLabb.model.Message;
import journalLabb.model.MessageThreadType;
import journalLabb.model.Role;
import journalLabb.model.User;
import journalLabb.repository.MessageRepository;
import journalLabb.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;

    public MessageDto sendMessage(Long senderId, SendMessageDto dto) {
        MessageThreadType threadType = MessageThreadType.valueOf(dto.getThreadType().toUpperCase());

        Long receiverId = dto.getReceiverId();
        if (receiverId == null) {
            // Om du vill auto-routa STAFF-tråd till senaste staff, behåll logiken.
            // Annars kräver du receiverId i DTO.
            if (threadType == MessageThreadType.STAFF) {
                receiverId = findLastStaffInStaffThread(dto.getPatientId());
            }
        }

        if (receiverId == null) {
            throw new RuntimeException("receiverId saknas (kunde inte routa automatiskt).");
        }

        Message m = new Message();
        m.setSenderId(senderId);
        m.setReceiverId(receiverId);
        m.setPatientId(dto.getPatientId());
        m.setThreadType(threadType);
        m.setText(dto.getText());
        m.setTimestamp(LocalDateTime.now());

        return toDto(messageRepository.save(m));
    }

    public List<MessageDto> getMessagesForPatientByThread(Long patientId, String threadType) {
        MessageThreadType type = MessageThreadType.valueOf(threadType.toUpperCase());
        return messageRepository.findByPatientIdAndThreadTypeOrderByTimestampAsc(patientId, type)
                .stream()
                .map(this::toDto)
                .toList();
    }

    public List<MessageDto> getMessagesForUser(Long userId) {
        return messageRepository.findBySenderIdOrReceiverIdOrderByTimestampAsc(userId, userId)
                .stream()
                .map(this::toDto)
                .toList();
    }

    private Long findLastStaffInStaffThread(Long patientId) {
        List<Message> thread = messageRepository
                .findByPatientIdAndThreadTypeOrderByTimestampAsc(patientId, MessageThreadType.STAFF);

        for (int i = thread.size() - 1; i >= 0; i--) {
            Message msg = thread.get(i);
            User u = safeFindUser(msg.getSenderId());
            if (u != null && u.getRole() == Role.STAFF) {
                return u.getId();
            }
        }
        return null;
    }

    private MessageDto toDto(Message m) {
        MessageDto dto = new MessageDto();
        dto.setId(m.getId());
        dto.setPatientId(m.getPatientId());
        dto.setSenderId(m.getSenderId());
        dto.setReceiverId(m.getReceiverId());
        dto.setText(m.getText());
        dto.setTimestamp(m.getTimestamp());
        dto.setThreadType(m.getThreadType().name());

        dto.setSenderName(resolveName(m.getSenderId()));
        dto.setReceiverName(resolveName(m.getReceiverId()));

        return dto;
    }

    private String resolveName(Long userId) {
        User u = safeFindUser(userId);
        if (u == null) return "Unknown";
        return u.getUsername(); // microservice-säkert tills patient-service/identity-service kopplas
    }

    private User safeFindUser(Long id) {
        if (id == null) return null;
        return userRepository.findById(id).orElse(null);
    }
}