package journalLabb.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PatientDetailsDto {
    private Long id;
    private String firstName;
    private String lastName;
    private String personalNumber;
    private Long userId;
    private Long assignedDoctorId;
}