package journalLabb.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ConditionDto {
    private Long id;
    private String text;
    private String practitionerName;
    private String severity;
}
