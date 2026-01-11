package journalLabb.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class MessageDto {

    private Long id;

    private Long patientId;

    private Long senderId;
    private String senderName;

    private Long receiverId;
    private String receiverName;

    private String text;
    private LocalDateTime timestamp;

    // DOCTOR / STAFF (string räcker i DTO)
    private String threadType;
}