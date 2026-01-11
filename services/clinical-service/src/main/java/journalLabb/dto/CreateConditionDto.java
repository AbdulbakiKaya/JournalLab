package journalLabb.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateConditionDto {
    private Long patientId;
    private String text;
    private String severity;
}