package journalLabb.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SendMessageDto {
    private Long patientId;
    private Long receiverId;
    private String text;
    private String threadType;
}