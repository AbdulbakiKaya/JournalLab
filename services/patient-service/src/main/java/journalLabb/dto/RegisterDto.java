package journalLabb.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterDto {
    private String username;
    private String password;
    private String role;

    // patient
    private String firstName;
    private String lastName;
    private String personalNumber;
    private Long assignedDoctorPractitionerId;

    // doctor/staff
    private String practitionerFirstName;
    private String practitionerLastName;
    private String licenseNumber;
}