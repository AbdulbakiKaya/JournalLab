package journalLabb.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PractitionerDto {
    private Long id;
    private String firstName;
    private String lastName;
    private Long userId;
    private String type;
}