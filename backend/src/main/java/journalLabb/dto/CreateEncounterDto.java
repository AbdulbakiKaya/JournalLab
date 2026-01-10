package journalLabb.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateEncounterDto {
    private Long locationId;
    private String note;
}