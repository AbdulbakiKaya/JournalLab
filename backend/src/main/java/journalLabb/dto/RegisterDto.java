package journalLabb.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterDto {
    private String username;
    private String password;
    private String role;
    private String firstName;
    private String lastName;
    private String personalNumber;
    private String practitionerFirstName;
    private String practitionerLastName;
    private String licenseNumber;
    private Long assignedDoctorPractitionerId;
}
