package journalLabb.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ConditionDto {
    private Long id;
    private Long patientId;
    private Long practitionerUserId;
    private String text;
    private String severity;
    private LocalDateTime createdAt;
}