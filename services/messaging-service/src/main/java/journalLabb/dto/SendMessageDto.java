package journalLabb.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SendMessageDto {

    private Long patientId;

    // Om null och threadType=STAFF kan backend försöka auto-routa
    private Long receiverId;

    private String threadType; // "DOCTOR" / "STAFF"
    private String text;
}